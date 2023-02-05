# kt-android-clicktoeat
<img src="/app/src/main/play_store_512.png" width="200" align="left" />
ClickToEat is a restaurant review app. Users after creating an account are able to search for restaurants and add reviews and ratings to them. If they do not find a restaurant they want to leave a review at, they can then create the restaurant and review that. Users can also add branches to existing restaurants and edit them when they become outdated. Users also can search for other users and like or dislike other reviews, which will then notify the user through push notifications.
<br/>
<br/>
I built this app for my school project to learn more about Jetpack Compose, animations and testing. This app consumes the updated <a href="https://clicktoeat.nasportfolio.com">ClickToEat API</a>.
<br clear="left" />

## Features
- User Authentication through the api
- Fetch relevant restaurants, branches, comments, users' favorite restaurants, likes and dislikes
- Able to create reviews of restaurants
- Able to add a restaurant to favorites
- Able to either like or dislike a user
- Recieve push notifications whenever other users like or dislike your review

## Installation
You can download this project by either downloading as a <a href="https://github.com/Coeeter/kt-android-clicktoeat/archive/refs/heads/master.zip">zip</a> or by cloning this repository.
After installation, open the project in the latest <a href="https://developer.android.com/studio">Android Studio</a> version. Open the file at `local.properties` and add your google maps api key to it.
```properties
MAPS_API_KEY=YOUR_GOOGLE_MAPS_API_KEY
```
If you do not have a google maps api key you can follow the instructions <a href="https://developers.google.com/maps/documentation/javascript/get-api-key">here</a> to get one.

Afterwards you can run the project on a emulator or a physical android device using Android Studio.

## Project Structure
This project is a multi module project and built using the layer strategy and follows Clean Architecture principles. This app also contains Unit and UI testing.

## Built Using
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Google Maps for Android SDK](https://developers.google.com/maps/documentation/android-sdk)
- [Coroutines for Android](https://developer.android.com/kotlin/coroutines)
- [Hilt](https://dagger.dev/hilt/)
- [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging)
- [JUnit](https://junit.org/junit4/)
- [Mockito](https://site.mockito.org/)
- [Compose Test](https://developer.android.com/jetpack/compose/testing)
- [UI Automator](https://developer.android.com/training/testing/other-components/ui-automator)
- [OkHttp](https://square.github.io/okhttp/)
- [Gson](https://github.com/google/gson)
- [Accompanist libraries for Jetpack Compose](https://google.github.io/accompanist/)
