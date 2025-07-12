package com.example.citycycle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SigninActivity extends AppCompatActivity {

    private EditText etEmail,etPassword;
    private Button btnSignin;
    private TextView tvSignup;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);

        tvSignup = findViewById(R.id.tvSignup);
        btnSignin = findViewById(R.id.btnSignin);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        tvSignup.setOnClickListener(view -> {
            Intent intent = new Intent(SigninActivity.this, SignupActivity.class);
            startActivity(intent);
        });


        dbHelper = new DBHelper(this);

        btnSignin.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String encryptedPassword = dbHelper.encryptPassword(password);

            if (dbHelper.checkUser(email, encryptedPassword)) {
                // Get user ID from database
                int userId = dbHelper.getUserIdByEmail(email);

                if (userId == -1) {
                    Toast.makeText(this, "Error: User ID not found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //  Save user ID in SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("user_id", userId);
                editor.putString("email", email);
                editor.apply();

                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                // Navigate to MainActivity
                Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        });



    }
}