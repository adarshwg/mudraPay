package org.example;
import org.example.Services.TransactionService;
import org.example.Services.UserService;
import org.example.Services.WalletService;
import org.example.models.ConnectDB;
import org.example.models.User;
import org.example.utils.Exceptions;

import java.sql.*;

public class Main {
    public static ConnectDB db = new ConnectDB();
    public static Connection connection = db.getConnection();
    public  static  void createTables()  {
        try {
            Statement statement = connection.createStatement();

//            String createTable = "CREATE TABLE IF NOT EXISTS user (username TEXT NOT NULL,  password TEXT NOT NULL, mudraPin TEXT NOT NULL)";
//            String createTable = "CREATE TABLE  IF NOT EXISTS wallet (username TEXT NOT NULL,  balance INTEGER NOT NULL)";

            String createTable = """
            CREATE TABLE IF NOT EXISTS transactions (
                transaction_id TEXT PRIMARY KEY NOT NULL,
                sender TEXT NOT NULL,
                receiver TEXT NOT NULL,
                amount INTEGER NOT NULL,
                epochTime BIGINT NOT NULL
                )""";
            statement.executeUpdate(createTable);
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
    }
    public static void main(String[] args) throws SQLException, Exceptions.UserNotFoundException, Exceptions.InvalidCredentials, Exceptions.DatabaseException, Exceptions.LowBalanceException {
        try {
            createTables();
           TransactionService ts = new TransactionService();
           WalletService ws = new WalletService(ts);
           UserService us = new UserService(ws);
           User ad = us.getUser("ad123");
           User dv= us.getUser("dv123");
           User aman= us.getUser("aman123");
           ws.sendMoney(aman,ad,1);
            System.out.println( ts.getContactTransactions("ad123","aman123",3,2025));
            System.out.println(ts.getRecentContacts("ad123",2));
            System.out.println(ts.getTransactions("ad123",10,1));
            us.updatePin("ad123","111000","111000");
            us.updatePassword("ad123","Ad123@","Ad123@@");
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
    }

}

