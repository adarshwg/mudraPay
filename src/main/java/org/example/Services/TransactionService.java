package org.example.Services;
import org.example.DataAccess.DataAccess;
import org.example.models.ConnectDB;
import org.example.models.Transaction;
import org.example.utils.Exceptions.*;
import org.example.utils.Utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

public class TransactionService {
    private final Connection conn;

    public TransactionService() {
        this.conn = new ConnectDB().getConnection();
    }

    public Transaction createTransaction(String sender, String receiver, int amount) throws SQLException, DatabaseException {
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
    public ArrayList<Transaction> getTransactions(String username, int n, int mode) throws SQLException, DatabaseException {
        try {
            String order = mode==0?"epochTime":"amount";
            ArrayList<ArrayList<Object>> queryResList = DataAccess.executeQuery(
                    this.conn,
                    "transactions",
                    "*",
                    "(sender=? or receiver=?) order by " + order + " desc limit ?",
                    username, username,n
            );

            // Convert the result into Transaction objects
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

    public ArrayList<Transaction> getTransactionsByMonth(String username, int month, int year) throws SQLException, DatabaseException {
        try {
            long startEpoch = Instant.parse(String.format("%04d-%02d-01T00:00:00Z", year, month)).toEpochMilli();

            // Get end of the month
            int nextMonth = month == 12 ? 1 : month + 1;
            int nextYear = month == 12 ? year + 1 : year;
            long endEpoch = Instant.parse(String.format("%04d-%02d-01T00:00:00Z", nextYear, nextMonth)).toEpochMilli();

            // Query transactions by epochTime range
            ArrayList<ArrayList<Object>> queryResList = DataAccess.executeQuery(
                    this.conn,
                    "transactions",
                    "*",
                    "epochTime >= ? AND epochTime < ? AND (sender = ? OR receiver = ?) ORDER BY epochTime DESC",
                    startEpoch, endEpoch, username, username
            );

            // Convert the result into Transaction objects
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
    public ArrayList<Transaction> getContactTransactions(String username, String contact, int month, int year) throws SQLException,DatabaseException{
        try {
            long startEpoch = Utils.getEpochTimeForMonth(year,month);

            // Get end of the month
            int nextMonth = month == 12 ? 1 : month + 1;
            int nextYear = month == 12 ? year + 1 : year;
            long endEpoch = Utils.getEpochTimeForMonth(nextYear,nextMonth);

            // Query transactions by epochTime range
            ArrayList<ArrayList<Object>> queryResList = DataAccess.executeQuery(
                    this.conn,
                    "transactions",
                    "*",
                    "epochTime >= ? AND epochTime < ? AND (sender IN (?, ?) AND receiver IN (?, ?)) ORDER BY epochTime DESC",
                    startEpoch, endEpoch, username, contact, username, contact
            );

            // Convert the result into Transaction objects
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
    public ArrayList<String> getRecentContacts(String username, int n) throws DatabaseException {
        try{
            ArrayList<ArrayList<Object>> queryResList = DataAccess.executeQuery(
                    this.conn,
                    "transactions",
                    "distinct receiver",
                    "sender=? order by transaction_id desc limit ?",
                    username,n
            );
            ArrayList<String> recentContacts = new ArrayList<>();
            for(ArrayList<Object> contactList:queryResList){
                recentContacts.add((String)contactList.getFirst());
            }
            return recentContacts;
        } catch (SQLException e) {
            throw new DatabaseException("Internal Server error");
        }
    }


}
