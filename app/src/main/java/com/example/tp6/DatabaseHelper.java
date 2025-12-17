package com.example.tp6;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;

import com.example.tp6.Models.RequestModel;
import com.example.tp6.Models.Company;
import com.example.tp6.Models.UserModel;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "companies.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_COMPANIES = "companies";
    public static final String TABLE_USERS = "users";

    private static final String ALGORITHM = "AES";
    private static final String KEY = "1234567890123456";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "password TEXT," +
                "role TEXT," +
                "email TEXT," +
                "gender TEXT," +
                "phone TEXT," +
                "dateOfBirth TEXT," +
                "place TEXT" +
                ")");

        ContentValues admin = new ContentValues();
        admin.put("name", "admin");
        admin.put("email", "admin@admin.com");
        admin.put("password", encryptPassword("admin123"));
        admin.put("role", "ADMIN");
        admin.put("gender", "");
        admin.put("phone", "");
        admin.put("dateOfBirth", "");
        admin.put("place", "");
        db.insert(TABLE_USERS, null, admin);

        String query = "CREATE TABLE " + TABLE_COMPANIES + " (" +
                "id" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name" + " TEXT, " +
                "services" + " TEXT, " +
                "phone" + " TEXT, " +
                "website" + " TEXT, " +
                "localisation" + " TEXT, " +
                "image" + " TEXT, " +
                "email" + " TEXT)";
        db.execSQL(query);

        String sql = "CREATE TABLE requests (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId INTEGER, " +
                "companyId INTEGER, " +
                "cvPath TEXT, " +
                "status TEXT DEFAULT 'Pending', " +
                "FOREIGN KEY(userId) REFERENCES users(id), " +
                "FOREIGN KEY(companyId) REFERENCES companies(id))";
        db.execSQL(sql);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPANIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public Cursor login(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email=? AND password=?",
                new String[]{email,encryptPassword(password)});
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String hashedPassword = encryptPassword(password);

        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE email=? AND password=?",
                new String[]{email, hashedPassword}
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public Cursor getUsersCursor() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE role='USER' ORDER BY name ASC", null);
    }

    public void addUser(String name, String email, String password, String role,
                        String gender, String phone, String dateOfBirth, String place) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", name);
        cv.put("email", email);
        cv.put("password", encryptPassword(password));
        cv.put("role", role);
        cv.put("gender", gender);
        cv.put("phone", phone);
        cv.put("dateOfBirth", dateOfBirth);
        cv.put("place", place);

        db.insert(TABLE_USERS, null, cv);
        db.close();
    }


    public List<UserModel> getAllUsers() {
        List<UserModel> list = new ArrayList<>();
        Cursor c = getUsersCursor();

        if (c.moveToFirst()) {
            do {
                UserModel u = new UserModel(
                        c.getInt(c.getColumnIndexOrThrow("id")),
                        c.getString(c.getColumnIndexOrThrow("name")),
                        c.getString(c.getColumnIndexOrThrow("password")),
                        c.getString(c.getColumnIndexOrThrow("email")),
                        c.getString(c.getColumnIndexOrThrow("role")),
                        c.getString(c.getColumnIndexOrThrow("gender")),
                        c.getString(c.getColumnIndexOrThrow("phone")),
                        c.getString(c.getColumnIndexOrThrow("dateOfBirth")),
                        c.getString(c.getColumnIndexOrThrow("place"))
                );
                list.add(u);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public void updateUser(UserModel user) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", user.getName());
        cv.put("email", user.getEmail());
        cv.put("password", encryptPassword(user.getPassword()));
        cv.put("role", user.getRole());
        cv.put("gender", user.getGender());
        cv.put("phone", user.getPhone());
        cv.put("dateOfBirth", user.getDateOfBirth());
        cv.put("place", user.getPlace());

        db.update(TABLE_USERS, cv, "id=?", new String[]{String.valueOf(user.getId())});
        db.close();
    }


    public UserModel getUserById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE id=?", new String[]{String.valueOf(id)});

        if (c != null && c.moveToFirst()) {
            UserModel u = new UserModel(
                    c.getInt(c.getColumnIndexOrThrow("id")),
                    c.getString(c.getColumnIndexOrThrow("name")),
                    c.getString(c.getColumnIndexOrThrow("password")),
                    c.getString(c.getColumnIndexOrThrow("email")),
                    c.getString(c.getColumnIndexOrThrow("role")),
                    c.getString(c.getColumnIndexOrThrow("gender")),
                    c.getString(c.getColumnIndexOrThrow("phone")),
                    c.getString(c.getColumnIndexOrThrow("dateOfBirth")),
                    c.getString(c.getColumnIndexOrThrow("place"))
            );
            c.close();
            return u;
        }

        if (c != null) c.close();
        return null;
    }

    public UserModel getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE LOWER(email) = LOWER(?)", new String[]{email});

        if (c != null && c.moveToFirst()) {
            UserModel u = new UserModel(
                    c.getInt(c.getColumnIndexOrThrow("id")),
                    c.getString(c.getColumnIndexOrThrow("name")),
                    c.getString(c.getColumnIndexOrThrow("password")),
                    c.getString(c.getColumnIndexOrThrow("email")),
                    c.getString(c.getColumnIndexOrThrow("role")),
                    c.getString(c.getColumnIndexOrThrow("gender")),
                    c.getString(c.getColumnIndexOrThrow("phone")),
                    c.getString(c.getColumnIndexOrThrow("dateOfBirth")),
                    c.getString(c.getColumnIndexOrThrow("place"))
            );
            c.close();
            return u;
        }

        if (c != null) c.close();
        return null;
    }


    public void deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, "id=?", new String[]{String.valueOf(id)});
        // delete user's requests

        db.close();
    }
    public String encryptPassword(String password) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes("UTF-8"), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(password.getBytes("UTF-8"));
            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String decryptPassword(String encryptedPassword) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes("UTF-8"), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.decode(encryptedPassword, Base64.DEFAULT);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    // Insert company
    public long insertCompany(Company company) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", company.getName());
        values.put("services", company.getServices());
        values.put("phone", company.getPhone());
        values.put("website", company.getWebsite());
        values.put("localisation", company.getLocalisation());
        values.put("image", company.getImageUri());
        values.put("email", company.getEmail());

        return db.insert(TABLE_COMPANIES, null, values);
    }

    // Get all companies
    public ArrayList<Company> getAllCompanies() {
        ArrayList<Company> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_COMPANIES, null);

        if (cursor.moveToFirst()) {
            do {
                Company company = new Company(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7)
                );
                list.add(company);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    public Company getCompanyById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_COMPANIES + " WHERE id=?",
                new String[]{String.valueOf(id)});

        if (c.moveToFirst()) {
            return new Company(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getString(4),
                    c.getString(5),
                    c.getString(6),
                    c.getString(7)
            );
        }
        return null;
    }

    public void updateCompany(Company company) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("name", company.getName());
        v.put("services", company.getServices());
        v.put("phone", company.getPhone());
        v.put("website", company.getWebsite());
        v.put("localisation", company.getLocalisation());
        v.put("image", company.getImageUri());
        v.put("email", company.getEmail());

        db.update(TABLE_COMPANIES, v, "id" + "=?", new String[]{String.valueOf(company.getId())});
    }

    public void deleteCompany(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("companies", "id=?", new String[]{String.valueOf(id)});
    }
    // Ajouter une demande
    public void addRequest(int userId, int companyId, String cvPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("userId", userId);
        cv.put("companyId", companyId);
        cv.put("cvPath", cvPath);
        cv.put("status", "Pending");
        db.insert("requests", null, cv);
        db.close();
    }

    // Récupérer toutes les demandes
    public List<RequestModel> getAllRequests() {
        List<RequestModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM requests", null);
        if (c.moveToFirst()) {
            do {
                RequestModel r = new RequestModel(
                        c.getInt(c.getColumnIndexOrThrow("id")),
                        c.getInt(c.getColumnIndexOrThrow("userId")),
                        c.getInt(c.getColumnIndexOrThrow("companyId")),
                        c.getString(c.getColumnIndexOrThrow("cvPath")),
                        c.getString(c.getColumnIndexOrThrow("status"))
                );
                list.add(r);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public RequestModel getRequestByUserAndCompany(int userId, int companyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM requests WHERE userId=? AND companyId=?",
                new String[]{String.valueOf(userId), String.valueOf(companyId)});
        if(c.moveToFirst()) {
            RequestModel r = new RequestModel(
                    c.getInt(c.getColumnIndexOrThrow("id")),
                    c.getInt(c.getColumnIndexOrThrow("userId")),
                    c.getInt(c.getColumnIndexOrThrow("companyId")),
                    c.getString(c.getColumnIndexOrThrow("cvPath")),
                    c.getString(c.getColumnIndexOrThrow("status"))
            );
            c.close();
            return r;
        }
        c.close();
        return null;
    }

    // Mettre à jour le statut
    public void updateRequestStatus(int requestId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        db.update("requests", cv, "id=?", new String[]{String.valueOf(requestId)});
        db.close();
    }



}

