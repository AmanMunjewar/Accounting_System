import java.sql.*;

public class DatabaseService {
    String url = "jdbc:mysql://localhost:3306/banking";
    String user = "root";
    String pass = "root";


    public void getInfo() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(url,user,pass);
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("select * from user");

            while (resultSet.next()){
                System.out.println(resultSet.getInt(1)+" "+resultSet.getString(2));
            }

            connection.close();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
