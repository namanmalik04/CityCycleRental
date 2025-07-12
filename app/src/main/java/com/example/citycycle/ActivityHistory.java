package com.example.citycycle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.citycycle.databinding.ActivityAllCycleBinding;
import com.example.citycycle.databinding.ActivityHistoryBinding;
import com.example.citycycle.databinding.ActivityMainBinding;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.List;

public class ActivityHistory extends AppCompatActivity {
    private RecyclerView rvFuture, rvOngoing, rvHistory;
    private DBHelper dbHelper;
    private int userId;
    private ActivityHistoryBinding binding;// Correct binding
    private RecyclerView recyclerViewfutureRental,recyclerViewOnGoingRental,recyclerViewRentalHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        bottomNavigation();

        dbHelper = new DBHelper(this);


        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Error fetching user ID!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvFuture = findViewById(R.id.recyclerViewfutureRental);
        rvOngoing = findViewById(R.id.recyclerViewOnGoingRental);
        rvHistory = findViewById(R.id.recyclerViewRentalHistory);

        rvFuture.setLayoutManager(new LinearLayoutManager(this));
        rvOngoing.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        loadRentals();
        loadOngoingRentals();
        loadPastRentals();


    }


    private void bottomNavigation() {
        ChipNavigationBar bottomNav = binding.bottomNavigation;
        bottomNav.setItemSelected(R.id.activity, true);

        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                Log.d("BottomNav", "Selected ID: " + id); // Debugging log

                Intent intent = null;

                if (id == R.id.home) {
                    intent = new Intent(ActivityHistory.this, MainActivity.class);
                } else if (id == R.id.activity) {
                    return; // Already in AllCycleActivity, no need to restart
                } else if (id == R.id.all_cycle) {
                    intent = new Intent(ActivityHistory.this, AllCycleActivity.class);
                } else if (id == R.id.profile) {
                    intent = new Intent(ActivityHistory.this, ProfileActivity.class);
                } else {
                    Log.e("BottomNav", "Unknown selection: " + id);
                    return;
                }

                if (intent != null) {
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }
        });
    }

    private void loadRentals() {
        List<Rental> futureRentals = dbHelper.getFutureRentals(userId);

        // Create adapter and set it to the RecyclerView
        FutureRentalAdapter futureAdapter = new FutureRentalAdapter(this, futureRentals);
        rvFuture.setAdapter(futureAdapter);
    }

    private void loadOngoingRentals() {
        List<Rental> ongoingRentals = dbHelper.getOngoingRentals(userId);
        Log.d("ActivityHistory", " Ongoing Rentals Count: " + ongoingRentals.size());

        if (ongoingRentals.isEmpty()) {
            Log.e("ActivityHistory", " No Ongoing Rentals Found!");
        } else {
            for (Rental rental : ongoingRentals) {
                Log.d("ActivityHistory", " Ongoing Rental Loaded: " + rental.getName() + " | End: " + rental.getEnd());
            }
        }

        OngoingRentalAdapter ongoingAdapter = new OngoingRentalAdapter(this, ongoingRentals);
        rvOngoing.setAdapter(ongoingAdapter);
        ongoingAdapter.notifyDataSetChanged();

    }

    private void loadPastRentals() {
        List<Rental> pastRentals = dbHelper.getPastRentals(userId);
        Log.d("ActivityHistory", " Past Rentals Count: " + pastRentals.size());

        if (pastRentals.isEmpty()) {
            Log.e("ActivityHistory", " No Past Rentals Found!");
        } else {
            for (Rental rental : pastRentals) {
                Log.d("ActivityHistory", " Past Rental Loaded: " + rental.getName() + " | End: " + rental.getEnd());
            }
        }

        HistoryRentalAdapter historyAdapter = new HistoryRentalAdapter(this, pastRentals);
        rvHistory.setAdapter(historyAdapter);
    }










}