package pectin.classtags;

import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@SupportedAnnotationTypes({ "pectin.classtags.ClassTags", "pectin.classtags.ClassTagSpec" })
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class AnnotationProcessor extends AbstractProcessor {

    // tag -> set(classesNames)
    private final LinkedHashMap<String, HashSet<String>> modelMap = new LinkedHashMap<>();

    private final HashMap<String, UpperBound> uppers = new HashMap<>();

    public AnnotationProcessor() {
        super();
    }

    @Override public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (TypeElement annotTypeElem: annotations) {
            final TypeMirror antype = annotTypeElem.asType();
            if (antype.toString().equals("pectin.classtags.ClassTags")) {
            
                for (Element elem : roundEnv.getElementsAnnotatedWith(annotTypeElem)) {
                    ClassTags tags = elem.getAnnotation(ClassTags.class);
                    final Name className = processingEnv.getElementUtils().getBinaryName((TypeElement) elem);
                    for (String thetag : tags.value()) {
                        final String tag = thetag.trim();
                        if (tag.isEmpty()) {
                            processingEnv.getMessager().printMessage(Kind.ERROR,
                                    "empty tag not allowed in @ClassTags: " + className);
                        }
                        getOrInit(tag).add(className.toString());
                    }
                }
            } else if (antype.toString().equals("pectin.classtags.ClassTagSpec")) {
    
                for (Element elem : roundEnv.getElementsAnnotatedWith(annotTypeElem)) {
                    List<? extends AnnotationMirror> amirros = elem.getAnnotationMirrors();
                    
                    final AnnotationMirror spec = Iterables.find(amirros, new Predicate<AnnotationMirror>() {
                        public boolean apply(AnnotationMirror input) {
                            return processingEnv.getTypeUtils().isSameType(input.getAnnotationType(), antype);
                        }
                    });
                    String head = null, value = null;
                    for ( Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> e: spec.getElementValues().entrySet()) {
                        if (e.getKey().getSimpleName().toString().equals("upperBound")) {
                            final String str = e.getValue().getValue().toString();
                            head = str.substring(0, str.length() - ".class".length());
                        }
                        if (e.getKey().getSimpleName().toString().equals("value")) {
                            value = e.getValue().getValue().toString();
                        }
                    }
                    UpperBound u = new UpperBound();
                    if (head != null) {
                        u.head = head;
                        /*
                         * u.tail = spec.upperBound().length == 1 ? null :
                         * Iterables.transform(
                         * Arrays.asList(Arrays.copyOfRange(spec.upperBound(), 1,
                         * spec.upperBound().length)), new Function<Class<?>, String>()
                         * {
                         * 
                         * @Override public String apply(Class<?> input) { // TODO
                         * Auto-generated method stub return input.getCanonicalName(); }
                         * });
                         */
                    } else {
                        u.head = processingEnv.getElementUtils().getBinaryName((TypeElement) elem).toString();
                    }
                    processingEnv.getMessager().printMessage(Kind.NOTE, "@ClassTags typeBound: " + value + " " + u.head);
                    value = value.substring(1, value.length() - 1); // remove double quotes;
                    if (uppers.get(value) != null) {
                        processingEnv.getMessager().printMessage(Kind.ERROR, "Expecting only one @ClassTagSpec per tag: " + value);
                    } else {
                        uppers.put(value, u);
                    }
                }
            }
        }
        
        

        if (roundEnv.processingOver() && !modelMap.isEmpty()) {
            final Filer filer = processingEnv.getFiler();
            try {
                FileObject o = filer.createResource(StandardLocation.SOURCE_OUTPUT, "pectin.classtags.codegen",
                        "ClassSetResolverImpl.java");

                class Model {
                    public ArrayList<Tag> tags = new ArrayList<>();

                    Model() {
                        for (Entry<String, HashSet<String>> e : modelMap.entrySet()) {
                            final Tag t = new Tag();
                            t.tag = e.getKey();
                            t.upper = uppers.get(e.getKey());
                            t.klasses = e.getValue();
                            tags.add(t);
                        }
                    }
                }
                MustacheFactory mf = new DefaultMustacheFactory();
                Mustache mustache = mf.compile(new InputStreamReader(this.getClass().getClassLoader()
                        .getResourceAsStream("META-INF/cs_gen_template.mustache")), "csgen");
                try (Writer wrt = o.openWriter()) {
                    mustache.execute(wrt, new Model());
                }
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Kind.ERROR, "@ClassTags error in code generation ");
                e.printStackTrace();
                throw new RuntimeException(e);
            }

        }
        return true;
    }

    private HashSet<String> getOrInit(String tag) {
        HashSet<String> r = modelMap.get(tag);
        if (r == null) {
            r = new HashSet<>();
            modelMap.put(tag, r);
        }
        return r;
    }

    static class Tag {
        public String tag;
        public Iterable<String> klasses;
        public UpperBound upper;
    }

    static class UpperBound {
        public String head;
        public Iterable<String> tail;
    }
}
