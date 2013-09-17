package net.guipsp.gindex;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("net.guipsp.gindex.IndexAndInstanceSubclasses")
public class IndexAndInstanceSubclassesProcessor extends AbstractProcessor {

	private Map<String, List<String>> indexmap = new HashMap<String, List<String>>();

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
			for (String key : indexmap.keySet()) {
				try {
					BufferedWriter writer = new BufferedWriter(processingEnv.getFiler().createSourceFile("key").openWriter());

					if (key.contains(".")) {
						String[] parts = key.split("\\.(?=[^\\.]+$)");//Split into package and class
						key = parts[1];

						writer.write("package ");
						writer.write(parts[0]);
						writer.newLine();
					}

					writer.write("//THIS IS AN AUTOMATICALLY GENERATED FILE");
					writer.newLine();
					writer.write("//DO NOT MANUALLY EDIT");
					writer.newLine();
					writer.write("public class ");
					writer.write(key);
					writer.write("{");
					writer.newLine();
					writer.write("public static final String[] INDEX = {");
					writer.newLine();
					for (String file : indexmap.get(key)) {
						writer.write("\"");
						writer.write(key);
						writer.write(",\"");
						writer.newLine();
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
}
