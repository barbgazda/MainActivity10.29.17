/*
 * This program is influenced and patterned after the Udacity Inventory App
 */
package com.example.android.mainactivity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mainactivity.Data.InvContract;

public class InvCursorAdapter extends CursorAdapter {

    public InvCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }




    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.name);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        TextView priceTextView = view.findViewById(R.id.price);
        ImageView itemImageView = view.findViewById(R.id.list_item_picture);
        //TextView itemEmailView = view.findViewById(R.id.edit_email);
        Button saleButton = view.findViewById(R.id.sale_button);

        // Find the columns of we're interested in
        int idColumnIndex = cursor.getColumnIndex(InvContract.ItemEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(InvContract.ItemEntry.COLUMN_INVENTORY_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InvContract.ItemEntry.COLUMN_INVENTORY_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InvContract.ItemEntry.COLUMN_INVENTORY_PRICE);
        int imageColumnIndex = cursor.getColumnIndex(InvContract.ItemEntry.COLUMN_ITEM_IMAGE);
        //int emailColumnIndex = cursor.getColumnIndex(InvContract.ItemEntry.COLUMN_SUPPLIER_EMAIL);

        // Read the item attributes
        String itemName = cursor.getString(nameColumnIndex);
        final int itemQuantity = cursor.getInt(quantityColumnIndex);
        String itemPrice = cursor.getString(priceColumnIndex);
        String itemImage = cursor.getString(imageColumnIndex);
        //String itemEmail = cursor.getString(emailColumnIndex);

        // If the inventory name is empty string or null, then use some default text
        // that says "Unknown name", so the TextView isn't blank.
        if (TextUtils.isEmpty(itemName)) {
            itemName = context.getString(R.string.unknown_inv);
        }

        // Update the TextViews with the attributes for the current item
        nameTextView.setText(itemName);
        quantityTextView.setText(Integer.toString(itemQuantity));
        priceTextView.setText(itemPrice);
        //itemEmailView.setText(itemEmail);
        itemImageView.setImageURI(Uri.parse(itemImage));
        final int productId = cursor.getInt(idColumnIndex);



        // Set a clickListener on sale button
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri currentItemUri = ContentUris.withAppendedId(InvContract.ItemEntry.CONTENT_URI, productId);
                itemSold(view, itemQuantity, currentItemUri);
            }
        });
    }

    private void itemSold(View view, int item_quantity, Uri uri) {

        if (item_quantity > 0) {
            item_quantity--;

            ContentValues values = new ContentValues();
            values.put(InvContract.ItemEntry.COLUMN_INVENTORY_QUANTITY, item_quantity);
            mContext.getContentResolver().update(uri, values, null, null);
        } else {
            Toast.makeText(view.getContext(), "You are out of stock on this item", Toast.LENGTH_SHORT).show();
        }
    }
}
