package net.guipsp.gindex;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("net.guipsp.gindex.IndexAndInstanceSubclasses")
public class IndexAndInstanceSubclassesProcessor extends AbstractProcessor {

	private Map<String, SubclassList> indexmap = new HashMap<String, SubclassList>();

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (!roundEnv.processingOver()) {
			for (TypeElement t : annotations) {
				for (Element e : roundEnv.getElementsAnnotatedWith(t)) {

					if (!(e instanceof TypeElement)) {
						continue;
					}

					final TypeElement te = (TypeElement) e;

					if (t.getKind() != ElementKind.ANNOTATION_TYPE) {
						break;
					}
					for (Element re : roundEnv.getRootElements()) {

						if (!(re instanceof TypeElement)) {
							continue;
						}

						final TypeElement tre = (TypeElement) re;

					}
				}
			}
		} else {
			for (Map.Entry<String, SubclassList> key : indexmap.entrySet()) {
				try {
					BufferedWriter writer = new BufferedWriter(processingEnv.getFiler().createSourceFile(key.getKey()).openWriter());

					String name;

					if (key.getKey().contains(".")) {
						String[] parts = key.getKey().split("\\.(?=[^\\.]+$)");//Split into package and class
						name = parts[1];

						writer.write("package ");
						writer.write(parts[0]);
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
					writer.write(key.getKey());
					writer.write("[] INDEX = {");
					writer.newLine();
					{
						int i = 0;
						for (String file : key.getValue().subclasses) {
							writer.write("new ");
							writer.write(file);
							writer.write("(");
							writer.write(i);
							writer.write("),");
							writer.newLine();
							i++;
						}
					}
					writer.write("}");
					writer.newLine();
					writer.write("}");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return true;
	}

	private static class SubclassList {
		public final String superclass;
		public final List<String> subclasses;

		private SubclassList(String superclass, List<String> subclasses) {
			this.superclass = superclass;
			this.subclasses = subclasses;
		}
	}
}
