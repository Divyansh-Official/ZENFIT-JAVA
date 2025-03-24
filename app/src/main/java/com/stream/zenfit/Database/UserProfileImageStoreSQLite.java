package com.stream.zenfit.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserProfileImageStoreSQLite extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UserProfileImageDB";
    private static final String TABLE_USER_PROFILE_IMAGE = "UserProfileImage";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_IMAGE_URI = "imageUri";

    public UserProfileImageStoreSQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_PROFILE_IMAGE_TABLE = "CREATE TABLE " + TABLE_USER_PROFILE_IMAGE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_IMAGE_URI + " TEXT" + ")";
        db.execSQL(CREATE_USER_PROFILE_IMAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_PROFILE_IMAGE);
        onCreate(db);
    }

    public void addOrUpdateUserProfileImage(String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE_URI, imageUri);

        db.insertWithOnConflict(TABLE_USER_PROFILE_IMAGE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public String getUserProfileImage() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER_PROFILE_IMAGE, new String[]{COLUMN_IMAGE_URI}, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String imageUri = cursor.getString(0);
            cursor.close();
            return imageUri;
        }

        return null;
    }
}
