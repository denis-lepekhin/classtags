package pectin.classtags;

import com.google.common.base.Supplier;

public class ClassSets {
    public static final ClassSetResolver RESOLVER =   
            (new Supplier<ClassSetResolver>() {
             
                public ClassSetResolver get() {
                    try {
                        @SuppressWarnings("unchecked")
                        final Class<ClassSetResolver> k = (Class<ClassSetResolver>) 
                                Class.forName("pectin.classtags.codegen.ClassSetResolverImpl");
                        return k.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("check that codegeneration was ok", e);
                    }
                };
            }).get();
    
    private ClassSets() {
    }
}
