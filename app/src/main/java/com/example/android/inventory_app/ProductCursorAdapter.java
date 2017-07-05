package com.example.android.inventory_app;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CursorAdapter;
import com.example.android.inventory_app.Data.ProductContract.ProductEntry;
/**
 * Created by mr on 19-03-2017.
 */

public class ProductCursorAdapter extends CursorAdapter {
    public ProductCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView companyTextView = (TextView) view.findViewById(R.id.company);
        final int product_Id = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry._ID));

        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int companyColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_COMPANY);

        String name_Product = cursor.getString(nameColumnIndex);
        final int price_Product = cursor.getInt(priceColumnIndex);
        final int quantity_Product = cursor.getInt(quantityColumnIndex);
        String company_Product = cursor.getString(companyColumnIndex);

        Button sellButton = (Button) view.findViewById(R.id.sell);
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity_left = quantity_Product;
                if (quantity_left <= 0) {
                    Toast.makeText(context, "Product is not available", Toast.LENGTH_SHORT).show();
                } else {
                    quantity_left--;
                }
                ContentValues contentValues = new ContentValues();
                contentValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity_left);
                Uri uri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, product_Id);
                context.getContentResolver().update(uri, contentValues, null, null);
                quantityTextView.setText(quantity_Product + " Products is available");
            }
        });

        nameTextView.setText("Product Name is " + name_Product);
        priceTextView.setText("Price is Rs." + price_Product);
        quantityTextView.setText("Products available = "+quantity_Product);
        companyTextView.setText("Company Name = "+  company_Product);
    }
}
