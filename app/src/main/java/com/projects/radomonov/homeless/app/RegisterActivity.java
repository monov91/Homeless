package com.projects.radomonov.homeless.app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.projects.radomonov.homeless.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etConfirmPass;
    private Button btnRegister;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        etName = (EditText) findViewById(R.id.et_nick_name_reg);
        etEmail = (EditText) findViewById(R.id.et_email_reg);
        etPassword = (EditText) findViewById(R.id.et_password_reg);
        etConfirmPass = (EditText) findViewById(R.id.et_confirm_password_reg);

        btnRegister = (Button) findViewById(R.id.btn_register_reg);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });
    }

    private void startRegister() {

        final String name = etName.getText().toString().trim();
        String eMail = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String passConfirm = etConfirmPass.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(eMail) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(passConfirm)) {
            if (pass.equals(passConfirm)) {
                mAuth.createUserWithEmailAndPassword(eMail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userID = mAuth.getCurrentUser().getUid();

                            DatabaseReference currentUserDb = mDatabaseUsers.child(userID);

                            currentUserDb.child("nickName").setValue(name);

                            Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                            Toast.makeText(RegisterActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(RegisterActivity.this, "Pass and ConfirmPass doesn't match!", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(RegisterActivity.this, "You have an empty field!", Toast.LENGTH_SHORT).show();
        }
    }

}

