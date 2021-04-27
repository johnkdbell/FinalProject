package ca.johnnydb.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;

///AddGameActivity is the class that allows a user to add a game to their collection
public class AddGameActivity extends AppCompatActivity
{
  private final int STANDARD_REQUEST_CODE = 0;
  private FirebaseAuth mFirebaseAuth;
  private FirebaseUser mFirebaseUser;
  private String mUsername;
  private String mPhotoUrl;

  private Toolbar myToolbar;
  private ListView lvItems;
  private Button addBtn, btnImage;

  private EditText etTitle;
  private EditText etPlatform;
  private TextView tvLink;

  private ImageView ivBoxart;

  ArrayList<Game> games;
  Game game;

  Uri imageUri;
  StorageReference mRef;

  SharedPreferences sharedPreferences;

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
    setContentView(R.layout.activity_add_game);
    sharedPreferences = getSharedPreferences("general_setting", MODE_PRIVATE);

    myToolbar  = findViewById(R.id.myToolbar);
    lvItems    = findViewById(R.id.lvItems);
    addBtn     = findViewById(R.id.addBtn);
    btnImage   = findViewById(R.id.btnImage);

    mRef = FirebaseStorage.getInstance().getReference();

    etTitle    = findViewById(R.id.etTitle);
    etPlatform = findViewById(R.id.etPlatform);
    tvLink = findViewById(R.id.tvLink);

    ivBoxart = findViewById(R.id.ivBoxart);

    games = new ArrayList<>();

    setSupportActionBar(myToolbar);
    ActionBar ab = getSupportActionBar();
    ab.setDisplayHomeAsUpEnabled(true);

    mFirebaseAuth = FirebaseAuth.getInstance();
    mFirebaseUser = mFirebaseAuth.getCurrentUser();
    db.setFirestoreSettings(settings);

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

    addBtn.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View view)
      {
        final String sharedImageURL = sharedPreferences.getString("imageURL", "NO IMAGE");

        Map<String, Object> gameMap = new HashMap<>();
        gameMap.put("title", etTitle.getText().toString());
        gameMap.put("platform", etPlatform.getText().toString());
        gameMap.put("imageURL", sharedImageURL);

        db.collection("users")
          .document(user.getEmail())
          .collection("games")
          .document()
          .set(gameMap)
          .addOnSuccessListener(new OnSuccessListener<Void>()
          {
            @Override
            public void onSuccess(Void aVoid)
            {
              game = new Game(etTitle.getText().toString(),
                              etPlatform.getText().toString(),
                              tvLink.getText().toString());
              games.add(game);
              etTitle.setText("");
              etPlatform.setText("");
            }
          })
          .addOnFailureListener(new OnFailureListener()
          {
            @Override
            public void onFailure(@NonNull Exception e)
            {
            }
          });
      }
    });

    btnImage.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View view)
      {
        choosePicture();
      }
    });

  }

  public void choosePicture()
  {
    Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    startActivityForResult(intent, 1);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null)
    {
      imageUri = data.getData();
      ivBoxart.setImageURI(imageUri);
      uploadPicture();
    }
  }

  private void uploadPicture()
  {
    final StorageReference gameRef = mRef.child(user.getEmail() + "/docs/games/" + etTitle.getText().toString() + "/" + imageUri.getLastPathSegment());
    StorageTask<UploadTask.TaskSnapshot> uploadTask = gameRef.putFile(imageUri)
      .addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
          Log.d("FAIL", "FAIL");
          Toast.makeText(getApplicationContext(), "Failed to Upload", Toast.LENGTH_LONG);
        }
      }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
          Log.d("SUCCESS", "SUCCESS");
          Toast.makeText(getApplicationContext(), "Successfully Uploaded", Toast.LENGTH_LONG);
        }
      });

    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
      @Override
      public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
        if (!task.isSuccessful()) {
          throw task.getException();
        }
        // Continue with the task to get the download URL
        return gameRef.getDownloadUrl();
      }
    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
      @Override
      public void onComplete(@NonNull Task<Uri> task) {
        if (task.isSuccessful()) {
          Uri downloadUri = task.getResult();
          Log.d("PATH", downloadUri.toString());
          tvLink.setText(downloadUri.toString());

          SharedPreferences sharedPreferences = getSharedPreferences("general_setting", MODE_PRIVATE);
          SharedPreferences.Editor editor = sharedPreferences.edit();
          editor.putString("imageURL", downloadUri.toString());
          editor.apply();
        } else {
          // Handle failures
          // ...
        }
      }
    });
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

    boolean dark = prefs.getBoolean("checkbox_dark", false);

    View layout = findViewById(R.id.layout);
    etTitle = findViewById(R.id.etTitle);
    etPlatform = findViewById(R.id.etPlatform);

    int layout_background_color = dark ? Color.rgb(30, 30, 30) : Color.rgb(230, 230, 255);
    int text_color = dark ? Color.rgb(238, 238, 238) : Color.rgb(50, 50, 50);
    final int hint_text_color = dark ? Color.argb(50, 235, 235, 235) : Color.argb(50, 30, 30, 30);

    layout.setBackgroundColor(layout_background_color);
    etTitle.setHintTextColor(hint_text_color);
    etPlatform.setHintTextColor(hint_text_color);
    etTitle.setTextColor(text_color);
    etPlatform.setTextColor(text_color);

    ColorStateList colorStateList = ColorStateList.valueOf(hint_text_color);
    ViewCompat.setBackgroundTintList(etTitle, colorStateList);
    ViewCompat.setBackgroundTintList(etPlatform, colorStateList);

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
      case R.id.action_search:
        return true;

      case android.R.id.home:
        onBackPressed();
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

}