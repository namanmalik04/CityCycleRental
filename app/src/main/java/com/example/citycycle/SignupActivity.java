package com.example.citycycle;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

public class SignupActivity extends AppCompatActivity {
    private EditText etFName, etLName, etEmail, etPhone, etAddress, etDob, etPassword, etCPassword;
    private RadioGroup rgGender;
    private ImageView imgProfile;
    private SQLiteOpenHelper dbHelper;
    private Button btnSignup;
    private Uri imageUri;
    private Bitmap selectedBitmap;
    private static final int PICK_IMAGE = 1;
    private static final int TAKE_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        etFName = findViewById(R.id.etFName);
        etLName = findViewById(R.id.etLName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etDob = findViewById(R.id.etDob);
        etPassword = findViewById(R.id.etPassword);
        etCPassword = findViewById(R.id.etCPassword);
        rgGender = findViewById(R.id.rgGender);
        imgProfile = findViewById(R.id.imgProfile);
        btnSignup = findViewById(R.id.btnUpdateProfile);

        dbHelper = new DBHelper(this);


        TextView tvSignin = findViewById(R.id.tvSignin);
        tvSignin.setOnClickListener(view -> {
            Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
            startActivity(intent);
        });


        etDob.setOnClickListener(view -> showDatePicker());


        imgProfile.setOnClickListener(view -> showImagePickerOptions());


        btnSignup.setOnClickListener(this::onSignupClick);
    }

    public void onSignupClick(View view) {
        String firstName = etFName.getText().toString().trim();
        String lastName = etLName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etCPassword.getText().toString().trim();


        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() ||
                address.isEmpty() || dob.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showToast("All fields are required.");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Invalid email format.");
            return;
        }

        if (!phone.matches("\\d{10}")) {
            showToast("Phone number must be 10 digits.");
            return;
        }


        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        if (selectedGenderId == -1) {
            showToast("Please select a gender.");
            return;
        }
        RadioButton selectedGender = findViewById(selectedGenderId);
        String gender = selectedGender.getText().toString();

        if (password.length() < 6) {
            showToast("Password must be at least 6 characters.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showToast("Passwords do not match.");
            return;
        }

        CheckBox cbTerms = findViewById(R.id.cbTerms);
        if (!cbTerms.isChecked()) {
            showToast("Please accept the terms and conditions.");
            return;
        }

        if (isEmailExists(email)) {
            showToast("Email already exists.");
            return;
        }


        String encryptedPassword = encryptPassword(password);


        saveUserToDatabase(firstName, lastName, email, phone, address, dob, gender, encryptedPassword);
    }

    private void saveUserToDatabase(String fName, String lName, String email, String phone, String address,
                                    String dob, String gender, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("firstname", fName);
        values.put("lastname", lName);
        values.put("email", email);
        values.put("phone", phone);
        values.put("address", address);
        values.put("dob", dob);
        values.put("gender", gender);
        values.put("password", password);


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (selectedBitmap == null) {
            Bitmap defaultImage = BitmapFactory.decodeResource(getResources(), R.drawable.user);
            defaultImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } else {
            selectedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        }
        values.put("profile_image", outputStream.toByteArray());

        long result = db.insert("users", null, values);
        db.close();

        if (result == -1) {
            showToast("Signup failed. Try again.");
        } else {
            showToast("Signup successful!");


            Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears the activity stack
            startActivity(intent);
            finish(); // Close SignupActivity to prevent going back
        }
    }

    private boolean isEmailExists(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    private String encryptPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password;
        }
    }

    private void showImagePickerOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Profile Picture");

        String[] options = {"Choose from Gallery", "Take a Photo"};
        builder.setItems(options, (dialog, which) -> {
            Intent intent;
            if (which == 0) {
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            } else {
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });

        builder.show();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);

                    if (selectedDate.after(Calendar.getInstance())) {
                        showToast("Future dates are not allowed.");
                    } else {
                        etDob.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                    }
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
