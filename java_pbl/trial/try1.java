package pava_pbl.trail;

import java.sql.*;
import java.util.Scanner;

public class MyUse {
    private static final String url="jdbc:mysql://localhost:3306/studentTemporaryData";
    private static final String username="root";
    private static final String password="xxxxxxxxxxxxxxxxxx";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // the drivers are present in com.mysql.cj.jdbc
        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection connection=DriverManager.getConnection(url, username, password); //connection is built
            // Collection Interface is used to hold the connection
            Statement statement=connection.createStatement();
            //createStatement() is present in connection object of Connection Interface
            //Statement Interface is used to execute SQL queries with the help of createStatement()

            Scanner sc=new Scanner(System.in);
            String str=sc.nextLine();
            sc.close();

            String query="SELECT * FROM student_temporary_data WHERE `MyUnknownColumn_[1]` LIKE '%"+str+"%';";
            ResultSet resultSet=statement.executeQuery(query);
            while(resultSet.next()){
                int uniRollNo=resultSet.getInt("MyUnknownColumn");
                int stuId=resultSet.getInt("MyUnknownColumn_[0]");
                String name=resultSet.getString("MyUnknownColumn_[1]");
                System.out.println("University Roll Number: " + " " + uniRollNo);
                System.out.println("Student ID: " + " " + stuId);
                System.out.println("Name: " + " " + name);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
