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

            System.out.println("Account\tName\tDOB\t\t\tPanCard\tBalance");

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(resultSet.getString(i) + "\t");
                }
                System.out.println(); // New line after printing each row
            }
            resultSet.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addClient(int account, String name, String DOB, String Pan, int balance){
        try{
            Formatter formatter = new Formatter();
            formatter.format("insert into client values(%d,'%s','%s','%s','%d')",account,name,DOB,Pan,balance);
            String formattedString = formatter.toString();

            statement.execute(formattedString);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
