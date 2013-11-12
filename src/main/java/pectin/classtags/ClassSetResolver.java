package pectin.classtags;

import java.util.Set;

/**
 * every method return type of this interface is @Nonnull;
 * @author denis.lepekhin
 */
public interface ClassSetResolver {

    Set<Class<?>> getClassesByTag(String tag);

    Set<Class<?>> getClassesByTags(Iterable<String> tags);
}
