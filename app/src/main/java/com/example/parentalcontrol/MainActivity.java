package com.example.parentalcontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.parentalcontrol.utils.Constants;
import com.example.parentalcontrol.utils.UStats;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
//signup activity
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private EditText name,email,password;
    private Button signup;
    private TextView alreadyMember;
    private HashMap<String, String> hashMap=new HashMap<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        name=findViewById(R.id.etWriteName);
        email=findViewById(R.id.etWriteEmail);
        password=findViewById(R.id.etWritePassword);
        alreadyMember=findViewById(R.id.tvGoToSignIn);
        alreadyMember.setOnClickListener(this);
        signup=findViewById(R.id.btnSignUp);
        signup.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
    }
    public  void checkPermission(){
        //check if user has granted usage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (UStats.getUsageStatsList(this).isEmpty()){
                showUsageDialog();
                return;
            }
        }
        PermissionUtils.permission(PermissionConstants.STORAGE)
                .callback(new PermissionUtils.SimpleCallback() {
                    @Override
                    public void onGranted() {

                        //   screenCaptureObserver();
                    }

                    @Override
                    public void onDenied() {
                        ToastUtils.showShort("Can't work without permissions");
                        finish();
                    }
                }).request();
    }
    void showUsageDialog() {

        new MaterialDialog.Builder(this)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        startActivity(intent);

                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ToastUtils.showShort("Can't work without permissions");
                        finish();

                    }
                })
                .title("Accept Usage Access Permission")
                .content("We need this permission to start exam")
                .positiveText("Grant Access")
                .negativeText("Later")
                .theme(Theme.LIGHT)
                .contentGravity(GravityEnum.START)
                .titleGravity(GravityEnum.START)
                .show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btnSignUp){
            Log.e("iamhere","clicked");
            final String strName=name.getText().toString();
            final String strEmail=email.getText().toString();
            final String strPassword=password.getText().toString();
            if (strName.equals("") ||strEmail.equals("")|| strPassword.equals("")){
                Toast.makeText(this, "Fill all fields.", Toast.LENGTH_SHORT).show();
            }
            else {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("Registering...");
                progressDialog.setMessage("Adding Data in database");
                progressDialog.setCancelable(false);
                progressDialog.show();
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        hashMap.put("name", strName);
                                        hashMap.put("email", strEmail);
                                        hashMap.put("Password", strPassword);
                                        final FirebaseUser user = mAuth.getCurrentUser();
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference myRef = database.getReference(getResources().getString(R.string.parent_node_name));
                                        myRef.child(user.getUid()).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                updateUI(user);
                                            }
                                        });
                                    }
                                }
                            });
                        } else {

                            Toast.makeText(getApplicationContext(), task.getException().getMessage() + "", Toast.LENGTH_LONG).show();
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                });
            }
        }
        else if (view.getId()==R.id.tvGoToSignIn){
            startActivity(new Intent(MainActivity.this,SignInActivity.class));
            finish();
        }
    }

    private void updateUI(FirebaseUser user) {
        Toast.makeText(this, "Please check your email for account verification", Toast.LENGTH_LONG).show();
        Intent intent=new Intent(MainActivity.this,SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}