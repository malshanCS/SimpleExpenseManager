package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class TransactionDAOMod extends SQLiteOpenHelper implements TransactionDAO {

    private static final String DATABASE_NAME = "200304N.db";
    private static final int DATABASE_VERSION = 1;
    private Context context;
    private static TransactionDAOMod transactionDAOMod;

    public TransactionDAOMod(@Nullable Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static TransactionDAOMod getInstance(Context context){
        if (transactionDAOMod == null){
            TransactionDAOMod transactionDAOMod = new TransactionDAOMod(context);
        }
        return transactionDAOMod;
    }

    private static final String TABLE_NAME = "account";
    private static final String ACCOUNT_NO = "accountNo";
    private static final String AMOUNT = "amount";
    private static final String DATE = "date";
    private static final String EXPENSE_TYPE = "expenseType";



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTransactionTableSQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " " +
                "(" + ACCOUNT_NO + " TEXT, " +
                DATE + " TEXT, " +
                EXPENSE_TYPE + " INT, " +
                AMOUNT + " REAL)";

        sqLiteDatabase.execSQL(createTransactionTableSQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    String pattern = "yyyy-MM-dd";


    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String stringDate= simpleDateFormat.format(date);

        contentValues.put(AMOUNT,amount);
        contentValues.put(ACCOUNT_NO,accountNo);
        contentValues.put(AMOUNT,amount);
        contentValues.put(DATE,stringDate);

        if(expenseType == ExpenseType.EXPENSE){
            contentValues.put(EXPENSE_TYPE, String.valueOf(expenseType));
        }

        db.insert(TABLE_NAME,null,contentValues);
        db.close();


    }
    @SuppressLint("SimpleDateFormat")
    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> allTransactionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + ";", null);

        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = null;
                try {
                    @SuppressLint("SimpleDateFormat") Date simpleDateFormat = new SimpleDateFormat(pattern).parse(cursor.getString(1));
                    transaction = new Transaction(simpleDateFormat, cursor.getString(0), (((cursor.getInt(2) == 1) ? ExpenseType.EXPENSE : ExpenseType.INCOME)), cursor.getDouble(3));
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                allTransactionList.add(transaction);
            } while (cursor.moveToNext());

        }
        cursor.close();
        return allTransactionList;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> allTransactionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " limit " + limit + ";", null);

        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = null;
                try {
                    @SuppressLint("SimpleDateFormat") Date simpleDateFormat = new SimpleDateFormat(pattern).parse(cursor.getString(1));
                    transaction = new Transaction(simpleDateFormat, cursor.getString(0), (((cursor.getInt(2) == 1) ? ExpenseType.EXPENSE : ExpenseType.INCOME)), cursor.getDouble(3));
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                allTransactionList.add(transaction);
            } while (cursor.moveToNext());

        }
        cursor.close();
        return allTransactionList;
    }


}
