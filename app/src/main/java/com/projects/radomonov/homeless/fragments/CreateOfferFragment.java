package com.projects.radomonov.homeless.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.adapters.OfferPhotosAdapter;
import com.projects.radomonov.homeless.database.DatabaseInfo;
import com.projects.radomonov.homeless.model.Offer;
import com.projects.radomonov.homeless.utilities.Utilities;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Tom on 21.10.2017.
 */

public class CreateOfferFragment extends Fragment {
    public static final int ADD_PHOTO_CROP = 5;
    public static final int GALLERY_REQUEST = 1;
    public static final int CHOOSE_IMAGE = 2;
    public static final int ADD_PHOTO = 3;
    private String title;
    private int price;
    private String description;
    private Offer.Currency currency;
    private int rooms;
    ProgressDialog progressDialog;

    private Uri thumbnail;

    private FirebaseAuth mAuth;
    private EditText etTitle, etPrice, etRooms,etDescription;
    private Button btnSave, btnDelete,btnCancel;
    private ImageButton imgbtnChoose, imgbtnAdd;
    private DatabaseReference offers;
    private DatabaseReference offer;
    private DatabaseReference currentUser;
    private StorageReference mStorage;
    private Uri imageUri;
    private String phoneNum;
    private RadioButton rdbtnEU;
    private RadioButton rddbtnBGN;
    private Spinner spinnerNeigh;
    private Utilities.Neighbourhood neighborhood;
    private boolean isNewOffer = true;
    private Offer editOffer;
    private boolean changedImage = false;

