package com.projects.radomonov.homeless.fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.projects.radomonov.homeless.R;
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

    private ImageView imgProfilePic;
    private Button btnEditPhoneNumber, btnSaveChanges, btnCancelChanges;
    private FirebaseAuth mAuth;

    private File file;
    private Uri imageUri;
    private Intent GalIntent, CropIntent;
    boolean flag = false;

    public  static final int GALLERY_REQUEST  = 10;
    public static final int REQUEST_PERMISSION_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setup_account, container, false);
        mAuth = FirebaseAuth.getInstance();

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

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setAspectRatio(1,1)
                    .start(getActivity());
//            Picasso.with(getContext()).load(imageUri).into(imgProfilePic);
            imgProfilePic.setImageURI(imageUri);
        }

        if (requestCode == CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                InputStream inputStream;

                try {
                    inputStream = getActivity().getContentResolver().openInputStream(resultUri);
//                    inputStream = getContentResolver().openInputStream(resultUri);

                    Bitmap image = BitmapFactory.decodeStream(inputStream);

                    RoundedBitmapDrawable round = RoundedBitmapDrawableFactory.create(getResources(),image);
                    round.setCircular(true);

                    imgProfilePic.setImageDrawable(round);

                    flag = true;
                    Toast.makeText(getActivity().getBaseContext(), "Image changed!", Toast.LENGTH_SHORT).show();

                }catch (FileNotFoundException e) {
                    e.printStackTrace();
                    flag = false;
                    Toast.makeText(getActivity().getBaseContext(), "Unable to open image...", Toast.LENGTH_LONG).show();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getActivity().getBaseContext(),"Somethings wrong with cropping...", Toast.LENGTH_SHORT).show();
            }
        }

//        if (requestCode == 2 && resultCode == RESULT_OK) {
//
//            if (data != null) {
//                uri = data.getData();
//                ImageCropFunction();
//            }
//        }
//        else if (requestCode == REQUEST_PERMISSION_CODE) {
//
//            if (data != null) {
//                Bundle bundle = data.getExtras();
//                Bitmap bitmap = bundle.getParcelable("data");
//                imgProfilePic.setImageBitmap(bitmap);
//            }
//        }
    }


    public void ImageCropFunction() {
        // Image Crop Code
        try {
            CropIntent = new Intent("com.android.camera.action.CROP");

            CropIntent.setDataAndType(imageUri, "image/*");

            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("outputX", 180);
            CropIntent.putExtra("outputY", 180);
            CropIntent.putExtra("aspectX", 3);
            CropIntent.putExtra("aspectY", 4);
            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);

            startActivityForResult(CropIntent, 1);

        } catch (ActivityNotFoundException e) {

        }
    }
    //Image Crop Code End Here


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
