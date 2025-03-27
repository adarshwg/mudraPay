package org.example.Services;

import org.example.DataAccess.DataAccess;
import org.example.models.ConnectDB;
import org.example.models.Transaction;
import org.example.models.User;
import org.example.utils.Exceptions.*;
import org.example.utils.Validators;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Objects;

public class WalletService {
    private final Connection conn;
    public TransactionService transactionService;

    public WalletService(TransactionService transactionService) {
        this.conn = new ConnectDB().getConnection();
        this.transactionService = transactionService;
    }

    public int getBalance(User user) throws SQLException, UserNotFoundException {
        if (!Validators.checkUsernameFormat(user.getUsername())) {
            throw new IllegalArgumentException("Invalid username format.");
        }

        try {
            ArrayList<ArrayList<Object>> queryResList =
                    DataAccess.executeQuery(this.conn, "wallet", "balance", "username=?", user.getUsername());

            int walletBalance = (int) queryResList.getFirst().getFirst();
            return walletBalance;
        } catch (NoSuchElementException e) {
            throw new UserNotFoundException("User was not found");
        }
    }

    private void updateUserBalance(User user, int amount, boolean hasReceivedAmount)
            throws SQLException, LowBalanceException, DatabaseException, UserNotFoundException {

        if (!Validators.checkUsernameFormat(user.getUsername())) {
            throw new IllegalArgumentException("Invalid username format.");
        }

        try {
            int walletBalance = getBalance(user);
            if (!hasReceivedAmount) {
                amount *= -1;
                if (amount > walletBalance) {
                    throw new LowBalanceException("Wallet balance is low for the transaction");
                }
            }

            DataAccess.executeUpdate(this.conn, "wallet", "update",
                    "set balance=?", "username=?", amount + walletBalance, user.getUsername());

        } catch (SQLException e) {
            conn.rollback();
            throw new DatabaseException("Internal Server Error!");
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public Transaction sendMoney(User user, User receiver, int amount)
            throws SQLException, LowBalanceException, UserNotFoundException, DatabaseException, InvalidAmountException, SelfTransferException {

        if (!Validators.checkUsernameFormat(user.getUsername()) ||
                !Validators.checkUsernameFormat(receiver.getUsername())||
                Objects.equals(user.getUsername(), receiver.getUsername())
        ) {
            throw new SelfTransferException("Cannot transfer to your own account!.");
        }
        if(amount<=0){
            throw new InvalidAmountException("Invalid amount entered!");
        }
        try {
            conn.setAutoCommit(false);
            updateUserBalance(user, amount, false);
            updateUserBalance(receiver, amount, true);
            Transaction newTransaction = this.transactionService.createTransaction(user.getUsername(), receiver.getUsername(), amount);
            conn.commit();
            conn.setAutoCommit(true);
            return newTransaction;
        } catch (SQLException e) {
            throw new DatabaseException("Internal Server Error!");
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
