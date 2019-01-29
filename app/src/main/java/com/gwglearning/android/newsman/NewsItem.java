package com.gwglearning.android.newsman;

/**
 * Class to store information about each article in for the ListView.
 */

public class NewsItem {
    String mTitle; // The headline.
    String mUrl; // The URL of the news item.
    String mCategory; // Category this belongs to.
    String mAuthor; // The author.
    String mDatePublished; // Date published.

    public NewsItem(String title, String url, String category, String author, String datePublished) {
        // Constructor for the com.gwglearning.android.newsman.NewsItem class.
        mTitle = title;
        mCategory = category;
        mAuthor = author;
        mDatePublished = datePublished;
        mUrl = url;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getCategory() {
        return mCategory;
    }

    public String getDatePublished() {
        return mDatePublished;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getTitle() {
        return mTitle;
    }
}
