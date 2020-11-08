# Popular-Movies2
This product uses the TMDb API but is not endorsed or certified by TMDb.


This is the second project in Udacity Android Nanodegree course purely for educational purposes

Stage 2: Trailers, Reviews, and Favorites

User Experience
In this stage you’ll add additional functionality to the app you built in Stage 1.

You’ll add more information to your movie details view:

Your app will:
- You’ll allow users to view and play trailers ( either in the youtube app or a web browser).
- You’ll allow users to read reviews of a selected movie.
- You’ll also allow users to mark a movie as a favorite in the details view by tapping a button(star). This is for a local movies collection that you will maintain and does not require an API request*.
- You’ll modify the existing sorting criteria for the main view to include an additional pivot to show their favorites collection.

Specifications:
* App is written solely in the Java Programming Language.

* Movies are displayed in the main layout via a grid of their corresponding movie poster thumbnails.

* UI contains an element (i.e a spinner or settings menu) to toggle the sort order of the movies by: most popular, highest rated.

* UI contains a screen for displaying the details for a selected movie.

* Movie details layout contains title, release date, movie poster, vote average, and plot synopsis.

* App utilizes stable release versions of all libraries, Gradle, and Android Studio.

Kindly note that you will need an API Key from [TMDB.org][1]. in order to build and try this application

Add the following line to [USER_HOME]/.gradle/gradle.properties

For Windows OS, example for Denis user:

    C:\Users\Denis\.gradle
    
find the gradle.properties file and write in it :

**myTMDBApiKey="<_YOUR-API-KEY_>"** 

[1]:https://developers.themoviedb.org/3/getting-started/introduction

<img src="ScreenShots/device-2020-05-05-120708.png" width="300">    <img src="ScreenShots/device-2020-05-05-123133.png" width="300">

<img src="ScreenShots/device-2020-05-05-123223.png" width="300">    <img src="ScreenShots/device-2020-05-05-123342.png" width="300">
