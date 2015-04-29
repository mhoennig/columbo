columbo
=======

purpose
-------

The core of _columbo_ is to scan bytecode of the classes within a given package structure
and pass all referred Java elements (classes, methods, fields etc.) to a callback interface.

One plan is to inspect library client code to make sure (e.g. in a JUnit test):
- not to use internal APIs (marked with @Internal)
- not to override methods which should not (marked with @Final) 
	see JavaDoc of de.javagil.columbo.api.Final for explanation why Javas _final_ does not always work
- not to use specific external APIs, e.g. to disallow using java.util.Date
- not to use certain @Deprecated elements anymore

Another plan is to determine whether a new release of a Java library is compatible to the previous.

These functionalities are expecially useful for library vendors and within large scale Java projects.


implementation
--------------

Basically  _columbo_ is a wrapper for _asm_ which offers an easy to use visitor API which uses Java reflection objects.
It's especially usefuful to create cross-reference reports.

application
-----------

  ATTENTION: This chapter just mentions what you can implement based on this library, but 
  the validation code is not yet included in _columbo_. 
  As mentioned above, the plan is to include such functionality in _columbo_ in the future.

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
