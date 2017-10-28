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
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.projects.radomonov.homeless.model.Owner;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;
import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;

/**
 * Created by Tom on 22.10.2017.
 */

public class SetupAccountFragment extends Fragment {

    private View view;
    private ImageView imgProfilePic;
    private Button btnEditPhoneNumber, btnSaveChanges, btnCancelChanges;
    private FirebaseAuth mAuth;
    private DatabaseReference currentUser;
    private StorageReference mStorage;
    private RoundedBitmapDrawable round;

    private Intent GalIntent;
    boolean flag = false;
    private Uri downloadURL;
    public static final int GALLERY_REQUEST = 10;
    public static final int REQUEST_PERMISSION_CODE = 1;

    private OnFragmentUpdateListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_setup_account, container, false);
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference().child("ProfilePics");
        currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        EnableRuntimePermission();

        btnEditPhoneNumber = view.findViewById(R.id.btn_edit_phone_number);
        btnSaveChanges = view.findViewById(R.id.btn_save_changes_setup_acc);
        btnCancelChanges = view.findViewById(R.id.btn_cancel_changes_setup_acc);
        imgProfilePic = view.findViewById(R.id.img_edit_profile);

        imgProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetImageFromGallery();
            }
        });

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mListener.updateFragment();
                getActivity().getFragmentManager().beginTransaction().remove(SetupAccountFragment.this).commit();

            }
        });

        btnCancelChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().getFragmentManager().beginTransaction().remove(SetupAccountFragment.this).commit();

            }
        });

        return view;
    }


    public void GetImageFromGallery() {
        GalIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

                    imgProfilePic.setImageDrawable(round);

                    StorageReference filepath = mStorage.child(resultUri.getLastPathSegment());
                    filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            downloadURL = taskSnapshot.getDownloadUrl();
                            currentUser.child("profilePic").setValue(downloadURL.toString());
                        }
                    });

                    filepath.putFile(resultUri).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });

                    flag = true;
                    Toast.makeText(getActivity().getBaseContext(), "Image changed!", Toast.LENGTH_SHORT).show();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    flag = false;
                    Toast.makeText(getActivity().getBaseContext(), "Unable to open image...", Toast.LENGTH_LONG).show();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getActivity().getBaseContext(), "Somethings wrong with cropping...", Toast.LENGTH_SHORT).show();
            }
        }
    }




    public void EnableRuntimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {
            Toast.makeText(getActivity(), "CAMERA permission allows us to Access CAMERA app",
                    Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.CAMERA}, REQUEST_PERMISSION_CODE);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    // interface for connection between fragments
    public interface OnFragmentUpdateListener {
        void updateFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (OnFragmentUpdateListener) activity;
        mListener.updateFragment();
    }
}
