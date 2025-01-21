package com.stream.zenfit.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UserProfileImageStoreSQLite extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "UserProfileImage.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "MY_PROFILE";
    private static final String COLUMN_IMAGE_NUMBER = "ID";
    private static final String COLUMN_IMAGE_UPLOAD_DATE = "UPLOAD_DATE";
    private static final String COLUMN_IMAGE = "IMAGE";

    public UserProfileImageStoreSQLite(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        receiveImageBitmap();

        String query = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_IMAGE_NUMBER + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_IMAGE_UPLOAD_DATE + " TEXT, " + COLUMN_IMAGE + " TEXT);";
        db.execSQL(query);
    }

    private void receiveImageBitmap() {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addImage(String uri) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        String currentDate = dateFormat.format(calendar.getTime());

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

//        Uri uriImage = Uri.parse(uri);

        contentValues.put(COLUMN_IMAGE_UPLOAD_DATE, currentDate);
        contentValues.put(COLUMN_IMAGE, uri);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1)
        {
            Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context, "Upload Successful", Toast.LENGTH_SHORT).show();
        }
    }
}
