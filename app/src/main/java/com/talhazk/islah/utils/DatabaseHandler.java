package com.talhazk.islah.utils;

/**
 * Created by Talhazk on 25-Mar-16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.talhazk.islah.model.Category;
import com.talhazk.islah.model.Favorities;
import com.talhazk.islah.model.FavoritiesList;
import com.talhazk.islah.model.Ayats;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "com.talhazk.islah";

    // Contacts table name
    private static final String TABLE_FAVORITES = "favorites";
    private static final String TABLE_TRENDING = "trending";
    private static final String TABLE_CATEGORIES = "categories";

    // Contacts Table Columns names
    private static final String KEY_TABLE_ID = "tableid";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LINK = "link";
    private static final String KEY_USE = "use";
    private static final String KEY_COUNT_HINDI = "countHindi";
    private static final String KEY_COUNT_ENGLISH = "countEnglish";
    private static final String KEY_UPCOMING = "upcoming";
    private static final String KEY_STATUS = "status";
    Context context;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_FAVORITES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_LINK + " TEXT," + KEY_USE + " TEXT" + ")";

        String CREATE_TRENDING_TABLE = "CREATE TABLE " + TABLE_TRENDING + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_LINK + " TEXT" + ")";

        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES
                + "(" + KEY_TABLE_ID + " INTEGER," + KEY_ID
                + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_LINK
                + " TEXT," + KEY_COUNT_HINDI + " TEXT," + KEY_UPCOMING
                + " INTEGER," + KEY_STATUS + " TEXT," + KEY_COUNT_ENGLISH
                + " TEXT" + ")";

        db.execSQL(CREATE_FAVORITES_TABLE);
        db.execSQL(CREATE_TRENDING_TABLE);
        db.execSQL(CREATE_CATEGORIES_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact

    public void addFavourite(Ayats fvrt) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, fvrt.getAyatId());
        values.put(KEY_NAME, fvrt.getAyatTitle()); // Contact Name
        values.put(KEY_LINK, fvrt.getIslahAudio());
        values.put(KEY_USE, "1"); // Contact Phone
        db.insert(TABLE_FAVORITES, null, values); // Inserting Row
        db.close(); // Closing database connection

        getAllFavorities();
    }

/*
    public void addCategory(Categories cat, int seq) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_TABLE_ID, seq);
        values.put(KEY_ID, cat.getId());
        values.put(KEY_NAME, cat.getMfvrt()); // Contact Name
        values.put(KEY_LINK, cat.getLink());
        values.put(KEY_COUNT_HINDI, cat.getCountHindi());
        values.put(KEY_COUNT_ENGLISH, cat.getCountEnglish());
        values.put(KEY_UPCOMING, cat.getUpComing());
        values.put(KEY_STATUS, cat.getStatus());

        db.insert(TABLE_CATEGORIES, null, values); // Inserting Row
        db.close(); // Closing database connection

    }

    public void addTrending(Dialogues trend) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, trend.getId());
        values.put(KEY_NAME, trend.getDialogue()); // Contact Name
        values.put(KEY_LINK, trend.getLink());
        db.insert(TABLE_TRENDING, null, values); // Inserting
        // Row
        db.close(); // Closing database connection
    }

*/
    public void getAllFavorities() {

        FavoritiesList.get().clearFavorities();
        String selectQuery = "SELECT  * FROM " + TABLE_FAVORITES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                FavoritiesList.get().addFavorities(
                        new Favorities(cursor.getInt(0), cursor.getString(1),
                                cursor.getString(2)));

            } while (cursor.moveToNext());
        }
        db.close();
    }
/*

    public void getAllCategories() {

        CategoriesList.get().clearCategories();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORIES
                + " ORDER BY " + KEY_TABLE_ID + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                CategoriesList.get().addCategories(
                        new Categories(cursor.getInt(1), cursor.getString(2),
                                cursor.getString(3), cursor.getInt(4), cursor
                                .getInt(5), cursor.getString(6), cursor
                                .getInt(7)));
            } while (cursor.moveToNext());
        }
        db.close();
    }
*/

   /* public ArrayList<Ayats> getAllTrendings() {

        String selectQuery = "SELECT  * FROM " + TABLE_TRENDING;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Ayats> dialoguesList = new ArrayList<Ayats>();
        if (cursor.moveToFirst()) {
            do {
                dialoguesList.add(new Dialogues(cursor.getInt(0), cursor
                        .getString(1), cursor.getString(2)));
            } while (cursor.moveToNext());
        }
        db.close();
        return dialoguesList;

    }
*/
    public void deleteFrndList() {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_FAVORITES, null, null);
        db.close();
    }

    public void deleteCategories() {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_CATEGORIES, null, null);
        db.close();
    }

    public boolean checkFvrt(int fvrt) {

        String selectQuery = "SELECT  * FROM " + TABLE_FAVORITES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {

            do {

                if (fvrt == cursor.getInt(0)) {
                    db.close();
                    return true;

                }
            } while (cursor.moveToNext());
            db.close();
            return false;
        }
        db.close();
        return false;
    }

    public boolean removeFvrt(int fvrt) {

        SQLiteDatabase db = this.getWritableDatabase();
        boolean flag = db.delete(TABLE_FAVORITES, KEY_ID + "=" + fvrt, null) > 0;
        getAllFavorities();
        db.close();
        return flag;
    }

}
