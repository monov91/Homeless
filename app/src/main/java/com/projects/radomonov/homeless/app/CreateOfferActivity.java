package com.projects.radomonov.homeless.app;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projects.radomonov.homeless.R;

import static android.R.attr.data;
import static android.R.attr.description;
import static com.projects.radomonov.homeless.R.id.et_rooms_create;
import static com.projects.radomonov.homeless.R.id.image;

public class CreateOfferActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 1;

    private FirebaseAuth mAuth;
    private EditText etTitle, etPrice, etCurrency, etRooms, etNeighbourhood;
    private Button btnSave;
    private ImageButton imgbtnChoose;
    private DatabaseReference databaseMyOffers;
    private StorageReference mStorage;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_offer);
        mAuth = FirebaseAuth.getInstance();
        databaseMyOffers = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("MyOffers");
        mStorage = FirebaseStorage.getInstance().getReference();

        etTitle = (EditText) findViewById(R.id.et_title_create);
        etPrice = (EditText) findViewById(R.id.et_price_create);
        etCurrency = (EditText) findViewById(R.id.et_currency_create);
        etRooms = (EditText) findViewById(R.id.et_rooms_create);
        etNeighbourhood = (EditText) findViewById(R.id.et_neighbourhood_create);

        imgbtnChoose = (ImageButton) findViewById(R.id.img_select_create);
        imgbtnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

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
                //DatabaseReference offerImages = newOffer.child("images");
                if (imageUri != null) {
                    //StorageReference filepath = mStorage.child("Blog_images").child(imageUri.getLastPathSegment());
                    StorageReference filepath = mStorage.child("OfferImages").child(imageUri.getLastPathSegment());
                    filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            DatabaseReference newOffer = databaseMyOffers.push();
                            newOffer.child("title").setValue(title);
                            newOffer.child("price").setValue(price);
                            newOffer.child("currency").setValue(currency);
                            newOffer.child("rooms").setValue(rooms);
                            newOffer.child("neighbourhood").setValue(neighbourhood);
                            newOffer.child("image").setValue(downloadUrl.toString());
                            finish();
                        }
                    });
                    filepath.putFile(imageUri).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateOfferActivity.this, "Couldnt upload file", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            imageUri = data.getData();
            imgbtnChoose.setImageURI(imageUri);

        }
    }
}
