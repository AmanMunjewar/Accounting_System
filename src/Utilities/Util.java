package Utilities;

import java.util.*;

public class Util
{
    /*
     *  check_choice method is to check if choice is valid or not .
     */
    public int check_initial()
    {
        int choice = 0 , flag = -1 ;
        do
        {
            Scanner Sc = new Scanner(System.in) ;
            try
            {
                System.out.print("\nEnter the initial deposit: ") ;
                choice = Sc.nextInt() ;
                flag = 1 ;
                break ;
            }
            catch(InputMismatchException e)
            {
                System.out.println("Oops ... Wrong Input Please provide a no. as input\n") ;
                flag = 0 ;
            }
        }while(flag != 1) ;
        return choice ;
    }

    public int check_amount()
    {
        int choice = 0 , flag = -1 ;
        do
        {
            Scanner Sc = new Scanner(System.in) ;
            try
            {
                System.out.print("\nEnter the amount: ") ;
                choice = Sc.nextInt() ;
                flag = 1 ;
                break ;
            }
            catch(InputMismatchException e)
            {
                System.out.println("Oops ... Wrong Input Please provide a no. as input\n") ;
                flag = 0 ;
            }
        }while(flag != 1) ;
        return choice ;
    }

    public int check_account()
    {
        int choice = 0 , flag = -1 ;
        do
        {
            Scanner Sc = new Scanner(System.in) ;
            try
            {
                System.out.print("Enter your account number: ") ;
                choice = Sc.nextInt() ;
                flag = 1 ;
                break ;
            }
            catch(InputMismatchException e)
            {
                System.out.println("Oops ... Wrong Input Please provide a no. as input\n") ;
                flag = 0 ;
            }
        }while(flag != 1) ;
        return choice ;
    }

    public int check_int()
    {
        int choice = 0 , flag = -1 ;
        do
        {
            Scanner Sc = new Scanner(System.in) ;
            try
            {
                System.out.print("Enter your choice: ") ;
                choice = Sc.nextInt() ;
                flag = 1 ;
                break ;
            }
            catch(InputMismatchException e)
            {
                System.out.println("Oops ... Wrong Input Please provide a no. as input\n") ;
                flag = 0 ;
            }
        }while(flag != 1) ;
        return choice ;
    }
    
    /*
     * capitalizeWord method takes a string(name) and makes it capitalized
     *
     * s.append(Character.toUpperCase(str.charAt(i))) - appends character in s also at same time capitalize them.
     * s.append(Character.toLowerCase(str.charAt(i))) - appends character in s also at same time capitalize them.
     *
     * ch = str.charAt(i) - gets character at i index.
     * s.toString().trim() - trims s and convert it into string ;
     */
    public String capitalizeWord(String str)
    {
        StringBuffer s = new StringBuffer();
        char ch = ' ';
        for (int i = 0; i < str.length(); i++)
        {

            if (ch == ' ' && str.charAt(i) != ' ')
            {
                s.append(Character.toUpperCase(str.charAt(i)));
            }
            else
            {
                s.append(Character.toLowerCase(str.charAt(i)));
            }
            ch = str.charAt(i);
        }
        return s.toString().trim();
    }
}