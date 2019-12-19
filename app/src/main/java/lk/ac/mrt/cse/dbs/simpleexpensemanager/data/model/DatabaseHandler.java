package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {
    //information of database
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "170337E";

    //table names
    private static final String ACCOUNT = "account";
    private static final String TRANSACTION = "trancation";

    //account table column names
    private static final String BANK_NAME = "bank_name";
    private static final String ACCOUNT_HOLDER_NAME = "account_holder_name";
    private static final String BALANCE = "balance";

    //common column names
    private static final String ACCOUNT_NO = "account_no";

    //transaction table column names
    private static final String EXPENSE_TYPE = "expense_type";
    private static final String AMOUNT = "amount";
    private static final String DATE = "date";

    //account table create statement
    private static final String CREATE_TABLE_ACCOUNT = "CREATE TABLE " + ACCOUNT + "(" +
            ACCOUNT_NO + "String PRIMARY KEY," +
            BANK_NAME + "String," +
            ACCOUNT_HOLDER_NAME + "String," +
            BALANCE + "double);";


    //transaction table create statement
    private static final String CREATE_TABLE_TRANSACTION = "CREATE TABLE " + TRANSACTION + "(" +
            ACCOUNT_NO + "String," +
            EXPENSE_TYPE + "String," +
            AMOUNT + "double" +
            DATE + "DATETIME," +
            "FOREIGN KEY(" + ACCOUNT_NO + ") REFERENCES " + ACCOUNT + "(" + ACCOUNT_NO + "));";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_ACCOUNT);
        db.execSQL(CREATE_TABLE_TRANSACTION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT);
        db.execSQL(("DROP TABLE IF EXISTS " + TRANSACTION));

        //create new tables
        onCreate(db);
    }

    //insert new account to database
    public void insertAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();   // get writable database as we want to write data

        ContentValues values = new ContentValues();  //if there are autoincrement values ,auto add them
        values.put(ACCOUNT_NO, account.getAccountNo());
        values.put(BANK_NAME, account.getBankName());
        values.put(ACCOUNT_HOLDER_NAME, account.getAccountHolderName());
        values.put(BALANCE, account.getBalance());

        db.insert(ACCOUNT, null, values);  //insert row

        db.close();
    }


    //show account related to given account no
    public Account showAccount(String accountNo) {
        SQLiteDatabase db = this.getReadableDatabase();  //get readable database as we are not inserting anything

        Cursor cursor = db.query(ACCOUNT,
                new String[]{ACCOUNT_NO, BANK_NAME, ACCOUNT_HOLDER_NAME, BALANCE},
                ACCOUNT_NO + "=?",
                new String[]{accountNo}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        //prepare account object
        Account account = new Account(
                cursor.getString(cursor.getColumnIndex(ACCOUNT_NO)),
                cursor.getString(cursor.getColumnIndex(BANK_NAME)),
                cursor.getString(cursor.getColumnIndex(ACCOUNT_HOLDER_NAME)),
                cursor.getDouble(cursor.getColumnIndex(BALANCE)));

        cursor.close();

        return account;
    }


    //show all the accounts
    public List<Account> showAccountList() {
        List<Account> accounts = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + ACCOUNT + " ORDER BY " +
                ACCOUNT_NO + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Account acc = new Account(cursor.getString(cursor.getColumnIndex(ACCOUNT_NO)),
                        cursor.getString(cursor.getColumnIndex(BANK_NAME)),
                        cursor.getString(cursor.getColumnIndex(ACCOUNT_HOLDER_NAME)),
                        cursor.getDouble(cursor.getColumnIndex(BALANCE)));

                accounts.add(acc);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return accounts;
    }


    //show all the account numbers
    public List<String> showAccountNumberList() {
        List<String> accountnumbers = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT " + ACCOUNT_NO + " FROM " + ACCOUNT + " ORDER BY " +
                ACCOUNT_NO + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String accnum = cursor.getString(cursor.getColumnIndex(ACCOUNT_NO));
                accountnumbers.add(accnum);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return accountnumbers;
    }


    //delete the account
    public void deleteAccount(String accountNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ACCOUNT, ACCOUNT_NO + " = ?",
                new String[]{accountNo});
        db.close();
    }


    //update the balance
    public void updateDetails(String accountNo, ExpenseType expenseType, double amount) {
        Account account = showAccount(accountNo);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        switch (expenseType) {
            case EXPENSE:
                values.put(BALANCE, (Double.toString(Double.valueOf(account.getBalance()) - amount)));
                break;
            case INCOME:
                values.put(BALANCE, (Double.toString(Double.valueOf(account.getBalance()) + amount)));
                break;
        }
        db.update(ACCOUNT, values, ACCOUNT_NO + " = ?", new String[]{accountNo});
    }

    // transaction logs
    public void logTransaction(String date, String accNo, String expenceType, String amount){
        ContentValues values = new ContentValues();
        values.put(DATE,date);
        values.put(ACCOUNT_NO,accNo);
        values.put(EXPENSE_TYPE,expenceType);
        values.put(AMOUNT,amount);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TRANSACTION, null,values);
        db.close();
    }

    //get all transaction logs
    public List getAllTransactionLogs() throws ParseException {
        List transactions = new LinkedList<>();
        String query = "SELECT * FROM " + TRANSACTION;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            String accNo = cursor.getString(0);
            String date = cursor.getString(1);
            String exType = cursor.getString(2);
            double amount = cursor.getDouble(3);
            ExpenseType expenseType = ExpenseType.valueOf(exType);

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
            Date date1 =formatter.parse(date);
            calendar.setTime(date1);
            Date date2 = calendar.getTime();

            Transaction transaction = new Transaction(date2,accNo,expenseType,amount);
            transactions.add(transaction);
        }
        return transactions;
    }

    //get limited transaction logs
    public List getPaginatedTransactionLogs(int limit) throws ParseException {
        List transactions = new LinkedList<>();
        String query = "SELECT*FROM " + TRANSACTION + " LIMIT " + String.valueOf(limit) ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            String accNo = cursor.getString(0);
            String date = cursor.getString(1);
            String exType = cursor.getString(2);
            double amount = cursor.getDouble(3);
            ExpenseType expenseType = ExpenseType.valueOf(exType);

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
            Date date1 =formatter.parse(date);
            calendar.setTime(date1);
            Date date2 = calendar.getTime();

            Transaction transaction = new Transaction(date2,accNo,expenseType,amount);
            transactions.add(transaction);
        }
        return transactions;
    }

}

