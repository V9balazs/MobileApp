package com.example.waterconsumption.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.waterconsumption.MainActivity;
import com.example.waterconsumption.R;
import com.example.waterconsumption.pages.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    TextView textViewForgotPassword, textViewRegister;
    ProgressBar progressBar;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        textViewRegister = (TextView) findViewById(R.id.txtSigninRegiszeter);
        textViewForgotPassword = (TextView) findViewById(R.id.txtSigninForgetPassword);

        progressBar = (ProgressBar) findViewById(R.id.progressBarSignIn);

        mAuth = FirebaseAuth.getInstance();
    }

    public void txtSignInForgotPasswordClicked(View view){
        Intent intent = new Intent(SignIn.this, ForgotPassword.class);
        startActivity(intent);
    }

    public void txtSignInRegisterClicked(View view){
        Intent intent = new Intent(SignIn.this, SignUp.class);
        startActivity(intent);
    }

    public void buttonSignInClicked(View view){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please Enter a Valid Email");
            editTextEmail.requestFocus();
        }

        if(editTextPassword.length() < 6){
            editTextPassword.setError("Please Enter Password containing atleast 8 characters");
            editTextPassword.requestFocus();
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignIn.this, "User Successfully Sign In", Toast.LENGTH_SHORT).show();
                    Intent intentsignin = new Intent(SignIn.this, MainActivity.class);
                    startActivity(intentsignin);
                    finish();
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignIn.this, "User Failed to Sign In", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}