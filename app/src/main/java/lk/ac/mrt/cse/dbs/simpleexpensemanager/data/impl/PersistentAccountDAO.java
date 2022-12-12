package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO extends DataBaseHelper implements AccountDAO {
    private List<String> accountNos;
    private List<Account> accounts;

    public PersistentAccountDAO(Context context) {
        super(context);
        this.accountNos = new ArrayList<String>();
        this.accounts = new ArrayList<Account>();
    }

    @Override
    public List<String> getAccountNumbersList() {
        this.accountNos = new ArrayList<String>();

        String sql = "select accountNo from accounts";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql,null);

        if (cursor.moveToFirst()){
            do{
                this.accountNos.add(cursor.getString(0));
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return this.accountNos;
    }

    @Override
    public List<Account> getAccountsList() {
        this.accounts = new ArrayList<Account>();

        String sql = "select * from accounts";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql,null);

        if (cursor.moveToFirst()){
            do{
                String accountNo = cursor.getString(0);
                String bankName = cursor.getString(1);
                String accountHolderName = cursor.getString(2);
                double balance = cursor.getInt(3);

                Account account = new Account(accountNo,bankName,accountHolderName,balance);
                this.accounts.add(account);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return this.accounts;
    }

    @Override
    public Account getAccount(String accountNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from accounts where accountNo = '"+ accountNo + "' ;";

        Cursor cursor = db.rawQuery(sql,null);

        Account account = null;

        String bankName = cursor.getString(1);
        String accountHolderName = cursor.getString(2);
        double balance = cursor.getInt(3);

        account = new Account(accountNo,bankName,accountHolderName,balance);

        cursor.close();
        db.close();
        return account;

    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "select * from accounts where accountNo = '"+ account.getAccountNo() + "' ;";

        Cursor cursor = db.rawQuery(sql,null);
        if(cursor.getCount() == 0) {
            ContentValues cv = new ContentValues();

            cv.put("accountNo", account.getAccountNo());
            cv.put("bankName", account.getBankName());
            cv.put("accountHolderName", account.getAccountHolderName());
            cv.put("balance", account.getBalance());

            db.insert("accounts", null, cv);
        }
        cursor.close();
        db.close();

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        String sql = "delete from accounts where accountNo = '"+ accountNo + "' ;";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "select balance from accounts where accountNo = '"+ accountNo +"' ;";
        Cursor cursor = db.rawQuery(sql,null);
        cursor.moveToFirst();
        double balance = cursor.getDouble(0);

        String type = (String.valueOf(expenseType)).toUpperCase();
        String updateSql = "update accounts set balance = "+ ((type.equals("EXPENSE")) ? balance-amount: balance+amount) +" where accountNo = '"+accountNo+"' ;";
        db.execSQL(updateSql);
        cursor.close();
        db.close();

    }
}