import java.sql.*;

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

    public void getInfo() {
        try{
            //statement.execute("insert into user values(1,'manish')");
            resultSet = statement.executeQuery("select * from client");

            while (resultSet.next()){
                System.out.println(resultSet);
            }
        }
        catch (Exception e) {
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
}
