/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.data.network;


/* Class not needed anymore , kept for future reference*/

/*
public class JsonUtils {
    private static final String TAG = JsonUtils.class.getSimpleName();
    private static final String ID = "id";
    private static final String ORIGINAL_TITLE = "original_title";
    private static final String POSTER_PATH = "poster_path";
    private static final String BACKDROP_PATH = "backdrop_path";
    private static final String OVERVIEW = "overview";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String RELEASE_DATE = "release_date";
    private static final String RESULTS = "results";

    private JsonUtils() {
    }

    */
/**
     * Return a list of {@link Movie} objects that has been built up from
     * parsing the given JSON response.
     *//*

    public static ArrayList<Movie> parseJson(String movieJson) {

        // Create an empty ArrayList that we can start adding movies to.
        ArrayList<Movie> moviesArrayList = new ArrayList<>();

        //try to parse the JSON response string. If there is a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try{
            JSONObject root = new JSONObject(movieJson);

            if (root.has(RESULTS)) {
                JSONArray movieArray = root.getJSONArray(RESULTS);

                for (int i = 0; i < movieArray.length(); i++) {
                    JSONObject currentMovie = movieArray.getJSONObject(i);

                    int id = currentMovie.getInt(ID);
                    String originalTitle = currentMovie.optString(ORIGINAL_TITLE);
                    String posterPath = currentMovie.optString(POSTER_PATH);
                    String backDropPath = currentMovie.optString(BACKDROP_PATH);
                    String overview = currentMovie.optString(OVERVIEW);
                    double voteAverage = currentMovie.optDouble(VOTE_AVERAGE);
                    String releaseDate = currentMovie.optString(RELEASE_DATE);

                    Movie movie = new Movie(id, originalTitle, posterPath, backDropPath, overview,
                            voteAverage, releaseDate);

                    moviesArrayList.add(movie);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "parseJson: ", e );
        }
        return moviesArrayList;
    }

}
*/
