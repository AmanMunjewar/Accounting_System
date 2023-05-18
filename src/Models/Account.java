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

    public Account()
    {
        Util util = new Util();
        Scanner sc = new Scanner(System.in);
        Scanner sc1 = new Scanner(System.in);

        int min = 1000;
        int max = 9999;
        account = (int) (Math.random()*(max-min+1)+min);

        /*System.out.println("Enter your name: ");
        name = sc1.nextLine();
        name = util.capitalizeWord(name);

        System.out.println("Enter your DOB(yyyy-mm-dd): ");
        date = sc.next();

        System.out.println("Enter your pan card number: ");
        pan = sc.next();

        balance = util.check_choice();

        System.out.println("Enter your password: ");
        pass = sc.next();

         */
        DatabaseService service = new DatabaseService();
        //service.addClient(account,name,date,pan,balance,pass);
        service.addClient(account,"manish modak","2003-09-11","FGHY5678",7000,"monkey");

    }

}
