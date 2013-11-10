classtags
=========

Java annotation processor, that generates class sets based on class tagging;
------------------------------------------------------------------------


You tag classes based on your app needs like this:

```java
@ClassTags({"sweet", "stage1"})
public class Orange {
}

@ClassTags({"spicy", "stage1"})
public class Pepper {
}


@ClassTags({"dao"})
public class Bean {
}
```
After running Annotation Processor, all this sets of classes
will be available to your code (for injection or any other possible usage), thru
ClassSets.RESOLVER (static final variable) which supports the following interface:

```java
public interface ClassSetResolver {
      Set<Class<?>> getClassesByTag(String tag);
    
       // the result is an intersection of tag sets;
       // note that it's actually a guava SetView;
      Set<Class<?>> getClassesByTags(Iterable<String> tags);
}
```




