package com.gwglearning.android.newsman;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Settings JAVA for the Settings Layout
 */

public class SettingsLayout extends AppCompatActivity {

    @Override
    // on create inflate the view
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
    }

    // Inner class for preferences.  Settings.  Whichever.
    public static class SettingsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            // settings for number of items per screen.
            Preference numberOfItems = findPreference(getString(R.string.settings_number_of_items_key));
            bindPrecerence(numberOfItems);

            // Settings for search content (SECTIONS in the URL)
            Preference section = findPreference(getString(R.string.settings_sections_key));
            bindPrecerence(section);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String sValue = newValue.toString();
            // if this is on the list of things to select from....
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int itemIndex = listPreference.findIndexOfValue(sValue);
                // if something valid is selected...
                if (itemIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[itemIndex]);
                }
            } else {
                preference.setSummary(sValue);
            }
            return true;
        }

        // This helper method is sure useful.
        private void bindPrecerence(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }
}
