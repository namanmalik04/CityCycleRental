package com.example.citycycle;

import android.app.TimePickerDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RentActivity extends AppCompatActivity {

    private Spinner spDayOrHour;
    private TextView tvStart, tvEnd, tvCount, tvTotal, tvCountPlus, tvCountMinus;
    private Button btnRentCycle;
    private ImageView btnBack;
    private int count = 1, available = 10;
    private double price = 20;
    private boolean isTimeBasedRental = false;
    private int userId;
    private int cycleId;
    private double totalPrice;

    private Calendar selectedStartDate = Calendar.getInstance();
    private Calendar selectedEndDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent);
        DBHelper dbHelper = new DBHelper(this);

        // Retrieve cycle_id from Intent
        Intent intent = getIntent();
        cycleId = intent.getIntExtra("cycle_id", -1);
        price = intent.getDoubleExtra("price", 20);

        //  Retrieve userId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        Log.d("RentActivity", "Received cycle_id: " + cycleId);
        Log.d("RentActivity", "Received user_id: " + userId);

        if (cycleId == -1 || userId == -1) {
            Toast.makeText(this, "Error: Missing cycle or user ID!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        spDayOrHour = findViewById(R.id.spDayOrHour);
        tvStart = findViewById(R.id.tvStart);
        tvEnd = findViewById(R.id.tvEnd);
        tvCount = findViewById(R.id.tvCount);
        tvTotal = findViewById(R.id.tvTotal);
        tvCountMinus = findViewById(R.id.tvCountMinus);
        tvCountPlus = findViewById(R.id.tvCountPlus);
        btnRentCycle = findViewById(R.id.btnRentCycle);
        btnBack = findViewById(R.id.btnBack);

        available = dbHelper.getAvailableCount(cycleId);
        updateCycleCount();

        spDayOrHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isTimeBasedRental = position == 1;
                clearDateTimeFields();
                if (isTimeBasedRental) {
                    setupTimePickers();
                } else {
                    setupDatePickers();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        tvCountMinus.setOnClickListener(v -> {
            if (count > 1) {
                count--;
                updateCycleCount();
                updateTotalPrice();
            }
        });

        tvCountPlus.setOnClickListener(v -> {
            if (count < available) {
                count++;
                updateCycleCount();
                updateTotalPrice();
            }
        });

        btnRentCycle.setOnClickListener(v -> rentCycle());


        btnBack.setOnClickListener(v -> {
            Intent intentBack = new Intent(RentActivity.this, AllCycleActivity.class);
            startActivity(intentBack);
            finish();
        });
    }

    private void setupTimePickers() {
        tvStart.setOnClickListener(v -> showTimePicker(tvStart, true));
        tvEnd.setOnClickListener(v -> showTimePicker(tvEnd, false));
    }

    private void setupDatePickers() {
        tvStart.setOnClickListener(v -> showDatePicker(tvStart, true));
        tvEnd.setOnClickListener(v -> showDatePicker(tvEnd, false));
    }

    private void showTimePicker(TextView targetView, boolean isStart) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            Calendar selectedTime = Calendar.getInstance();
            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedTime.set(Calendar.MINUTE, minute1);


            if (isStart) {
                if (selectedTime.before(Calendar.getInstance())) {
                    showToast("Start time cannot be in the past.");
                    return;
                }
                selectedStartDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedStartDate.set(Calendar.MINUTE, minute1);
                targetView.setText(formatTime(hourOfDay, minute1));
            } else {
                Calendar minEndTime = (Calendar) selectedStartDate.clone();
                minEndTime.add(Calendar.HOUR_OF_DAY, 1);

                if (selectedTime.before(minEndTime)) {
                    showToast("End time must be at least 1 hour after the start time.");
                    return;
                }
                selectedEndDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedEndDate.set(Calendar.MINUTE, minute1);
                targetView.setText(formatTime(hourOfDay, minute1));
            }

            updateTotalPrice();
        }, hour, minute, false);
        timePickerDialog.show();
    }
    private String formatTime(int hourOfDay, int minute) {
        return String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
    }



    private void showDatePicker(TextView targetView, boolean isStart) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year1, month1, dayOfMonth);

            if (selectedDate.before(Calendar.getInstance())) {
                showToast("Past dates are not allowed.");
                return;
            }

            if (isStart) {
                selectedStartDate.set(year1, month1, dayOfMonth);
                targetView.setText(year1 + "-" + (month1 + 1) + "-" + dayOfMonth);
            } else {

                Calendar minEndDate = (Calendar) selectedStartDate.clone();
                minEndDate.add(Calendar.DAY_OF_MONTH, 1);

                if (selectedDate.before(minEndDate)) {
                    showToast("End date must be at least one day after the start date.");
                    return;
                }

                selectedEndDate.set(year1, month1, dayOfMonth);
                targetView.setText(year1 + "-" + (month1 + 1) + "-" + dayOfMonth);
            }
            updateTotalPrice();
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateTotalPrice() {
        if (tvStart.getText().toString().isEmpty() || tvEnd.getText().toString().isEmpty()) {
            tvTotal.setText("Total: $0");
            return;
        }


        if (isTimeBasedRental) {
            totalPrice = price * count * getTimeDifference(tvStart.getText().toString(), tvEnd.getText().toString());
        } else {
            totalPrice = price * count * 24 * getDateDifference(tvStart.getText().toString(), tvEnd.getText().toString());
        }
        tvTotal.setText("Total: $" + totalPrice);
    }

    private int getTimeDifference(String start, String end) {
        if (start.isEmpty() || end.isEmpty()) return 1;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date startTime = sdf.parse(start);
            Date endTime = sdf.parse(end);
            long diff = (endTime.getTime() - startTime.getTime()) / (1000 * 60 * 60);
            return (int) Math.max(diff, 1);
        } catch (ParseException e) {
            e.printStackTrace();
            return 1;
        }
    }

    private int getDateDifference(String start, String end) {
        if (start.isEmpty() || end.isEmpty()) return 1;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date startDate = sdf.parse(start);
            Date endDate = sdf.parse(end);
            long diff = (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
            return (int) Math.max(diff, 1);
        } catch (ParseException e) {
            e.printStackTrace();
            return 1;
        }
    }

    private void rentCycle() {
        String start = tvStart.getText().toString();
        String end = tvEnd.getText().toString();


        if (start.isEmpty() || end.isEmpty()) {
            Toast.makeText(this, "Please select Start and End Date/Time!", Toast.LENGTH_SHORT).show();
            return;
        }

//        double totalPrice = calculateTotalPrice();

        if (count > available) {
            Toast.makeText(this, "Not enough cycles available!", Toast.LENGTH_SHORT).show();
            return;
        }

        DBHelper dbHelper = new DBHelper(this);

        boolean isInserted = isTimeBasedRental
                ? dbHelper.insertTimeRental(userId, cycleId, count, start, end, totalPrice)
                : dbHelper.insertDayRental(userId, cycleId, count, start, end, totalPrice);

        if (isInserted) {
            available -= count;
            updateCycleCount();
            dbHelper.updateAvailableCount(cycleId, available);
            Toast.makeText(this, "Cycle rented successfully!", Toast.LENGTH_SHORT).show();
            clearDateTimeFields();  //  Clear Fields After Success
        } else {
            Toast.makeText(this, "Error renting cycle!", Toast.LENGTH_SHORT).show();
        }
    }

//    private double calculateTotalPrice() {
//        int duration = isTimeBasedRental ? 2 : 1;
//        return isTimeBasedRental ? price * count * duration : price * count * 24 * duration;
//    }

    private void clearDateTimeFields() {
        tvStart.setText("");
        tvEnd.setText("");
        tvTotal.setText("Total: $0");
    }

    private void updateCycleCount() {
        tvCount.setText("Cycles: " + count + " / " + available);
    }
}








