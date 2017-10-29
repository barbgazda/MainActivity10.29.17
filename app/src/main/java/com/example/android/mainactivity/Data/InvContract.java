/*
 * This program is influenced and patterned after the Udacity Inventory App
 */
package com.example.android.mainactivity.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Inventory app.
 */
public final class InvContract  {

    private InvContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.android.inventory_app_10142017";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_INVENTORY = "inventory";

    public static final class ItemEntry implements BaseColumns {


        public static final Uri CONTENT_URI =Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /** Name of database table for Inventorys */
        public final static String TABLE_NAME = "Items";
        public final static String _ID = BaseColumns._ID;
        public static final String COLUMN_ITEM_IMAGE = "image";
        public final static String COLUMN_INVENTORY_NAME ="name";
        public final static String COLUMN_INVENTORY_QUANTITY = "quantity";
        public final static String COLUMN_INVENTORY_PRICE = "price";
        public final static String COLUMN_SUPPLIER_EMAIL = "email";



    }

}

