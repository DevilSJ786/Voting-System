package com.devil.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.devil.test.Util.PollApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private EditText phone_number, otp;
    private Button loginButton, verifyButton, resendButton;
    private String code = "+91", id;
    public FirebaseUser onlineUser;
    public String userId;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference LoginRef = db.collection("Login").document("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = findViewById(R.id.login_progress);
        phone_number = findViewById(R.id.login_number);
        otp = findViewById(R.id.otp_number);
        verifyButton = findViewById(R.id.verify_button);
        resendButton = findViewById(R.id.resend_button);
        loginButton = findViewById(R.id.login_button);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        FirebaseApp.initializeApp(this);

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = firebaseAuth -> {
            onlineUser = firebaseAuth.getCurrentUser();
            if (onlineUser != null) {
                //user log in
                newActivity();
            }
        };


        loginButton.setOnClickListener(v -> {
            if (!phone_number.getText().toString().trim().isEmpty()) {
                if (phone_number.getText().toString().trim().length() == 10) {
                    progressBar.setVisibility(View.VISIBLE);
                    loginButton.setVisibility(View.GONE);
                    phone_number.setVisibility(View.GONE);
                    otp.setVisibility(View.VISIBLE);
                    verifyButton.setVisibility(View.VISIBLE);
                    resendButton.setVisibility(View.VISIBLE);

                    phoneAuth();
                }
            }
        });

        resendButton.setOnClickListener(v -> {
            resendButton.startAnimation(animation);
            progressBar.setVisibility(View.VISIBLE);
            phoneAuth();
        });

        verifyButton.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(otp.getText().toString())) {
                progressBar.setVisibility(View.VISIBLE);
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, otp.getText().toString());

                firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        onlineUser = task.getResult().getUser();
                        newActivity();
                    } else {
                        Toast.makeText(LoginActivity.this, "wrong otp", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "wrong otp" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void phoneAuth() {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(code + phone_number.getText().toString())
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(LoginActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                        Toast.makeText(LoginActivity.this, "OTP sent", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        id = s;
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void newActivity() {
        progressBar.setVisibility(View.INVISIBLE);
        userId = onlineUser.getUid();
        PollApi pollApi = PollApi.getInstance();
        pollApi.setUserId(userId);

        LoginRef.get().addOnCompleteListener(task -> {
            Boolean userBoolean = task.getResult().getBoolean(onlineUser.getUid());
            if (userBoolean == null || userBoolean) {
                LoginRef.update(onlineUser.getUid(), false).addOnSuccessListener(unused -> {
                    Toast.makeText(LoginActivity.this, "User save", Toast.LENGTH_SHORT).show();
                    DocumentReference voterRef = db.collection("Users").document(onlineUser.getUid());
                    Map<String, Boolean> map = new HashMap<>();
                    map.put("devil", false);
                    voterRef.set(map).addOnSuccessListener(unuse -> {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    });
                }).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        onlineUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}