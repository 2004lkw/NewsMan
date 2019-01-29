package com.gwglearning.android.newsman;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<NewsItem> {

    public NewsAdapter(Context context, ArrayList<NewsItem> newsItems) {
        super(context, 0, newsItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);
        }

        NewsItem currentNews = getItem(position); // Get the current news listing.

        // Get all the views and set them up with some logic as to how they should be displayed.
        TextView titleView = listItemView.findViewById(R.id.news_title);
        TextView sectionView = listItemView.findViewById(R.id.section);
        TextView authorView = listItemView.findViewById(R.id.author);
        TextView dateView = listItemView.findViewById(R.id.date);

        // Title of this news (headline)
        titleView.setText(currentNews.getTitle());

        // Section this is from
        sectionView.setText(currentNews.getCategory());

        // Sometimes an author is there, sometimes it isn't.  Sometimes it says " in ..." and
        //  we don't need the extra info.  ****
        // Error checking on the author...
        String author = currentNews.getAuthor();
        if (author != null && !TextUtils.isEmpty(author)) {
            // Remove all the 'extra' from the author line.  Just need a name and the screen
            //      only has so much room on it.
            if (author.contains(" in ")) {
                // so if it's there....
                author = author.substring(0, author.indexOf(" in "));
            }
            // Also sometimes you get a FOR ....  same thing.
            if (author.contains(" for ")) {
                // if " for " is there....
                author = author.substring(0, author.indexOf(" for "));
            }
        } else {
            // there is no author!
            author = getContext().getResources().getString(R.string.no_author);
        }
        authorView.setText(author);

        // Only need to show the date.
        String dateOutput = currentNews.getDatePublished();
        if ((dateOutput != null && !TextUtils.isEmpty(dateOutput)) && dateOutput.contains("T")) {
            // get rid of the T and everything after that.
            dateOutput = dateOutput.substring(0, dateOutput.indexOf("T"));
        }

        dateView.setText(dateOutput);

        return listItemView;
    }


}
