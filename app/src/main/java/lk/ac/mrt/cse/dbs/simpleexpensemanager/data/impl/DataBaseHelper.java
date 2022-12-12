package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context) {
        super(context, "200580E.db", null,  1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createAccQuery = "CREATE TABLE accounts(accountNo TEXT PRIMARY KEY, bankName TEXT, accountHolderName TEXT, balance REAL);";
        db.execSQL(createAccQuery);

        String createTransactionQuery = "CREATE TABLE transactions(transactionId INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, accountNo TEXT, type TEXT, amount REAL, FOREIGN KEY (accountNo) REFERENCES accounts(accountNo));";
        db.execSQL(createTransactionQuery);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}