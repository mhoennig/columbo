columbo
=======

purpose
-------

Bytecode inspection of classes in classpath to determine usage of specified classes/methods etc., 
It utilizes asm class and method visitors, but focuses on usage of classes and methods 
(finding fields and annotations not yet implemented) which are converted into java.lang.reflect
objects for easy identification and direct access to their annotations (e.g. @Deprecated).

application
-----------

  ATTENTION: This chapter just mentions what you can implement based on this library, but neither
  the annotations nor the validation code is included in the library yet.  One reason is that
  your production code should not need to include this jar and instead you define your own 
  annotations.  Thus just your test code needs a dependency on this jar.  I will eventually
  also release a columbo-annotations.jar including the annotations and a columbo-validator.jar
  containing validators based on these annotations.

The initial idea was to offer a Java library to suppor implementing tests to determine usage of 
@Deprecated elements which are about to get removed.  E.g. you could offer such a test to your clients.

```Java
@Deprecated
@YourDeprecationReleaseAnnotation(soft="2.0", hard="3.0")
public YourDeprecatedClass {
...
}

public SomeClient {

    public void someMethodUsingYourDeprecatedClass(final YourDeprecatedClass param) {
        ...
    }
```

Output could be:
```
WARNING: SomeClient.java:45 SomeClient#someMethodUsingYourDeprecatedClass uses @Deprecated class YourDeprecatedClass which will be removed with releas 3.0
```

Another usage would be a kind of _poor mans Jigsaw subtitute_:

```Java
package your.servicepackage;

public YourClass {

    @YourInternalAnnotation
    public void yourInternalMethod() {
    ...
    }
}

package some.clientepackage;

public SomeClient {

    public void someMethodUsingYourInternalMethod(final YourClass param) {
        param.yourInternalMethod();
    }
```

Output could be:
```
ERROR: SomeClient.java:46 SomeClient#someMethodUsingYourInternalMethod uses @Internal method YourClass#yourInternalMethod
```
