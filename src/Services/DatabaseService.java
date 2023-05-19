package Services;

import java.sql.*;
import java.util.Objects;

public class DatabaseService {
    static Connection connection;
    static  Statement statement;
    static ResultSet resultSet;

    public void setConnection(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/banking","root","root");
            statement = connection.createStatement();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        try {
            statement.close();
            connection.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addClient(int account, String name, String DOB, String Pan, int balance, String pass) {
        try {
            // Insert into client table
            String clientQuery = "INSERT INTO client VALUES (?, ?, ?, ?, ?)";
            PreparedStatement clientStatement = connection.prepareStatement(clientQuery);
            clientStatement.setInt(1, account);
            clientStatement.setString(2, name);
            clientStatement.setString(3, DOB);
            clientStatement.setString(4, Pan);
            clientStatement.setInt(5, balance);
            clientStatement.execute();

            // Insert into account table
            String accountQuery = "INSERT INTO account VALUES (?, ?)";
            PreparedStatement accountStatement = connection.prepareStatement(accountQuery);
            accountStatement.setInt(1, account);
            accountStatement.setString(2, pass);
            accountStatement.execute();

            // Close the prepared statements
            clientStatement.close();
            accountStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getInfo(int account) {
        try {
            String clientQuery = "SELECT * FROM CLIENT WHERE accountNo=?";
            PreparedStatement clientStatement = connection.prepareStatement(clientQuery);
            clientStatement.setInt(1,account);
            resultSet = clientStatement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            System.out.println("Account\t\tName\t\t\tDOB\t\tPanCard\tBalance");

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(resultSet.getString(i) + "\t");
                }
                System.out.println(); // New line after printing each row
            }

            resultSet.close();
            clientStatement.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean validateClient(int account, String pass){
        try{
            resultSet = statement.executeQuery("select * from account");
            while(resultSet.next()){
                if(resultSet.getInt(1)==account && Objects.equals(resultSet.getString(2), pass)){
                    return true;
                }
            }

            resultSet.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void deposit(int account, int amount){
        try{
            String clientQuery = "update client set balance= balance+? where accountNo = ?;";
            PreparedStatement clientStatement = connection.prepareStatement(clientQuery);
            clientStatement.setInt(1,amount);
            clientStatement.setInt(2,account);
            clientStatement.execute();

            getInfo(account);

            clientStatement.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void withdraw(int account, int amount){
        try{
            String clientQuery = "update client set balance= balance-? where accountNo = ?;";
            PreparedStatement clientStatement = connection.prepareStatement(clientQuery);
            clientStatement.setInt(1,amount);
            clientStatement.setInt(2,account);
            clientStatement.execute();

            getInfo(account);

            clientStatement.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
