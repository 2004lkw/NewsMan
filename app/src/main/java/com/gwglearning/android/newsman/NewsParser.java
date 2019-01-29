package com.gwglearning.android.newsman;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * com.gwglearning.android.newsman.NewsParser Class used to pull and parse the JSON
 */

public class NewsParser {
    // Private constructor makes this a series of functions? (instead of a class)
    //  I think that's what this does...
    private NewsParser() {
    }

    // WGET for a JSON.  This pulls the JSON raw string from an HTTP URL.
    public static String wgetForJSON(URL url) throws IOException {
        // String to hold our output.
        String jsonOut = "";

        // Check to make sure url has something in it.
        if (url == null) {
            return jsonOut;
        }

        HttpURLConnection connection = null;
        InputStream inputStream = null;

        // Trying to get some info here...
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000); //Seems to be a common time...
            connection.setConnectTimeout(15000); //Also ...  Common time...
            connection.setRequestMethod("GET"); // we are GETting information.
            connection.connect();

            if (connection.getResponseCode() == 200) {
                // Response of 200 means success!
                inputStream = connection.getInputStream();
                jsonOut = buildString(inputStream);
            } else {
                Log.e("Trying to connect: ", "Got " + connection.getResponseCode() + " from server instead of 200.");
            }
        } catch (IOException e) {
            Log.e("HTTP Connection: ", "Could not get news JSON results.", e);
        } finally {
            if (connection != null) {
                // need to close connections!
                connection.disconnect();
            }
            if (inputStream != null) {
                // This was declared originall with a THROWS IOException
                // because input stream requires it.  Tell it not to throw!
                inputStream.close();
            }
        }
        return jsonOut;
    }


    // This converts an InputStream int a string.
    private static String buildString(InputStream stream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (stream != null) {
            InputStreamReader streamReader = new InputStreamReader(stream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(streamReader);
            String lineIn = reader.readLine();
            while (lineIn != null) {
                output.append(lineIn);
                lineIn = reader.readLine();
            }
        }
        return output.toString();
    }

    // Create a URL from a string.
    public static URL makeURL(String url) {
        URL resultURL = null;
        if (url != null && !TextUtils.isEmpty(url)) {
            // Create a URL from a string.
            try {
                resultURL = new URL(url);
            } catch (MalformedURLException e) {
                resultURL = null; // if this is null, handle the null value in the listener for the list.
            }
        }
        return resultURL;
    }

    public static ArrayList<NewsItem> buildTheNews(String jsonURL) {
        // Check string.
        if (jsonURL == null || TextUtils.isEmpty(jsonURL)) {
            // then
            return null;
        }

        // The list that will be returned.
        ArrayList<NewsItem> returnList = new ArrayList<NewsItem>();

        // make the url string into a URL
        URL jURL = makeURL(jsonURL);
        if (jURL == null) {
            // stop here if the url is null
            return null;
        }

        // get raw JSON string
        String baseString = "";
        try {
            baseString = wgetForJSON(jURL);
        } catch (IOException e) {
            Log.e("GET JSON: ", " Got " + e);
            return null;
        } finally {
            if (baseString == null || TextUtils.isEmpty(baseString)) {
                return null;
            }
        }

        try {
            // Base string contains the raw JSON info now.  Lets parse it!
            JSONObject mainJSONObject = new JSONObject(baseString);

            JSONObject mainJSONObject2 = mainJSONObject.getJSONObject("response");

            // Get the array from the "results" portion of the JSON which contains the news
            //      objects.
            JSONArray mainJSONArray = mainJSONObject2.getJSONArray("results");

            // loop through each news result and place them into the arraylist.
            for (int counter = 0; counter < mainJSONArray.length(); counter++) {
                // Get the current news result.
                JSONObject currentNews = mainJSONArray.getJSONObject(counter);

                // Get the Title first.
                String title = currentNews.getString("webTitle");

                // Get URL string next...
                String url = currentNews.getString("webUrl");

                // Get the Category (section)
                String section = currentNews.getString("sectionName");

                // get the date published.
                String datePublished = currentNews.getString("webPublicationDate");

                // Try to get the author here.
                String author = "";
                try {
                    JSONObject currentNewsAuthorObj = currentNews.getJSONObject("fields");
                    // now, get the "byline" which contains the author.
                    author = currentNewsAuthorObj.getString("byline");
                } catch (JSONException e) {
                    Log.e("Fields Error :", "No author??" + e);
                }

                // Add this entry to the list and move on to the next.
                returnList.add(new NewsItem(title, url, section, author, datePublished));
            }
        } catch (JSONException e) {
            Log.e("JSON ERROR: ", "Got this :: " + e);
            return null;
        }

        return returnList;
    }
}
