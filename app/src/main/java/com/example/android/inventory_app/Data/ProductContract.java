package com.example.android.inventory_app.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by mr on 19-03-2017.
 */

public class ProductContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.inventory_app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCT = "product";
    private ProductContract() {
    }
    public static final class ProductEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCT);
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;
        public static final String TABLE_NAME = "products";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_PRODUCT_PRICE = "product_price";
        public static final String COLUMN_PRODUCT_QUANTITY = "product_quantity";
        public static final String COLUMN_PRODUCT_COMPANY = "product_company";
        public final static String COLUMN_PRODUCT_IMAGE = "product_image";
    }
}
