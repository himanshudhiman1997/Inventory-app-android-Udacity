package com.example.android.inventory_app.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventory_app.Data.ProductContract.ProductEntry;
/**
 * Created by mr on 19-03-2017.
 */

public class ProductHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "product.db";
    private static final int DATABASE_VERSION = 4;
    public ProductHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " (" + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " + ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProductEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, " + ProductEntry.COLUMN_PRODUCT_IMAGE + " TEXT, " + ProductEntry.COLUMN_PRODUCT_COMPANY + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DELETE_TABLE = "DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME + ";";
        db.execSQL(DELETE_TABLE);
        onCreate(db);
    }
}
