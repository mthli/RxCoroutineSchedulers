# RxCoroutineSchedulers

Kotlin Coroutines as RxJava Schedulers 😈

The main idea comes from [Allow converting CoroutineDispatcher to RxJava scheduler](https://github.com/Kotlin/kotlinx.coroutines/issues/968) 💡

## Examples

Use extension function `asScheduler()` at your target [CoroutineDispatcher](https://kotlinlang.org/docs/reference/coroutines/coroutine-context-and-dispatchers.html#dispatchers-and-threads) 😎

```kotlin
val disposable = Observable
  .create(...)
  .subscribeOn(Dispatchers.IO.asScheduler())
  .observeOn(Dispatchers.Main.asScheduler())
  .subscribe(...)
```

You can also specify a [CoroutineScope](https://kotlinlang.org/docs/reference/coroutines/coroutine-context-and-dispatchers.html#coroutine-scope) that all coroutine jobs will run in it and canceled by it 💅

```kotlin
val disposable = Observable
  .create(...)
  .subscribeOn(Dispatchers.IO.asScheduler(yourScope))
  .observeOn(Dispatchers.Main.asScheduler(yourScope))
  .subscribe(...)
```

**Attention:**

`subscribeOn` and `observeOn` must run in **same** scope,

otherwise you need to call `disposable.dispose()` after call `yourScope.cancel()` 👀

## Gradle

At your top-level `build.gradle` ⬇️

```groovy
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```

And then at your project `build.gradle` ⬇️

```groovy
dependencies {
  // for RxJava2 users, use v2.0.0
  implementation 'com.github.mthli:RxCoroutineSchedulers:v3.0.0'
}
```

Done 🍻

## Maven

At your `pom.xml` ⬇️

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
<dependencies>
  <dependency>
    <groupId>com.github.mthli</groupId>
    <artifactId>RxCoroutineSchedulers</artifactId>
    <!-- for RxJava2 users, use v2.0.0 -->
    <version>v3.0.0</version>
  </dependency>
</dependencies>
```

Done 🍻

## License

    Copyright 2020 Matthew Lee

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
