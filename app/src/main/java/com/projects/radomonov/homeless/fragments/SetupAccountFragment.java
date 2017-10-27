package com.projects.radomonov.homeless.fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
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

import com.google.firebase.auth.FirebaseAuth;
import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.app.MainActivity;
import com.projects.radomonov.homeless.model.Owner;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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

    private RoundedBitmapDrawable round;
    private File file;
    private Uri imageUri;
    private Intent GalIntent, CropIntent, SaveIntent;
    boolean flag = false;
    private Owner owner;

    public  static final int GALLERY_REQUEST  = 10;
    public static final int REQUEST_PERMISSION_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_setup_account, container, false);
        mAuth = FirebaseAuth.getInstance();

        EnableRuntimePermission();

        owner = new Owner();
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
                Intent SaveIntent = new Intent(getContext(), MainActivity.class);
                owner.setUserPic(round);
                SaveIntent.putExtra("owner", owner);
                startActivity(SaveIntent);
            }
        });

        return view;
    }


    public void GetImageFromGallery(){
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
//            Picasso.with(getContext()).load(imageUri).into(imgProfilePic);
//            imgProfilePic.setImageURI(imageUri);
        }

        Log.e("vlado", "1");
        if (requestCode == CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.e("vlado", "2");
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Log.e("vlado", "3");
                Uri resultUri = result.getUri();

                InputStream inputStream;

                try {
                    inputStream = getActivity().getContentResolver().openInputStream(resultUri);

                    Bitmap image = BitmapFactory.decodeStream(inputStream);

                    round = RoundedBitmapDrawableFactory.create(getResources(),image);
                    round.setCircular(true);

                    imgProfilePic.setImageDrawable(round);

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


    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA))
        {
            Toast.makeText(getActivity(),"CAMERA permission allows us to Access CAMERA app",
                    Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(getActivity(),new String[]{
                    Manifest.permission.CAMERA}, REQUEST_PERMISSION_CODE);
        }
    }

}
