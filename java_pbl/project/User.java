package project;

import java.sql.*;
import java.util.*;

public class User {
    private static final String URL="jdbc:mysql://localhost:3306/mydb_java";
    private static final String USERNAME="root";
    private static final String PASSWORD="xxxxxxxxxxx";

    private final String id;
    private final String rollno;
    static String name;
    private final String email;
    private final String status;

    User(String id, String rollno, String name, String email, String status){
        this.id=id;
        this.rollno=rollno;
        User.name =name;
        this.email=email;
        this.status=status;
    }

    void viewProfile(){
        if(status.equals("Student")) {
            System.out.println("Id: " + id);
            System.out.println("Roll no: " + rollno);
        }
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Status: " + status);

        viewExtraProfile(name);
        viewCertificates(name);
    }

    void addCertificates(){
        Scanner sc=new Scanner(System.in);
        System.out.print("Enter title: ");
        String title=sc.nextLine();
        System.out.print("Enter credentials: ");
        String credentials=sc.nextLine();

        String query="insert into certificates (person, title, credentials) values (?, ?, ?)";

        try(Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement ps = connection.prepareStatement(query)){

            ps.setString(1, User.name);
            ps.setString(2, title);
            ps.setString(3, credentials);

            int rowsAffected= ps.executeUpdate();

            if(rowsAffected>0){
                System.out.println("Certificate added successfully !!");
            }
            else System.out.println("Something went wrong.");

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    void viewPersonProfile(){
        Scanner sc=new Scanner(System.in);
        System.out.print("Enter Name to Search: ");
        String name=sc.nextLine();
        String query="select * from users where name = ?";

        try(Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement ps = connection.prepareStatement(query)){

            ps.setString(1, name.toUpperCase());

            ResultSet rs=ps.executeQuery();

            if(rs.next()){
                String thisRollNo=rs.getString("rollno");
                String thisId=rs.getString("id");
                String thisName=rs.getString("name");
                String thisEmail=rs.getString("email");
                String thisStatus=rs.getString("status");

                System.out.println("Name: " + thisName);
                if(thisStatus.equalsIgnoreCase("student")){
                    System.out.println("Roll No: " + thisRollNo);
                    System.out.println("ID: " + thisId);
                }
                System.out.println("Email: " + thisEmail);
                System.out.println("Status: " + thisStatus);

                viewExtraProfile(thisName);
                viewCertificates(thisName);

            }
            else System.out.println("Profile not found !!");

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    void viewExtraProfile(String person){
        String query="select * from bio where person = ?";

        try(Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement ps = connection.prepareStatement(query)){

            ps.setString(1, person);

            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                String linkedIn=rs.getString("linkedIn");
                String github=rs.getString("github");
                String leetcode=rs.getString("leetcode");
                String description=rs.getString("description");

                if(!linkedIn.equalsIgnoreCase("null")) System.out.println("LinkedIn: " + linkedIn);
                if(!github.equalsIgnoreCase("null")) System.out.println("GitHub: " + github);
                if(!leetcode.equalsIgnoreCase("null")) System.out.println("LeetCode: " + leetcode);
                if(!description.equalsIgnoreCase("null")) System.out.println("Description: " + description);
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    void viewCertificates(String person){
        String query="select * from certificates where person = ?";

        try(Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement ps = connection.prepareStatement(query)){

            ps.setString(1, person);

            ResultSet rs=ps.executeQuery();
            System.out.println("Certificates:");
            while(rs.next()){
                String title=rs.getString("title");
                String credentials=rs.getString("credentials");

                System.out.println("Title: " + title);
                System.out.println("Credentials: " + credentials);
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    void updateBio(){
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter following links (null to ignore):");
        System.out.print("LinkedIn: ");
        String linkedIn=sc.nextLine();
        System.out.print("GitHub: ");
        String github=sc.nextLine();
        System.out.print("LeetCode: ");
        String leetcode=sc.nextLine();

        String query="update bio set linkedIn = ?, github = ?, leetcode = ? where person = ?";

        try(Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement ps = connection.prepareStatement(query)){

            ps.setString(1, linkedIn);
            ps.setString(2, github);
            ps.setString(3, leetcode);
            ps.setString(4, User.name);

            int rowsAffected=ps.executeUpdate();

            if(rowsAffected>0){
                System.out.println("Details added successfully !!");
            }
            else System.out.println("Something went wrong !!");

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    void updateDescription(){
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter Description:");
        String description=sc.nextLine();

        String query="update bio set description = ? where person = ?";

        try(Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement ps = connection.prepareStatement(query)){

            ps.setString(1, description);
            ps.setString(2, User.name);

            int rowsAffected=ps.executeUpdate();
            if(rowsAffected>0){
                System.out.println("Details added successfully !!");
            }
            else System.out.println("Something went wrong !!");

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

}
