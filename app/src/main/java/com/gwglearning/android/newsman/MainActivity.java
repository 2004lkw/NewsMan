package com.gwglearning.android.newsman;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements LoaderCallbacks<ArrayList<NewsItem>> {

    // The link to the guardian with API key. (This one does US results with Tech + Science)
    // private static final String GUARDIAN_LINK ="https://content.guardianapis.com/search?";
    private static final int LOADER_ID = 1; // ID of the loader for use in this project.
    private TextView mEmptyState;
    private NewsAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.main_list);       // This is for the List View.
        mEmptyState = findViewById(R.id.nothing_label); // This is for NO NETWORK or NO ITEMS (Empty State)

        // put the empty state text view on the listview.
        listView.setEmptyView(mEmptyState);

        // making an adapter with an empty list.
        mAdapter = new NewsAdapter(this, new ArrayList<NewsItem>());

        // put the adapter on the listview.
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsItem thisNewsItem = mAdapter.getItem(position);

                // get the URL
                Uri newsUri = Uri.parse(thisNewsItem.getUrl());

                // Create an intent to view the link to this news item.
                Intent linkIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Start the intent.
                startActivity(linkIntent);
            }
        });

        // Getting the connectivity mgr for network connection state.
        ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get detail on the current primary network data
        NetworkInfo netInfo = connectionManager.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            // WE HAVE NETWORK SERVICE!!!
            // This is where we make the Loader start.
            LoaderManager loaderManager = getLoaderManager();

            // Init the loader.
            loaderManager.initLoader(LOADER_ID, null, this);

        } else {
            // Get rid of the progress view.
            View progress = findViewById(R.id.progress);
            progress.setVisibility(View.GONE);

            // Tell them there is no internet connection.
            mEmptyState.setText(R.string.no_network);
        }

    }


    public Loader<ArrayList<NewsItem>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedP = PreferenceManager.getDefaultSharedPreferences(this);
        String numberOfItems = sharedP.getString(
                getString(R.string.settings_number_of_items_key),
                getString(R.string.settings_number_of_items_default));

        String sectionType = sharedP.getString(
                getString(R.string.settings_sections_key),
                getString(R.string.settings_sections_default)
        );

        // Create a URI Builder off the base link
        // API key, value and base link are stored in string.  Not private for sure.
        String guardianLink = getString(R.string.base_url);
        Uri startUri = Uri.parse(guardianLink);
        Uri.Builder builder = startUri.buildUpon();

        // Use builder.appendQueryParameter to add KEY = VALUE to the string for the HTTP REQUEST.
        //      really it's just inserting '&' and a '='.
        // Add the number of items per page.
        builder.appendQueryParameter(getString(R.string.settings_number_of_items_key), numberOfItems);
        // Add the section type.
        builder.appendQueryParameter(getString(R.string.settings_sections_key), sectionType);
        // Add the order by value
        builder.appendQueryParameter(getString(R.string.order_by_key), getString(R.string.order_by_value));
        // Add the author setting.
        builder.appendQueryParameter(getString(R.string.author_key), getString(R.string.author_value));
        // Add the api key at the end.
        builder.appendQueryParameter(getString(R.string.api_key), getString(R.string.api_value));

        Log.e(":URI:", builder.toString());

        // start the loader with this URI
        return new NewsLoader(this, builder.toString());
    }

    public void onLoadFinished(Loader<ArrayList<NewsItem>> loader, ArrayList<NewsItem> newsList) {
        // Hide loading indicator cause we got ... something.
        View loadingCircle = findViewById(R.id.progress);
        loadingCircle.setVisibility(View.GONE);

        // set empty state as if there is nothing to show.
        mEmptyState.setText(R.string.no_news);

        // Clear adapter
        mAdapter.clear();

        // Add the stuff to the list!!!!
        if (newsList != null) {
            mAdapter.addAll(newsList);
        }
    }

    public void onLoaderReset(Loader<ArrayList<NewsItem>> loader) {
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // THIS function (method) creates the Settings icon
        //      at the top of the screen and allows the user to
        //      click on the dots to open the R.menu.<RESOURCE XML>
        //      which should be located under res/menu directory.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // If they click on the settings icon this is invoked.
        int selected = item.getItemId();
        if (selected == R.id.settings) {
            // If they clicked on this menu item which is
            //      named as Settings in the /res/menu/menu.xml
            //      then send this as an intent using the java file associated
            //      with that xml.
            Intent settings = new Intent(this, SettingsLayout.class);
            startActivity(settings);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


