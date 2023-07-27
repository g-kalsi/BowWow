package com.codeninjas.bowwow.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codeninjas.bowwow.R;
import com.codeninjas.bowwow.activities.MainActivity;
import com.codeninjas.bowwow.base.BaseFragment;
import com.codeninjas.bowwow.models.DogModel;
import com.codeninjas.bowwow.utils.Config;
import com.codeninjas.bowwow.utils.CustomEditText;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFrament extends BaseFragment implements View.OnClickListener{

    EditText dogNameET, ageET, genderET, breedET, breederET, colorET, notesET;
    TextView titleTV, subTitleTV, editImageTV;
    CircleImageView profileCIV;
    Button registerBT;
    String profilePicFromDB = "";
    Uri fileUri = null;
    private DatabaseReference databaseReference, userProfileDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (!baseActivity.store.getBoolean(Config.profileUpdated)) {
            ((MainActivity) baseActivity).showToolbarBottomNavigation("", false, false, false);
        } else {
            ((MainActivity) baseActivity).showToolbarBottomNavigation("Profile", true, false, true);
        }
        return inflater.inflate(R.layout.fragment_profile_frament, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editImageTV = view.findViewById(R.id.editImageTV);
        editImageTV.setOnClickListener(this);
        profileCIV = view.findViewById(R.id.profileCIV);
        titleTV = view.findViewById(R.id.titleTV);
        subTitleTV = view.findViewById(R.id.subTitleTV);
        dogNameET = view.findViewById(R.id.dogNameET);
        ageET = view.findViewById(R.id.ageET);
        genderET = view.findViewById(R.id.genderET);
        breedET = view.findViewById(R.id.breedET);
        breederET = view.findViewById(R.id.breederET);
        colorET = view.findViewById(R.id.colorET);
        notesET = view.findViewById(R.id.notesET);
        registerBT = view.findViewById(R.id.registerBT);
        view.findViewById(R.id.registerBT).setOnClickListener(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        getUserInfo();
        if (!baseActivity.store.getBoolean(Config.profileUpdated)) {
            registerBT.setText("Register");
            titleTV.setVisibility(View.VISIBLE);
            subTitleTV.setVisibility(View.VISIBLE);
        } else {
            registerBT.setText("Update");
            titleTV.setVisibility(View.GONE);
            subTitleTV.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.registerBT) {
            uploadImage();
        } else {
            Intent intent = new Intent();
            // on below line setting type of files which we want to pick in our case we are picking images.
            intent.setType("image/*");
            // on below line we are setting action to get content
            intent.setAction(Intent.ACTION_GET_CONTENT);
            // on below line calling start activity for result to choose image.
            startActivityForResult(Intent.createChooser(intent, "Pick your image to upload!"), 22);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 22 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // on below line initializing file uri with the data which we get from intent
            fileUri = data.getData();
            try {
                // on below line getting bitmap for image from file uri.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(baseActivity.getContentResolver(), fileUri);
                // on below line setting bitmap for our image view.
                profileCIV.setImageBitmap(bitmap);
            } catch (Exception e) {
                // handling exception on below line
                e.printStackTrace();
            }
        }

    }

    private boolean isValid() {

        if (dogNameET.getText().toString().trim().isEmpty()) {
            showSnackBar("Please enter your dog's name!");
        } else if (ageET.getText().toString().trim().isEmpty()) {
            showSnackBar("Please enter age of your dog!");
        } else if (genderET.getText().toString().trim().isEmpty()) {
            showSnackBar("Please enter your dog's gender!");
        }else if (breedET.getText().toString().trim().isEmpty()) {
            showSnackBar("Please enter your dog's breed!");
        }else if (breederET.getText().toString().trim().isEmpty()) {
            showSnackBar("Please enter your dog's breeder name!");
        }else if (colorET.getText().toString().trim().isEmpty()) {
            showSnackBar("Please enter your dog's color!");
        } else {
            return true;
        }

        return false;
    }

    private void getUserInfo() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();
        userProfileDatabase = FirebaseDatabase.getInstance().getReference(Config.dogTable).child(uid);
        userProfileDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DogModel usersModel = dataSnapshot.getValue(DogModel.class);
                if (usersModel!=null){
                    dogNameET.setText(usersModel.getName());
                    ageET.setText(usersModel.getAge());
                    genderET.setText(usersModel.getGender());
                    breedET.setText(usersModel.getBreed());
                    breederET.setText(usersModel.getBreeder());
                    colorET.setText(usersModel.getColor());
                    notesET.setText(usersModel.getNotes());
                    profilePicFromDB = usersModel.getProfilePic();
                    Picasso.get().load(profilePicFromDB).placeholder(R.drawable.dog).into(profileCIV);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void uploadImage() {
        if (fileUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference ref = storageReference.child(UUID.randomUUID().toString());
            UploadTask uploadTask = ref.putFile(fileUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        fileUri = task.getResult();
                        uploadData();
                    } else {
                        showSnackBar("Image upload failed!");
                        uploadData();
                    }
                }
            });

        } else {
            uploadData();
        }
    }

    private void uploadData() {
        if (isValid()) {
            String uid = FirebaseAuth.getInstance().getUid();
            String dogName = dogNameET.getText().toString().trim();
            String age = ageET.getText().toString().trim();
            String gender = genderET.getText().toString().trim();
            String breed = breedET.getText().toString().trim();
            String breeder = breederET.getText().toString().trim();
            String color = colorET.getText().toString().trim();
            String notes = notesET.getText().toString().trim();
            DogModel usersModel = new DogModel(uid, dogName, age, gender, color, breed, breeder, notes, profilePicFromDB);
            if (fileUri != null) {
                usersModel = new DogModel(uid, dogName, age, gender, color, breed, breeder, notes, fileUri.toString());
            }

            databaseReference.child(Config.dogTable).child(uid).setValue(usersModel).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (!baseActivity.store.getBoolean(Config.profileUpdated)) {
                        gotofragmenttransnostack(new FeedsFragment());
                    }
                    baseActivity.store.setBoolean(Config.profileUpdated, true);
                    showSnackBar("Profile updated successfully!");

                }
            });

        }
    }

}