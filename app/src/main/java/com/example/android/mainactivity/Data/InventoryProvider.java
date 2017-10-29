/*
 * This program is influenced and patterned after the Udacity Inventory App
 */
package com.example.android.mainactivity.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;


/**
 * {@link ContentProvider} for Inventory app.
 */
public class InventoryProvider extends ContentProvider {

    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    private static final int INVENTORY = 100;

    private static final int INVENTORY_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(InvContract.CONTENT_AUTHORITY, InvContract.PATH_INVENTORY, INVENTORY);
        sUriMatcher.addURI(InvContract.CONTENT_AUTHORITY, InvContract.PATH_INVENTORY + "/#", INVENTORY_ID);
    }

    private InvDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InvDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                cursor = database.query(InvContract.ItemEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case INVENTORY_ID:
                selection = InvContract.ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InvContract.ItemEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }
    //sale button error
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertInventory(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    private Uri insertInventory(Uri uri, ContentValues values) {
        String name = values.getAsString(InvContract.ItemEntry.COLUMN_INVENTORY_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Inventory requires a name");
        }

        String imageUri = values.getAsString(InvContract.ItemEntry.COLUMN_ITEM_IMAGE);
        if (imageUri == null) {
            throw new IllegalArgumentException("Inventory requires a picture ");
        }

        Integer quantity = values.getAsInteger(InvContract.ItemEntry.COLUMN_INVENTORY_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Inventory requires valid number");
        }

        Double price = values.getAsDouble(InvContract.ItemEntry.COLUMN_INVENTORY_PRICE);
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Inventory requires valid price");
        }
        // Check that the supplier email address is not null
        String email = values.getAsString(InvContract.ItemEntry.COLUMN_SUPPLIER_EMAIL);
        if (email == null) {
            throw new IllegalArgumentException("Product requires a valid email");
        }


        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(InvContract.ItemEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return update(uri, contentValues, selection, selectionArgs);
            case INVENTORY_ID:
                selection = InvContract.ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link InvContract.ItemEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(InvContract.ItemEntry.COLUMN_INVENTORY_NAME)) {
            String name = values.getAsString(InvContract.ItemEntry.COLUMN_INVENTORY_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Inventory requires a name");
            }
        }
        if (values.containsKey(InvContract.ItemEntry.COLUMN_ITEM_IMAGE)) {
            String imageUri = values.getAsString(InvContract.ItemEntry.COLUMN_ITEM_IMAGE);
            if (imageUri == null) {
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
            }
        }
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

                // Otherwise, get writeable database to update the data
                SQLiteDatabase database = mDbHelper.getWritableDatabase();
                int rowsUpdated = database.update(InvContract.ItemEntry.TABLE_NAME, values, selection, selectionArgs);

                // If 1 or more rows were updated, then notify all listeners that the data at the
                // given URI has changed
                if (rowsUpdated != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                // Return the number of rows updated
                return rowsUpdated;
            }


        @Override
        public int delete (Uri uri, String selection, String[]selectionArgs){
            // Get writeable database
            SQLiteDatabase database = mDbHelper.getWritableDatabase();
            int rowsDeleted;

            final int match = sUriMatcher.match(uri);
            switch (match) {
                case INVENTORY:
                    // Delete all rows that match the selection and selection args
                    rowsDeleted = database.delete(InvContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
                    break;
                case INVENTORY_ID:
                    // Delete a single row given by the ID in the URI
                    selection = InvContract.ItemEntry._ID + "=?";
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                    rowsDeleted = database.delete(InvContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
                    break;
                default:
                    throw new IllegalArgumentException("Deletion is not supported for " + uri);
            }

            // If 1 or more rows were deleted, then notify all listeners that the data at the
            // given URI has changed
            if (rowsDeleted != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }

            // Return the number of rows deleted
            return rowsDeleted;
        }

        @Override
        public String getType (Uri uri){
            final int match = sUriMatcher.match(uri);
            switch (match) {
                case INVENTORY:
                    return InvContract.ItemEntry.CONTENT_LIST_TYPE;
                case INVENTORY_ID:
                    return InvContract.ItemEntry.CONTENT_ITEM_TYPE;
                default:
                    throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
            }
        }
    }



