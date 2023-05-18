package Services;

import java.sql.*;
import java.util.Formatter;

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
            resultSet.close();
            statement.close();
            connection.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getInfo() {
        try {
            resultSet = statement.executeQuery("SELECT * FROM client");
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            System.out.println("Models.Account\tName\tDOB\t\t\tPanCard\tBalance");

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(resultSet.getString(i) + "\t");
                }
                System.out.println(); // New line after printing each row
            }
        }
        catch (Exception e) {
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



}
