package com.lahiriproductions.socialapp.activities;

import static com.lahiriproductions.socialapp.utils.Controller.sendNotification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.lahiriproductions.socialapp.R;
import com.lahiriproductions.socialapp.utils.Controller;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();
    private Context mContext;

    private Toolbar tbProfile;

    private EditText etProfileFullName, etProfileEmail, etProfileAge;
    private TextInputLayout tilProfileName, tilProfileEmail, tilProfileAge;
    private Button bProfileSubmit;
    private CircleImageView civProfileImage;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private Uri profilePictureURI = null;
    private Bitmap mCompressedProfileImage;
    private ProgressDialog progressDialog;
    private boolean isEdit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mContext = ProfileActivity.this;

        if (getIntent() != null) {
            isEdit = getIntent().getBooleanExtra("isEdit", false);
        }

        tbProfile = findViewById(R.id.tbProfile);
        tbProfile.setTitle("Profile");
        tbProfile.setTitleTextColor(Color.WHITE);
        setSupportActionBar(tbProfile);

        etProfileFullName = findViewById(R.id.etProfileFullName);
        etProfileEmail = findViewById(R.id.etProfileEmail);
        etProfileAge = findViewById(R.id.etProfileAge);
        bProfileSubmit = findViewById(R.id.bProfileSubmit);
        civProfileImage = findViewById(R.id.civProfileImage);
        tilProfileName = findViewById(R.id.tilProfileName);
        tilProfileEmail = findViewById(R.id.tilProfileEmail);
        tilProfileAge = findViewById(R.id.tilProfileAge);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl(getString(R.string.storage_reference_url));

        if (currentUser == null) {
            Controller.sendToLogin(ProfileActivity.this);
        } else {
            mDatabase.child("users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue().toString();
                        String email = snapshot.child("email").getValue().toString();
                        String age = snapshot.child("age").getValue().toString();
                        if (!snapshot.child("profile_image").exists()) {
                            Glide.with(ProfileActivity.this).load(R.drawable.default_profile).into(civProfileImage);
                        } else {
                            Glide.with(ProfileActivity.this).load(snapshot.child("profile_image").getValue().toString()).into(civProfileImage);
                        }
                        etProfileAge.setText(age);
                        etProfileFullName.setText(name);
                        etProfileEmail.setText(email);

                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        getSupportActionBar().setDisplayShowHomeEnabled(true);
                        tbProfile.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_white_arrow_back_24));
                        tbProfile.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onBackPressed();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        civProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(ProfileActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setMinCropResultSize(512, 512)
                                        .setAspectRatio(1, 1)
                                        .start(ProfileActivity.this);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        })
                        .check();
            }
        });

        bProfileSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etProfileFullName.getText().toString();
                String email = etProfileEmail.getText().toString();
                String age = etProfileAge.getText().toString();

                if (name.isEmpty()) {
                    tilProfileName.setError("Please enter your name");
                } else if (email.isEmpty()) {
                    tilProfileEmail.setError("Please enter your email");
                } else if (age.isEmpty()) {
                    tilProfileEmail.setError("Please enter your age");
                } else if (!Controller.isValidEmail(email)) {
                    tilProfileEmail.setError("Please enter valid email");
                } else {
                    updateProfile(name, email, age);
                }
            }
        });

    }



    private void updateProfile(String name, String email, String age) {
        if (profilePictureURI == null) {
            saveProfile(name, email, age, null);
        } else {
            uploadProfilePic(name, email, age);

        }


    }

    private void saveProfile(String name, String email, String age, Uri downloadUri) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        HashMap<String, Object> mDataMap = new HashMap<>();
        mDataMap.put("user_id", currentUser.getUid());
        mDataMap.put("name", name);
        mDataMap.put("email", email);
        mDataMap.put("age", age);
        if (downloadUri != null) {
            mDataMap.put("profile_image", downloadUri.toString());
        }
        mDataMap.put("created_on", System.currentTimeMillis());
        mDatabase.child("users").child(currentUser.getUid()).updateChildren(mDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (isEdit) {
                        sendNotification(mDatabase, currentUser.getUid(), "profile");
                    }
                    progressDialog.dismiss();
                    Toast.makeText(mContext, "Profile saved successfully", Toast.LENGTH_LONG).show();
                    sendToMain();
                } else {
                    progressDialog.dismiss();
                    String errMsg = task.getException().getMessage();
                    Toast.makeText(getApplicationContext(), "Error: " + errMsg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            //filePath = data.getData();

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                profilePictureURI = result.getUri();

                Glide.with(getApplicationContext()).load(profilePictureURI).into(civProfileImage);


                //Toast.makeText(getApplicationContext(), (CharSequence) postImageUri, Toast.LENGTH_LONG).show();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Toast.makeText(getApplicationContext(), (CharSequence) error, Toast.LENGTH_LONG).show();
            }


            //Setting image to ImageView


        }
    }

    private void uploadProfilePic(String name, String email, String age) {

        final Long ts_long = System.currentTimeMillis() / 1000;
        final String ts = ts_long.toString();
        File mFileProfileImage = new File(profilePictureURI.getPath());
        //final StorageReference childRef = storageReference.child("users/profiles/profile_images/" + currentUser.getUid() + ".jpg");
        //final StorageReference thumb_childRef = storageReference.child("users/profile_images/profile_images/" + currentUser.getUid() + ".jpg");

        try {
            mCompressedProfileImage = new Compressor(ProfileActivity.this).setQuality(50).compressToBitmap(mFileProfileImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream mProfileBAOS = new ByteArrayOutputStream();
        mCompressedProfileImage.compress(Bitmap.CompressFormat.JPEG, 15, mProfileBAOS);
        byte[] mProfileData = mProfileBAOS.toByteArray();

        final StorageReference mChildRefProfile = storageReference.child("users/profiles/profile_images/" + currentUser.getUid() + ".jpg");
        //final StorageReference mThumbChildRefProfile = storageReference.child("users/profiles/profile_images/thumbs/" + currentUser.getUid() + ".jpg");

        final UploadTask profile_uploadTask = mChildRefProfile.putBytes(mProfileData);

        profile_uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Task<Uri> uriTask = profile_uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return mChildRefProfile.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                //final String product_key = mDatabase.child("products").child(item).push().getKey();
                                saveProfile(name, email, age, downloadUri);
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Image Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finishAffinity();

    }

}