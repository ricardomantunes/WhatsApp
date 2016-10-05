package whatsapp.android.com.whatsapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import whatsapp.android.com.whatsapp.R;
import whatsapp.android.com.whatsapp.helper.Base64Custom;
import whatsapp.android.com.whatsapp.helper.Preferences;
import whatsapp.android.com.whatsapp.model.Contact;
import whatsapp.android.com.whatsapp.model.User;

public class LoginActivity extends AppCompatActivity {

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    private static final String TAG = "EmailPassword";

    private EditText name;
    private EditText email;
    private EditText password;
    private Button btLogin;
    //private User user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference firebaseDB = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        verifyUserLoged();

        email = (EditText) findViewById(R.id.edit_email);
        password = (EditText) findViewById(R.id.edit_password);
        btLogin = (Button) findViewById(R.id.bt_login);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // [START_EXCLUDE]
                updateUI(user);
                // [END_EXCLUDE]
            }
        };

        btLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                signIn(email.getText().toString(), password.getText().toString());
            }
        });
    }

    public void openUserRegister(View view)
    {
        Intent intent = new Intent(LoginActivity.this, RegisterUserActivity.class);
        startActivity(intent);
    }

    public void verifyUserLoged()
    {
        if (mAuth.getCurrentUser() != null)
        {
            goToMenuActivity();
        }
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            goToMenuActivity();
                        }

                        // [START_EXCLUDE]
                        //if (!task.isSuccessful()) {
                        //    mStatusTextView.setText(R.string.auth_failed);
                        //}
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private void goToMenuActivity()
    {
        final String idUserLogged = Base64Custom.convertBase64(mAuth.getCurrentUser().getEmail());
        DatabaseReference contactReference = firebaseDB.child("users").child(idUserLogged);

        contactReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userContact = dataSnapshot.getValue(User.class);

                Preferences preferences = new Preferences(LoginActivity.this);
                preferences.SaveData(idUserLogged, userContact.getName() != null ? userContact.getName() : "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean validateForm() {
        boolean valid = true;

        String emailField = email.getText().toString();
        if (TextUtils.isEmpty(emailField)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }

        String passwordField = password.getText().toString();
        if (TextUtils.isEmpty(passwordField)) {
            password.setError("Required.");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            //mStatusTextView.setText(getString(R.string.emailpassword_status_fmt, user.getEmail()));
            //mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            //findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            //findViewById(R.id.email_password_fields).setVisibility(View.GONE);
            //findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        } else {
            //mStatusTextView.setText(R.string.signed_out);
            //mDetailTextView.setText(null);

            //findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            //findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            //findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
