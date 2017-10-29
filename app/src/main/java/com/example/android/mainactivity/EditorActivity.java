/*
 * This program is influenced and patterned after the Udacity Inventory App_BASG
 */
package com.example.android.mainactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.android.mainactivity.Data.InvContract;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static android.util.Log.e;
import static java.lang.Double.parseDouble;


/**
 * Allows user to create a new inventory or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {


    public static final String LOG_TAG = EditorActivity.class.getSimpleName();

    private static final int EXISTING_INVENTORY_LOADER = 0;
    static final int PICK_IMAGE_REQUEST = 1;

    private static final String STATE_URI = "STATE_URI";
    private Uri mCurrentInventoryUri;
    private Uri mItemUri;               //camera usage to take pic of item
    private EditText mNameEditText;     //name of item being added
    private EditText mPriceEditText;    //price of item
    public Button mMinusButton;         // removing inventory
    public Button mPlusButton;          //adding inventory
    public Button mPlaceOrder;
    private boolean minventoryHasChanged = false;
    public ImageView mImageView;       //listview image of item being in stock
    public ImageButton mImageButton;    //camera icon
    public EditText mstartQuantity;
    public EditText mEmailAddress;
    public Button mSalesButton;         //completing sale


    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the minventoryHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            minventoryHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);



        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new inventory or editing an existing one.
        Intent intent = getIntent();
        mCurrentInventoryUri = intent.getData();

        // If the intent DOES NOT contain a inventory content URI, then we know that we are
        // creating a new inventory.
        if (mCurrentInventoryUri == null) {
            // This is a new inventory, so change the app bar to say "Add a inventory"
            setTitle(getString(R.string.editor_activity_title_new_inventory));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a inventory that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing inventory, so change app bar to say "Edit inventory"
            setTitle(getString(R.string.editor_activity_title_edit_inventory));

            // Initialize a loader to read the inventory data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_item_name);

        mPriceEditText = (EditText) findViewById(R.id.edit_price);
        mImageButton = (ImageButton) findViewById(R.id.camera_image);
        mMinusButton = (Button) findViewById(R.id.button_minus);
        mPlusButton = (Button) findViewById(R.id.button_plus);
        mPlaceOrder = (Button) findViewById(R.id.button_order);
        mImageView = (ImageView)findViewById(R.id.editor_item_picture);
        mstartQuantity = (EditText)findViewById(R.id.edit_view_quantity);
        mEmailAddress = (EditText)findViewById(R.id.edit_email);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mImageButton.setOnTouchListener(mTouchListener);
        mMinusButton.setOnTouchListener(mTouchListener);
        mPlusButton.setOnTouchListener(mTouchListener);
        mPlaceOrder.setOnTouchListener(mTouchListener);
        mstartQuantity.setOnTouchListener(mTouchListener);
        mImageView.setOnTouchListener(mTouchListener);

        mPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //stackoverflow https://stackoverflow.com/posts/4903531/edit
                  //tracking the change in inventory
                try {

                    int newQuantity;

                    int quantityEntered = Integer.parseInt(mstartQuantity.getText().toString());
                    if (mstartQuantity.getText().toString().trim().length() >= 0) {
                        newQuantity = quantityEntered + 1;
                        mstartQuantity.setText(String.valueOf(newQuantity));
                        String toastMessage = "Quanty to be ordered  " + newQuantity;
                        Toast.makeText(view.getContext(), toastMessage, Toast.LENGTH_LONG).show();
                    } else {
                        String toastMessage = "Please enter a valid number";
                        Toast.makeText(view.getContext(), toastMessage, Toast.LENGTH_LONG).show();
                    }

                } catch (NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }
            }
        });
        mMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    int newQuantity;

                    int quantityEntered = Integer.parseInt(mstartQuantity.getText().toString());
                    if (mstartQuantity.getText().toString().trim().length() >= 0) {
                        newQuantity = quantityEntered - 1;
                        mstartQuantity.setText(String.valueOf(newQuantity));
                        String toastMessage = "Quanty to be ordered  " + newQuantity;
                        Toast.makeText(view.getContext(), toastMessage, Toast.LENGTH_LONG).show();
                    } else {
                        String toastMessage = "Please enter a valid number";
                        Toast.makeText(view.getContext(), toastMessage, Toast.LENGTH_LONG).show();
                    }

                } catch (NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }
            }
        });
        //patterned after Just Java program
        mPlaceOrder.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                String orderNeeded = mNameEditText.getText().toString().trim();
                String quantitiyNeeded = mstartQuantity.getText().toString().trim();
                String message = "Bill Me, \n Thank You  \n Barb Gazda \n Account Number:  3805";


                Intent intent=new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + mEmailAddress.getText().toString().trim()));
                intent.putExtra(Intent.EXTRA_SUBJECT,"Inventory Needed ");
                intent.putExtra(Intent.EXTRA_TEXT, " Item Needed: " + orderNeeded + "  \n" + "Quantity Needed: " + quantitiyNeeded +  "  \n" + message );

                if(intent.resolveActivity(getPackageManager())!=null){
                    startActivity(intent);

                  }}});
        //https://developer.android.com/training/camera/photobasics.html
            mImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openImageSelector();
                     }
            });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        if (mItemUri != null)
            outState.putString(STATE_URI, mItemUri.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(STATE_URI) &&
                !savedInstanceState.getString(STATE_URI).equals("")) {
            mItemUri = Uri.parse(savedInstanceState.getString(STATE_URI));


            ViewTreeObserver viewTreeObserver = mImageView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mImageView.setImageBitmap(getBitmapFromUri(mItemUri));
                }
            });
        }
    }

    public void openImageSelector() {

        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"

            if (resultData != null) {
                mItemUri = resultData.getData();
                Log.i(LOG_TAG, "Uri: " + mItemUri.toString());

                //mTextView.setText(mUri.toString());
                mImageView.setImageBitmap(getBitmapFromUri(mItemUri));
            }

        }
    }

    public Bitmap getBitmapFromUri(Uri uri) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }










    /**
     * Get user input from editor and save inventory into database.
     */
     private boolean saveInventory() {
        // Read frosm input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mstartQuantity.getText().toString().trim();

        // Check if this is supposed to be a new inventory
        // and check if all the fields in the editor are blank
        if (mCurrentInventoryUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString)) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return true;
        } else if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, R.string.editor_nameNeeded,
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, R.string.editor_quantityNeeded,
                    Toast.LENGTH_SHORT).show();
            return false;

        } else if (TextUtils.isEmpty(priceString) || parseDouble(priceString) <= 0)  {
            Toast.makeText(this, R.string.editor_priceNeeded,
                    Toast.LENGTH_SHORT).show();
            return false;
        //} else if (mImageViewUri == null) {
          //  Toast.makeText(this, R.string.editor_pictureNeeded,
            //        Toast.LENGTH_SHORT).show();
            //return true; //change back to false


        }else {

            e(LOG_TAG, "Problem saving item.");
        }



        // Create a ContentValues object where column names are the keys,
        // and inventory attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(InvContract.ItemEntry.COLUMN_INVENTORY_NAME, nameString);
        values.put(InvContract.ItemEntry.COLUMN_INVENTORY_PRICE, priceString);
        values.put(InvContract.ItemEntry.COLUMN_INVENTORY_PRICE, quantityString);


        // Determine if this is a new or existing inventory by checking if mCurrentinventoryUri is null or not
        if (mCurrentInventoryUri == null) {
            // This is a NEW inventory, so insert a new inventory into the provider,
            // returning the content URI for the new inventory.
            Uri newUri = getContentResolver().insert(InvContract.ItemEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_suceessful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING inventory, so update the inventory with content URI: mCurrentinventoryUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentinventoryUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentInventoryUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_suceessful),
                        Toast.LENGTH_SHORT).show();
            }
        }
         return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new inventory, hide the "Delete" menu item.
        if (mCurrentInventoryUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save inventory to database
                saveInventory();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the inventory hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!minventoryHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the inventory hasn't changed, continue with handling back button press
        if (!minventoryHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all inventory attributes, define a projection that contains
        // all columns from the inventory table
        String[] projection = {
                InvContract.ItemEntry._ID,
                InvContract.ItemEntry.COLUMN_INVENTORY_NAME,
                InvContract.ItemEntry.COLUMN_INVENTORY_QUANTITY,
                InvContract.ItemEntry.COLUMN_INVENTORY_PRICE,};
                // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentInventoryUri,         // Query the content URI for the current inventory
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of inventory attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InvContract.ItemEntry.COLUMN_INVENTORY_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(InvContract.ItemEntry.COLUMN_INVENTORY_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(InvContract.ItemEntry.COLUMN_INVENTORY_PRICE);
            int imageColumnIndex = cursor.getColumnIndex(InvContract.ItemEntry.COLUMN_ITEM_IMAGE);
            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            String image = cursor.getString(imageColumnIndex);
            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            //mQuantityEditText.setText(Integer.toString(quantity));
            mPriceEditText.setText(Integer.toString(price));
            mstartQuantity.setText(Integer.toString(quantity));
            mItemUri = Uri.parse(image);
            mImageView.setImageURI(mItemUri);
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        //mQuantityEditText.setText("");
        mPriceEditText.setText("");
    }
    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the inventory.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    /**
     * Prompt the user to confirm that they want to delete this inventory.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the inventory.
                deleteinventory();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the inventory.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    /**
     * Perform the deletion of the inventory in the database.
     */
    private void deleteinventory() {
        // Only perform the delete if this is an existing inventory.
        if (mCurrentInventoryUri != null) {
            // Call the ContentResolver to delete the inventory at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentinventoryUri
            // content URI already identifies the inventory that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentInventoryUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this,
                        getString(R.string.editor_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_suceessful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * The following code was taken from the Android Develpers website.
     * https://developer.android.com/training/camera/photobasics.html
     */



    }
