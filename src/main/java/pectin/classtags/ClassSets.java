package pectin.classtags;

import java.util.Set;

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
    
    
    public static final ClassSetResolver STRICT_RESOLVER = new AbstractClassSetResolver() {
        @Override public Set<Class<?>> getClassesByTag(String tag) {
            final Set<Class<?>> result = RESOLVER.getClassesByTag(tag);
            if (result.isEmpty()) {
                throw new IllegalStateException("not found any class for tag: " + tag);
            }
            return result;
        }
    };
    
    private ClassSets() {
    }
    
    
    @SuppressWarnings("all")
    public static <T, Z extends Class<? extends T>> Set<Z> verifyClassSet(Class<T> type, Set<? extends Class<?>> classSet) {
        for (Class<?> c: classSet) {
            if (!type.isAssignableFrom(type)) {
                throw new IllegalStateException("class is not a member of typed class set: " + c);
            }
        }
        return (Set<Z>) classSet;
    }
}
