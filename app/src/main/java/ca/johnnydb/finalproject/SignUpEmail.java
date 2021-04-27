package ca.johnnydb.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

///SignUpEmail is the activity that allows a user to sign up for the application using their own email and password
public class SignUpEmail extends AppCompatActivity {

  FirebaseAuth mFirebaseAuth;

  EditText etEmail;
  EditText etPassword;
  EditText etPasswordConfirm;
  Button btnConfirm;

  String email;
  String password;
  String passwordConfirm;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signup_email);

    mFirebaseAuth = FirebaseAuth.getInstance();

    etEmail = findViewById(R.id.etEmail);
    etPassword = findViewById(R.id.etPassword);
    etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
    btnConfirm = findViewById(R.id.btnConfirm);

    btnConfirm.setOnClickListener(v -> {

      email = etEmail.getText().toString();
      password = etPassword.getText().toString();
      passwordConfirm = etPasswordConfirm.getText().toString();

      if (inputsValid())
      {
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
          .addOnCompleteListener(SignUpEmail.this, task -> {
            if (task.isSuccessful()) {
              // Sign in success, update UI with the signed-in user's information
              Log.d("TAG", "createUserWithEmail:success");
              Toast.makeText(SignUpEmail.this, "Sign Up Successful! Please login!.",Toast.LENGTH_SHORT).show();
              FirebaseUser user = mFirebaseAuth.getCurrentUser();
              signInEmailActivity();
            } else {
              // If sign in fails, display a message to the user.
              Log.w("TAG", "createUserWithEmail:failure", task.getException());
              Toast.makeText(SignUpEmail.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
            }
          });
      }

    });
  }

  public void signInEmailActivity()
  {
    Intent intent = new Intent(SignUpEmail.this, Signin.class);
    startActivity(intent);
    finish();
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
    etPasswordConfirm = findViewById(R.id.etPasswordConfirm);

    TextView tvEmailSignUp = findViewById(R.id.tvEmailSignUp);

    int layout_background_color = dark ? Color.rgb(30, 30, 30) : Color.rgb(238, 238, 238);
    int text_color = dark ? Color.rgb(238, 238, 238) : Color.rgb(50, 50, 50);
    final int hint_text_color = dark ? Color.argb(50, 235, 235, 235) : Color.argb(50, 30, 30, 30);

    layout.setBackgroundColor(layout_background_color);
    etEmail.setTextColor(text_color);
    etPassword.setTextColor(text_color);
    etPasswordConfirm.setTextColor(text_color);
    etEmail.setHintTextColor(hint_text_color);
    etPassword.setHintTextColor(hint_text_color);
    etPasswordConfirm.setHintTextColor(hint_text_color);

    ColorStateList colorStateList = ColorStateList.valueOf(hint_text_color);
    ViewCompat.setBackgroundTintList(etEmail, colorStateList);
    ViewCompat.setBackgroundTintList(etPassword, colorStateList);
    ViewCompat.setBackgroundTintList(etPasswordConfirm, colorStateList);

    tvEmailSignUp.setTextColor(text_color);

  }

  public boolean inputsValid()
  {
    Log.d("email", email);
    Log.d("password", password);
    Log.d("passwordConfirm", passwordConfirm);
    Log.d("equal", String.valueOf(passwordConfirm == password));

    if (email.length() > 0 && password.length() > 0 && passwordConfirm.length() > 0)
    {
      if (password.equals(passwordConfirm))
      {
        return true;
      }
    }
    return false;
  }

}