package com.example.citycycle;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "citycyclerental_db.db";
    private static final int DATABASE_VERSION = 1;
    private final Context context;
    private static final String TAG = "DBHelper";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;


    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createUsersTable = "CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, firstname TEXT, lastname TEXT, email TEXT UNIQUE, phone TEXT, address TEXT, dob TEXT, gender TEXT, password TEXT, profile_image BLOB)";
        db.execSQL(createUsersTable);


        String createCyclesTable = "CREATE TABLE cycles (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT, type TEXT, branch TEXT, availability INTEGER, price REAL, image BLOB)";
        db.execSQL(createCyclesTable);

        // Table for Day Rent
        String createDayRentTable = "CREATE TABLE day_rent (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, cycle_id INTEGER, " +
                "booking_datetime DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "start_date DATETIME, " +
                "end_date DATETIME, " +
                "days INTEGER, total_price REAL, " +
                "count INTEGER, " +
                "restored INTEGER DEFAULT 0, " +
                "FOREIGN KEY(user_id) REFERENCES users(id), " +
                "FOREIGN KEY(cycle_id) REFERENCES cycles(id))";
        db.execSQL(createDayRentTable);

        String createTimeRentTable = "CREATE TABLE time_rent (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, cycle_id INTEGER, " +
                "booking_datetime DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "start_time DATETIME, " +
                "end_time DATETIME, " +
                "hours INTEGER, total_price REAL, " +
                "count INTEGER, " +
                "restored INTEGER DEFAULT 0, " +
                "FOREIGN KEY(user_id) REFERENCES users(id), " +
                "FOREIGN KEY(cycle_id) REFERENCES cycles(id))";
        db.execSQL(createTimeRentTable);



        // Create Bookings Table
        String createBookingsTable = "CREATE TABLE bookings (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, cycle_id INTEGER, start_date TEXT, end_date TEXT, total_price REAL, FOREIGN KEY(user_id) REFERENCES users(id), FOREIGN KEY(cycle_id) REFERENCES cycles(id))";
        db.execSQL(createBookingsTable);

        // Create Favorites Table
        String createFavoritesTable = "CREATE TABLE favorite (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, cycle_id INTEGER, FOREIGN KEY(user_id) REFERENCES users(id), FOREIGN KEY(cycle_id) REFERENCES cycles(id))";
        db.execSQL(createFavoritesTable);

        insertDefaultCycles(db);
    }

    public int getUserIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE email = ?", new String[]{email});
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return userId;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS cycles");
        db.execSQL("DROP TABLE IF EXISTS bookings");
        db.execSQL("DROP TABLE IF EXISTS favorite");
        db.execSQL("DROP TABLE IF EXISTS time_rent");
        db.execSQL("DROP TABLE IF EXISTS day_rent");
        onCreate(db);
    }

    private void insertDefaultCycles(SQLiteDatabase db) {
        if (isTableEmpty(db, "cycles")) {
            insertCycle(db, "Cyclocross Bike", "Best for off-road trails.", "Road Bikes", "Kandy Branch", 25, 200, R.drawable.c1);
            insertCycle(db, "Endurance Bike", "Smooth ride for city roads.", "Road Bikes", "Colombo Branch", 15, 150, R.drawable.c2);
            insertCycle(db, "Mountain Bike", "Best for off-road trails.", "Road Bikes", "Jaffna Branch", 10, 250, R.drawable.c3);
            insertCycle(db, "Gravel Bike", "Smooth ride for city roads.", "Road Bikes", "Colombo Branch", 15, 170, R.drawable.c4);
            Log.d(TAG, "Default cycles inserted.");
        } else {
            Log.d(TAG, "Cycles table already contains data. Skipping default insertion.");
        }
    }


    private boolean isTableEmpty(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
        boolean isEmpty = true;
        if (cursor.moveToFirst()) {
            isEmpty = cursor.getInt(0) == 0;
        }
        cursor.close();
        return isEmpty;
    }


    public boolean insertDayRental(int userId, int cycleId, int count, String start, String end, double totalPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("cycle_id", cycleId);
        values.put("count", count);
        values.put("start_date", start);
        values.put("end_date", end);
        values.put("total_price", totalPrice);

        long result = db.insert("day_rent", null, values);
        db.close();
        return result != -1;
    }

    public boolean insertTimeRental(int userId, int cycleId, int count, String start, String end, double totalPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("cycle_id", cycleId);
        values.put("count", count);
        values.put("start_time", start);
        values.put("end_time", end);
        values.put("total_price", totalPrice);

        long result = db.insert("time_rent", null, values);
        db.close();
        return result != -1;
    }











    public void updateAvailableCount(int cycleId, int newAvailableCount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("availability", newAvailableCount);

        int rowsAffected = db.update("cycles", values, "id = ?", new String[]{String.valueOf(cycleId)});

        if (rowsAffected > 0) {
            Log.d("DBHelper", "Available count updated for cycle ID: " + cycleId);
        } else {
            Log.e("DBHelper", "Failed to update available count for cycle ID: " + cycleId);
        }

        db.close();
    }





    public void restoreAvailabilityForExpiredRentals() {
        SQLiteDatabase db = this.getWritableDatabase();
        String currentDate = getCurrentDate();
        String currentTime = getCurrentTime();

        Log.d("DBHelper", " Checking expired rentals at Date: " + currentDate + " and Time: " + currentTime);

        db.beginTransaction();
        try {
            Cursor dayCursor = db.rawQuery(
                    "SELECT id, cycle_id, count FROM day_rent WHERE date(end_date) < date(?) AND restored = 0",
                    new String[]{currentDate}
            );

            while (dayCursor.moveToNext()) {
                int rentalId = dayCursor.getInt(0);
                int cycleId = dayCursor.getInt(1);
                int count = dayCursor.getInt(2);

                if (cycleExists(cycleId)) {
                    updateCycleAvailability(db, cycleId, count, true);
                    markRentalAsRestored(db, "day_rent", rentalId);
                    Log.d("DBHelper", " Restored " + count + " cycles for cycle ID: " + cycleId + " (Day Rent)");
                }
            }
            dayCursor.close();


            Cursor timeCursor = db.rawQuery(
                    "SELECT id, cycle_id, count FROM time_rent WHERE time(end_time) < time(?) AND restored = 0",
                    new String[]{currentTime}
            );

            while (timeCursor.moveToNext()) {
                int rentalId = timeCursor.getInt(0);
                int cycleId = timeCursor.getInt(1);
                int count = timeCursor.getInt(2);

                if (cycleExists(cycleId)) {
                    updateCycleAvailability(db, cycleId, count, true);
                    markRentalAsRestored(db, "time_rent", rentalId);
                    Log.d("DBHelper", " Restored " + count + " cycles for cycle ID: " + cycleId + " (Time Rent)");
                }
            }
            timeCursor.close();

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DBHelper", "️ Error restoring availability: " + e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    public String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }










    private void updateCycleAvailability(SQLiteDatabase db, int cycleId, int count, boolean isAdding) {
        if (db == null || !db.isOpen()) {
            Log.e("DBHelper", " Database is not open!");
            return;
        }

        Cursor cursor = null;
        int currentAvailability = 0;

        try {
            cursor = db.rawQuery("SELECT availability FROM cycles WHERE id = ?", new String[]{String.valueOf(cycleId)});
            if (cursor.moveToFirst()) {
                currentAvailability = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e("DBHelper", " Error fetching availability: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }

        int newAvailability = isAdding ? currentAvailability + count : Math.max(currentAvailability - count, 0);

        ContentValues values = new ContentValues();
        values.put("availability", newAvailability);

        try {
            int rowsAffected = db.update("cycles", values, "id = ?", new String[]{String.valueOf(cycleId)});
            if (rowsAffected > 0) {
                Log.d("DBHelper", " Updated availability for cycle ID: " + cycleId + " to " + newAvailability);
            } else {
                Log.e("DBHelper", "️ Failed to update availability for cycle ID: " + cycleId);
            }
        } catch (Exception e) {
            Log.e("DBHelper", " Error updating availability: " + e.getMessage());
        }
    }







    private void markRentalAsRestored(SQLiteDatabase db, String table, int rentalId) {
        ContentValues values = new ContentValues();
        values.put("restored", 1);

        int rowsUpdated = db.update(table, values, "id = ?", new String[]{String.valueOf(rentalId)});

        if (rowsUpdated > 0) {
            Log.d("DBHelper", " Marked rental as restored in table: " + table + " for rental ID: " + rentalId);
        } else {
            Log.e("DBHelper", " Failed to mark rental as restored for ID: " + rentalId);
        }
    }


    public List<String> getAllBranches() {
        List<String> branches = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT branch FROM cycles", null);

        while (cursor.moveToNext()) {
            branches.add(cursor.getString(0));
        }
        cursor.close();
        db.close();

        if (branches.isEmpty()) {
            branches.add("No Branches Available");
        }

        return branches;
    }










    private boolean cycleExists(int cycleId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM cycles WHERE id = ?", new String[]{String.valueOf(cycleId)});
        boolean exists = cursor.moveToFirst() && cursor.getInt(0) > 0;
        cursor.close();
        return exists;
    }



    private void insertCycle(SQLiteDatabase db, String name, String description, String type, String branch, int availability, double price, int drawableId) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", description);
        values.put("type", type);
        values.put("branch", branch);
        values.put("availability", availability);
        values.put("price", price);
        values.put("image", getImageBytes(drawableId));
        db.insert("cycles", null, values);
    }

    private byte[] getImageBytes(int drawableId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public boolean insertUser(String firstname, String lastname, String email, String phone, String address, String dob, String gender, String password, Bitmap profileImage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("firstname", firstname);
        values.put("lastname", lastname);
        values.put("email", email);
        values.put("phone", phone);
        values.put("address", address);
        values.put("dob", dob);
        values.put("gender", gender);


        String hashedPassword = encryptPassword(password);
        values.put("password", hashedPassword);

        if (profileImage != null) {
            values.put("profile_image", convertBitmapToByteArray(profileImage));
        }

        long result = db.insert("users", null, values);
        db.close();
        return result != -1;
    }



    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT password FROM users WHERE email = ?", new String[]{email});

        if (cursor.moveToFirst()) {
            String storedHash = cursor.getString(0);
            String hashedPassword = password;

            Log.d("LoginDebug", "Stored Hash: " + storedHash);
            Log.d("LoginDebug", "Entered Hash: " + hashedPassword);

            cursor.close();
            db.close();

            return storedHash.equals(hashedPassword);
        }

        cursor.close();
        db.close();
        return false;
    }

    public String encryptPassword(String password) {
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

    public List<Cycle> getAllCycles() {
        List<Cycle> cycleList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM cycles", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String description = cursor.getString(2);
                String type = cursor.getString(3);
                String branch = cursor.getString(4);
                int availability = cursor.getInt(5);
                double price = cursor.getDouble(6);
                byte[] image = cursor.getBlob(7);

                Cycle cycle = new Cycle(id, name, description, type, branch, price, availability, image);
                cycleList.add(cycle);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return cycleList;
    }


    public int getAvailableCount(int cycleId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT availability FROM cycles WHERE id = ?", new String[]{String.valueOf(cycleId)});

        int availableCount = 0;
        if (cursor.moveToFirst()) {
            availableCount = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return availableCount;
    }






    public List<Rental> getFutureRentals(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String currentDate = getCurrentDate();
        String currentTime = getCurrentTime();
        List<Rental> rentals = new ArrayList<>();

        Log.d("DBHelper", "🔍 Fetching future rentals for User ID: " + userId);
        Log.d("DBHelper", "🔍 Current Date: " + currentDate + " | Time: " + currentTime);

        Cursor cursor = db.rawQuery(
                "SELECT 'day' AS rentalType, d.id, d.cycle_id, c.name, c.type, c.image, d.start_date, d.end_date, d.count, d.total_price " +
                        "FROM day_rent d INNER JOIN cycles c ON d.cycle_id = c.id " +
                        "WHERE d.user_id = ? " +
                        "AND d.start_date > ? " +
                        "UNION ALL " +
                        "SELECT 'time' AS rentalType, t.id, t.cycle_id, c.name, c.type, c.image, t.start_time, t.end_time, t.count, t.total_price " +
                        "FROM time_rent t INNER JOIN cycles c ON t.cycle_id = c.id " +
                        "WHERE t.user_id = ? " +
                        "AND (t.start_time > ? OR DATE(t.booking_datetime) > ?) " +
                        "ORDER BY rentalType DESC, start_date ASC, start_time ASC",
                new String[]{String.valueOf(userId), currentDate, String.valueOf(userId), currentTime, currentDate});

        while (cursor.moveToNext()) {
            String rentalType = cursor.getString(0);
            int id = cursor.getInt(1);
            int cycleId = cursor.getInt(2);
            String name = cursor.getString(3);
            String type = cursor.getString(4);
            byte[] image = cursor.getBlob(5);
            String start = cursor.getString(6);
            String end = cursor.getString(7);
            int count = cursor.getInt(8);
            double totalPrice = cursor.getDouble(9);

            boolean isTimeBased = rentalType.equals("time");
            boolean isDayBased = rentalType.equals("day");

            rentals.add(new Rental(id, cycleId, name, type, image, start, end, count, totalPrice, isTimeBased, isDayBased));

            Log.d("DBHelper", "✅ Future Rental Added: " + name + " | Start: " + start + " | End: " + end);
        }

        cursor.close();
        db.close();
        return rentals;
    }




















    public String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }









    public boolean deleteDayRental(int rentalId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete("day_rent", "id = ?", new String[]{String.valueOf(rentalId)});
        db.close();
        return rowsAffected > 0;
    }


    public boolean deleteTimeRental(int rentalId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete("time_rent", "id = ?", new String[]{String.valueOf(rentalId)});
        db.close();
        return rowsAffected > 0;
    }











    public List<Rental> getOngoingRentals(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String currentDate = getCurrentDate();  // e.g., "2025-03-13"
        String currentDateTime = getCurrentDateTime();  // e.g., "2025-03-13 20:00:33"
        List<Rental> rentals = new ArrayList<>();

        Log.d("DBHelper", "🔍 Fetching ongoing rentals for User ID: " + userId);
        Log.d("DBHelper", "🔍 Current Date for Filtering: " + currentDate);
        Log.d("DBHelper", "🔍 Current DateTime for Filtering: " + currentDateTime);


        Cursor dayCursor = db.rawQuery(
                "SELECT d.id, d.cycle_id, c.name, c.type, c.image, d.start_date, " +
                        "d.end_date || ' 23:59:59' AS end_datetime, d.count, d.total_price " +
                        "FROM day_rent d INNER JOIN cycles c ON d.cycle_id = c.id " +
                        "WHERE d.user_id = ? " +
                        "AND date(d.start_date) <= date(?) " +
                        "AND date(d.end_date) >= date(?) " +
                        "ORDER BY d.end_date ASC",
                new String[]{String.valueOf(userId), currentDate, currentDate});

        int dayRentCount = 0;
        while (dayCursor.moveToNext()) {
            int id = dayCursor.getInt(0);
            int cycleId = dayCursor.getInt(1);
            String name = dayCursor.getString(2);
            String type = dayCursor.getString(3);
            byte[] image = dayCursor.getBlob(4);
            String start = dayCursor.getString(5);
            String end = dayCursor.getString(6);
            int count = dayCursor.getInt(7);
            double totalPrice = dayCursor.getDouble(8);

            rentals.add(new Rental(id, cycleId, name, type, image, start, end, count, totalPrice, false, true));
            dayRentCount++;
            Log.d("DBHelper", " Ongoing Day Rental: " + name + " | End: " + end);
        }
        dayCursor.close();
        Log.d("DBHelper", " Total Ongoing Day Rentals Fetched: " + dayRentCount);


        Cursor timeCursor = db.rawQuery(
                "SELECT t.id, t.cycle_id, c.name, c.type, c.image, " +
                        "t.start_time, " +
                        "(?) || ' ' || t.end_time AS end_datetime, " +
                        "t.count, t.total_price " +
                        "FROM time_rent t INNER JOIN cycles c ON t.cycle_id = c.id " +
                        "WHERE t.user_id = ? " +
                        "AND datetime((?) || ' ' || t.start_time) <= datetime(?) " +
                        "AND datetime((?) || ' ' || t.end_time) > datetime(?) " +
                        "ORDER BY t.end_time ASC",
                new String[]{currentDate, String.valueOf(userId), currentDate, currentDateTime, currentDate, currentDateTime});

        int timeRentCount = 0;
        while (timeCursor.moveToNext()) {
            int id = timeCursor.getInt(0);
            int cycleId = timeCursor.getInt(1);
            String name = timeCursor.getString(2);
            String type = timeCursor.getString(3);
            byte[] image = timeCursor.getBlob(4);
            String start = timeCursor.getString(5);
            String end = timeCursor.getString(6);
            int count = timeCursor.getInt(7);
            double totalPrice = timeCursor.getDouble(8);

            rentals.add(new Rental(id, cycleId, name, type, image, start, end, count, totalPrice, true, false));
            timeRentCount++;
            Log.d("DBHelper", " Ongoing Time Rental: " + name + " | End: " + end);
        }
        timeCursor.close();
        Log.d("DBHelper", " Total Ongoing Time Rentals Fetched: " + timeRentCount);

        db.close();
        return rentals;
    }







    public List<Rental> getPastRentals(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Rental> rentals = new ArrayList<>();

        Log.d("DBHelper", "🔍 Fetching past rentals for User ID: " + userId);


        Cursor dayCursor = db.rawQuery(
                "SELECT d.id, d.cycle_id, c.name, c.type, c.image, d.start_date, d.end_date, d.count, d.total_price " +
                        "FROM day_rent d INNER JOIN cycles c ON d.cycle_id = c.id " +
                        "WHERE d.user_id = ? " +
                        "AND d.restored = 1 " +
                        "ORDER BY d.end_date DESC",
                new String[]{String.valueOf(userId)});

        while (dayCursor.moveToNext()) {
            int id = dayCursor.getInt(0);
            int cycleId = dayCursor.getInt(1);
            String name = dayCursor.getString(2);
            String type = dayCursor.getString(3);
            byte[] image = dayCursor.getBlob(4);
            String start = dayCursor.getString(5);
            String end = dayCursor.getString(6);
            int count = dayCursor.getInt(7);
            double totalPrice = dayCursor.getDouble(8);

            rentals.add(new Rental(id, cycleId, name, type, image, start, end, count, totalPrice, false, true));
            Log.d("DBHelper", " Past Day Rental: " + name + " | End: " + end);
        }
        dayCursor.close();


        Cursor timeCursor = db.rawQuery(
                "SELECT t.id, t.cycle_id, c.name, c.type, c.image, t.start_time, t.end_time, t.count, t.total_price " +
                        "FROM time_rent t INNER JOIN cycles c ON t.cycle_id = c.id " +
                        "WHERE t.user_id = ? " +
                        "AND t.restored = 1 " +
                        "ORDER BY t.end_time DESC",
                new String[]{String.valueOf(userId)});

        while (timeCursor.moveToNext()) {
            int id = timeCursor.getInt(0);
            int cycleId = timeCursor.getInt(1);
            String name = timeCursor.getString(2);
            String type = timeCursor.getString(3);
            byte[] image = timeCursor.getBlob(4);
            String start = timeCursor.getString(5);
            String end = timeCursor.getString(6);
            int count = timeCursor.getInt(7);
            double totalPrice = timeCursor.getDouble(8);

            rentals.add(new Rental(id, cycleId, name, type, image, start, end, count, totalPrice, true, false));
            Log.d("DBHelper", " Past Time Rental: " + name + " | End: " + end);
        }
        timeCursor.close();
        db.close();
        return rentals;
    }




    public UserModel getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        UserModel user = null;

        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE id = ?", new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            user = new UserModel(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getBlob(9)
            );
        }
        cursor.close();
        db.close();
        return user;
    }



}
