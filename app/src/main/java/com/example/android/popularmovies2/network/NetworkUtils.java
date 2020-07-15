/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.network;

import java.io.InputStream;

/* Class not needed anymore , kept for future reference*/
/*public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    // please acquire your API KEY from https://www.themoviedb.org/ then:
    // user your API key in the project's gradle.properties file: MY_TMDB_API_KEY="<your API KEY>"
    private static final String MY_TMDB_API_KEY = BuildConfig.TMDB_API_KEY;
    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String API_PARAMETER = "api_key";

    *//**
     * Returns new URL object by building URI by provided parameter and converting it to URL
     *//*
    public static URL buildUrl(String sortByParameter) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(sortByParameter)
                .appendQueryParameter(API_PARAMETER, MY_TMDB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "buildUrl: ", e);
        }
        Log.d(TAG, "buildUrl() called with: sortByParameter = [" + sortByParameter + "]" + "Built URL is: " + url);
        return url;
    }

    public static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";
        InputStream inputStream = null;

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        try {
            httpURLConnection.connect();


            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } catch (IOException e) {
            Log.e(TAG, "makeHttpRequest: Problem retrieving Json results", e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    *//**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     *//*
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

}*/
