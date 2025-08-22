package org.firstinspires.ftc.teamcode.pedroPathing.DataStorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class LinearMultiplierDataBass extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LinearTuner.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "multipliers";

    public LinearMultiplierDataBass(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, value REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    // Insert new multiplier
    public void insertMultiplier(double value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("value", value);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // Get the average of all multipliers
    public double getAverageMultiplier() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT AVG(value) FROM " + TABLE_NAME, null);
        double avg = 0;
        if (cursor.moveToFirst()) {
            avg = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return avg;
    }

    // ✅ Get all multipliers as a List
    public List<Double> getAllMultipliers() {
        List<Double> multipliers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT value FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                multipliers.add(cursor.getDouble(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return multipliers;
    }

    // (Optional) Clear the database
    public void clearMultipliers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}