    private List<Uri> offerImages;
    private List<Uri> offerImagesUrlsOrig;
    private List<Uri> offerImagesUrls;
    private String neighbourhood;
    private HashMap<String,Uri> originalPics;
    private HashMap<String,Uri> toDeletePics;
    private OfferPhotosAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_offer, container, false);
        offerImagesUrlsOrig = new ArrayList<>();
        offerImages = new ArrayList<>();
        offerImagesUrls = new ArrayList<>();
        originalPics = new HashMap<>();
        toDeletePics = new HashMap<>();
        setUpRecyclerForPhotos(view);
        offers = FirebaseDatabase.getInstance().getReference().child("Offers");

        mAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mStorage = FirebaseStorage.getInstance().getReference();

        rdbtnEU = view.findViewById(R.id.rbtn_eu);
        rddbtnBGN = view.findViewById(R.id.rbtn_bgn);
        etTitle = (EditText) view.findViewById(R.id.et_title_create);
        etPrice = (EditText) view.findViewById(R.id.et_price_create);
        etRooms = (EditText) view.findViewById(R.id.et_rooms_create);
        etDescription = view.findViewById(R.id.et_description_create);

        btnDelete = (Button) view.findViewById(R.id.btn_delete_create);
        btnDelete.setVisibility(View.GONE);
        spinnerNeigh = view.findViewById(R.id.spinnner_neigh_create);
        spinnerNeigh.setAdapter(new ArrayAdapter<Utilities.Neighbourhood>(getContext(), android.R.layout.simple_spinner_item, Utilities.Neighbourhood.values()));
        spinnerNeigh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                neighborhood = (Utilities.Neighbourhood) spinnerNeigh.getSelectedItem();
                Log.i("spinner","selected " + spinnerNeigh.getSelectedItem());
                Log.i("spinner","selected " + neighborhood);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        imgbtnAdd = view.findViewById(R.id.imgbtn_add_photo);
        imgbtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                        ADD_PHOTO);
            }
        });

        imgbtnChoose = (ImageButton) view.findViewById(R.id.img_select_create);
        imgbtnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                        CHOOSE_IMAGE);

            }
        });
        btnSave = (Button) view.findViewById(R.id.btn_save_create);
        btnCancel = view.findViewById(R.id.btn_cancel_create);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchFragment searchFrag = new SearchFragment();
                getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_container_main, searchFrag, "searchFrag").commit();
            }
        });
        Bundle bundle = getArguments();
        if (bundle != null) {
            editOffer = (Offer) getArguments().getSerializable("offer");
        }
        if (editOffer != null) {
            isNewOffer = false;
            fillFields(editOffer);

            btnDelete.setVisibility(View.VISIBLE);
            setDeleteListener();
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getContext());
                progressDialog.show();

                description = etDescription.getText().toString();
                title = etTitle.getText().toString().trim();
                price = Integer.parseInt(etPrice.getText().toString());

                if (rdbtnEU.isChecked()) {
                    currency = Offer.Currency.EU;
                } else {
                    currency = Offer.Currency.BGN;
                }
                rooms = Integer.parseInt(etRooms.getText().toString());
                // if(nqkvi validacii)

                if (isNewOffer) {
                    offer = offers.push();
                } else {
                    offer = offers.child(editOffer.getId());
                }

                if (isNewOffer) {
                    if (thumbnail != null) {
                        updateThumbnail();
                        writeToDB(offer);
                    } else {
                        Toast.makeText(getContext(), "Choose Image", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (changedImage) {
                        updateThumbnail();
                    }
                    deletePics(toDeletePics);
                    writeToDB(offer);
                }
            }
        });
        return view;
    }

    private void setDeleteListener() {
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Offer> offersList;
                String currentOfferID = editOffer.getId();
                offersList = DatabaseInfo.getOffersList();
//                for (Offer of : offersList) {
//                    if (of.getId().equals(currentOfferID)) {
//                        offersList.remove(of);
//                    }
//                }
                final Query offerQuery = offers.child(currentOfferID);

                offerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeValue();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                deletePics(originalPics);
                deleteThumbnail();
                SearchFragment searchFrag = new SearchFragment();
                getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_container_main, searchFrag, "searchFrag").commit();

            }
        });
    }

    private void deleteThumbnail(){
        FirebaseStorage mFireBaseStorage = FirebaseStorage.getInstance();
        StorageReference pic = mFireBaseStorage.getReferenceFromUrl(String.valueOf(editOffer.getImageThumbnail()));
        pic.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("delete","thumbnail successful deletion");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("delete","thumbnail UNsuccessful deletion");
            }
        });
    }
    private void deletePics(final HashMap<String,Uri> pics){
        for(Map.Entry<String,Uri> entry : pics.entrySet()){
            Uri url = entry.getValue();
            final String key = entry.getKey();
            FirebaseStorage mFireBaseStorage = FirebaseStorage.getInstance();
            StorageReference pic = mFireBaseStorage.getReferenceFromUrl(String.valueOf(url));
            pic.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i("delete","successful deletion");
                    if(pics == toDeletePics){
                        FirebaseDatabase.getInstance().getReference().child("Offers").child(offer.getKey())
                                .child("imageUrls").child(key).removeValue();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("delete"," UNsuccessful deletion");
                }
            });
        }
    }


    private void updateThumbnail() {
        StorageReference filepath2 = mStorage.child("OfferImages").child("Offers").child(offer.getKey()).child("thumbnail");
        filepath2.putFile(thumbnail).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i("thumb", " =====onSuccess=====");
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                offer.child("imageThumbnail").setValue(downloadUrl.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("thumb", "=========onFail========");
            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    private void writeToDB(final DatabaseReference offer) {
        offer.child("id").setValue(offer.getKey());
        offer.child("title").setValue(title);
        offer.child("price").setValue(price);
        offer.child("currency").setValue(currency);
        offer.child("rooms").setValue(rooms);
        offer.child("neighbourhood").setValue(neighborhood.toString());
        offer.child("description").setValue(description);
        DatabaseReference phoneRef = currentUser.child("phoneNumber");
        phoneRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                phoneNum = (String) dataSnapshot.getValue();
                offer.child("phoneNumber").setValue(phoneNum);
                // Toast.makeText(getActivity(), phoneNum, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //  Toast.makeText(getActivity(), "Failed phone", Toast.LENGTH_SHORT).show();
            }
        });
        storeNewPics();
        offer.child("owner").setValue(currentUser.getKey().toString());
        progressDialog.dismiss();
        SearchFragment searchFrag = new SearchFragment();
        getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_container_main, searchFrag, "searchFrag").commit();
        //getActivity().getFragmentManager().beginTransaction().remove(CreateOfferFragment.this).commit();
    }

    private void storeNewPics() {
        Log.i("proba","offerImagesUrls size v UPDATE - > " + offerImagesUrls.size());
        for (Uri photo : offerImagesUrls) {
            if(!isValidURL(String.valueOf(photo))){
                final String randomString = getRandomString();
                StorageReference pictureRef = mStorage.child("OfferImages")
                        .child("Offers").child(offer.getKey()).child("Photos").child(randomString);
                pictureRef.putFile(photo).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.i("kachena","pic uploaded");
                        Uri link = taskSnapshot.getDownloadUrl();
                        // offerImagesUrls.add(link);
                        //StorageReference storageRefDelete = FirebaseStorage.getInstance().getReferenceFromUrl(String.valueOf(link));
                        DatabaseReference pictureDBRef = offer.child("imageUrls").child(randomString);
                        pictureDBRef.setValue(link.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("kachena","pic upload failed");
                    }
                });
            }
        }
        offer.child("owner").setValue(currentUser.getKey().toString());
    }

    public void fillFields(Offer offer) {
        Glide.with(getContext()).load(offer.getImageThumbnail()).into(imgbtnChoose);
        etTitle.setText(offer.getTitle());
        etRooms.setText("" + offer.getRooms());
        etPrice.setText("" + offer.getPrice());
        Offer.Currency currency = offer.getCurrency();
        if (currency == Offer.Currency.BGN) {
            rddbtnBGN.setChecked(true);
        }
        if (currency == Offer.Currency.EU) {
            rdbtnEU.setChecked(true);
        }
        Utilities.Neighbourhood neighbourhood = offer.getNeighbourhood();
        spinnerNeigh.setSelection(((ArrayAdapter)spinnerNeigh.getAdapter()).getPosition(neighbourhood));
        etDescription.setText(offer.getDescription());
        //get pics links
        if(offer.getImageUrls()!= null){

            Log.i("proba","url-ite NE sa null");
            for(Map.Entry<String,String> entry : offer.getImageUrls().entrySet()){
                originalPics.put(entry.getKey(), Uri.parse(entry.getValue()));
                offerImagesUrls.add(Uri.parse(entry.getValue()));
                adapter.notifyDataSetChanged();
            }

            Log.i("proba","originalPics size -> " + originalPics.size());
            Log.i("proba","offerImagesUlrs size -> " + offerImagesUrls.size());
        } else {
            Log.i("proba","url-ite sa null");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK) {
            changedImage = true;
            //imageUri = data.getData();
            //imgbtnChoose.setImageURI(imageUri);
            Intent intent = CropImage.activity(data.getData()).setMaxCropResultSize(1100, 800)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setAspectRatio(5, 3)
                    .setFixAspectRatio(true).getIntent(getActivity());
            startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            try {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Bitmap bitmap = MediaStore.Images.Media
                        .getBitmap(getActivity().getContentResolver(), result.getUri());
                /*Picasso.with(getActivity()).load(result.getUri())
                        .transform(new CropSquareTransformation())
                        .into(imgbtnChoose);*/
                thumbnail = getImageUri(getContext(), bitmap);
                imgbtnChoose.setImageURI(thumbnail);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        if (requestCode == ADD_PHOTO && resultCode == RESULT_OK) {
            Uri uncroppedPhoto = data.getData();
            // ne trqa da e taka
            offerImagesUrls.add(uncroppedPhoto);
            adapter.notifyDataSetChanged();

            /*Intent intent = CropImage.activity(data.getData()).setMaxCropResultSize(1100, 800)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setAspectRatio(5, 3)
                    .setFixAspectRatio(true).getIntent(getActivity());
            startActivityForResult(intent, ADD_PHOTO_CROP);*/
        }
       /* if (requestCode == ADD_PHOTO_CROP) {
            try {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Bitmap bitmap = MediaStore.Images.Media
                        .getBitmap(getActivity().getContentResolver(), result.getUri());
                Uri uri = getImageUri(getContext(), bitmap);
                offerImagesThumbnails.add(uri);
                adapter.notifyDataSetChanged();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }*/
    }

    private void setUpRecyclerForPhotos(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_photos);
        adapter = new OfferPhotosAdapter(getContext(), offerImagesUrls, new OfferPhotosAdapter.deleteClickListener() {
            @Override
            public void onDeleteClick(Uri uri) {
                offerImagesUrls.remove(uri);
                adapter.notifyDataSetChanged();
                if(isValidURL(String.valueOf(uri))) {
                    for(Map.Entry<String,Uri> entry : originalPics.entrySet()){
                        if(entry.getValue().equals(uri)){
                            toDeletePics.put(entry.getKey(),entry.getValue());
                        }
                    }
                }
            }
        });
        recyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
    }

    private boolean isValidURL(String urlStr) {
        try {
            URI uri = new URI(urlStr);
            return uri.getScheme().equals("http") || uri.getScheme().equals("https");
        }
        catch (Exception e) {
            return false;
        }
    }
    private String getRandomString(){
        String uuid = UUID.randomUUID().toString();
        return  uuid;
    }
    public void btnGone() {
        btnDelete.setVisibility(View.GONE);
    }

}
