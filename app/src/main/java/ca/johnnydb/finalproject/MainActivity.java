package ca.johnnydb.finalproject;

  import android.content.Intent;
  import android.content.SharedPreferences;
  import android.graphics.Color;
  import android.graphics.drawable.ColorDrawable;
  import android.os.Bundle;
  import android.util.Log;
  import android.view.Menu;
  import android.view.MenuInflater;
  import android.view.MenuItem;
  import android.view.View;
  import android.widget.AdapterView;
  import android.widget.Button;
  import android.widget.EditText;
  import android.widget.ListView;
  import androidx.appcompat.widget.SearchView;
  import com.google.android.gms.auth.api.signin.GoogleSignIn;
  import com.google.android.gms.auth.api.signin.GoogleSignInClient;
  import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
  import com.google.android.gms.tasks.OnCompleteListener;
  import com.google.android.gms.tasks.OnFailureListener;
  import com.google.android.gms.tasks.Task;
  import com.google.firebase.auth.FirebaseAuth;
  import com.google.firebase.auth.FirebaseUser;
  import com.google.firebase.firestore.DocumentSnapshot;
  import com.google.firebase.firestore.FirebaseFirestore;
  import com.google.firebase.firestore.FirebaseFirestoreSettings;
  import com.google.firebase.firestore.QueryDocumentSnapshot;
  import com.google.firebase.firestore.QuerySnapshot;
  import androidx.annotation.NonNull;
  import androidx.appcompat.app.AppCompatActivity;
  import androidx.appcompat.widget.Toolbar;
  import androidx.core.view.MenuItemCompat;
  import androidx.preference.PreferenceManager;
  import java.util.ArrayList;
  import cn.pedant.SweetAlert.SweetAlertDialog;

///MainActivity is the activity which becomes populated with games
public class MainActivity extends AppCompatActivity
{
  private final int STANDARD_REQUEST_CODE = 0;
  private FirebaseAuth mFirebaseAuth;
  private FirebaseUser mFirebaseUser;
  private GoogleSignInClient mSignInClient;

  private String mUsername;
  private String mPhotoUrl;

  private Toolbar myToolbar;
  private ListView lvItems;
  private Button btnGameActivity;

  private EditText etTitle;
  private EditText etPlatform;

  ArrayList<Game> games;
  Game game;

  GameAdapter adapter;
  SweetAlertDialog pDialog;

  private SharedPreferences sharedPreferences;

  FirebaseFirestore db = FirebaseFirestore.getInstance();
  FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
  FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
    .setPersistenceEnabled(true)
    .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
    .build();

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestIdToken(getString(R.string.default_web_client_id))
      .requestEmail()
      .build();
    mSignInClient = GoogleSignIn.getClient(this, gso);

    mFirebaseAuth = FirebaseAuth.getInstance();
    mFirebaseUser = mFirebaseAuth.getCurrentUser();

    if(mFirebaseUser == null)
    {
      startActivity(new Intent(this, Signin.class));
      finish();
      return;
    }
    else
    {
      mUsername = mFirebaseUser.getDisplayName();
      if (mFirebaseUser.getPhotoUrl() != null)
      {
        mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
      }
    }

    db.setFirestoreSettings(settings);

    setContentView(R.layout.activity_main);
    sharedPreferences = getSharedPreferences("general_setting", MODE_PRIVATE);

    myToolbar       = findViewById(R.id.myToolbar);
    lvItems         = findViewById(R.id.lvItems);
    btnGameActivity = findViewById(R.id.btnGameActivity);

    etTitle         = findViewById(R.id.etTitle);
    etPlatform      = findViewById(R.id.etPlatform);

    EventHandler eventHandler = new EventHandler();
    btnGameActivity.setOnClickListener(eventHandler);

    games = new ArrayList<>();

    setSupportActionBar(myToolbar);

    if(games != null)
    {
      pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
      pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
      pDialog.setTitleText("Loading");
      pDialog.setCancelable(false);
      pDialog.show();
    }


    populateListView();

    setLongClick();

