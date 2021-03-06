package com.projects.radomonov.homeless.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.app.MainActivity;
import com.projects.radomonov.homeless.database.DatabaseInfo;
import com.projects.radomonov.homeless.model.ContextWrapper;
import com.projects.radomonov.homeless.model.Owner;
import com.projects.radomonov.homeless.model.User;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;
import static android.telephony.PhoneNumberUtils.isGlobalPhoneNumber;
import static com.projects.radomonov.homeless.R.id.btn_save_changes_setup_acc;
import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;
import static java.lang.System.out;

/**
 * Created by Tom on 22.10.2017.
 */

public class SetupAccountFragment extends Fragment implements View.OnClickListener{

    public static final int GALLERY_REQUEST = 10;

    private View view;
    private ImageView imgProfilePic;
    private Button btnSaveChanges, btnCancelChanges;
    private FirebaseAuth mAuth;
    private DatabaseReference currentUser;
    private StorageReference mStorage;
    private RoundedBitmapDrawable round;
    private EditText etPhoneNumber;
    private Intent GalIntent;
    boolean flag = false;
    private Uri downloadURL;
    private OnFragmentUpdateListener mListener;
    private DatabaseReference currentUserPic;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setup_account, container, false);

        initialiseData();
        updateProfilePic();

        getCurrentPhoneNumber();

        imgProfilePic.setOnClickListener(this);
        btnSaveChanges.setOnClickListener(this);
        btnCancelChanges.setOnClickListener(this);

        return view;
    }

    public void GetImageFromGallery() {
        GalIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), GALLERY_REQUEST);
    }

    InputStream input = null;

    public void updateProfilePic() {
        // In this method we are taking current user profile picture,
        // cropping it with RoundedBitmapDrawableFactory by using Asynctask,
        // make it round and setting it to imgProfilePic to this fragment
        Bitmap bitmap = loadImageBitmap(getContext());
        round = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        round.setCircular(true);

        imgProfilePic.setImageDrawable(round);
       /* currentUserPic = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.users_directory_DB))
                .child(mAuth.getCurrentUser().getUid()).child(getResources().getString(R.string.profilePic_in_user_DB));

        currentUserPic.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    final String picURL = dataSnapshot.getValue().toString();

                    new AsyncTask<Void, Void, Bitmap>() {
                        @Override
                        protected Bitmap doInBackground(Void... voids) {
                            try {
                                input = new java.net.URL(picURL).openStream();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Bitmap image = BitmapFactory.decodeStream(input);
                            return image;
                        }

                        @Override
                        protected void onPostExecute(Bitmap object) {
                            round = RoundedBitmapDrawableFactory.create(getResources(), object);
                            round.setCircular(true);

                            imgProfilePic.setImageDrawable(round);
                            super.onPostExecute(object);
                        }
                    }.execute();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // In this method we are choosing profile picture from gallery,
        // cropping it with RoundedBitmapDrawableFactory,
        // make it round, setting it to imgProfilePic to this fragment
        // and writing this data into Firebase Database
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri2 = data.getData();
            CropImage.activity(imageUri2)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setAspectRatio(1, 1)
                    .start(getContext(), this);
        }

        if (requestCode == CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                InputStream inputStream;

                try {
                    inputStream = getActivity().getContentResolver().openInputStream(resultUri);
                    Log.i("pic", String.valueOf(resultUri));

                    Bitmap image = BitmapFactory.decodeStream(inputStream);

                    round = RoundedBitmapDrawableFactory.create(getResources(), image);
                    round.setCircular(true);
                    saveImage(getContext(),image);

                    imgProfilePic.setImageDrawable(round);

                    StorageReference filepath = mStorage.child(resultUri.getLastPathSegment());
                    filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            downloadURL = taskSnapshot.getDownloadUrl();
                            currentUser.child(getResources().getString(R.string.profilePic_in_user_DB)).setValue(downloadURL.toString());
                        }
                    });

                    filepath.putFile(resultUri).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });

                    flag = true;
                    Toast.makeText(getActivity().getBaseContext(), getResources().getString(R.string.successful_image_change_setup), Toast.LENGTH_SHORT).show();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    flag = false;
                    Toast.makeText(getActivity().getBaseContext(),getResources().getString(R.string.fnf_exception_message_setup), Toast.LENGTH_LONG).show();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getActivity().getBaseContext(), getResources().getString(R.string.cropping_error_setup), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.img_edit_profile:
                GetImageFromGallery();
                break;

            case R.id.btn_save_changes_setup_acc:
                String phoneNumber = etPhoneNumber.getText().toString().trim();
                if (!isValidPhoneNumber(phoneNumber)) {
                    etPhoneNumber.setError(getResources().getString(R.string.et_error_phone_setupaccount));
                    return;
                }
                currentUser.child(getResources().getString(R.string.phoneNumber_in_user_DB)).setValue(phoneNumber);
                mListener.updateFragment();
                goToMain();
                break;

            case R.id.btn_cancel_changes_setup_acc :
                goToMain();
                break;
        }
    }

    public interface OnFragmentUpdateListener {
        // interface for connection between fragments
        void updateFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (OnFragmentUpdateListener) activity;
        mListener.updateFragment();
    }

    public void goToMain() {
        //Sending to the Main(Search in uor case) fragment
        SearchFragment searchFrag = new SearchFragment();
        getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_container_main, searchFrag, "searchFrag").commit();
    }

    private void initialiseData() {
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference().child("ProfilePics");
        currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        etPhoneNumber = view.findViewById(R.id.edit_phone_number_setup_frag);
        btnSaveChanges = view.findViewById(btn_save_changes_setup_acc);
        btnCancelChanges = view.findViewById(R.id.btn_cancel_changes_setup_acc);
        imgProfilePic = view.findViewById(R.id.img_edit_profile);
    }

    private void getCurrentPhoneNumber() {
        // Setting PhoneNumber field with Current User PhoneNumber value
        String currentUserPhoneNumber = null;
        for (int i = 0; i < DatabaseInfo.getUsersList().size(); i++) {
            if (mAuth.getCurrentUser().getUid().equals(DatabaseInfo.getUsersList().get(i).getID())) {
                currentUserPhoneNumber = DatabaseInfo.getUsersList().get(i).getPhoneNumber();
            }
        }
        if (currentUserPhoneNumber != null) {
            etPhoneNumber.setText(currentUserPhoneNumber);
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Create regex to validate input phoneNumber
        String regex = "0[8-9]{2}[0-9]{7}";
        if(phoneNumber.matches(regex)) {
            return true;
        } else
            regex = "[+]359[8-9]{2}[0-9]{7}";
            if(phoneNumber.matches(regex)) {
                return true;
            } else {
            return false;
        }
    }

    public void saveImage(Context context, Bitmap bitmap){
        String name = "profilePic" + "." + "jpg";
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(name, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fileOutputStream != null){
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap loadImageBitmap(Context context){
        String name = "profilePic" + "." + "jpg";
        FileInputStream fileInputStream = null;
        Bitmap bitmap = null;
        try{
            fileInputStream = context.openFileInput(name);
            bitmap = BitmapFactory.decodeStream(fileInputStream);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fileInputStream != null){
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
}
