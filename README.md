classtags
=========

Java annotation processor, generating class sets based on class tagging;
------------------------------------------------------------------------


You tag classes based on your app needs like this:


@ClassTags({"sweet", "stage1"})
public class Orange {
}

@ClassTags({"spicy", "stage1"})
public class Pepper {
}


@ClassTags({"dao"})
public class Bean {
}

After running Annotation Processor, all this sets of classes
will be available to your code (for injection or any other possible usage), thru
ClassSets.RESOLVER (static final variable) wicth supports the following interface:

public interface ClassSetResolver {
      Set<Class<?>> getClassesByTag(String tag);
    
       // the result is intersection of tag sets;
      Set<Class<?>> getClassesByTags(Iterable<String> tags);
}





