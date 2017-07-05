package com.example.android.inventory_app;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.android.inventory_app.Data.ProductContract.ProductEntry;
import static android.view.View.GONE;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private EditText productNameEditText;
    private EditText priceEditText;
    private EditText quantityEditText;
    private EditText companyEditText;
    private ImageView imageView;
    private static final int GET_PRODUCT_IMAGE = 1;
    private static int PRODUCT_LOADER = 1;
    String product_image = null;
    private boolean hasProductChanged = false;
    private Uri mProductUri = null;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            hasProductChanged = true;
            return false;
        }
    };

    Uri imageUri;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Intent intent = getIntent();
        mProductUri = intent.getData();
        Button deleteButton = (Button) findViewById(R.id.delete_id);
        if (mProductUri == null) {
            imageUri = Uri.parse("android.resource://" + this.getPackageName() + "/drawable/pic");
            setTitle(getString(R.string.new_product));
            deleteButton.setVisibility(GONE);
        } else {
            setTitle(getString(R.string.edit_product));
            getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        }
        productNameEditText = (EditText) findViewById(R.id.product_edit_text);
        priceEditText = (EditText) findViewById(R.id.price_edit_text);
        quantityEditText = (EditText) findViewById(R.id.quantity_edit_text);
        companyEditText = (EditText) findViewById(R.id.company_edit);
        imageView = (ImageView) findViewById(R.id.product_image);

        productNameEditText.setOnTouchListener(mTouchListener);
        priceEditText.setOnTouchListener(mTouchListener);
        quantityEditText.setOnTouchListener(mTouchListener);
        companyEditText.setOnTouchListener(mTouchListener);

        Button addImage = (Button) findViewById(R.id.add_image);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(Intent.createChooser(intent, "Select Image From"), GET_PRODUCT_IMAGE);
                }
            }
        });
        Button saveData = (Button) findViewById(R.id.save_id);
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProduct();
                finish();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
        Button incrementButton = (Button) findViewById(R.id.inc_button);
        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orderProductQuantity = quantityEditText.getText().toString();
                if(TextUtils.isEmpty(orderProductQuantity))
                {
                    quantityEditText.setText("0");
                }
                int productQuantity = Integer.parseInt(quantityEditText.getText().toString());
                productQuantity++;
                quantityEditText.setText("" + productQuantity);
            }
        });
        Button decrementButton = (Button) findViewById(R.id.dec_button);
        decrementButton.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orderProductQuantity = quantityEditText.getText().toString();
                if(TextUtils.isEmpty(orderProductQuantity))
                {
                    quantityEditText.setText("0");
                }
                int productQuantity = Integer.parseInt(quantityEditText.getText().toString());
                if (productQuantity <= 0) {
                    Toast.makeText(EditorActivity.this, "First Buy some item", Toast.LENGTH_SHORT).show();
                } else {
                    productQuantity--;
                }
                quantityEditText.setText("" + productQuantity);
            }
        });
        final Button orderButton = (Button) findViewById(R.id.order_id);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String orderProductName = productNameEditText.getText().toString();
                String orderProductQuantity = quantityEditText.getText().toString();
                Intent orderProductIntent = new Intent(Intent.ACTION_SENDTO);
                orderProductIntent.setData(Uri.parse("mailto:himanshudhimanhr@gmail.com"));
                orderProductIntent.putExtra(Intent.EXTRA_TEXT, "Please order " + orderProductName + "\n" + "Quantity = " + orderProductQuantity);
                startActivity(orderProductIntent);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_PRODUCT_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            product_image = imageUri.toString();
            imageView.setImageURI(imageUri);
        }
    }
    @Override
    public void onBackPressed() {
        if (!hasProductChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {ProductEntry._ID, ProductEntry.COLUMN_PRODUCT_NAME, ProductEntry.COLUMN_PRODUCT_PRICE, ProductEntry.COLUMN_PRODUCT_QUANTITY, ProductEntry.COLUMN_PRODUCT_IMAGE, ProductEntry.COLUMN_PRODUCT_COMPANY};
        return new CursorLoader(this, mProductUri, projection, null, null, null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }
        if (data.moveToFirst()) {
            int nameProductColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceProductColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityProductColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int companyProductColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_COMPANY);
            int imageProductColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE);

            String nameProduct = data.getString(nameProductColumnIndex);
            int priceProduct = data.getInt(priceProductColumnIndex);
            int quantityProduct = data.getInt(quantityProductColumnIndex);
            String companyProduct = data.getString(companyProductColumnIndex);
            String imageProduct = data.getString(imageProductColumnIndex);

            productNameEditText.setText(nameProduct);
            priceEditText.setText(Integer.toString(priceProduct));
            quantityEditText.setText(Integer.toString(quantityProduct));
            companyEditText.setText(companyProduct);
            imageUri = Uri.parse(imageProduct);
            imageView = (ImageView) findViewById(R.id.product_image);
            imageView.setImageURI(imageUri);
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productNameEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("");
        companyEditText.setText("");
        imageView.setImageResource(R.drawable.thapar);
    }
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_product_dialog);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void saveProduct() {
        String nameProduct = productNameEditText.getText().toString().trim();
        String priceProduct = priceEditText.getText().toString().trim();
        String quantityProduct = quantityEditText.getText().toString().trim();
        String companyProduct = companyEditText.getText().toString().trim();
        String imageProduct = imageUri.toString();

        if (mProductUri == null &&
                TextUtils.isEmpty(nameProduct) || TextUtils.isEmpty(priceProduct) ||
                TextUtils.isEmpty(quantityProduct) || TextUtils.isEmpty(imageProduct) || TextUtils.isEmpty(companyProduct)) {
            Toast.makeText(EditorActivity.this, "Please fill All the Details", Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductEntry.COLUMN_PRODUCT_NAME, nameProduct);
        int productPrice = 0;
        if (!TextUtils.isEmpty(priceProduct)) {
            productPrice = Integer.parseInt(priceProduct);
        }
        contentValues.put(ProductEntry.COLUMN_PRODUCT_PRICE, productPrice);
        int productQuantity = 0;
        if (!TextUtils.isEmpty(quantityProduct)) {
            productQuantity = Integer.parseInt(quantityProduct);
        }
        contentValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_IMAGE, imageProduct);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_COMPANY, companyProduct);

        if (mProductUri == null) {
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, contentValues);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_product_successfully),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mProductUri, contentValues, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_product_successfully),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_edit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteProduct() {
        if (mProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_product_successfully),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
