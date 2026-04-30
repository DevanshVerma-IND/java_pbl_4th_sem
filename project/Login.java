package project;

import java.sql.*;
import java.util.*;

public class Login {
    private static final String URL="jdbc:mysql://localhost:3306/mydb_java";
    private static final String USERNAME="root";
    private static final String PASSWORD="root";

    static ArrayList<String> getDetails(String name, String passcode) {
        ArrayList<String> list = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String query = "select id, rollno, name, email, status from users where name = ? and password = SHA2(?, 256) and isEligible = 1";

            try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                 PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setString(1, name);
                ps.setString(2, passcode);

                ResultSet resultSet = ps.executeQuery();

                if (resultSet.next()) {
                    list.add(String.valueOf(resultSet.getInt("id")));
                    list.add(String.valueOf(resultSet.getInt("rollno")));
                    list.add(resultSet.getString("name"));
                    list.add(resultSet.getString("email"));
                    list.add(resultSet.getString("status"));
                }

                resultSet.close();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return list;
    }

    static void changePassword(String name, String password, String newPassword){

        try(Connection connection=DriverManager.getConnection(URL, USERNAME, PASSWORD);
            Statement statement=connection.createStatement()){

            String query = "update users set password = SHA2(?, 256) where name = ? and password = SHA2(?, 256)";
            PreparedStatement ps = connection.prepareStatement(query);

            ps.setString(1, newPassword);
            ps.setString(2, name);
            ps.setString(3, password);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Password updated successfully.");
            } else {
                System.out.println("Invalid username or password.");
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
