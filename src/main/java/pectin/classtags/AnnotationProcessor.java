package pectin.classtags;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

@SupportedAnnotationTypes("pectin.classtags.ClassTags")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class AnnotationProcessor extends AbstractProcessor {
    
    public AnnotationProcessor() {
        super();
    }
    
    private final LinkedHashMap<String, HashSet<String>> modelMap = new LinkedHashMap<>();

    @Override public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        
       for(Element elem: roundEnv.getElementsAnnotatedWith(ClassTags.class)) {
           ClassTags tags =elem.getAnnotation(ClassTags.class);
           final Name className = processingEnv.getElementUtils().getBinaryName((TypeElement)elem);
           for (String thetag: tags.value()) {
               final String tag = thetag.trim();
               if (tag.isEmpty()) {
                   processingEnv.getMessager().printMessage(Kind.ERROR, "empty tag not allowed");
               }
               getOrInit(tag).add(className.toString());
               processingEnv.getMessager().printMessage(Kind.NOTE, "processed the class " + className);
           }
       }
       
       if (roundEnv.processingOver()) {
           final Filer filer = processingEnv.getFiler();
       }
        
       return true;
    }
    
    
    private  HashSet<String> getOrInit(String tag) {
        HashSet<String> r = modelMap.get(tag);
        if (r == null) {
            r = new HashSet<>();
            modelMap.put(tag, r);
        }
        return r;
        
    }
    
    
    

}
