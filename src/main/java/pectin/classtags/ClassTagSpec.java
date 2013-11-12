package pectin.classtags;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Using this annotation you'll get compile-time error if 
 * classes of some tag do not satisfy the upper bound;
 * 
 * @author denis.lepekhin
 */
@Target({ElementType.TYPE})
public @interface ClassTagSpec {
    String[] value();
    /**
     * if not present than upperBound is annotated type itself
     * @return
     */
    Class<?>[] upperBound() default Void.class; 
}
