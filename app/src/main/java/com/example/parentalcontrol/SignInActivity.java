package com.example.parentalcontrol;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.parentalcontrol.utils.Constants;
import com.example.parentalcontrol.utils.PermissionClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText email,password;
    private Button signin;
    ProgressDialog progressDialog;
    private TextView registerHere,forgotPassword,verifyEmail;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        firebaseAuth=FirebaseAuth.getInstance();
        PermissionClass.checkPermission(SignInActivity.this);
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE);
        //if user already logedin
        if (prefs.contains("email") && prefs.contains("password")){
            startActivity(new Intent(SignInActivity.this,Dashboard.class));
            finish();
        }
        email=findViewById(R.id.etWriteEmail);
        forgotPassword=findViewById(R.id.tvForgotPassword);
        password=findViewById(R.id.etWritePassword);
        signin=findViewById(R.id.btnSignIn);
        signin.setOnClickListener(this);
        registerHere=findViewById(R.id.tvGoToSignUp);
        verifyEmail=findViewById(R.id.tvverifyEmail);
        registerHere.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        verifyEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final String strPassword=password.getText().toString();
        final String strEmail=email.getText().toString();
        if (view.getId()==R.id.btnSignIn){
            Log.e("iamhere","signinclicked");
            if (strPassword.equals("") || strEmail.equals("")){
                Toast.makeText(this, "Enter password.", Toast.LENGTH_SHORT).show();
            }
            else {
                progressDialog = new ProgressDialog(SignInActivity.this);
                progressDialog.setTitle("Authenticating...");
                progressDialog.setMessage("Verifying password");
                progressDialog.setCancelable(false);
                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(strEmail,strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            //check if user has verified email by clicking on link
                            if (firebaseAuth.getCurrentUser().isEmailVerified()){
                            SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE).edit();
                            editor.putString("email", strEmail);
                            editor.putString("password", strPassword);
                            editor.apply();
                            Intent intent=new Intent(SignInActivity.this,Dashboard.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            }
                            else {
                                Toast.makeText(SignInActivity.this, "Please verify your email", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                DatabaseReference myRef = database.getReference(getResources().getString(R.string.parent_node_name));
//                myRef.orderByChild("email").equalTo(strEmail).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()){
//                        progressDialog.dismiss();
//                            SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE).edit();
//                            editor.putString("email", strEmail);
//                            editor.putString("password", strPassword);
//                            editor.apply();
//                            Intent intent=new Intent(SignInActivity.this,HomeActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                            finish();
//                    }
//                    else {
//                        progressDialog.dismiss();
//                        Toast.makeText(SignInActivity.this, "No user exist with this password", Toast.LENGTH_LONG).show();
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
            }
        }
        else if (view.getId()==R.id.tvGoToSignUp){
            startActivity(new Intent(SignInActivity.this,MainActivity.class));
            finish();
        }
        else if (view.getId()==R.id.tvForgotPassword){
            if (strEmail.equals("")){
                Toast.makeText(this, "Please enter a valid email address first", Toast.LENGTH_LONG).show();
            }
            else {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(strEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignInActivity.this, "Check your email", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        else if (view.getId()==R.id.tvverifyEmail){
            if (strEmail.equals("")){
                Toast.makeText(this, "Please enter a valid email address first", Toast.LENGTH_LONG).show();
            }
            else {
                ActionCodeSettings actionCodeSettings =
                        ActionCodeSettings.newBuilder()
                                // URL you want to redirect back to. The domain (www.example.com) for this
                                // URL must be whitelisted in the Firebase Console.
                                .setUrl("https://www.example.com/finishSignUp?cartId=1234")
                                // This must be true
                                .setHandleCodeInApp(true)
                                .setIOSBundleId("com.example.ios")
                                .setAndroidPackageName(
                                        "com.example.parentalcontrol",
                                        true, /* installIfNotAvailable */
                                        "12"    /* minimumVersion */)
                                .build();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendSignInLinkToEmail(strEmail,actionCodeSettings).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(SignInActivity.this, "Check your email", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}