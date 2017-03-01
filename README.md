## Where to start?

First off, read carefully the requirements of a specific test. Normally companies tend to ask you for simple tasks, probably related to what is their business about. They might ask you to develop a functionality with Google Maps, perhaps to show your knowledge about device sensors (i.e for IoT companies), or simply demonstrate a good scalable architecture.

All they have in common is that, usually, you have to create a project from scratch (or reuse one) and develop the requirements based on it.

`The better your code base is, the more interest they will confer.`

## The Structure

The sample I like most of a [Clean Architecture in Android](https://github.com/android10/Android-CleanArchitecture) is the one developed by [Fernando Cejas](https://github.com/android10), which follows the well known [Layered Architecture Pattern](https://www.oreilly.com/ideas/software-architecture-patterns/page/2/layered-architecture). 

I prefer to split layers in different modules to keep consistency with team development, because otherwise it can lead to use some classes in a wrong place, or in places out of a particular scope. For instance, a java module completely isolated and decoupled from the Android Framework will perform easier and better unit test, and will also encourage the team to separate the business classes from the UI, like avoiding the use of a presentation class into the domain layer.

<p align="center">
  <img alt="Clean Architecture" width="500" src="https://8thlight.com/blog/assets/posts/2012-08-13-the-clean-architecture/CleanArchitecture-8b00a9d7e2543fa9ca76b81b05066629.jpg"/>
  <br/><a href="http://blog.cleancoder.com/">Uncle Bob</a> and <a href="https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html"><em>The Clean Architecture</em></a>
</p>

By this way, you can easily separate your project into 3 layers, described within the following modules:

* One Java library called **_system_**, a module that doesn’t belong to any particular layered architecture but which might contains primary files like third party library dependencies, default JSON/XML config files, property files, some scripts, and any other file related to a build system.

<p align="center">
  <img src="/images/system.png"/>
</p>

* Another Java library called **_domain_**, that contains only business logic, and completely decoupled from the Android framework.

<p align="center">
  <img src="/images/domain.png"/>
</p>

* An Android library called **_data_**, that contains all related data and middleware services as well as provider implementations.

<p align="center">
  <img src="/images/data.png"/>
</p>

* As last but not least, the application module called **_app_**, which deals with all the UI logics, fully coupled with the Android Framework, it has the responsibility of interacting straight with the end user.

<p align="center">
  <img src="/images/app.png"/>
</p>


## The Business

The Skeleton App shows a list of Posts with its user avatar and a Detail page for each clicked item, so you can immediately suspect you would need a service provider that retrieves data from the cloud, performs some logic on it and present it to a customer.


<p align="center">
  <img src="https://fernandocejas.com/assets/migrated/clean_architecture_android.png"/>
  <br/><a href="https://fernandocejas.com/2014/09/03/architecting-android-the-clean-way/"><em>Layered Architecture Schema</em></a> by <em>Fernando Cejas</em>
</p>

## The Domain Layer

Once I have created the project structure I start from the domain layer, because this is the module that defines the business contract and logic I will use. At first, I need to introduce some plain objects representing the business data, and since I have access to some samples of the JSON format, I can make use of some [Json2Java](http://www.jsonschema2pojo.org/) website generator and place straight away my new objects into that layer faster than doing it manually.

### Considerations
My first consideration is then whether it worthwhile to generate getters and setters on these business objects, because you might be keen of placing any logic right into them, like the following:

```java
public void setTime(Date date) {
    this.time = date.getTime();
}

public Date getTime() {
    return new Date(this.time);
}
```

But that was not the case, and even so, it would probably belong to a _Model Object_ placed into the Presentation layer.

Next consideration is, do I need to implement the `Parcelable` interface? :thinking_face:

This interface is very useful, for instance, when you have to transport huge and heavy Objects between Activities or Services. You can make use of any Android Studio plugin like [Android Parcelable code generator](https://github.com/mcharmas/android-parcelable-intellij-plugin) for that purpose. I rather prefer to generate code from a plugin, since it does not add another dependency into your project and the code can be easily modified at any time without relying on a “magic boilerplate-less annotation library”.

Although that’s not the case either, if you find out in this situation and you need to sort this problem out, regarding this particular architecture, create some _Model Views_ in the _Presentation_ layer and map _Domain_ objects into these Models, because `Parcelable` needs the Android Framework.

In other circumstances, it might worth it to introduce the [Project Lombok](https://projectlombok.org/setup/android.html) library and plugin into the project, because is meant to avoid such boilerplate tasks, but honestly for an Interview Test I wouldn’t go for it, as it adds more complexity for the interviewer to run your project.

Other considerations in the _Domain_ layer might be to pass to the `UseCase` a `ThreadExecutor` interface as a parameter of its constructor, mainly because you will figure out how useful it is handling those Threads, and in general any dependency, when dealing with [Unit Tests](https://developer.android.com/training/testing/unit-testing/index.html) and especially in a [Reactive Programming](https://medium.com/@kuassivi/functional-reactive-programming-with-rxjava-part-2-78db194e7d35#.p4orpm7qc) approach.

```java
TestScheduler testScheduler = new TestScheduler();
given(mockThreadExecutor.getScheduler()).willReturn(testScheduler);
given(mockPostRepository.getComments(anyLong())).willReturn(Single.just(commentList));

// Attaches the subscriber and executes the Observable.
Subscription subscription = getCommentSizeUseCase.execute(testSubscriber, mock(GetCommentSize.Params.class));
testScheduler.triggerActions();
```

### Testing
Once I have finished writing the first business classes, even if I haven’t implemented everything, I can start preparing some [Unit Test](https://developer.android.com/training/testing/unit-testing/index.html) without building any data service or any UI component. Actually this looks pretty much like a [TDD First](https://www.versionone.com/agile-101/agile-software-programming-best-practices/test-first-programming/) approach, and **that** could be one of the questions an interviewer wants to ask you about.

What only matters in the _Domain_ sample, in terms of unit tests, are the `UseCase`’s implementations, since plain objects do not really contribute with any significant logic as well as interfaces, so you should avoid testing some sort of objects like entities or [pojos](https://en.wikipedia.org/wiki/Plain_old_Java_object), but _Model Views_ might be a candidate for a test though, and that should only happen in the presentation layer.

Regarding the assessment of your code, everything depends on how an interviewer think the things must be done. Some engineers don’t like to use [Powermock](https://github.com/powermock/powermock/wiki/GettingStarted) in their test, because even if it speeds up the way of testing classes, you might fall in some errors like: creating too much static classes, a wrong implementation of inheritance, over-visibility, wrong encapsulation, classes that don’t belong to your domain layer, etc.

So, unless you are required to do so, go only for [Mockito](https://github.com/mockito/mockito/wiki) in your tests. This would certainly give you an **extra bonus**, since it means you should be aware about every single piece of code you implemented and tested.

Aside from what it’s been said before, also follow these advices:

* Create a test class for every single class that should be tested, do not mix multiple test cases within the same place and make them independent, otherwise it might become very confusing.
* **Do not mock the class you are testing**, mock its dependencies.
* If you use _RxJava_ in your project, pass dependencies in the constructors, like `Schedulers` and make use of the `TestScheduler` class instead of overriding the `RxJavaHooks` behaviour. 
  `TestScheduler` gives you a better control over what happens during the execution of an `Observable` in terms of **Threading**. Look at the provided sample skeleton project, and have a read on [TestingRx](http://www.introtorx.com/content/v1.0.10621.0/16_TestingRx.html).
* Try to focus on checking the **behaviour of your code**, and ensures that all objects and methods you need for a specific purpose are fired _n_ times.
* **Skip testing real data** or even mocked data, but you should test you got the expected result, like a number of items or a particular class.
* **Do not test** an `Exception`, you shouldn’t throw any of them as they are risky especially in mobile development. If coding with _RxJava_, instead, carry on `Exception` through the `Observable.error(Throwable)`.
* Make all assumptions you need to guarantee your code works as expected, but try to **refactor** some repetitive logic into private methods, making the code more **readable**.

<p align="center">
  <img src="/images/build.jpeg"/>
</p>

## The Data Layer

Next decision to be made is what type of [Android HTTP Client](https://developer.android.com/reference/org/apache/http/client/HttpClient.html) you will be using in the project.

### Considerations
I could go for [Volley](https://developer.android.com/training/volley/index.html), [Retrofit](https://square.github.io/retrofit/) or my own implementation using [OkHttp](http://square.github.io/okhttp/) or [Apache HttpClient](http://hc.apache.org/httpcomponents-client-ga/), it does not really matter, but choose one you know well.

To be as short as posible, you should skip developing a _datasource_ or a cache _[strategy](https://github.com/kuassivi/RepositoryCache)_, unless required, and focus on your [Repository](https://msdn.microsoft.com/en-us/library/ff649690.aspx) implementation. If the interviewer really wants to know more upon this approach, they will probably ask you such scenario later.

### Testing
Time to test both the _Repository_ implementation and their associated [Rest Services](http://docs.oracle.com/javaee/6/tutorial/doc/gijqy.html). These processes are a very inner execution, so I didn’t have a way nor the need to supply a [Scheduler](https://github.com/ReactiveX/RxJava/wiki/Scheduler) for them, like I did in the `UseCase` classes, therefor to test those functionalities I should rely on a default execution of some [Observable](http://reactivex.io/documentation/contract.html) object. Testing such `Observable` needs to be immediate, so let’s use an own **runner** to override the default `Schedulers` and test this implementation on a [mocked server](https://github.com/square/okhttp/tree/master/mockwebserver).

## Presentation Layer

The presentation layer is probably the most convoluted part of the project, since you might go for very different structures that all match with a [layered architecture](https://www.oreilly.com/ideas/software-architecture-patterns/page/2/layered-architecture).

### Considerations
Instead of hindering someone to understand the code with a [MVVM Pattern](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel) or so, I chose to go for a simple [MVP Design Pattern](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter) and a [Package by Features](https://medium.com/@cesarmcferreira/package-by-features-not-layers-2d076df1964d) approach (for the _presentation_ layer only).

In my implementation, any view will be based on a `Fragment`, so I managed to create a `DispatcherActivity` to handle it.

In order to introduce [Inversion of control](https://martinfowler.com/bliki/InversionOfControl.html) in this skeleton project, I make use of [Dagger 2](https://google.github.io/dagger/) as a logic dependency injector and [Butterknife](http://jakewharton.github.io/butterknife/) as a view component binding.

### Testing
Normally one of the purposes of having a [MVP Design Pattern](https://antonioleiva.com/mvp-android/) is to isolate all the logic into the Presenter and let all UI stuff to belong to the View. Hence I decoupled all Android specific dependencies from the Presenter, so I could easily **mocked the View interface in a Unit Test**, execute all the functionalities, assess the Presenter, and assert the mocked View is firing properly.

In order to check the [Acceptance Criteria](https://nomad8.com/acceptance_criteria/) of the UI part of the App, I made use of the [Android Espresso API](https://google.github.io/android-testing-support-library/docs/espresso/), which is also designed to care itself about some async executions through the `AsyncTask` object. However, in some cases, usually in a real project, you might probably want to use your own `Executor` or other components like the [Schedulers](http://reactivex.io/documentation/scheduler.html) of _RxJava_.

In this particular skeleton project, I had to implement a way to control the Thread executions and override any dependency the program was handling. After a hard research over the Internet I managed to unify _Espresso_ testing and its [IdlingResource](https://github.com/googlesamples/android-testing/tree/master/ui/espresso/IdlingResourceSample) functionality with the dependency injection supported by _Dagger 2_.

Of course you still have the chance to use some library like [DaggerMock](https://github.com/fabioCollini/DaggerMock) published in Github, but that way you wouldn’t get the real control of it. There are some comments in the code that explain why I overrided dependency modules even if it’s [discouraged by the Dagger 2 team](https://google.github.io/dagger/testing.html).

## Handling Concurrency

While I was programming the features, I realised I would need to make 1 more request per item of the list of Posts just to get its associated User, because every Post item comes with the ID of the User instead of an Object.

The Posts request call returned 100 items, so I ended up with 101 request calls when the App was firstly loaded, which last almost **1 minute to finish**. Of course **I had to improve this behaviour**, in fact in the real world you might find yourself with this kind of scenarios. Accordingly, I managed to do a small experiment loading groups of emitted items in different Threads (one thread per core), and making parallel requests for each group.

This approach improved the performance execution in 85%, reaching a total time up to **8 seconds** (instead of 1 minute). I based this experiment straight into the following article written a year ago by [Thomas Nield](http://tomstechnicalblog.blogspot.co.uk/), who followed a comment made by [Dávid Karnok](https://github.com/akarnokd) which is the project lead of _RxJava_.

http://tomstechnicalblog.blogspot.co.uk/2016/02/rxjava-maximizing-parallelization.html

Although the skeleton project is not 100% complete, and at this moment is using an outdated version of RxJava, it’s a good sample you can reuse in your technical tests or as a place to see some strategies followed.

I will be updating and adding new technical features to the project every now and again.

If you think it might be of interest **share it!**

Thanks for reading.
