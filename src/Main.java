import java.util.Scanner;

public class Main {
    static boolean entry = true;
    static int k;
    public static void main(String[] args) {
        DatabaseService service = new DatabaseService();
        service.setConnection();
        Scanner sc = new Scanner(System.in);



        System.out.println("-------------Welcome to VECTOR bank-------------");

        while (entry){
            System.out.println("1. Create a account");
            System.out.println("2. Access an existing account");
            System.out.println("3. Exit ");
            System.out.print("Enter your choice:");
            k = sc.nextInt();
            System.out.println();

            switch (k){
                case 1:
                    Account newAccount = new Account();
                    break;
                case 2:
                    break;
                default:
                    entry = false;
                    break;
            }
        }

        service.closeConnection();
    }
}