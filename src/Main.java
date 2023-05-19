import Models.Account;
import Services.DatabaseService;
import Utilities.Util;

public class Main {
    static boolean entry = true, acc_entry = true;
    public static void main(String[] args) {
        DatabaseService service = new DatabaseService();
        Account account = new Account();
        Util util = new Util();

        service.setConnection();

        System.out.println("-----------------Welcome to VECTOR bank-----------------");

        do {
            System.out.println("\n1. Create a account");
            System.out.println("2. Access an existing account");
            System.out.println("3. Exit ");
            int k = util.check_int();
            System.out.println();

            switch (k) {
                case 1 -> account.createAccount();
                case 2 -> {
                    int accountNo = util.check_account();
                    account.getClient(accountNo);
                    do {
                        System.out.println("\n1. View account info");
                        System.out.println("2. Deposit amount");
                        System.out.println("3. Withdraw amount");
                        System.out.println("4. Open passbook");
                        System.out.println("5. Exit");
                        int m = util.check_int();

                        switch (m) {
                            case 1 -> service.getInfo(accountNo);
                            case 2 -> account.deposit(accountNo);
                            case 3 -> account.withdraw(accountNo);
                            case 4 -> service.viewPassbook(accountNo);
                            case 5 -> acc_entry = false;
                        }
                    } while (acc_entry);
                }
                case 3 -> entry = false;
            }
        } while (entry);

        service.closeConnection();
    }
}