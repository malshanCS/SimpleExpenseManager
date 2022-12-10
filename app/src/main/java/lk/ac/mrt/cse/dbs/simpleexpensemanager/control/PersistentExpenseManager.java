package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.AccountDAOMod;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.TransactionDAOMod;


public class PersistentExpenseManager extends ExpenseManager{
    Context context;


    public PersistentExpenseManager(Context context) throws ExpenseManagerException {
        this.context = context;
        setup();
    }

    @Override
    public void setup() throws ExpenseManagerException {
        //setup the transactionDAOMod
        TransactionDAO transactionDAOMod = TransactionDAOMod.getInstance(context);
        setTransactionsDAO(transactionDAOMod);

        //setup account
        AccountDAO accountDAOMod = AccountDAOMod.getInstance(context);
        setAccountsDAO(accountDAOMod);
    }
}
