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
After running Annotation Processor (which generates the code, namely the class ClassSetResolverImpl) all these sets of classes
will be available to your code (for injection or any other possible usage) thru
ClassSets.RESOLVER (static final field) which supports the following interface:

```java
public interface ClassSetResolver {
      Set<Class<?>> getClassesByTag(String tag);
    
       // the result is an intersection of tag sets;
       // note that it's actually a guava SetView;
      Set<Class<?>> getClassesByTags(Iterable<String> tags);
}
```

To improve type-safety (by compile-time type checking) it's possible
to specify upper type bound for some tag. 

```java
@ClassTagSpec("dao")
public interface DaoBaseClass {
//You get compile-time error if any class tagged with "dao"
//doesn't implement this interface;
	
}

```



