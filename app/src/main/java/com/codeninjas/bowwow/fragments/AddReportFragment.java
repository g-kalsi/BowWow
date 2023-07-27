package com.codeninjas.bowwow.fragments;

import static android.app.Activity.RESULT_OK;

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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.codeninjas.bowwow.R;
import com.codeninjas.bowwow.activities.MainActivity;
import com.codeninjas.bowwow.base.BaseFragment;
import com.codeninjas.bowwow.models.FeedsModel;
import com.codeninjas.bowwow.models.ReportsModel;
import com.codeninjas.bowwow.utils.Config;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddReportFragment extends BaseFragment implements View.OnClickListener {
// show success message & loader
    ImageView imageIV;
    RadioGroup radioGroup;
    RadioButton radioButton;
    TextView editImageTV;
    TextInputEditText reportET;
    ImageView infoIV;
    Uri fileUri = null;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity) baseActivity).showToolbarBottomNavigation("Add Report", true, false, true);
        return inflater.inflate(R.layout.fragment_add_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        radioGroup = view.findViewById(R.id.radioGroup);
        reportET = view.findViewById(R.id.reportET);
        infoIV = view.findViewById(R.id.infoIV);
        imageIV = view.findViewById(R.id.imageIV);
        editImageTV = view.findViewById(R.id.editImageTV);
        editImageTV.setOnClickListener(this);
        infoIV.setOnClickListener(this);
        view.findViewById(R.id.uploadBT).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        if (v.getId() == R.id.uploadBT) {
            if (isValid()) {
              uploadImage();
            }
        } else if (v.getId() == R.id.infoIV){
            baseActivity.showInfoDialog("Write detailed characterstics of the dog whatever you know at your best knowledge: color, breed, any name tag, any injuries etc. Also, write down your contact info (most possibly email)");
        } else if (v.getId() == R.id.editImageTV) {
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
                imageIV.setImageBitmap(bitmap);
            } catch (Exception e) {
                // handling exception on below line
                e.printStackTrace();
            }
        }

    }
    private boolean isValid() {

        if (reportET.getText().toString().trim().isEmpty()) {
            showSnackBar("Please write down your report!");
        } else {
            return true;
        }

        return false;

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

        String uid = firebaseUser.getUid();

        String userName = baseActivity.store.getString(Config.dogName);
        String profilePicUrl = baseActivity.store.getString(Config.profilePic);
        String report = reportET.getText().toString().trim();
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) radioGroup.findViewById(selectedId);
        String type = radioButton.getText().toString().trim(); //types will be lost or found
        String key = databaseReference.child(Config.userReports).child(uid).push().getKey();
        long created_at = System.currentTimeMillis();
        ReportsModel reportsModel = new ReportsModel(uid, userName, profilePicUrl, report, type, fileUri.toString(), created_at, key);

        databaseReference.child(Config.userReports).child(uid).child(key).setValue(reportsModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(e -> baseActivity.showSnackBar("Something went wrong! Please try again later."));

        databaseReference.child(Config.publicReports).child(key).setValue(reportsModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(e -> baseActivity.showSnackBar("Something went wrong! Please try again later."));

        baseActivity.showSnackBar("Your " + type.toLowerCase() + " report added successfully.");
        baseActivity.onBackPressed();

    }

}