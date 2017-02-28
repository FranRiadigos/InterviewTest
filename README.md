# Skeleton Project for Interview Tests

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-InterviewTest-brightgreen.svg?style=flat)](https://android-arsenal.com/details/3/5377)

An Android Project for developers to use as a template in order to speed up building
an interview test from scratch.

You can read what decisions and considerations were taken while developing this sample in
the following article:

**medium:** [Technical Interview Tests](https://medium.com/@kuassivi/technical-interview-tests-2b4794aa0070)


Some frequently questions you might be asking
-

* **How can I test large amount of data from a mocked request like a JSON file?**

   Have a view into _/data/src/test/_ `MockResponseDispatcher` class and see how it is handled.

    ```java
    new File(classLoader.getResource(fileName).getFile())
    ```

* **How can I chain and make parallel requests with RxJava and Retrofit?**

    Look straight into the _/data/.../interactor/_ `GetPostList` class and you'll find a particular strategy
    that uses `groupBy` and `flatMap` [RxJava Operators][16] plus the number of available
    processors in the device, to achieve the parallelism execution.

* **How can I take control of Dagger 2 modules and implement `IdlingResource` in Espresso tests?**

    Pay a special attention into the _/app/src/androidTest/.../feature/_ `EspressoTest` class.

    Remember you need to attach `EspressoTestRunner` to your Run/Debug Configurations in Android Studio.

* **How can I override default Schedulers?**

    Have a look into the _/data/src/test/_ `RxJavaTestRunner` class and you will see Hooks implementation
    and how to use it for RxJava v1.2

    ```java
    RxJavaHooks.reset();
    RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
    RxJavaHooks.setOnNewThreadScheduler(scheduler -> Schedulers.immediate());
    ```

* **How can I deal with Threads in RxJava (Unit Tests specific)?**

    The quick answer is by using `TestScheduler` class.

    Although in order to use it properly, you must implement your code with the ability
    to pass the `Scheduler` you want as a parameter, normally in the constructor of your class.

    ```java
    public interface ThreadExecutor {
        Scheduler getScheduler();
    }

    ...

    public class ClassToBeTested {
        public ClassToBeTested(ThreadExecutor executor) {
            // any implementation that uses ThreadExecutor#getScheduler()
        }
    }

    ...

    ThreadExecutor mockThreadExecutor = mock(ThreadExecutor.class);
    ClassToBeTested classToBeTested = new ClassToBeTested(mockThreadExecutor);

    TestScheduler testScheduler = new TestScheduler();
    given(mockThreadExecutor.getScheduler()).willReturn(testScheduler);

    // An execution that uses that Scheduler
    classToBeTested.execute();
    testScheduler.triggerActions();
    ```


Main Technologies
-

- [Layered Architecture Pattern][1] ([Clean Architecture][2])
- [Reactive Programming][9] with [RxJava][8]
- [Dependency Injection][3] with [Dagger 2][4]
- Android View [Binding][17] with [Butterknife][5]
- [Repository Pattern][6]
- [HTTP Client API][7]
- [Unit Test][10] following [TDD First][11] approach ([JUnit][12])
- [Testing UI][14] with [Espresso][14] ([black-box testing][15])



License
-

    Copyright (C) 2017 Francisco Gonzalez-Armijo Riádigos

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.




[1]: https://www.oreilly.com/ideas/software-architecture-patterns/page/2/layered-architecture
[2]: https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html
[3]: https://martinfowler.com/articles/injection.html
[4]: https://google.github.io/dagger/
[5]: http://jakewharton.github.io/butterknife/
[6]: https://msdn.microsoft.com/en-us/library/ff649690.aspx
[7]: https://square.github.io/retrofit/
[8]: https://github.com/ReactiveX/RxJava
[9]: https://medium.com/@kuassivi/functional-reactive-programming-with-rxjava-part-2-78db194e7d35#.7mx0stygm
[10]: https://developer.android.com/training/testing/unit-testing/index.html
[11]: https://www.versionone.com/agile-101/agile-software-programming-best-practices/test-first-programming/
[12]: http://junit.org/junit4/
[13]: https://developer.android.com/training/testing/unit-testing/local-unit-tests.html
[14]: https://google.github.io/android-testing-support-library/docs/espresso/
[15]: http://www.guru99.com/black-box-testing.html
[16]: http://reactivex.io/documentation/operators.html
[17]: http://softwareengineering.stackexchange.com/questions/200115/what-is-early-and-late-binding

