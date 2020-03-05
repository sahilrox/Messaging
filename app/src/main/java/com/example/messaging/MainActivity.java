package com.example.messaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private Button signUpBtn, login;
    private View mainLayout;
    private EditText email, password;
    private ProgressDialog progressDialog;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Intent intent = new Intent(MainActivity.this, HomePage.class);
            startActivity(intent);
            finish();
        }

        signUpBtn = findViewById(R.id.signUpBtn);

        mAuth = FirebaseAuth.getInstance();
        mainLayout = findViewById(R.id.mainLayout);

        email = findViewById(R.id.emailAddress);
        password = findViewById(R.id.password);

        login = findViewById(R.id.loginBtn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty())
                {
                    Snackbar.make(mainLayout,"Fill email and password",Snackbar.LENGTH_SHORT).show();
                }
                else
                {
                    validate(email.getText().toString(),password.getText().toString());
                }
            }
        });

        progressDialog = new ProgressDialog(this);

        email.setText("");
        password.setText("");

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void validate(String email, String pass) {
        progressDialog.setMessage("Verifying User...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    checkEmailVerification();
                } else {
                    String errorMsg = showError(task.getException());
                    Snackbar.make(mainLayout, errorMsg, Snackbar.LENGTH_SHORT).show();
                    Log.w("Task error", task.getException().toString());
                    progressDialog.dismiss();
                }
            }
        });
    }

    private String showError(Exception exception) {
        String msg = "Login Failed";
        if (exception.getLocalizedMessage().trim().equalsIgnoreCase("There is no user record corresponding to this identifier. The user may have been deleted.")) {
            msg = "Login Failed, Invalid email ID";
        }
        else if(exception.getLocalizedMessage().trim().equalsIgnoreCase("The email address is badly formatted.")) {
            msg = "Email Address is not in proper format";
        }
        else if(exception.getLocalizedMessage().trim().equalsIgnoreCase("The password is invalid or the user does not have a password.")) {
            msg = "Login failed, Invalid password";
        }
        return msg;
    }

    private void checkEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        boolean emailflag = user.isEmailVerified();

        if(emailflag) {
            //finish();
            Snackbar.make(mainLayout, "Login Successful",Snackbar.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this,HomePage.class));
        }
        else {
            Snackbar.make(mainLayout, "Verify your email", Snackbar.LENGTH_SHORT).show();
            mAuth.signOut();
        }
    }


}
