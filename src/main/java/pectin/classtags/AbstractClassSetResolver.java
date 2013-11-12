package pectin.classtags;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.collect.Sets.intersection;

import java.util.Collections;
import java.util.Set;

public abstract class AbstractClassSetResolver implements ClassSetResolver {

    @Override public  Set<Class<?>> getClassesByTags(Iterable<String> tags) {
        Set<Class<?>> result = null;
        for(String tag: tags) {
            final Set<Class<?>> cur = getClassesByTag(tag);
            result = result == null ? cur : intersection(result, cur);
        }
        return firstNonNull(result, Collections.<Class<?>>emptySet());
    }
}
