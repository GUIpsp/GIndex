package net.guipsp.gindex;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("net.guipsp.gindex.IndexAndInstanceSubclasses")
public class IndexAndInstanceSubclassesProcessor extends AbstractProcessor {

	private Map<String, SubclassList> indexmap = new HashMap<String, SubclassList>();
	private List<Element> roots = new LinkedList<Element>();//This probably isn't best practice... whatever

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (!roundEnv.processingOver()) {
			for (TypeElement t : annotations) {
				for (Element e : roundEnv.getElementsAnnotatedWith(t)) {
					String file = e.getAnnotation(IndexAndInstanceSubclasses.class).value();
					indexmap.put(file, new SubclassList((TypeElement) e));
					roots.addAll(roundEnv.getRootElements());
				}
			}
		} else {
			{
				Collection<SubclassList> subclassLists = indexmap.values();//This needs optimization
				final Types types = processingEnv.getTypeUtils();
				for (Element re : roots) {
					for (TypeMirror t : types.directSupertypes(re.asType())) {
						for (SubclassList subclassList : subclassLists) {
							if (types.isSameType(t, subclassList.superclass.asType())) {
								subclassList.subclasses.add((TypeElement) re);
							}
						}
					}
				}
			}

			for (Map.Entry<String, SubclassList> key : indexmap.entrySet()) {
				try {
					BufferedWriter writer = new BufferedWriter(processingEnv.getFiler().createSourceFile(key.getKey()).openWriter());

					String name;

					if (key.getKey().contains(".")) {
						String[] parts = key.getKey().split("\\.(?=[^\\.]+$)");//Split into package and class
						name = parts[1];

						writer.write("package ");
						writer.write(parts[0]);
						writer.write(";");
						writer.newLine();
					} else {
						name = key.getKey();
					}

					writer.write("//THIS IS AN AUTOMATICALLY GENERATED FILE");
					writer.newLine();
					writer.write("//DO NOT MANUALLY EDIT");
					writer.newLine();
					writer.write("public class ");
					writer.write(name);
					writer.write("{");
					writer.newLine();
					writer.write("public static final ");
					writer.write(key.getValue().superclass.getQualifiedName().toString());
					writer.write("[] INDEX = {");
					writer.newLine();
					{
						Collections.sort(key.getValue().subclasses, new Comparator<TypeElement>() {
							@Override
							public int compare(TypeElement o1, TypeElement o2) {
								return o1.getQualifiedName().toString().compareTo(o2.getQualifiedName().toString());
							}
						});
						int i = 0;
						for (TypeElement file : key.getValue().subclasses) {
							writer.write("new ");
							writer.write(file.getQualifiedName().toString());
							writer.write("(");
							writer.write("" + i);
							writer.write("),");
							writer.newLine();
							i++;
						}
					}
					writer.write("};");
					writer.newLine();
					writer.write("}");
					writer.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return true;
	}

	private static class SubclassList {
		public final TypeElement superclass;
		public final List<TypeElement> subclasses;

		private SubclassList(TypeElement superclass) {
			this.superclass = superclass;
			this.subclasses = new LinkedList<TypeElement>();
		}
	}
}
