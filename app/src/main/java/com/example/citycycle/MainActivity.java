package com.example.citycycle;
import com.example.citycycle.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.citycycle.databinding.ActivityMainBinding;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private RecyclerView recyclerViewCycle;
    private CycleAdapter adapter;
    private List<Cycle> cycleList;
    private DBHelper dbHelper;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerViewCycle = findViewById(R.id.recyclerViewCycle);
        recyclerViewCycle.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DBHelper(this);


        dbHelper.restoreAvailabilityForExpiredRentals();

        try {
            cycleList = dbHelper.getAllCycles();
            if (cycleList == null || cycleList.isEmpty()) {
                Toast.makeText(this, "No cycles available!", Toast.LENGTH_SHORT).show();
                Log.e("MainActivity", "Cycle list is empty or null.");
            } else {
                adapter = new CycleAdapter(this, cycleList);
                recyclerViewCycle.setAdapter(adapter);
                Log.d("MainActivity", "Cycles loaded: " + cycleList.size());
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error fetching cycles: " + e.getMessage());
        }

        setupRecyclerView(R.id.recyclerViewCycle, cycleList);
        bottomNavigation();
    }

    private void setupRecyclerView(int rcId, List<Cycle> cycleList) {
        RecyclerView recyclerView = findViewById(rcId);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));

        CycleAdapter adapter = new CycleAdapter(this, cycleList);
        recyclerView.setAdapter(adapter);
    }

    private void bottomNavigation() {
        ChipNavigationBar bottomNav = binding.bottomNavigation;


        bottomNav.setItemSelected(R.id.home, true);

        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                Log.d("BottomNav", "Selected ID: " + id);

                if (id == R.id.home) {
                    return;
                }

                Intent intent = null;
                if (id == R.id.all_cycle) {
                    intent = new Intent(MainActivity.this, AllCycleActivity.class);
                } else if (id == R.id.activity) {
                    intent = new Intent(MainActivity.this, ActivityHistory.class);
                } else if (id == R.id.profile) {
                    intent = new Intent(MainActivity.this, ProfileActivity.class);
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




    private Runnable availabilityUpdater = new Runnable() {
        @Override
        public void run() {
            DBHelper dbHelper = new DBHelper(MainActivity.this);
            dbHelper.restoreAvailabilityForExpiredRentals();
            handler.postDelayed(this, 60000);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(availabilityUpdater);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(availabilityUpdater);
    }

}
