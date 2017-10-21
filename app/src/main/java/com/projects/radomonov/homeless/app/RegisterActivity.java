package com.projects.radomonov.homeless.app;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.radomonov.homeless.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etConfirmPass;
    private Button btnRegister;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference usersNickNamesEmails;
    private Map<String, String> mapUserNamesEmails = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        usersNickNamesEmails = FirebaseDatabase.getInstance().getReference().child("UsersNickNamesEmails");




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

        usersNickNamesEmails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mapUserNamesEmails = (Map<String, String>) dataSnapshot.getValue();

                for(Map.Entry<String,String> entry : mapUserNamesEmails.entrySet()) {
                    Log.i("tagche",entry.getKey() + " ---> " + entry.getValue());
                }

                final String userName = etName.getText().toString().trim();
                final String eMail = etEmail.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();
                String passConfirm = etConfirmPass.getText().toString().trim();

                if(!validateStringForNullAndIsEmpty(userName)) {
                    etName.setError("Invalid Name");
                    return;
                }

                if(mapUserNamesEmails.containsKey(userName)) {
                    etName.setError("NickName already exist!");
                    return;
                }

                if(!validateStringForNullAndIsEmpty(eMail)) {
                    etEmail.setError("Invalid eMail");
                    return;
                }

                if(mapUserNamesEmails.containsValue(eMail)) {
                    etEmail.setError("e-Mail already exist!");
                    return;
                }

                if(!validateStringForNullAndIsEmpty(pass)) {
                    etPassword.setError("Invalid Password");
                    return;
                }

                if(!pass.equals(passConfirm)) {
                    etConfirmPass.setError("Passwords don't match");
                    return;
                }


//        Map<String, String> userNamesEmails = new HashMap<>();
//        userNamesEmails.put(userName, eMail);

//        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(eMail) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(passConfirm)) {
//            if (pass.equals(passConfirm)) {
                mAuth.createUserWithEmailAndPassword(eMail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userID = mAuth.getCurrentUser().getUid();

                            DatabaseReference currentUser = usersNickNamesEmails.child(userName);
                            currentUser.setValue(eMail);

                            DatabaseReference currentUserDb = mDatabaseUsers.child(userID);

                            currentUserDb.child("nickName").setValue(userName);

                            Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                            Toast.makeText(RegisterActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
//            } else {
//                Toast.makeText(RegisterActivity.this, "Pass and ConfirmPass doesn't match!", Toast.LENGTH_SHORT).show();
//            }

//        } else {
//            Toast.makeText(RegisterActivity.this, "You have an empty field!", Toast.LENGTH_SHORT).show();
//        }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private boolean validateStringForNullAndIsEmpty(String str) {
        if(str == null || str.isEmpty()) {
            return false;
        }
        return true;
    }

}

