package com.example.parentalcontrol;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.blankj.utilcode.util.LogUtils;
import com.example.parentalcontrol.utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

//this screens open after successful sign in

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseUser user;
    private String userid;
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        //getting loged in user
        user=FirebaseAuth.getInstance().getCurrentUser();
        userid=user.getUid();
        LogUtils.e(userid);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.parent_node_name)).child(userid).child("name");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("inside","here");
                    String name=dataSnapshot.getValue(String.class);
                Log.e("inside",name);
                    TextView textName=findViewById(R.id.welcomeName);
                    textName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Dashboard.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_home, R.id.nav_locked_apps, R.id.nav_apps_usage,
//                R.id.nav_logout)
//                .setDrawerLayout(drawer)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
    }



//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
    //Navigation clicks for navigation to different activities
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_locked_apps:
                startActivity(new Intent(Dashboard.this,HomeActivity.class).putExtra(Constants.SOURCE_ACTIVITY,Constants.LOCK_APPS_ACTIVITY));
                break;
            case R.id.nav_apps_usage:
                //startActivity(new Intent(Dashboard.this,HomeActivity.class).putExtra(Constants.SOURCE_ACTIVITY,Constants.APPS_USAGE_ACTIVITY));
                startActivity(new Intent(Dashboard.this,AppsUsageChart.class).putExtra(Constants.SOURCE_ACTIVITY,Constants.APPS_USAGE_ACTIVITY));
                break;

            case R.id.nav_apps_chart:
                startActivity(new Intent(Dashboard.this,ChartsActivity.class));

                break;
            case R.id.nav_logout:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                FirebaseAuth.getInstance().signOut();
                                SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.clear();
                                editor.apply();
                                Intent intent=new Intent(Dashboard.this,SignInActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.cancel();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
