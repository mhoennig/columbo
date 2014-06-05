columbo
=======

ATTENTION: this library is under early development, no release yet, better don't use yet

bytecode inspection of classes in classpath to determine usage of specified classes/methods etc., 

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
