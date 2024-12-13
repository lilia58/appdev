package com.example.myapplication7;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelperApp extends SQLiteOpenHelper {

    // Nom de la base de données
    private static final String DATABASE_NAME = "app_database";
    private static final int DATABASE_VERSION = 1;

    // Noms des tables
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_PRODUCTS = "products";
    private static final String TABLE_CART = "cart";

    // Colonnes de la table Categories
    private static final String CATEGORY_ID = "id";
    private static final String CATEGORY_NAME = "name";

    // Colonnes de la table Products
    private static final String PRODUCT_ID = "id";
    private static final String PRODUCT_NAME = "name";
    private static final String PRODUCT_PRICE = "price";
    private static final String PRODUCT_CATEGORY_ID = "category_id";  // clé étrangère vers categories

    // Colonnes de la table Cart
    private static final String CART_ID = "id";
    private static final String CART_PRODUCT_ID = "product_id";
    private static final String CART_QUANTITY = "quantity";

    // Constructeur de la classe DatabaseHelper
    public DatabaseHelperApp(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Création des tables lors de l'installation de la base de données
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CATEGORY_NAME + " TEXT NOT NULL)";

        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PRODUCT_NAME + " TEXT NOT NULL, "
                + PRODUCT_PRICE + " REAL, "
                + PRODUCT_CATEGORY_ID + " INTEGER, "
                + "FOREIGN KEY (" + PRODUCT_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + CATEGORY_ID + "))";

        String CREATE_CART_TABLE = "CREATE TABLE " + TABLE_CART + "("
                + CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CART_PRODUCT_ID + " INTEGER, "
                + CART_QUANTITY + " INTEGER, "
                + "FOREIGN KEY (" + CART_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + PRODUCT_ID + "))";

        db.execSQL(CREATE_CATEGORIES_TABLE);
        db.execSQL(CREATE_PRODUCTS_TABLE);
        db.execSQL(CREATE_CART_TABLE);
    }

    // Méthode pour mettre à jour la base de données (en cas de changement de version)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Supprimer les tables existantes si elles existent et recréer les nouvelles
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }

    // Méthode pour insérer une catégorie
    public void insertCategory(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CATEGORY_NAME, name);
        db.insert(TABLE_CATEGORIES, null, values);
        db.close();
    }

    // Méthode pour insérer un produit
    public void insertProduct(String name, double price, int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PRODUCT_NAME, name);
        values.put(PRODUCT_PRICE, price);
        values.put(PRODUCT_CATEGORY_ID, categoryId);
        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }

    // Méthode pour ajouter un produit au panier
    public void addToCart(int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CART_PRODUCT_ID, productId);
        values.put(CART_QUANTITY, quantity);
        db.insert(TABLE_CART, null, values);
        db.close();
    }

    // Méthode pour récupérer les produits dans le panier
    public Cursor getCartItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT p." + PRODUCT_NAME + ", p." + PRODUCT_PRICE + ", c." + CART_QUANTITY + " FROM " + TABLE_CART + " c "
                + "JOIN " + TABLE_PRODUCTS + " p ON c." + CART_PRODUCT_ID + " = p." + PRODUCT_ID;
        return db.rawQuery(query, null);
    }

    // Méthode pour récupérer les catégories
    public Cursor getCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CATEGORIES, new String[]{CATEGORY_ID, CATEGORY_NAME}, null, null, null, null, null);
    }

    // Méthode pour récupérer les produits par catégorie
    public Cursor getProductsByCategory(int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = PRODUCT_CATEGORY_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(categoryId)};
        return db.query(TABLE_PRODUCTS, new String[]{PRODUCT_ID, PRODUCT_NAME, PRODUCT_PRICE},
                selection, selectionArgs, null, null, null);
    }

    // Méthode pour supprimer un produit du panier
    public void removeFromCart(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, CART_PRODUCT_ID + " = ?", new String[]{String.valueOf(productId)});
        db.close();
    }

    // Méthode pour vider le panier
    public void clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, null, null);
        db.close();
    }
}
