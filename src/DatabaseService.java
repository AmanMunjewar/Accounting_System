import java.sql.*;

public class DatabaseService {
    String url = "jdbc:mysql://localhost:3306/banking";
    String user = "root";
    String pass = "root";
    Connection connection;
    Statement statement;
    ResultSet resultSet;

    DatabaseService(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(url,user,pass);
            statement = connection.createStatement();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getInfo() {
        try{
            resultSet = statement.executeQuery("select * from user");

            while (resultSet.next()){
                System.out.println(resultSet.getInt(1)+" "+resultSet.getString(2));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
