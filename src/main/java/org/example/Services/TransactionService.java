package org.example.Services;
import org.example.DataAccess.DataAccess;
import org.example.models.ConnectDB;
import org.example.models.Transaction;
import org.example.utils.Exceptions.*;
import org.example.utils.Utils;
import org.example.utils.Validators;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;

public class TransactionService {
    private final Connection conn;

    public TransactionService() {
        this.conn = new ConnectDB().getConnection();
    }

    public Transaction createTransaction(String sender, String receiver, int amount)
            throws SQLException, DatabaseException {

        if (!Validators.checkUsernameFormat(sender) || !Validators.checkUsernameFormat(receiver)) {
            throw new IllegalArgumentException("Invalid username format.");
        }

        long epochTime = Utils.getCurrentEpochTime();
        String uuid = Utils.getUUID();
        String transactionId = epochTime + ":" + uuid;

        try {
            DataAccess.executeUpdate(this.conn,
                    "transactions",
                    "insert",
                    "(transaction_id, sender, receiver, amount, epochTime) VALUES (?, ?, ?, ?, ?)",
                    "",
                    transactionId, sender, receiver, amount, epochTime);

            return new Transaction(transactionId, sender, receiver, amount, epochTime);

        } catch (SQLException e) {
            throw new DatabaseException("Internal Server Error!");
        }
    }

    public ArrayList<Transaction> getTransactions(String username, int n, int mode)
            throws SQLException, DatabaseException {

        if (!Validators.checkUsernameFormat(username)) {
            throw new IllegalArgumentException("Invalid username format.");
        }

        try {
            String order = mode == 0 ? "epochTime" : "amount";

            ArrayList<ArrayList<Object>> queryResList = DataAccess.executeQuery(
                    this.conn,
                    "transactions",
                    "*",
                    "(sender=? or receiver=?) ORDER BY " + order + " DESC LIMIT ?",
                    username, username, n
            );

            ArrayList<Transaction> transactions = new ArrayList<>();
            for (ArrayList<Object> row : queryResList) {
                String transactionId = (String) row.get(0);
                String sender = (String) row.get(1);
                String receiver = (String) row.get(2);
                int amount = (int) row.get(3);
                long epochTime = (long) row.get(4);

                transactions.add(new Transaction(transactionId, sender, receiver, amount, epochTime));
            }
            return transactions;

        } catch (SQLException e) {
            throw new DatabaseException("Internal Server Error!");
        }
    }

    public ArrayList<Transaction> getTransactionsByMonth(String username, int month, int year)
            throws SQLException, DatabaseException {

        if (!Validators.checkUsernameFormat(username)) {
            throw new IllegalArgumentException("Invalid username format.");
        }

        try {
            long startEpoch = Utils.getEpochTimeForMonth(year, month);
            int nextMonth = month == 12 ? 1 : month + 1;
            int nextYear = month == 12 ? year + 1 : year;
            long endEpoch = Utils.getEpochTimeForMonth(nextYear, nextMonth);

            ArrayList<ArrayList<Object>> queryResList = DataAccess.executeQuery(
                    this.conn,
                    "transactions",
                    "*",
                    "epochTime >= ? AND epochTime < ? AND (sender = ? OR receiver = ?) ORDER BY epochTime DESC",
                    startEpoch, endEpoch, username, username
            );

            ArrayList<Transaction> transactions = new ArrayList<>();
            for (ArrayList<Object> row : queryResList) {
                String transactionId = (String) row.get(0);
                String sender = (String) row.get(1);
                String receiver = (String) row.get(2);
                int amount = (int) row.get(3);
                long epochTime = (long) row.get(4);
                transactions.add(new Transaction(transactionId, sender, receiver, amount, epochTime));
            }
            return transactions;
        } catch (SQLException e) {
            throw new DatabaseException("Internal Server Error!");
        }
    }

    public ArrayList<Transaction> getContactTransactions(String username, String contact, int month, int year)
            throws SQLException, DatabaseException {

        if (!Validators.checkUsernameFormat(username) || !Validators.checkUsernameFormat(contact)) {
            throw new IllegalArgumentException("Invalid username format.");
        }

        try {
            long startEpoch = Utils.getEpochTimeForMonth(year, month);
            int nextMonth = month == 12 ? 1 : month + 1;
            int nextYear = month == 12 ? year + 1 : year;
            long endEpoch = Utils.getEpochTimeForMonth(nextYear, nextMonth);

            ArrayList<ArrayList<Object>> queryResList = DataAccess.executeQuery(
                    this.conn,
                    "transactions",
                    "*",
                    "epochTime >= ? AND epochTime < ? AND (sender IN (?, ?) AND receiver IN (?, ?)) ORDER BY epochTime DESC",
                    startEpoch, endEpoch, username, contact, username, contact
            );

            ArrayList<Transaction> transactions = new ArrayList<>();
            for (ArrayList<Object> row : queryResList) {
                String transactionId = (String) row.get(0);
                String sender = (String) row.get(1);
                String receiver = (String) row.get(2);
                int amount = (int) row.get(3);
                long epochTime = (long) row.get(4);

                transactions.add(new Transaction(transactionId, sender, receiver, amount, epochTime));
            }
            return transactions;

        } catch (SQLException e) {
            throw new DatabaseException("Internal Server Error!");
        }
    }

    public ArrayList<String> getRecentContacts(String username, int n)
            throws DatabaseException {

        if (!Validators.checkUsernameFormat(username)) {
            throw new IllegalArgumentException("Invalid username format.");
        }

        try {
            ArrayList<ArrayList<Object>> queryResList = DataAccess.executeQuery(
                    this.conn,
                    "transactions",
                    "distinct receiver",
                    "sender=? ORDER BY transaction_id DESC LIMIT ?",
                    username, n
            );

            ArrayList<String> recentContacts = new ArrayList<>();
            for (ArrayList<Object> contactList : queryResList) {
                recentContacts.add((String) contactList.getFirst());
            }
            return recentContacts;

        } catch (SQLException e) {
            throw new DatabaseException("Internal Server error");
        }
    }
}
