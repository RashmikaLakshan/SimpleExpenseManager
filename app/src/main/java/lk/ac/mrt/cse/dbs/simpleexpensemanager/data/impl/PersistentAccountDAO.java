package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.DatabaseHandler;


public class PersistentAccountDAO implements AccountDAO {
    private DatabaseHandler db;

    public PersistentAccountDAO(Context context) {
        this.db = new DatabaseHandler(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        return db.showAccountNumberList();
    }

    @Override
    public List<Account> getAccountsList() {
        return db.showAccountList();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        return db.showAccount(accountNo);
    }

    @Override
    public void addAccount(Account account) {
        db.insertAccount(account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        db.deleteAccount(accountNo);

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        db.updateDetails(accountNo ,expenseType ,amount);
    }
}
