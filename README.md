# OMDB Android client
This is the repository for the Android client of OMDB.
The purpose of this repository is to illustrate the use of Kotlin Coroutines to achieve 
concurrency in Android programming, which is normally done by RxJava.

## Language, libraries and tools
- [Kotlin](https://kotlinlang.org/docs/reference/) the project is 100% written in Kotlin
- [Coroutines](https://kotlinlang.org/docs/reference/coroutines/coroutines-guide.html) a Kotlin
light-weighted concurrency library. This project uses it as a RxJava alternative to demonstrate how
it simplifies the code base
- [Koin](https://insert-koin.io/) a Kotlin light-weighted dependency injection framework. This
project use it as a Dagger alternative to demonstrate how simple it is to understand
- [Retrofit](https://square.github.io/retrofit/) A type-safe HTTP client for Android
- [Moshi](https://github.com/square/moshi) A modern JSON library for Kotlin and Java
- [Room](https://developer.android.com/training/data-storage/room/index.html) The Room persistence
library provides an abstraction layer over SQLite to allow for more robust database access while
harnessing the full power of SQLite
- [AndroidX libraries](https://developer.android.com/jetpack/androidx/)
- [Architecture components](https://developer.android.com/topic/libraries/architecture/) provides
the life-cycle aware components and observable data
- [Glide](https://github.com/bumptech/glide) An image loading and caching library for Android
focused on smooth scrolling
- [JUnit4](https://junit.org/junit4/) supports writing Kotlin unit tests
- [Espresso](https://developer.android.com/training/testing/espresso) supports writing Android UI
tests
- [Mockk](https://mockk.io/) an alternative of Mockito. Mockk is Kotlin friendly. It has better
support for coroutines mocking compared to Mockito

## Architecture
The architecture of the project follows the principles of Clean Architecture. 
![architecture](https://preview.ibb.co/hyTZMc/architecture.png)

### Modularisation
Each of the following layers will be implemented as a its own module in order to keep them 
separated in alignment with clean architecture and enable parallel builds in gradle.  

### View
Android UI components belong here. View communicates with ViewModel, subscribing to ViewState 
using LiveData component. Each View has a ViewModel attached that provides the data required by 
the view to make state transitions.


### ViewModels
![viewmodel](https://preview.ibb.co/neLQSH/viewmodel.png)

This module provides the logic of the View, by providing subscribing components to the view to 
react to. The ViewModel changes the ViewState based on the use case responses.


### Domain
Contain use cases of the application. They provide the application specific business rules and are 
are responsible of accessing data from different repositories, combine and transform them, to 
provide single use case business rule.

### Data
The Data layer is our access point to external data layers and is used to fetch data from multiple 
sources (network, cache). It contains implementations of Repositories, which request data from 
necessary RemoteDataSources and CacheDataSources to feed the use case and make communication 
between the 2 types of data sources.


### Remote
The Remote layer handles all communications with remote sources such as API calls using a Retrofit 
interface. A RemoteImpl class implements a Remote interface from the Data layer and uses a Service 
to retrieve data from the API.


### Cache
The Cache layer handles all communication with the local databasea.

## Tests

### Unit tests
Run the following command in a terminal window at the root level of the project

`./gradlew test`
### UI tests
1. Create and run an AVD Android Emulator (API 16 or above)
2. Go to developer options and disable all animations
3. Run the following command in a terminal window at the root level of the project
   
   `./gradlew connectedAndroidTest`
   
   Or simply right click the `app/src/androidTest/java` directory and click `Run 'All Tests'`
