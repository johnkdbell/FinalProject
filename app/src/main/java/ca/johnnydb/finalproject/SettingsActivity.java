package ca.johnnydb.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

///SettingsActivity is the activity that allows a user to change settings
public class SettingsActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
    getSupportFragmentManager().beginTransaction().replace(R.id.layout, new SettingsFragment()).commit();
    PreferenceManager.getDefaultSharedPreferences(this);
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

    boolean dark = prefs.getBoolean("checkbox_dark", false);

    View layout = findViewById(R.id.layout);
    int layout_background_color = dark ? Color.rgb(30, 30, 30) : Color.rgb(183, 183, 255);
    layout.setBackgroundColor(layout_background_color);

  }
}