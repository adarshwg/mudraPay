package org.example.Services;
import org.example.DataAccess.DataAccess;
import org.example.models.ConnectDB;
import org.example.models.User;
import org.example.utils.Exceptions.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class WalletService {
    private final Connection conn;
    public TransactionService transactionService;
    public WalletService(TransactionService transactionService) {
        this.conn = new ConnectDB().getConnection();
        this.transactionService = transactionService;
    }

    public int getBalance(User user) throws SQLException, UserNotFoundException {
        try {
            ArrayList<ArrayList<Object>> queryResList =  DataAccess.executeQuery(this.conn,"wallet","balance","username=?",user.getUsername());
            System.out.println(queryResList.toString());
            int walletBalance = (int)queryResList.getFirst().getFirst();
            return walletBalance;
        }
        catch (NoSuchElementException e){
            throw new UserNotFoundException("user was not found");
        }

    }
    private void updateUserBalance(User user, int amount, boolean hasReceivedAmount) throws SQLException, LowBalanceException, DatabaseException, UserNotFoundException {
        try {
            int walletBalance = getBalance(user);
            if(!hasReceivedAmount){
                amount*=-1;
                if(amount>walletBalance) {
                    throw new LowBalanceException("Wallet Balance is low for the transaction");
                }
            }
            DataAccess.executeUpdate(this.conn, "wallet","update","set balance=?","username=?",
                    amount+walletBalance,user.getUsername());
        }catch (SQLException e) {
            conn.rollback();
            throw new DatabaseException("Internal Server Error!");
        } catch (UserNotFoundException e) {
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }


    }
    public void sendMoney(User user,User receiver, int amount) throws SQLException, LowBalanceException, UserNotFoundException, DatabaseException {
        try {
            conn.setAutoCommit(false);
            updateUserBalance(user, amount, false);
            updateUserBalance(receiver, amount, true);
            this.transactionService.createTransaction(user.getUsername(),receiver.getUsername(),amount);
        } catch (SQLException e) {
            throw new DatabaseException("Internal Server Error!");
        } catch (UserNotFoundException e) {
            try{
                conn.rollback();
            }
            catch (SQLException err){
                throw new DatabaseException("Internal Server Error!");
            }
            throw new DatabaseException("Internal Server Error!");
        }
        finally {
            conn.setAutoCommit(true);
        }
    }
}
