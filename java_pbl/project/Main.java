package project;

import java.io.*;
import java.util.*;
public class Main {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        String choice;
        System.out.println("Welcome!");
        do{
            System.out.println("1. Login");
            System.out.println("2. Change Password");
            System.out.println("3. Exit")
            ;
            choice=sc.nextLine();
            switch (choice){
                case "1":{
                    System.out.print("Enter name: ");
                    String name=sc.nextLine();
                    Console console = System.console();
                    char[] passwordChars = console.readPassword("Enter password: ");
                    String password = new String(passwordChars);

                    ArrayList<String> login=Login.getDetails(name.toUpperCase(), password);

                    if(!login.isEmpty()){
                        String choice2;

                        User user=new User(login.get(0), login.get(1), login.get(2), login.get(3), login.get(4));

                        System.out.println("Welcome " + User.name + "!!");
                        do{
                            System.out.println("1. View Profile");
                            System.out.println("2. Update Profile");
                            System.out.println("3. Search Profile");
                            System.out.println("4. Create Community");
                            System.out.println("5. Go to a Community");
                            System.out.println("6. Logout");

                            choice2=sc.nextLine();

                            switch(choice2){
                                case "1" :{
                                    user.viewProfile();
                                    break;
                                }
                                case "2":{
                                    System.out.println("1. Update Bio.");
                                    System.out.println("2. Update Description.");
                                    System.out.println("3. Add Certificates.");
                                    System.out.println("4. Back.");

                                    String choice3=sc.nextLine();

                                    switch(choice3){
                                        case "1":{
                                            user.updateBio();
                                            break;
                                        }
                                        case "2":{
                                            user.updateDescription();
                                            break;
                                        }
                                        case "3":{
                                            user.addCertificates();
                                        }
                                        case "4":{
                                            System.out.println();
                                            break;
                                        }
                                        default:
                                            System.out.println("Invalid Input");
                                    }
                                    break;
                                }
                                case "3" :{
                                    user.viewPersonProfile();
                                    break;
                                }
                                case "4" :{
                                    Communication.createCommunity(User.name);
                                    break;
                                }
                                case "5" :{
                                    Communication.viewCommunities();
                                    break;
                                }
                                case "6" :
                                    System.out.println("Thanks For Visiting !!");
                                    break;
                            }
                        }while(!choice2.equals("6"));
                    }
                    else System.out.println("Account doesn't exist");

                    break;
                }
                case "2":{
                    System.out.println("Enter your name and password");
                    String name=sc.nextLine();
                    String password=sc.nextLine();
                    System.out.println("Enter new Password");
                    String newPassword=sc.nextLine();
                    Login.changePassword(name.toUpperCase(), password, newPassword);
                    break;
                }
                case "3":{
                    System.out.println("Exiting...");
                    break;
                }
                default:
                    System.out.println("Invalid Input");
            }
        }while(!choice.equals("3"));
        sc.close();
    }
}
