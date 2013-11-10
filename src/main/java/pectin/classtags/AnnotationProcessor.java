package pectin.classtags;

import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
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
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

@SupportedAnnotationTypes("pectin.classtags.ClassTags")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class AnnotationProcessor extends AbstractProcessor {
    
    // tag -> set(classesNames)
    private final LinkedHashMap<String, HashSet<String>> modelMap = new LinkedHashMap<>();
    
    public AnnotationProcessor() {
        super();
    }

    @Override public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        
       for(Element elem: roundEnv.getElementsAnnotatedWith(ClassTags.class)) {
           ClassTags tags =elem.getAnnotation(ClassTags.class);
           final Name className = processingEnv.getElementUtils().getBinaryName((TypeElement)elem);
           for (String thetag: tags.value()) {
               final String tag = thetag.trim();
               if (tag.isEmpty()) {
                   processingEnv.getMessager().printMessage(Kind.ERROR, "empty tag not allowed in @ClassTags: " + className);
               }
               getOrInit(tag).add(className.toString());
           }
       }
       
       if (roundEnv.processingOver() && !modelMap.isEmpty()) {
           final Filer filer = processingEnv.getFiler();
           try {
               FileObject o = filer.createResource(StandardLocation.SOURCE_OUTPUT,
                       "pectin.classtags.codegen", "ClassSetResolverImpl.java");
               
               class Model {
                   public ArrayList<Tag> tags = new ArrayList<>();
                   Model() {
                       for (Entry<String, HashSet<String>> e: modelMap.entrySet()) {
                           final Tag t = new Tag();
                           t.tag =  e.getKey();
                           t.klasses = e.getValue();
                           tags.add(t);
                       }
                   }
               }
               MustacheFactory mf = new DefaultMustacheFactory();
               Mustache mustache = mf.compile(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("META-INF/cs_gen_template.mustache")), 
                       "csgen");
               try(Writer wrt = o.openWriter()) {
                   mustache.execute(wrt, new Model());
               }
           } catch(Exception e) {
               processingEnv.getMessager().printMessage(Kind.ERROR, "@ClassTags error in code generation ");
               e.printStackTrace();
               throw new RuntimeException(e);
           }
                 
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
    
    static class Tag {
        public String tag;
        public Iterable<String> klasses;
    }
    
    
    
    

}
