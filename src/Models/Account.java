package Models;

import Services.DatabaseService;
import Utilities.Util;
import java.util.Scanner;

public class Account {
    int account;
    String name;
    String date;
    String pan;
    int balance;
    String pass;
    DatabaseService service = new DatabaseService();
    Scanner sc = new Scanner(System.in);
    Scanner sc1 = new Scanner(System.in);
    Util util = new Util();

    public void createAccount()
    {
        int min = 1000;
        int max = 9999;
        account = (int) (Math.random()*(max-min+1)+min);

        System.out.print("Enter your name: ");
        name = sc1.nextLine();
        name = util.capitalizeWord(name);

        System.out.print("\nEnter your DOB(yyyy-mm-dd): ");
        date = sc.next();

        System.out.print("\nEnter your pan card number: ");
        pan = sc.next();

        balance = util.check_initial();

        System.out.print("\nEnter your password: ");
        pass = sc.next();

        service.addClient(account,name,date,pan,balance,pass);

        System.out.println("Account successfully created!!");
        System.out.println("Your account number is: "+account);
    }

    public void getClient(int account){

        System.out.print("Enter your password: ");
        String pass = sc.next();

        if(service.validateClient(account,pass)){
            System.out.println("Logged in Successfully");
        }
        else{
            System.out.println("Log in failure ");
        }
    }

    public void deposit(int account){
        int amount = util.check_amount();
        service.deposite(account,amount);
    }

    public void withdraw(int account){
        int amount = util.check_amount();
        service.withdraw(account,amount);
    }

}
