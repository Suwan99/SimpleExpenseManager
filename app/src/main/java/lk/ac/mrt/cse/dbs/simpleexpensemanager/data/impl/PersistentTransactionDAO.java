package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO extends DataBaseHelper implements TransactionDAO {
    private List<Transaction> transactions;

    public PersistentTransactionDAO(Context context) {
        super(context);
        this.transactions = new ArrayList<Transaction>();
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        SimpleDateFormat formatter = new SimpleDateFormat("dd,MM,yyyy");
        String strDate= formatter.format(date);


        cv.put("date", strDate);
        cv.put("accountNo", accountNo);
        cv.put("amount", amount);
        cv.put("type", String.valueOf(expenseType));

        db.insert("transactions", null, cv);

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        this.transactions = new ArrayList<Transaction>();

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from transactions";

        Cursor cursor = db.rawQuery(sql,null);

        if (cursor.moveToFirst()){
            do{
                String strdate = cursor.getString(1);
                String accountNo = cursor.getString(2);
                String type = cursor.getString(3);
                double amount = cursor.getDouble(4);

                SimpleDateFormat formatter = new SimpleDateFormat("dd,MM,yyyy");
                Date transactionDate = null;
                try {
                    transactionDate = formatter.parse(strdate);
                } catch (ParseException e) {
                }

                ExpenseType expenseType = ExpenseType.valueOf(type.toUpperCase());

                Transaction acc = new Transaction(transactionDate,accountNo,expenseType,amount);
                transactions.add(acc);


            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return this.transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        transactions = getAllTransactionLogs();
        int size = transactions.size();

        if (size <= limit) {
            return transactions;
        }else{
            return transactions.subList(size - limit, size);
        }
    }
}