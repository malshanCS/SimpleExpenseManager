package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class AccountDAOMod extends SQLiteOpenHelper implements AccountDAO {

    private static final String DATABASE_NAME = "200304N.db";
    private static final int DATABASE_VERSION = 1;
    private Context context;
    private static AccountDAOMod accountDAOMod;





    AccountDAOMod(@Nullable Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
//        onCreate(this.getWritableDatabase());
    }

    public static AccountDAOMod getInstance(Context context){
        if (accountDAOMod == null){
            AccountDAOMod accountDAOMod = new AccountDAOMod(context);
        }
        return accountDAOMod;
    }

    private static final String TABLE_NAME = "account";
    private static final String ACCOUNT_NO = "accountNo";
    private static final String ACCOUNT_HOLDER_NAME = "HolderName";
    private static final String BANK_NAME = "bankName";
    private static final String ACCOUNT_BALANCE = "account_balance";

    @Override
    public List<String> getAccountNumbersList() {

        // create a database for reading our database
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> accNumberList = new ArrayList<>();

        Cursor cursorAccounts = db.rawQuery("SELECT " + ACCOUNT_NO + " FROM " + TABLE_NAME, null);

        if (cursorAccounts.moveToFirst()) {
            do{
                accNumberList.add(cursorAccounts.getString(0));
            }while (cursorAccounts.moveToNext());
        }

        cursorAccounts.close();
        return accNumberList;

    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase db =this.getReadableDatabase();
        Cursor accCursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        List<Account> accountList = new ArrayList<>();

        if (accCursor.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                accountList.add(new Account(accCursor.getString(0),
                        accCursor.getString(1),
                        accCursor.getString(2),
                        accCursor.getDouble(3)));
            } while (accCursor.moveToNext());
            // moving our cursor to next.
        }

        accCursor.close();
        return accountList;
        
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorAcc = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if(cursorAcc.getCount() == 0){
            throw new InvalidAccountException("Invalid Account number. Try again!");
        }

        cursorAcc.close();

        return new Account(cursorAcc.getString(0),cursorAcc.getString(1),cursorAcc.getString(2),cursorAcc.getDouble(3));
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();

        String accNo = account.getAccountNo();
        String accHolder = account.getAccountHolderName();
        String bank = account.getBankName();
        String balance = String.valueOf(account.getBalance());

        ContentValues contentValues = new ContentValues();
        contentValues.put(ACCOUNT_NO, accNo);
        contentValues.put(ACCOUNT_HOLDER_NAME, accHolder);
        contentValues.put(BANK_NAME, bank);
        contentValues.put(ACCOUNT_BALANCE, balance);

        long result = db.insert(TABLE_NAME,null,contentValues);
        if(result == -1){
            Toast.makeText(context,"failed", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Added Successfully!", Toast.LENGTH_SHORT).show();
        }

        db.close();


    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] accNo = new String[]{accountNo};
        db.execSQL("delete from " + TABLE_NAME + " where " + ACCOUNT_NO + " = ?",accNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = this.getWritableDatabase();
        Account acc = getAccount(accountNo);

        double updatedBalance;
        if(ExpenseType.EXPENSE == expenseType){
            updatedBalance = acc.getBalance() - amount;
        }else{
            updatedBalance = acc.getBalance() + amount;
        }

        String[] balance_accNo = new String[]{String.valueOf(updatedBalance),accountNo};
        db.execSQL("update "+ TABLE_NAME + " set " + ACCOUNT_BALANCE + " =?" + " where " + ACCOUNT_NO + " =?",  balance_accNo);



    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "create table if not exists " + TABLE_NAME + " ("
                + ACCOUNT_NO + " TEXT primary key, "
                + ACCOUNT_HOLDER_NAME + " TEXT,"
                + BANK_NAME + " TEXT,"
                + ACCOUNT_BALANCE + " REAL)";

        sqLiteDatabase.execSQL(query);


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
}
