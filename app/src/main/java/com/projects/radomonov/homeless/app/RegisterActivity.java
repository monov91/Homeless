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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etConfirmPass, etPhoneNumber;
    private Button btnRegister;

    private ArrayList<String> keyList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private Map<String, String> mapUserNamesEmails = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");


        etName = (EditText) findViewById(R.id.et_nick_name_reg);
        etEmail = (EditText) findViewById(R.id.et_email_reg);
        etPhoneNumber = (EditText) findViewById(R.id.et_phone_number_reg);
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

        mDatabaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                keyList = new ArrayList<>();
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    keyList.add(child.getKey());
                }


                for(Map.Entry<String,String> entry : mapUserNamesEmails.entrySet()) {
                    Log.i("tagche",entry.getKey() + " ---> " + entry.getValue());
                }

                final String userName = etName.getText().toString().trim();
                final String eMail = etEmail.getText().toString().trim();
                final String phoneNumber = etPhoneNumber.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();
                String passConfirm = etConfirmPass.getText().toString().trim();

                if(!validateStringForNullAndIsEmpty(userName)) {
                    etName.setError("Invalid Name");
                    return;
                }

                // iterating firebase and checking is user exist
                for (int i = 0; i < keyList.size(); i++) {
                    DatabaseReference nickName = mDatabaseUsers.child(keyList.get(i)).child("nickName");
                    nickName.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String nick = (String) dataSnapshot.getValue();
                            if(nick.equals(userName)) {
                                etName.setError("NickName already exist!");
                                return;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
//                if(mapUserNamesEmails.containsKey(userName)) {
//                    etName.setError("NickName already exist!");
//                    return;
//                }

                if(!validateStringForNullAndIsEmpty(eMail)) {
                    etEmail.setError("Invalid eMail");
                    return;
                }


                for (int i = 0; i < keyList.size(); i++) {
                    DatabaseReference nickName = mDatabaseUsers.child(keyList.get(i)).child("eMail");
                    nickName.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String nick = (String) dataSnapshot.getValue();
                            if(nick.equals(eMail)) {
                                etEmail.setError("e-Mail already exist!");
                                return;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
//                if(mapUserNamesEmails.containsValue(eMail)) {
//                    etEmail.setError("e-Mail already exist!");
//                    return;
//                }

                if(!validateStringForNullAndIsEmpty(phoneNumber)) {
                    etPhoneNumber.setError("Invalid PhoneNumber");
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


                mAuth.createUserWithEmailAndPassword(eMail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userID = mAuth.getCurrentUser().getUid();

                            DatabaseReference currentUserDb = mDatabaseUsers.child(userID);

                            currentUserDb.child("nickName").setValue(userName);
                            currentUserDb.child("eMail").setValue(eMail);
                            currentUserDb.child("phoneNumber").setValue(phoneNumber);

                            Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                            Toast.makeText(RegisterActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
