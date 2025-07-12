package com.example.citycycle;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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

public class EditProfileActivity extends AppCompatActivity {
    private EditText etFName, etLName, etEmail, etPhone, etAddress, etDob, etOldPassword, etNewPassword, etCPassword;
    private RadioGroup rgGender;
    private ImageView imgProfile,btnBack;
    private Button btnUpdateProfile;
    private DBHelper dbHelper;
    private Uri imageUri;
    private Bitmap selectedBitmap;
    private static final int PICK_IMAGE = 1;
    private static final int TAKE_PHOTO = 2;
    private int userId;
    private String existingPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        etFName = findViewById(R.id.etFName);
        etLName = findViewById(R.id.etLName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etDob = findViewById(R.id.etDob);
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etPassword);
        etCPassword = findViewById(R.id.etCPassword);
        rgGender = findViewById(R.id.rgGender);
        imgProfile = findViewById(R.id.imgProfile);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnBack = findViewById(R.id.btnBack);

        dbHelper = new DBHelper(this);


        userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Error fetching user details!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        loadUserDetails();


        etDob.setOnClickListener(view -> showDatePicker());


        imgProfile.setOnClickListener(view -> showImagePickerOptions());


        btnUpdateProfile.setOnClickListener(view -> updateProfile());


        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }


    private void showImagePickerOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Profile Picture");

        String[] options = {"Choose from Gallery", "Take a Photo"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {

                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, PICK_IMAGE);
            } else if (which == 1) {

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, TAKE_PHOTO);
            }
        });

        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE) {
                imageUri = data.getData();
                try {
                    selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    imgProfile.setImageBitmap(selectedBitmap);
                } catch (IOException e) {
                    Log.e("EditProfile", "Image Load Error: " + e.getMessage());
                    e.printStackTrace();
                }
            } else if (requestCode == TAKE_PHOTO) {
                selectedBitmap = (Bitmap) data.getExtras().get("data");
                imgProfile.setImageBitmap(selectedBitmap);
            }
        }
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
                        return;
                    }


                    String dob = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    etDob.setText(dob);
                },
                year, month, day
        );

        datePickerDialog.show();
    }




    private void loadUserDetails() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE id = ?", new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            etFName.setText(cursor.getString(cursor.getColumnIndexOrThrow("firstname")));
            etLName.setText(cursor.getString(cursor.getColumnIndexOrThrow("lastname")));
            etEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            etPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            etAddress.setText(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            etDob.setText(cursor.getString(cursor.getColumnIndexOrThrow("dob")));
            existingPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"));


            String gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
            if ("Male".equalsIgnoreCase(gender)) {
                rgGender.check(R.id.rbMale);
            } else {
                rgGender.check(R.id.rbFemale);
            }


            byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow("profile_image"));
            if (imageBytes != null) {
                selectedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                imgProfile.setImageBitmap(selectedBitmap);
            }
        }
        cursor.close();
    }


    private void updateProfile() {
        String firstName = etFName.getText().toString().trim();
        String lastName = etLName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etCPassword.getText().toString().trim();


        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || dob.isEmpty()) {
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


        String encryptedPassword = existingPassword;

        if (!oldPassword.isEmpty() || !newPassword.isEmpty() || !confirmPassword.isEmpty()) {
            if (!encryptPassword(oldPassword).equals(existingPassword)) {
                showToast("Old password is incorrect.");
                return;
            }
            if (newPassword.length() < 6) {
                showToast("New password must be at least 6 characters.");
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                showToast("New password and confirm password do not match.");
                return;
            }
            encryptedPassword = encryptPassword(newPassword);
        }


        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("firstname", firstName);
        values.put("lastname", lastName);
        values.put("email", email);
        values.put("phone", phone);
        values.put("address", address);
        values.put("dob", dob);
        values.put("gender", gender);
        values.put("password", encryptedPassword);


        if (selectedBitmap != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            selectedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            values.put("profile_image", imageBytes);
        }

        int rowsAffected = db.update("users", values, "id=?", new String[]{String.valueOf(userId)});
        db.close();

        if (rowsAffected > 0) {
            showToast("Profile updated successfully!");
            finish();
        } else {
            showToast("Update failed. Try again.");
        }
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


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
