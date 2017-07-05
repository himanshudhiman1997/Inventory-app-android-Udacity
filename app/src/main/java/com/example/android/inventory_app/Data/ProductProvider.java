package com.example.android.inventory_app.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.android.inventory_app.Data.ProductContract.ProductEntry;
/**
 * Created by mr on 19-03-2017.
 */

public class ProductProvider extends ContentProvider {
    private ProductHelper mProductHelper;
    private static final int PRODUCTS = 200;
    private static final int PRODUCT_ID = 201;
    private static final UriMatcher muriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        muriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,ProductContract.PATH_PRODUCT,PRODUCTS);
        muriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,ProductContract.PATH_PRODUCT + "/#",PRODUCT_ID);
    }
    @Override
    public boolean onCreate() {
        mProductHelper = new ProductHelper(getContext());
        return true;
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mProductHelper.getReadableDatabase();
        Cursor cursor;
        int match = muriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Error: Here,cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = muriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Error: Here,insertion is not valid for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = muriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Error: Here,update is not supported for " + uri);
        }
    }
    private int updateProduct(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String productName = contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (productName == null) {
                throw new IllegalArgumentException("Error: name of product cannot be empty");
            }
        }
        if (contentValues.containsKey(ProductEntry.COLUMN_PRODUCT_PRICE)) {
            Integer productPrice = contentValues.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
            if (productPrice == null || productPrice < 0) {
                throw new IllegalArgumentException("Error: price of product should be valid");
            }
        }

        if (contentValues.containsKey(ProductEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer productQuantity = contentValues.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (productQuantity != null && productQuantity < 0) {
                throw new IllegalArgumentException("Error: quantity of product should be positive");
            }
        }
        if (contentValues.containsKey(ProductEntry.COLUMN_PRODUCT_IMAGE)) {
            String productImage = contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_IMAGE);
            if (productImage == null)
                throw new IllegalArgumentException("Error: Here,It requires a valid image of product");
        }
        if (contentValues.containsKey(ProductEntry.COLUMN_PRODUCT_COMPANY)){
            String productCompany = contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_COMPANY);
            if (productCompany == null){
                throw new IllegalArgumentException("Error: Here,It requires a valid name of company");
            }
        }
        if (contentValues.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mProductHelper.getWritableDatabase();
        int rowsUpdated = database.update(ProductEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mProductHelper.getWritableDatabase();
        int dataRowsDeleted;
        final int match = muriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                dataRowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                dataRowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Error: Delete feature is not valid for " + uri);
        }
        if (dataRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return dataRowsDeleted;
    }
    @Override
    public String getType(Uri uri) {
        final int match = muriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Error:Unknown URI " + uri + " with match " + match);
        }
    }
    private Uri insertProduct(Uri uri, ContentValues values) {
        String productName = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if (productName == null){
            throw new IllegalArgumentException("Error: Here,product requires a name.");
        }
        Integer productPrice = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
        if (productPrice == null || productPrice < 0) {
            throw new IllegalArgumentException("Error: Here, product requires a valid price");
        }
        Integer productQuantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (productQuantity != null && productQuantity < 0) {
            throw new IllegalArgumentException("Error: Here,product requires a valid quantity");
        }
        String productImage = values.getAsString(ProductEntry.COLUMN_PRODUCT_IMAGE);
        if (productImage == null) {
            throw new IllegalArgumentException("Error: Here,product requires a valid image");
        }
        String productCompany = values.getAsString(ProductEntry.COLUMN_PRODUCT_COMPANY);
        if (productCompany == null){
            throw new IllegalArgumentException("Error: Here ,product requires a company name");
        }
        SQLiteDatabase database = mProductHelper.getWritableDatabase();
        long id = database.insert(ProductEntry.TABLE_NAME, null, values);
        if (id == -1) {
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri, id);
    }
}
