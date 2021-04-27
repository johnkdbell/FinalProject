package ca.johnnydb.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Field;

///Signin is the activity that allows a user to log into the application with an email or Google
public class Signin extends AppCompatActivity  implements View.OnClickListener {

  private static final String TAG = "SignInActivity";
  private static final int RC_SIGN_IN = 9001;

  private SignInButton mSignInButton;

  private GoogleSignInClient mSignInClient;

  final FirebaseFirestore db = FirebaseFirestore.getInstance();

  FirebaseAuth mFirebaseAuth;
  FirebaseUser mFirebaseUser;

  EditText etEmail, etPassword;
  Button btnSignIn, btnSignUp;

  String email, password;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signin);

    // Assign fields
    mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);

    // Set click listeners
    mSignInButton.setOnClickListener(this);

    // Configure Google Sign In
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestIdToken(getString(R.string.default_web_client_id))
      .requestEmail()
      .build();
    mSignInClient = GoogleSignIn.getClient(this, gso);

    // Initialize FirebaseAuth
    mFirebaseAuth = FirebaseAuth.getInstance();
    mFirebaseUser = mFirebaseAuth.getCurrentUser();

    etEmail = (EditText) findViewById(R.id.etEmail);
    etPassword = (EditText) findViewById(R.id.etPassword);

    btnSignIn = (Button) findViewById(R.id.btnSignIn);
    btnSignUp = (Button) findViewById(R.id.btnSignUp);

    btnSignUp.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        signUpEmailActivity();
      }
    });

    btnSignIn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        Log.d("email", email);
        Log.d("password", password);

        mFirebaseAuth.signInWithEmailAndPassword(email, password)
          .addOnCompleteListener(Signin.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("", "signInWithEmail:success");
                mFirebaseUser = mFirebaseAuth.getCurrentUser();
                startActivity(new Intent(Signin.this, MainActivity.class));
              } else {
                // If sign in fails, display a message to the user.
                Log.w("", "signInWithEmail:failure", task.getException());
                Toast.makeText(Signin.this, "Authentication failed.",
                  Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Signin.this, MainActivity.class));
              }
            }
          });
      }
    });
  }

  public void signUpEmailActivity()
  {
    Intent intent = new Intent(Signin.this, SignUpEmail.class);
    startActivity(intent);
    finish();
  }

  private void signIn() {
    Intent signInIntent = mSignInClient.getSignInIntent();
    startActivityForResult(signInIntent, RC_SIGN_IN);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.sign_in_button:
        signIn();
        break;
    }
  }

  private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
    Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
    AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
    mFirebaseAuth.signInWithCredential(credential)
      .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
          Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

          // If sign in fails, display a message to the user. If sign in succeeds
          // the auth state listener will be notified and logic to handle the
          // signed in user can be handled in the listener.
          if (!task.isSuccessful()) {
            Log.w(TAG, "signInWithCredential", task.getException());
            Toast.makeText(Signin.this, "Authentication failed.",
              Toast.LENGTH_SHORT).show();
          } else {
            startActivity(new Intent(Signin.this, MainActivity.class));
            finish();
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
    etEmail = findViewById(R.id.etEmail);
    etPassword = findViewById(R.id.etPassword);
    TextView tvSignUpWithGoogle = findViewById(R.id.tvSignUpWithGoogle);
    TextView tvOr = findViewById(R.id.tvOr);
    TextView tvLogin = findViewById(R.id.tvLogin);

    int layout_background_color = dark ? Color.rgb(30, 30, 30) : Color.rgb(238, 238, 238);
    int text_color = dark ? Color.rgb(238, 238, 238) : Color.rgb(50, 50, 50);
    final int hint_text_color = dark ? Color.argb(50, 235, 235, 235) : Color.argb(50, 30, 30, 30);

    layout.setBackgroundColor(layout_background_color);
    etEmail.setTextColor(text_color);
    etPassword.setTextColor(text_color);
    etEmail.setHintTextColor(hint_text_color);
    etPassword.setHintTextColor(hint_text_color);

    ColorStateList colorStateList = ColorStateList.valueOf(hint_text_color);
    ViewCompat.setBackgroundTintList(etEmail, colorStateList);
    ViewCompat.setBackgroundTintList(etPassword, colorStateList);

    tvLogin.setTextColor(text_color);
    tvSignUpWithGoogle.setTextColor(text_color);
    tvOr.setTextColor(text_color);

  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // Result returned from launching the Intent in signIn()
    if (requestCode == RC_SIGN_IN) {
      Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
      try {
        // Google Sign In was successful, authenticate with Firebase
        GoogleSignInAccount account = task.getResult(ApiException.class);
        firebaseAuthWithGoogle(account);
      } catch (ApiException e) {
        // Google Sign In failed, update UI appropriately
        Log.w(TAG, "Google sign in failed", e);
      }
    }
  }

}
