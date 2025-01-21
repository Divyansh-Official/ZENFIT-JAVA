package com.stream.zenfit.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.stream.zenfit.Modal.ChatBotModal;

import java.util.ArrayList;
import java.util.List;

public class ChatBotSQLiteDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ChatBot.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "chat_messages";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SENDER = "sender";
    private static final String COLUMN_MESSAGE = "message";

    public ChatBotSQLiteDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SENDER + " TEXT, " +
                COLUMN_MESSAGE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertMessage(String sender, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SENDER, sender);
        values.put(COLUMN_MESSAGE, message);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<ChatBotModal> getAllMessages() {
        List<ChatBotModal> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COLUMN_ID + " ASC");

        if (cursor.moveToFirst()) {
            do {
                String sender = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SENDER));
                String message = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE));
                messages.add(new ChatBotModal(sender, message));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return messages;
    }

    public void clearAllMessages() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null); // Deletes all rows
        db.close();
    }

    public List<String> searchMessages(String query) {
        List<String> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Perform a search query using LIKE for partial matches
        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{COLUMN_MESSAGE},
                COLUMN_MESSAGE + " LIKE ?",
                new String[]{"%" + query + "%"},
                null,
                null,
                COLUMN_ID + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                results.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return results;
    }

}

