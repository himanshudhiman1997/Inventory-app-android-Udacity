package com.example.android.inventory_app;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.app.AlertDialog;
import com.example.android.inventory_app.Data.ProductContract.ProductEntry;
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ProductCursorAdapter mAdapter;
    public static final int PRODUCT_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button delete = (Button) findViewById(R.id.deleteData);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialogBox();
            }
        });
        ListView listView = (ListView) findViewById(R.id.list_view);
        View emptyTextView = findViewById(R.id.empty);
        listView.setEmptyView(emptyTextView);
        Button addButton = (Button) findViewById(R.id.addData);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(addIntent);
            }
        });
        mAdapter = new ProductCursorAdapter(this, null);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                Intent intent1 = new Intent(MainActivity.this, EditorActivity.class);
                Uri productUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                intent1.setData(productUri);
                startActivity(intent1);
            }
        });
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {ProductEntry._ID, ProductEntry.COLUMN_PRODUCT_NAME, ProductEntry.COLUMN_PRODUCT_PRICE, ProductEntry.COLUMN_PRODUCT_QUANTITY, ProductEntry.COLUMN_PRODUCT_COMPANY};
        return new CursorLoader(this, ProductEntry.CONTENT_URI, projection, null, null, null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
    private void showDeleteConfirmationDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog);
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
    private void deleteProduct() {
        getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
    }
}
