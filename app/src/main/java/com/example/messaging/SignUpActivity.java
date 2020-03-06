package com.example.messaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    private String TAG = "APPING", UID;
    private TextInputEditText firstName, lastName, tag, email, pass, mobile, confpass;
    private TextInputLayout emailLayout, passLayout, mobileLayout, confpassLayout;
    private static final int SELECT_PICTURE = 100;
    private Uri imageUri;
    private Button signup;
    private View scrollView;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private CircleImageView profilePic;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("Users");
    private StorageReference storageReference;
    private String sname, semail, stag, spass, smobile, sconfpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Sign Up");
        }

        //usersRef.document("abc").set(tp);
        scrollView = findViewById(R.id.layout_sign_up);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        tag = findViewById(R.id.tag);
        mobile = findViewById(R.id.mobile);
        pass = findViewById(R.id.pass);
        confpass = findViewById(R.id.confpass);
        signup = findViewById(R.id.sign_up);
        emailLayout = findViewById(R.id.email_layout);
        passLayout = findViewById(R.id.pass_layout);
        confpassLayout = findViewById(R.id.confpass_layout);
        mobileLayout = findViewById(R.id.mobile_layout);
        profilePic = findViewById(R.id.profile_pic);
        progressBar = new ProgressBar(this);

        storageReference = FirebaseStorage.getInstance().getReference("Profiles");


        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String regex = "^(.+)@(.+)$";

                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(s.toString());
                if (!matcher.matches())
                    emailLayout.setError("Enter valid Email ID");
                else
                    emailLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 10)
                    mobileLayout.setError("Enter valid mobile no");
                else
                    mobileLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 6)
                    passLayout.setError("Password should be more than 5 characters");
                else
                    passLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confpass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!(s.toString().equals(pass.getText().toString())))
                    confpassLayout.setError("Passwords do not match");
                else
                    confpassLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mAuth = FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {

                    //upload to database
                    sname = firstName.getText().toString().trim() + " " + lastName.getText().toString().trim();
                    stag = tag.getText().toString().trim();
                    semail = email.getText().toString().trim();
                    spass = pass.getText().toString().trim();
                    smobile = mobile.getText().toString().trim();
                    sconfpass = confpass.getText().toString().trim();


                    mAuth.createUserWithEmailAndPassword(semail, spass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                UID = mAuth.getUid();
                                sendEmailVerification();
                                uploadFile();
                            } else {
                                Log.w(TAG, "Registration failed", task.getException());
                                Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(profilePic);
        }
    }

    private void sendEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        sendUserData();
                        Snackbar.make(scrollView, "Registration Successful", Snackbar.LENGTH_SHORT).show();
                        Snackbar.make(scrollView, "Verification mail sent", Snackbar.LENGTH_SHORT).show();
                        mAuth.signOut();
                        finish();
                    } else {
                        Snackbar.make(scrollView, "Error in sending verification mail", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserData() {
        /*UserProfile user = new UserProfile(sname, semail, stag, smobile, spass, mAuth.getUid());
        usersRef.add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("User", "onSuccess: Added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("User", "onFailure: "+e.getLocalizedMessage());
                    }
                });
        Log.d("User",user.toString());*/
    }

    private boolean validateFields() {
        boolean result = false;

        sname = firstName.getText().toString().trim() + " " + lastName.getText().toString().trim();
        semail = email.getText().toString().trim();
        stag = tag.getText().toString().trim();
        spass = pass.getText().toString().trim();
        smobile = mobile.getText().toString().trim();
        sconfpass = confpass.getText().toString().trim();

        if (sname.isEmpty() || spass.isEmpty() || stag.isEmpty() || sconfpass.isEmpty() || semail.isEmpty() || smobile.isEmpty()) {
            Snackbar.make(scrollView, "Fill all necessary details", Snackbar.LENGTH_SHORT).show();
        } else if (!(spass.equals(sconfpass))) {
            Snackbar.make(scrollView, "Passwords do not match", Snackbar.LENGTH_SHORT).show();
        } else {
            result = true;
        }
        return result;
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile() {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
            + "."+getFileExtension(imageUri));
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String path =taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                            if (taskSnapshot.getMetadata() != null) {
                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageURL = uri.toString();
                                        UserProfile user = new UserProfile(sname, semail, stag, smobile, spass, UID, imageURL);
                                        //usersRef.add(user);
                                        usersRef.document(UID).set(user);
                                    }
                                });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpActivity.this, "Error uploading profile pic", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            UserProfile user = new UserProfile(sname, semail, stag, smobile, spass, UID, "default");
            //usersRef.add(user);
            usersRef.document(UID).set(user);
        }
    }

}
