package com.projects.radomonov.homeless.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.radomonov.homeless.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView btnRegister;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabaseUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialiseData();
        mProgress = new ProgressDialog(this);
        mDatabaseUsers.keepSynced(true);

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

    }

    private void checkLogin() {
        boolean invalidEntries = false;
        String eMail = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        Drawable errorIcon = getResources().getDrawable(R.drawable.error_final);
        errorIcon.setBounds(new Rect(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight()));
        if (!validateStringForNullAndIsEmpty(eMail)) {
            etEmail.setError("Invalid eMail", errorIcon);
            invalidEntries = true;
        }

        if (!validateStringForNullAndIsEmpty(password)) {
            etPassword.setError("Invalid Password", errorIcon);
            invalidEntries = true;
        }
        if(invalidEntries == true) {
            return;
        }

        mProgress.setMessage("Checking Login...");
        mProgress.show();

        mAuth.signInWithEmailAndPassword(eMail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mProgress.dismiss();
                    Toast.makeText(LoginActivity.this, "You successfully logged in...", Toast.LENGTH_SHORT).show();
                    checkUserExist();
                } else {
                    mProgress.dismiss();
                    Toast.makeText(LoginActivity.this, "Sign In problems...", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void initialiseData() {
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        etEmail = (EditText) findViewById(R.id.et_email_login);
        etPassword = (EditText) findViewById(R.id.et_pass_login);
        btnLogin = (Button) findViewById(R.id.btn_log_in_login);
        btnRegister = (TextView) findViewById(R.id.create_account_text_login_act);
    }

    private void checkUserExist() {

        if(mAuth.getCurrentUser() != null) {

            final String userID = mAuth.getCurrentUser().getUid();

            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(userID)) {
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    } else {
                        Toast.makeText(LoginActivity.this, "User doesn't exist yet", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    private boolean validateStringForNullAndIsEmpty(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_log_in_login :
                checkLogin();
                break;

            case R.id.create_account_text_login_act :
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;

        }
    }
}
