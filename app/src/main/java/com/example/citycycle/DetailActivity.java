package com.example.citycycle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {
    private ImageView imgBike, btnBack;
    private TextView tvName, tvType, tvPrice, tvAvailable, tvDescription;
    private Button btnRent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);




        imgBike = findViewById(R.id.imgBike);
        btnBack = findViewById(R.id.btnBack);
        tvName = findViewById(R.id.tvName);
        tvType = findViewById(R.id.tvType);
        tvPrice = findViewById(R.id.tvTotalPrice);
        tvAvailable = findViewById(R.id.tvAvailable);
        tvDescription = findViewById(R.id.tvDescription);
        btnRent = findViewById(R.id.btnRent);



        Intent intent = getIntent();
        int cycleId = intent.getIntExtra("cycle_id", -1);
        String name = intent.getStringExtra("name");
        String type = intent.getStringExtra("type");
        double price = intent.getDoubleExtra("price", 0);
        int available = intent.getIntExtra("available", 0);
        String description = intent.getStringExtra("description");
        byte[] imageBytes = intent.getByteArrayExtra("image");




        if (cycleId == -1) {
            Toast.makeText(this, "Error: Cycle ID not found!", Toast.LENGTH_SHORT).show();
            finish();
        }


        Log.d("DetailActivity", "Received Cycle ID: " + cycleId);

        // Set data to views
        tvName.setText(name);
        tvType.setText(type);
        tvPrice.setText(String.format("Rs. %.2f", price));
        tvAvailable.setText("Available: " + available);
        tvDescription.setText(description);

        if (imageBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            if (bitmap != null) {
                imgBike.setImageBitmap(bitmap);
            } else {
                imgBike.setImageResource(R.drawable.loginimage);
            }
        } else {
            imgBike.setImageResource(R.drawable.loginimage);
        }




        // Handle Rent button click
        btnRent.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            int userId = sharedPreferences.getInt("user_id", -1);

            if (userId == -1) {
                Toast.makeText(this, "Error: User ID not found!", Toast.LENGTH_SHORT).show();
                return;
            }


            Intent rentIntent = new Intent(DetailActivity.this, RentActivity.class);
            rentIntent.putExtra("cycle_id", cycleId);
            rentIntent.putExtra("name", name);
            rentIntent.putExtra("type", type);
            rentIntent.putExtra("price", price);
            rentIntent.putExtra("available", available);
            rentIntent.putExtra("description", description);
            rentIntent.putExtra("cycle_id", cycleId);
            rentIntent.putExtra("user_id", userId);
            startActivity(rentIntent);
        });


        btnBack.setOnClickListener(v -> {
            Intent backIntent = new Intent(DetailActivity.this, AllCycleActivity.class);
            startActivity(backIntent);
            finish();
        });
    }
}