# OMDB Android client
This is the repository for the Android client of OMDB.
The purpose of this repository is to illustrate the use of Kotlin Coroutines to achieve 
concurrency in Android programming, which is normally done by RxJava.

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

## Languages, libraries and tools used
* [Kotlin](https://kotlinlang.org/)
* [Retrofit](http://square.github.io/retrofit/)
* [OkHttp](http://square.github.io/okhttp/)
* [Moshi](https://github.com/square/moshi)
* AndroidX Libraries
* [KotlinCoroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html)
* [Koin](https://insert-koin.io/docs/1.0/documentation/reference/index.html)
* [Mockk](https://mockk.io/)
* [Espresso](https://developer.android.com/training/testing/espresso/index.html)
* [JUnit4](https://github.com/junit-team/junit4/wiki/Getting-started)

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
