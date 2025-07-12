package com.example.citycycle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUser;
    private Button btnEditProfile, btnSignOut;
    private DBHelper dbHelper;
    private int userId;
    private ChipNavigationBar bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvUser = findViewById(R.id.tvUser);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnSignOut = findViewById(R.id.btnSingOut);
        bottomNav = findViewById(R.id.bottomNavigation);

        dbHelper = new DBHelper(this);


        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }


        loadUserDetails();


        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });


        btnSignOut.setOnClickListener(v -> logoutUser());


        setupBottomNavigation();
    }

    private void loadUserDetails() {
        UserModel user = dbHelper.getUserById(userId);

        if (user != null) {
            tvUser.setText("Hi, " + user.getFirstname());
        } else {
            Toast.makeText(this, "Error loading user details!", Toast.LENGTH_SHORT).show();
        }
    }

    private void logoutUser() {

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();


        Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(ProfileActivity.this, SigninActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupBottomNavigation() {
        bottomNav.setItemSelected(R.id.profile, true);
        bottomNav.setOnItemSelectedListener(id -> {
            Intent intent = null;
            if (id == R.id.home) {
                intent = new Intent(ProfileActivity.this, MainActivity.class);
            } else if (id == R.id.all_cycle) {
                intent = new Intent(ProfileActivity.this, AllCycleActivity.class);
            } else if (id == R.id.activity) {
                intent = new Intent(ProfileActivity.this, ActivityHistory.class);
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
    }
}
