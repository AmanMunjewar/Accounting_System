
public class Main {
    public static void main(String[] args) {
        DatabaseService service = new DatabaseService();
        service.setConnection();
        service.getInfo();
        service.closeConnection();
    }
}