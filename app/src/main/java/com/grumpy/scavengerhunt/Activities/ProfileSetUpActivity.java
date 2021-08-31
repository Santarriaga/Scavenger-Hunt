package com.grumpy.scavengerhunt.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grumpy.scavengerhunt.Fragments.ProfileEditFragment;
import com.grumpy.scavengerhunt.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSetUpActivity extends AppCompatActivity {

    private CircleImageView circleImageView;
    private EditText mName;
    private Button saveBtn;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private StorageReference storage;
    private String Uid;
    private Uri mImageUri;
    private boolean isChanged = false;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_set_up);

        circleImageView = findViewById(R.id.circleImageView);
        mName = findViewById(R.id.name);
        saveBtn = findViewById(R.id.save_profile);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
        Uid = auth.getCurrentUser().getUid();


        //retrieve user information if found
        firestore.collection("Users").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String name = task.getResult().getString("name");
                        String profile = task.getResult().getString("image");

                        mImageUri = Uri.parse(profile);
                        mName.setText(name);
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(R.drawable.ic_profile);

                        Glide.with(ProfileSetUpActivity.this).load(profile).into(circleImageView);
                    }
                }
            }
        });


        // lets user get image from camera roll
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Ask permission to access photos

                if (ContextCompat.checkSelfPermission(ProfileSetUpActivity.this , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    //permission for fragment
                    requestPermissions( new String[] {Manifest.permission.READ_EXTERNAL_STORAGE} , 1);
                }
                else{
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1,1)
                            .start(ProfileSetUpActivity.this);
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String name = mName.getText().toString();

                if (!name.isEmpty() && mImageUri != null){
                    //create child inside storage
                    StorageReference imageRef = storage.child("Profile_pics").child(Uid + ".jpg");

                    if (isChanged){

                        imageRef.putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()){
                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri){
                                            saveToFireStore(task , name ,uri);
                                        }
                                    });

                                }else{
                                    Toast.makeText(ProfileSetUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        saveToFireStore(null , name , mImageUri);
                    }
                }else{
                    Toast.makeText(ProfileSetUpActivity.this, "Please Select Image and write your name", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void saveToFireStore(Task<UploadTask.TaskSnapshot> task, String name, Uri downloadUri) {

        HashMap<String , Object> map = new HashMap<>();
        map.put("name" , name);
        map.put("image" , downloadUri.toString());
        firestore.collection("Users").document(Uid).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.VISIBLE);
                    Toast.makeText(ProfileSetUpActivity.this, "profile Settings Saved", Toast.LENGTH_SHORT).show();
                    //navigate to home activity
                    startActivity(new Intent(ProfileSetUpActivity.this, MainActivity.class));
                    finish();

                }else{
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(ProfileSetUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // results of the cropped image
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                circleImageView.setImageURI(mImageUri);
                isChanged = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(ProfileSetUpActivity.this, error.getMessage() , Toast.LENGTH_SHORT).show();
            }
        }
    }



}