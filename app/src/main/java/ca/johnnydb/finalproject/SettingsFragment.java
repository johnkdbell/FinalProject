package ca.johnnydb.finalproject;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

///SettingsFragment is the activity that displays settings to a user
public class SettingsFragment extends PreferenceFragmentCompat {

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.prefs, rootKey);
  }
}
