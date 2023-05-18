import java.util.Scanner;

public class Account {
    int account;
    String name;
    String date;
    String pan;
    int balance;

    Account()
    {
        Scanner sc = new Scanner(System.in);
        int min = 1000;
        int max = 9999;

        account = (int) (Math.random()*(max-min+1)+min);

        System.out.println("Enter your name: ");
        name = sc.next();

        System.out.println("Enter your DOB(yyyy-mm-dd): ");
        date = sc.next();

        System.out.println("Enter your pan card number: ");
        pan = sc.next();

        System.out.println("Enter the initial balance: ");
        balance = sc.nextInt();

        DatabaseService service = new DatabaseService();
        service.addClient(account,name,date,pan,balance);
    }

}
