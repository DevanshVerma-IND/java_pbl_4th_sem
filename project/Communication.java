package project;

import java.util.*;
import java.sql.*;
public class Communication {
    private static final String URL="jdbc:mysql://localhost:3306/mydb_java";
    private static final String USERNAME="root";
    private static final String PASSWORD="root";

    static void createCommunity(String creator){
        Scanner sc=new Scanner(System.in);
        System.out.print("Enter Community name: ");
        String name=sc.nextLine();
        if(name.isEmpty()){
            System.out.println("Enter a proper name.");
            return;
        }
        ArrayList<String> duplicates=checkCommunity(name);
        if(!duplicates.isEmpty()){
            System.out.println("Following communities with similar name exist :");
            for(String i: duplicates) System.out.println(i);
            System.out.println("Do you want to create a new community ?(n/Y)");

            String choice=sc.nextLine();

            if(!choice.equals("Y")){
                System.out.println("Canceling...");
                return;
            }
        }

        String query="insert into community(name, creator) values (?, ?)";

        try(Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement ps = connection.prepareStatement(query)){

            ps.setString(1, name);
            ps.setString(2, creator);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Community created successfully !!");
            } else {
                System.out.println("Something Went wrong !!");
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    static void viewCommunities(){
        Scanner sc=new Scanner(System.in);
        String query="select * from community";

        try(Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs=ps.executeQuery()){

            System.out.println("Following are the communities:");
            String id="1";
            while(rs.next()){
                id=rs.getString("id");
                String name=rs.getString("name");
                String creator=rs.getString("creator");
                System.out.println(id + ": " + name + "\n       (by " + creator + ")\n");
            }
            System.out.println("1. Go to a community.");
            System.out.println("2. Back.");
            String choice=sc.nextLine();

            switch(choice){
                case "1":
                    System.out.print("Enter Community ID: ");
                    String c=sc.nextLine();
                    if(Integer.parseInt(c)<=Integer.parseInt(id) && Integer.parseInt(c)>0) gotoCommunity(c);
                    else System.out.println("Invalid Community ID");
                    break;
                case "2":
                    System.out.println();
                    break;
                default:
                    System.out.println("Invalid  Input");
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    static void gotoCommunity(String  id){
        Scanner sc=new Scanner(System.in);
        String query="select * from community where id = ?";

        try(Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement ps = connection.prepareStatement(query)){

            ps.setString(1,id);
            ResultSet rs=ps.executeQuery();

            if(rs.next()){
                String community_id=rs.getString("id");
                String name=rs.getString("name");

                System.out.println("Welcome to " + name);
                System.out.println("1. View all questions.");
                System.out.println("2. Ask a question.");

                System.out.print("Enter choice: ");
                String choice=sc.nextLine();
                switch (choice) {
                    case "1":
                         viewAllQuestion(community_id);
                        break;
                    case "2":
                        askQuestion(community_id, User.name);
                        break;
                    default:
                        System.out.println("Invalid input");
                        break;
                }
            }
            else System.out.println("Community not found !");

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    static void askQuestion(String community_id, String person){
        Scanner sc=new Scanner(System.in);
        String query="insert into questions (community_id, title, question_text, person) values (?, ?, ?, ?)";

        try(Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement ps = connection.prepareStatement(query)){

            System.out.print("Enter title: ");
            String title=sc.nextLine();
            System.out.println("Enter question");
            String question_text=sc.nextLine();

            ps.setString(1, community_id);
            ps.setString(2, title);
            ps.setString(3, question_text);
            ps.setString(4, person);

            int rowsAffected = ps.executeUpdate();
            if(rowsAffected>0){
                System.out.println("Question added successfully !!");
            }
            else System.out.println("Something went wrong");

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    static void viewAllQuestion(String community_id){
        Scanner sc=new Scanner(System.in);

        String query="select * from questions where community_id = ?";
        try(Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement ps = connection.prepareStatement(query)){

            ps.setString(1, community_id);
            ResultSet rs=ps.executeQuery();

            int max=0;
            while(rs.next()){
                String id=rs.getString("id");
                max=Integer.parseInt(id);
                String title=rs.getString("title");
                String question_text=rs.getString("question_text");
                String person=rs.getString("person");

                System.out.println(id + ": " + title + " (by " + person + ")");
                System.out.println(question_text);
                System.out.println();
            }

            System.out.println("1. Go to a question.");
            System.out.println("2. Back.");
            String choice=sc.nextLine();

            switch(choice){
                case "1":
                    System.out.print("Enter question id: ");
                    String question_id=sc.nextLine();
                    if(Integer.parseInt(question_id)<=max && Integer.parseInt(question_id)>0){
                        goToQuestion(question_id, community_id);
                    }
                    else System.out.println("Invalid ID");
                    break;
                case "2":
                    System.out.println();
                    break;
                default:
                    System.out.println("Invalid Input");
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    static void goToQuestion(String id, String community_id){
        Scanner sc=new Scanner(System.in);
        String query="select * from questions where id = ? and community_id = ?";
        try(Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement ps = connection.prepareStatement(query)){

            ps.setString(1, id);
            ps.setString(2, community_id);

            ResultSet rs=ps.executeQuery();

            if(rs.next()){
                String title=rs.getString("title");
                String question_text=rs.getString("question_text");
                String person=rs.getString("person");

                System.out.println(id + ": " + title);
                System.out.println("    by: " + person);
                System.out.println(question_text);

                System.out.println("1. View all answers.");
                System.out.println("2. Answer the Question.");
                System.out.println("3. Back");
                String choice=sc.nextLine();

                switch(choice){
                    case "1":
                        viewAllAnswers(id);
                        break;
                    case "2":
                        answerQuestion(id, User.name);
                        break;
                    case "3":
                        System.out.println();
                        break;
                    default:
                        System.out.println("Invalid Input");
                }
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    static void viewAllAnswers(String question_id){
        Scanner sc=new Scanner(System.in);
        String query="select * from answers where question_id = ? order by count desc";
        try(Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement ps = connection.prepareStatement(query)){

            ps.setString(1,question_id);
            ResultSet rs=ps.executeQuery();

            while(rs.next()){
                String id=rs.getString("id");
                String answer_text=rs.getString("answer_text");
                String person=rs.getString("person");

                System.out.println("id: " + id +" (Answered by: " + person + ")");
                System.out.println(answer_text);
                System.out.println();

                System.out.println("1. Upvote.");
                System.out.println("2. Back.");

                System.out.print("Enter choice: ");
                String choice=sc.nextLine();


                switch(choice){
                    case "1":
                        System.out.print("Enter ID: ");
                        String answer_id=sc.nextLine();
                        upvote(question_id, answer_id);
                        break;
                    case "2":
                        System.out.println();
                        break;
                    default:
                        System.out.println("Invalid Input");
                        break;
                }
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    static void answerQuestion(String question_id, String person){
        Scanner sc=new Scanner(System.in);
        String query="insert into answers (question_id, answer_text, person, count) values (?, ?, ?, ?)";
        try(Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement ps = connection.prepareStatement(query)){

            System.out.println("Enter answer:");
            String answer_text=sc.nextLine();
            ps.setString(1, question_id);
            ps.setString(2, answer_text);
            ps.setString(3, person);
            ps.setString(4, "0");

            int rowsAffected = ps.executeUpdate();
            if(rowsAffected>0){
                System.out.println("Answer added successfully !!");
            }
            else System.out.println("Something went wrong");

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    static void upvote(String question_id, String id){
        String query="update answers set count = count +1 where id = ? and question_id = ?";

        try(Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement ps = connection.prepareStatement(query)){

            ps.setString(1, id);
            ps.setString(2, question_id);

            if(hasVoted(id, User.name)){
                System.out.println("You have already voted.");
                return;
            }

            int rowsAffected=ps.executeUpdate();
            if (rowsAffected > 0) {
                addVote(id, User.name);
                System.out.println("Upvoted successfully !!");
            } else {
                System.out.println("Something went wrong !!");
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    static boolean hasVoted(String id, String person){
        String query="select * from votes where id= ? and person= ?";
        try(Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement ps = connection.prepareStatement(query)){

            ps.setString(1, id);
            ps.setString(2, person);

            ResultSet rs=ps.executeQuery();

            if(rs.next()){
                return true;
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    static void addVote(String id, String person){
        String query="insert into votes (id, person) values (?, ?)";

        try(Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement ps = connection.prepareStatement(query)){
             ps.setString(1, id);
             ps.setString(2, person);

             int rowsAffected=ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Details Added !!");
            } else {
                System.out.println("Something went wrong !!");
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    static ArrayList<String> checkCommunity(String community){
        ArrayList<String> duplicates=new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String query="select name from community"; // this can make the project slow, (will try to fix it in future)

            try(Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement ps=connection.prepareStatement(query);
                ResultSet rs=ps.executeQuery()) {

                Set<String> ignoreWords=Set.of("the", "of", "in", "a", "an", "on", "by", "with", "at", "is", "am", "are", "for", "!", "@", "#", "$","%","^","&","*", "(",")","~","`","{","[","}","]",";","\"","'",":",",",".","|","\\","/","<",">","_","-","?","=","+");
                Set<String> inputWord=new HashSet<>(Arrays.asList(community.trim().toLowerCase().split("\\s+")));
                inputWord.removeAll(ignoreWords);

                while(rs.next()){
                    String outputWord=rs.getString("name");

                    Set<String> existingWords=new HashSet<>(Arrays.asList(outputWord.toLowerCase().split("\\s+")));
                    existingWords.removeAll(ignoreWords);
                    Set<String> commonWords=new HashSet<>(inputWord);
                    commonWords.retainAll(existingWords);

                    if(!commonWords.isEmpty()){
                        duplicates.add(outputWord);
                    }
                }

            }catch(Exception e){
                System.out.println(e.getMessage());
            }

        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        return duplicates;
    }
}