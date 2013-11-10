package pectin.classtags;

import java.util.Set;

public interface ClassSetResolver {
      Set<Class<?>> getClassesByTag(String tag);
    
      Set<Class<?>> getClassesByTags(Iterable<String> tags);
}