    lvItems.setOnItemClickListener(new ListView.OnItemClickListener()
    {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id)
      {
        final Game obj = (Game) lvItems.getAdapter().getItem(position);
        String value = obj.getTitle();

        db.collection("users")
          .document(user.getEmail())
          .collection("games")
          .whereEqualTo("title", value).get()
          .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
          {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
              if (task.isSuccessful())
              {
                for (DocumentSnapshot document : task.getResult())
                {
                  Log.d("Document", document.getId());

                  String title = obj.getTitle();
                  String platform = obj.getPlatform();
                  SharedPreferences.Editor editor = sharedPreferences.edit();
                  editor.putString("title", title);
                  editor.putString("platform", platform);
                  editor.apply();

                  Intent intent = new Intent(MainActivity.this, GameDetailActivity.class);
                  startActivityForResult(intent, STANDARD_REQUEST_CODE);
                }
              }
              else
              {
                Log.d("Document", "Error getting documents: ", task.getException());
              }
            }
          });
      }
    });
  }

  class EventHandler implements View.OnClickListener
  {
    @Override
    public void onClick(View view) {
      if (view.getId() == R.id.btnGameActivity)
      {
        Log.d("JOHN", "btnGameActivityClicked clicked");
        btnGameActivityClicked();
      }
    }
  }

  public void setLongClick()
  {
    lvItems.setOnItemLongClickListener(new ListView.OnItemLongClickListener()
    {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final Game obj = (Game) lvItems.getAdapter().getItem(position);
        String titleValue = obj.getTitle();
        //String imageValue = obj.getImage();
        //.whereEqualTo("image", imageValue)


        db.collection("users")
          .document(user.getEmail())
          .collection("games")
          .whereEqualTo("title", titleValue).get()
          .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
          {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
              if (task.isSuccessful())
              {
                final DocumentSnapshot document = task.getResult().getDocuments().get(0);
                Log.d("Document", document.getId());

                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                  .setTitleText("Are you sure?")
                  .setContentText("Once deleted, it will be gone forever!")
                  .setConfirmText("Yes, delete it!")
                  .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                      sDialog.dismissWithAnimation();
                      db.collection("users").document(user.getEmail()).collection("games").document(document.getId()).delete();
                      games.remove(obj);
                      adapter = new GameAdapter(getApplicationContext(), games);
                      adapter.notifyDataSetChanged();
                      lvItems.setAdapter(adapter);
                    }
                  })
                  .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                      sDialog.dismissWithAnimation();
                    }
                  })
                  .show();
              }
              else
              {
                Log.d("Document", "Error getting documents: ", task.getException());
              }
            }
          });
        return false;
      }
    });
  }

  //Loads ListView
  private void populateListView()
  {
    db.collection("users")
      .document(user.getEmail())
      .collection("games").get()
      .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
      {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task)
        {
          if (task.isSuccessful())
          {
            for (DocumentSnapshot document : task.getResult())
            {
              adapter = new GameAdapter(getApplicationContext(), games);
              game = new Game(document.getString("title"),
                              document.getString("platform"),
                              document.getString("imageURL"));
              games.add(game);
              lvItems.setAdapter(adapter);
              Log.d("SUCCESS", document.getId() + " => " + document.getData());
            }
            pDialog.dismiss();
            pDialog.dismissWithAnimation();
          }
          else
          {
            pDialog.dismiss();
            pDialog.dismissWithAnimation();
            Log.d("FAIL", "Error getting documents: ", task.getException());
          }
          pDialog.dismiss();
          pDialog.dismissWithAnimation();
        }
      });
  }

  //Method to move to AddGameActivity
  private void btnGameActivityClicked()
  {
    Intent intent = new Intent(this, AddGameActivity.class);
    startActivityForResult(intent, STANDARD_REQUEST_CODE);
  }

  //Menu and Search within Action Bar
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu3, menu);

    MenuItem item = menu.findItem(R.id.action_search);

    SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
    {
      @Override
      public boolean onQueryTextSubmit(String s)
      {
        searchData(s);
        return false;
      }

      @Override
      public boolean onQueryTextChange(String s)
      {
        return false;
      }
    });

    if (item == null)
    {
      return true;
    }

    MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
      @Override
      public boolean onMenuItemActionExpand(MenuItem item) {
        if (getSupportActionBar() != null)
        {
          getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.searchActive)));
        }
        return true;
      }

      @Override
      public boolean onMenuItemActionCollapse(MenuItem item) {
        if (getSupportActionBar() != null)
        {
          getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionBarColor)));
        }
        onStart();
        return true;
      }
    });

    return true;
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

    setLongClick();
  }

  @Override
  protected void onStart() {
    super.onStart();
    if(adapter != null)
    {
      this.recreate();
      populateListView();
    }

  }

  //Search Method
  private void searchData(String s)
  {
    pDialog.setTitle("Searching...");
    pDialog.show();

    db.collection("users")
      .document(s)
      .collection("games").get()
      .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
      {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task)
        {
          games.clear();

          for (QueryDocumentSnapshot document : task.getResult())
          {
            game = new Game(document.getString("title"),
                            document.getString("platform"),
                            document.getString("imageURL"));
            games.add(game);
            Log.d("SUCCESS", document.getId() + " => " + document.getData());
          }

          GameAdapter friendAdapter = new GameAdapter(MainActivity.this, games);
          lvItems.setAdapter(friendAdapter);
          lvItems.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
              return false;
            }
          });

          pDialog.dismiss();
          pDialog.dismissWithAnimation();
        }
      }).addOnFailureListener(new OnFailureListener()
        {
        @Override
        public void onFailure(@NonNull Exception e)
        {
          pDialog.dismissWithAnimation();
          pDialog.dismiss();
        }
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
      case R.id.action_search:
        return true;

      case R.id.sign_out_menu:
        mFirebaseAuth.signOut();
        mSignInClient.signOut();

        mUsername = "anonymous";
        startActivity(new Intent(this, Signin.class));
        finish();
        return true;

      case R.id.action_settings:
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivityForResult(intent, STANDARD_REQUEST_CODE);
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

}