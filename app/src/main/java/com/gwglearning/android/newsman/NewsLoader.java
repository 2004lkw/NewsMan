package com.gwglearning.android.newsman;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.ArrayList;

/**
 * Async task loader for news items.
 */

public class NewsLoader extends AsyncTaskLoader<ArrayList<NewsItem>> {

    private String mUrl;

    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<NewsItem> loadInBackground() {
        ArrayList<NewsItem> outList = NewsParser.buildTheNews(mUrl);
        return outList;
    }
}
