/*
 * This program is influenced and patterned after the Udacity Inventory App
 */
package com.example.android.mainactivity.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper for Inventory app. Manages database creation and version management.
 */
public class InvDbHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "item.db";
    private static final int DATABASE_VERSION = 1;


    public InvDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ITEM_TABLE =  "CREATE TABLE " + InvContract.ItemEntry.TABLE_NAME + " ("
                + InvContract.ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InvContract.ItemEntry.COLUMN_INVENTORY_NAME + " TEXT NOT NULL, "
                + InvContract.ItemEntry.COLUMN_INVENTORY_QUANTITY + " INTEGER NOT NULL, "
                + InvContract.ItemEntry.COLUMN_INVENTORY_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + InvContract.ItemEntry.COLUMN_ITEM_IMAGE + " TEXT NOT NULL, "
                + InvContract.ItemEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL);";
        // Execute the SQL statement
        db.execSQL(SQL_CREATE_ITEM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}