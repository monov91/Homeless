package com.projects.radomonov.homeless.app;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projects.radomonov.homeless.R;

import static android.R.attr.description;
import static com.projects.radomonov.homeless.R.id.et_rooms_create;

public class CreateOfferActivity extends AppCompatActivity {

    private EditText etTitle,etPrice,etCurrency,etRooms,etNeighbourhood;
    private Button btnSave;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_offer);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Offers");
        mStorage = FirebaseStorage.getInstance().getReference();

        etTitle = (EditText) findViewById(R.id.et_title_create);
        etPrice = (EditText) findViewById(R.id.et_price_create);
        etCurrency = (EditText) findViewById(R.id.et_currency_create);
        etRooms = (EditText) findViewById(R.id.et_rooms_create);
        etNeighbourhood = (EditText) findViewById(R.id.et_neighbourhood_create);

        btnSave = (Button) findViewById(R.id.btn_save_create);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String title = etTitle.getText().toString().trim();
                final int price = Integer.parseInt(etPrice.getText().toString());
                final String currency = etCurrency.getText().toString().trim();
                final int rooms = Integer.parseInt(etRooms.getText().toString());
                final String neighbourhood = etNeighbourhood.getText().toString().trim();

                // if(nqkvi validacii)

                StorageReference filepath = mStorage.child("Blog_images").child(imageUri.getLastPathSegment());
                filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        DatabaseReference newOffer = mDatabase.push();
                        newOffer.child("title").setValue(title);
                        newOffer.child("price").setValue(price);
                        newOffer.child("currency").setValue(currency);
                        newOffer.child("rooms").setValue(rooms);
                        newOffer.child("neighbourhood").setValue(neighbourhood);

                        finish();

                    }
                });
                filepath.putFile(imageUri).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateOfferActivity.this, "Couldnt get file", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });


    }
}
