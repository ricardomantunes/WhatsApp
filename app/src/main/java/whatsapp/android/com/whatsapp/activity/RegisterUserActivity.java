package whatsapp.android.com.whatsapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import whatsapp.android.com.whatsapp.R;
import whatsapp.android.com.whatsapp.helper.Base64Custom;
import whatsapp.android.com.whatsapp.model.User;

public class RegisterUserActivity extends AppCompatActivity {

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    private static final String TAG = "EmailPassword";

    private EditText name;
    private EditText email;
    private EditText password;
    private Button btRegister;
    //private User user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference firebaseDB = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        name = (EditText) findViewById(R.id.tb_userName);
        email = (EditText) findViewById(R.id.tb_userEmail);
        password = (EditText) findViewById(R.id.tb_userPassword);
        btRegister = (Button) findViewById(R.id.bt_registerUser);

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

        btRegister.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v)
                {
                    createAccount(email.getText().toString(), password.getText().toString());
                }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterUserActivity.this, "An error occurr",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            onAuthSuccess(task.getResult().getUser());
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
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
                            Toast.makeText(RegisterUserActivity.this, "An error occurr",
                                    Toast.LENGTH_SHORT).show();
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

    private void onAuthSuccess(FirebaseUser user) {
        String username = name.getText().toString();

        User userDB = new User();
        userDB.setEmail(user.getEmail());
        userDB.setName(name.getText().toString());
        String id = Base64Custom.convertBase64(userDB.getEmail());
        // Write new user
        writeNewUser(id, username, user.getEmail());
    }

    private void writeNewUser(String userId, String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);

        firebaseDB.child("users").child(userId).setValue(user);
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
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

        String nameField = name.getText().toString();
        if (TextUtils.isEmpty(nameField)) {
            name.setError("Required.");
            valid = false;
        } else {
            name.setError(null);
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
