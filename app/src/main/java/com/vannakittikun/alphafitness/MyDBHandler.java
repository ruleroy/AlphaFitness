package com.vannakittikun.alphafitness;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Rule on 11/4/2017.
 */

public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 26;
    private static final String DATABASE_NAME = "locationDB";

    public static final String TABLE_LOCATION = "location";
    public static final String TABLE_USER = "user";
    public static final String TABLE_DETAILS = "details";
    public static final String TABLE_CHART = "chart";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LNG = "lng";
    public static final String COLUMN_TIME = "time";

    public static final String USER_NAME = "name";
    public static final String USER_GENDER = "gender";
    public static final String USER_WEIGHT = "weight";

    public static final String DETAILS_USER_ID = "userID";
    public static final String DETAILS_AVG_DIST = "avgDist";
    public static final String DETAILS_AVG_STEPS = "avgSteps";
    public static final String DETAILS_AVG_TIME = "avgTime";
    public static final String DETAILS_AVG_WORKOUTS = "avgWorkouts";
    public static final String DETAILS_AVG_CALORIES_BURNED = "avgCaloriesBurned";

    public static final String CHART_USER_ID = "userID";
    public static final String CHART_STEPS = "chartSteps";
    public static final String CHART_CALORIES = "chartCalories";
    public static final String CHART_TIME = "chartTime";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_LOCATION + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_LAT + " TEXT," +
                COLUMN_LNG + " TEXT," +
                COLUMN_TIME + " TEXT" +
                ");";
        sqLiteDatabase.execSQL(query);

        String query2 = "CREATE TABLE " + TABLE_USER + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                USER_NAME + " TEXT," +
                USER_GENDER + " TEXT," +
                USER_WEIGHT + " INTEGER" +
                ");";
        sqLiteDatabase.execSQL(query2);

        sqLiteDatabase.execSQL("INSERT INTO " + TABLE_USER + " (name, gender, weight) VALUES ('Name', 'Male', 160)");
        //addUser("Name", "Male", 160);

        String query3 = "CREATE TABLE " + TABLE_DETAILS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DETAILS_USER_ID + " INTEGER," +
                DETAILS_AVG_DIST + " INTEGER DEFAULT 0," +
                DETAILS_AVG_STEPS + " INTEGER DEFAULT 0," +
                DETAILS_AVG_TIME + " INTEGER DEFAULT 0," +
                DETAILS_AVG_WORKOUTS + " INTEGER DEFAULT 0," +
                DETAILS_AVG_CALORIES_BURNED + " INTEGER DEFAULT 0" +
                ");";
        sqLiteDatabase.execSQL(query3);

        String query4 = "CREATE TABLE " + TABLE_CHART + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CHART_USER_ID + " INTEGER," +
                CHART_STEPS + " INTEGER DEFAULT 0," +
                CHART_CALORIES + " INTEGER DEFAULT 0," +
                CHART_TIME + " INTEGER DEFAULT 0" +
                ");";
        sqLiteDatabase.execSQL(query4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_DETAILS);
        onCreate(sqLiteDatabase);
    }

    public void addLocation(double lat, double lng, long time) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_LAT, lat);
        values.put(COLUMN_LNG, lng);
        values.put(COLUMN_TIME, time);

        db.insert(TABLE_LOCATION, null, values);
        //db.close();

    }

    public void addUser(String name, String gender, int weight) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USER_NAME, name);
        values.put(USER_GENDER, gender);
        values.put(USER_WEIGHT, weight);

        db.insert(TABLE_USER, null, values);
        //db.close();

    }

    public void updateUser(int id, String name, String gender, int weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NAME, name);
        values.put(USER_GENDER, gender);
        values.put(USER_WEIGHT, weight);

        db.update(TABLE_USER, values, "_id=" + Integer.toString(id), null);
        //db.close();
    }

    public void newUserDetailsSession(int userid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DETAILS_USER_ID, userid);

        if(userExists(userid)) {
            Log.d("TABLEUPDATE", "INSERT NEW USER DETAILS");
            db.insert(TABLE_DETAILS, null, values);
        } else {

        }

        //db.close();
    }

    public int getCurrentSessionID(){
        int session = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_DETAILS, null);
        c.moveToLast();
        if (c.getCount() > 0) {
            session = c.getInt(c.getColumnIndex(COLUMN_ID));
        }
        return session;
    }

    public void updateUserDetails(int id, int userid, double dist, int steps, long time, int workouts, double cals) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(COLUMN_ID, id);
        values.put(DETAILS_USER_ID, userid);
        values.put(DETAILS_AVG_DIST, dist);
        values.put(DETAILS_AVG_STEPS, steps);
        values.put(DETAILS_AVG_TIME, time);
        values.put(DETAILS_AVG_WORKOUTS, workouts);
        values.put(DETAILS_AVG_CALORIES_BURNED, cals);

        ContentValues values2 = new ContentValues();
        values2.put(CHART_USER_ID, userid);
        values2.put(CHART_STEPS, steps);
        values2.put(CHART_CALORIES, cals);
        values2.put(CHART_TIME, time);

        if(userExists(userid)) {
            //Log.d("DBUPDATE", "updated");
            db.update(TABLE_DETAILS, values, "_id=" + Integer.toString(id), null);
            db.insert(TABLE_CHART, null, values2);
        } else {
            //db.insert(TABLE_DETAILS, null, values);
            //Log.d("DBINSERT", "inserted");
        }

        //db.close();
    }

    public double getAllTimeDistance(int userid) {
        double distance = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(" + DETAILS_AVG_DIST + ")" + " FROM " + TABLE_DETAILS + " WHERE " + DETAILS_USER_ID + "=" + Integer.toString(userid), null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            distance = c.getDouble(0);
        }
        c.close();
        //db.close();
        return distance;
    }

    public int getAllTimeSteps(int userid) {
        int steps = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(" + DETAILS_AVG_STEPS + ")" + " FROM " + TABLE_DETAILS + " WHERE " + DETAILS_USER_ID + "=" + Integer.toString(userid), null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            steps = c.getInt(0);
        }
        c.close();
        //db.close();
        return steps;
    }

    public long getAllTimeTime(int userid) {
        long time = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(" + DETAILS_AVG_TIME + ")" + " FROM " + TABLE_DETAILS + " WHERE " + DETAILS_USER_ID + "=" + Integer.toString(userid), null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            time = c.getLong(0);
        }
        c.close();
        //db.close();
        return time;
    }

    public double getAllTimeCaloriesBurned(int userid) {
        double calories = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(" + DETAILS_AVG_CALORIES_BURNED + ")" + " FROM " + TABLE_DETAILS + " WHERE " + DETAILS_USER_ID + "=" + Integer.toString(userid), null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            calories = c.getDouble(0);
        }
        c.close();
        //db.close();
        return calories;
    }

    public int getAllTimeWorkouts(int userid) {
        int steps = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*)" + " FROM " + TABLE_DETAILS + " WHERE " + DETAILS_USER_ID + "=" + Integer.toString(userid), null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            steps = c.getInt(0);
        }
        c.close();
        //db.close();
        return steps;
    }

    public int getWeeklyWorkouts(int userid) {
        int workouts = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*)" + " FROM " + TABLE_DETAILS + " WHERE " + DETAILS_USER_ID + "=" + Integer.toString(userid), null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            //Log.d("WEEKLY", Double.toString(c.getDouble(0)/7));
            workouts = (int) Math.ceil(c.getDouble(0)/7);
        }
        c.close();
        //db.close();
        return workouts;
    }

    public double getWeeklyCaloriesBurned(int userid) {
        double calories = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT AVG(" + DETAILS_AVG_CALORIES_BURNED + ")" + " FROM " + TABLE_DETAILS + " WHERE " + DETAILS_USER_ID + "=" + Integer.toString(userid), null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            calories = c.getDouble(0);
        }
        c.close();
        //db.close();
        return calories;
    }

    public double getWeeklyDistance(int userid) {
        double distance = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(" + DETAILS_AVG_DIST + ")" + " FROM " + TABLE_DETAILS + " WHERE " + DETAILS_USER_ID + "=" + Integer.toString(userid), null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            distance = c.getDouble(0)/7;
        }
        c.close();
        //db.close();
        return distance;
    }

    public long getWeeklyTime(int userid) {
        long time = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT AVG("+ DETAILS_AVG_TIME + ") FROM " + TABLE_DETAILS + " WHERE " + DETAILS_USER_ID + "=" + Integer.toString(userid), null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            time = c.getLong(0);
        }
        c.close();
        //db.close();
        return time;
    }

    public void updateWeeklyTime(int id, long time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DETAILS_AVG_TIME, time);

            db.update(TABLE_DETAILS, values, "_id=" + Integer.toString(id), null);
        //db.close();
    }

    public void updateWeeklySteps(int id, int steps) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, id);
        values.put(DETAILS_AVG_STEPS, steps);

            db.update(TABLE_DETAILS, values, "_id=" + Integer.toString(id), null);

        //db.close();
    }

    public int getWeeklySteps(int userid) {
        int steps = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT AVG("+ DETAILS_AVG_STEPS + ") FROM " + TABLE_DETAILS + " WHERE " + DETAILS_USER_ID + "=" + Integer.toString(userid), null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            steps = c.getInt(0);
        }
        c.close();
        //db.close();
        return steps;
    }

    public int getCurrentSessionSteps(int id){
        int steps = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_DETAILS + " WHERE " + COLUMN_ID + "=" + Integer.toString(id), null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            steps = c.getInt(c.getColumnIndex(DETAILS_AVG_STEPS));
        }
        c.close();
        //db.close();
        return steps;
    }

    public double getCurrentSessionDistance(int id){
        double dist = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_DETAILS + " WHERE " + COLUMN_ID + "=" + Integer.toString(id), null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            dist = c.getDouble(c.getColumnIndex(DETAILS_AVG_DIST));
        }
        c.close();
        //db.close();
        return dist;
    }

    public long getCurrentSessionTime(int id){
        long time = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_DETAILS + " WHERE " + COLUMN_ID + "=" + Integer.toString(id), null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            time = c.getLong(c.getColumnIndex(DETAILS_AVG_TIME));
        }
        c.close();
        //db.close();
        return time;
    }

    public List<Integer> getChartSteps(int userid){
        List<Integer> steps = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CHART + " WHERE " + CHART_USER_ID + "=" + Integer.toString(userid), null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            steps.add(c.getInt(c.getColumnIndex(CHART_STEPS)));
            c.moveToNext();
        }
        c.close();
        //db.close();
        return steps;
    }

    public List<Double> getChartCalories(int userid){
        List<Double> calories = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CHART + " WHERE " + CHART_USER_ID + "=" + Integer.toString(userid), null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            calories.add(c.getDouble(c.getColumnIndex(CHART_CALORIES)));
            c.moveToNext();
        }
        c.close();
        //db.close();
        return calories;
    }

    public boolean userExists(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_ID + "=\"" + id + "\";";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        //db.close();
        return true;
    }

    public User getUser(int id) {
        User user = new User();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_ID + "=" + Integer.toString(id), null);
        c.moveToFirst();
        if (c.getString(c.getColumnIndex("_id")) != null) {
            user.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
            user.setName(c.getString(c.getColumnIndex(USER_NAME)));
            user.setGender(c.getString(c.getColumnIndex(USER_GENDER)));
            user.setWeight(c.getInt(c.getColumnIndex(USER_WEIGHT)));
        }
        c.close();
        //db.close();
        return user;
    }

    public void deleteAllLocation() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='" + TABLE_LOCATION + "'");
        db.execSQL("DELETE FROM " + TABLE_LOCATION);

        db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='" + TABLE_CHART + "'");
        db.execSQL("DELETE FROM " + TABLE_CHART);
        //db.close();
    }

    public void resetDetails(int userid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=1 WHERE NAME='" + TABLE_DETAILS + "'");
        db.execSQL("DELETE FROM " + TABLE_DETAILS + " WHERE userID=" + userid + ";");

        //db.close();
    }

    public ArrayList<LatLng> getLastWorkoutPath() {
        ArrayList<LatLng> result = new ArrayList<LatLng>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_LOCATION + ";", null);
        c.moveToLast();
        if (c.getCount() > 1) {
            for (int i = 0; i < 2; i++) {
                if (c.getString(c.getColumnIndex("_id")) != null) {
                    LatLng path = new LatLng(c.getDouble(c.getColumnIndex(COLUMN_LAT)), c.getDouble(c.getColumnIndex(COLUMN_LNG)));
                    result.add(path);
                }
                c.moveToPrevious();
            }
        }
        //db.close();
        c.close();
        return result;
    }

    public ArrayList<LatLng> getTotalPath() {
        ArrayList<LatLng> result = new ArrayList<LatLng>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_LOCATION + ";", null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("_id")) != null) {
                LatLng path = new LatLng(c.getDouble(c.getColumnIndex(COLUMN_LAT)), c.getDouble(c.getColumnIndex(COLUMN_LNG)));
                result.add(path);
            }
            c.moveToNext();
        }
        //db.close();
        c.close();
        return result;
    }

    public ArrayList<LocationObject> getSavedLocations() {
        ArrayList<LocationObject> locations = new ArrayList<LocationObject>();
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_LOCATION + ";";
        Cursor c = db.rawQuery(Query, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("_id")) != null) {
                LocationObject newLocationObject = new LocationObject(c.getDouble(c.getColumnIndex(COLUMN_LAT)), c.getDouble(c.getColumnIndex(COLUMN_LNG)), c.getLong(c.getColumnIndex(COLUMN_TIME)));
                locations.add(newLocationObject);
            }
            c.moveToNext();
        }
        //db.close();
        c.close();
        return locations;
    }

    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[]{"message"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);

            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {

                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {
            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }
    }
}
