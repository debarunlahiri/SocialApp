package com.lahiriproductions.socialapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lahiriproductions.socialapp.R;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    private static final String TAG = OtpActivity.class.getSimpleName();

    private Toolbar tbEnterOtp;

    private EditText etEnterOtp;
    private TextInputLayout tilEnterOtp;
    private Button bOtpSubmit;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String phone_number, verification_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        tbEnterOtp = findViewById(R.id.tbEnterOtp);
        tbEnterOtp.setTitle("Enter OTP");
        tbEnterOtp.setTitleTextColor(Color.WHITE);
        setSupportActionBar(tbEnterOtp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tbEnterOtp.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_white_arrow_back_24));
        tbEnterOtp.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        phone_number = getIntent().getExtras().getString("phone_number");
        verification_id = getIntent().getExtras().getString("verification_id");

        etEnterOtp = findViewById(R.id.etEnterOtp);
        tilEnterOtp = findViewById(R.id.tilEnterOtp);
        bOtpSubmit = findViewById(R.id.bOtpSubmit);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl(getString(R.string.storage_reference_url));

        bOtpSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = etEnterOtp.getText().toString();

                if (otp.isEmpty()) {
                    tilEnterOtp.setError("Otp cannot be empty");
                } else {
                    verifyOtp(otp);
                }
            }
        });

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+91" + phone_number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        // Save the verification id somewhere
                        // ...

                        // The corresponding whitelisted code above should be used to complete sign-in.
//                        enableUserManuallyInputCode();
                        Log.d(TAG, "onCodeSent:" + verificationId);
                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        // Sign in with the credential
                        // ...
                        Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Log.w(TAG + "onVerificationFailed", e);

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                        }
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyOtp(String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification_id, otp);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                            sendToProfile(user);
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void sendToProfile(FirebaseUser user) {
        Intent profileIntent = new Intent(OtpActivity.this, ProfileActivity.class);
        profileIntent.putExtra("user", user);
        startActivity(profileIntent);
    }
}