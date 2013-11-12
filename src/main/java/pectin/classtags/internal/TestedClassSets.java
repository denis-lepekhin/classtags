package pectin.classtags.internal;

import pectin.classtags.AbstractClassSetResolver;

abstract class TestedClassSets extends AbstractClassSetResolver {
    /*
    private TestedClassSets() {
    }
    
    private final ImmutableMap<String, Set<Class<?>>> tags2Classes = ImmutableMap.<String, Set<Class<?>>>builder()
            .put("tag", ImmutableSet.<Class<?>>builder()
                    .add(Object.class)
                    .add(Integer.class)
                    .build())
            .put("tag", ImmutableSet.<Class<?>>builder()
                    .add(Object.class)
                    .add(Integer.class)
                    .build())
            .build();
    
    public  Set<Class<?>> getClassesByTag(String tag) {
        return firstNonNull(tags2Classes.get(tag), Collections.<Class<?>>emptySet());
    }
    */
}
