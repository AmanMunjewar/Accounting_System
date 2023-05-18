import java.util.Scanner;

public class Main {
    static boolean entry = true;
    static int k;
    public static void main(String[] args) {
        DatabaseService service = new DatabaseService();
        service.setConnection();
        Scanner sc = new Scanner(System.in);

        System.out.println("-------------Welcome to VECTOR bank-------------");
        
        do 
        {
            System.out.println("1. Create a account");
            System.out.println("2. Access an existing account");
            System.out.println("3. Exit ");
            System.out.println("Enter your choice:");
                
            choice = pu.check_choice();
            switch (choice)                                          // MAIN MENU
            {                                                        // Try Catch Blocks For exceptions
                case 1:
                    try 
                    {
                        pu.purchase();                              // Purchase Method Defined in Peronal_utility
                    } 
                    catch (Exception e) 
                    {
                       throw new RuntimeException(e);
                    }
                    break;
            
                case 2:
                    try 
                    {
                        pu.sell();                                  // Sell Method Defined in Peronal_utility
                        pu.CustomerReceipt();                       // CustomerReceipt Method Defined in Peronal_utility
                    } 
                    catch (Exception e) 
                    {
                        throw new RuntimeException(e);
                    }
                    break;

                case 3:
                    try 
                    {
                        pu.ProfitLoss() ;                           // ProfitLoss Method Defined in Peronal_utility
                    }   
                    catch (Exception e) 
                    {
                        throw new RuntimeException(e);
                    }
                    break;

                case 0:
                    break;
                default:
                    System.out.println("\nEnter Valid Choice!!\n") ;
            }
        } while (choice != 0);

        service.closeConnection();
    }
}
