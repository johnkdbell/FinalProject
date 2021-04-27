package ca.johnnydb.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.events.Event;
import com.google.firebase.events.EventHandler;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

///GameDetailActivity is the activity which you can see more information about a game within.
public class GameDetailActivity extends AppCompatActivity
{
  private final int STANDARD_REQUEST_CODE = 0;

  private Toolbar myToolbar;

  private TextView tvTitle;
  private TextView tvPlatform;

  ArrayList<Game> games;

  private SharedPreferences sharedPreferences;

  SweetAlertDialog pDialog;

  FirebaseFirestore db = FirebaseFirestore.getInstance();
  FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
  FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
    .setPersistenceEnabled(true)
    .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
    .build();

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_game_detail);
    sharedPreferences = getSharedPreferences("general_setting", MODE_PRIVATE);

    myToolbar  = findViewById(R.id.myToolbar);

    tvTitle    = findViewById(R.id.tvTitle);
    tvPlatform = findViewById(R.id.tvPlatform);

    games = new ArrayList<>();

    setSupportActionBar(myToolbar);
    ActionBar ab = getSupportActionBar();
    ab.setDisplayHomeAsUpEnabled(true);

    db.setFirestoreSettings(settings);

    String title = sharedPreferences.getString("title", "NO TITLE");
    String platform = sharedPreferences.getString("platform", "NO PLATFORM");

    tvTitle.setText(String.valueOf(title));
    tvPlatform.setText(String.valueOf(platform));

    Log.d("JOHN", "data in onCreate(): Title: " + title + ", Platform: " + platform);

  }

  @Override
  protected void onResume()
  {
    super.onResume();
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

    boolean dark = prefs.getBoolean("checkbox_dark", false);

    View layout = findViewById(R.id.layout);
    int layout_background_color = dark ? Color.rgb(30, 30, 30) : Color.rgb(230, 230, 255);
    layout.setBackgroundColor(layout_background_color);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
      case android.R.id.home:
        onBackPressed();
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

}





















