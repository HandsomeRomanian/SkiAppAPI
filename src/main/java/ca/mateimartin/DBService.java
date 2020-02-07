package ca.mateimartin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;

import ca.mateimartin.dto.*;

public class DBService {

    public DBService() {
    }

    private static Connection connect() {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ski?useSSL=false", "user",
                    "passw0rd");
            return con;
        } catch (Exception e) {

            System.out.println(e.getMessage());
            return null;
        }
    }

    public static List<Level> getLevels() {

        Connection sql = null;
        List<Level> output = new ArrayList<>();
        try {
            sql = connect();
            Statement req = sql.createStatement();
            ResultSet res = req.executeQuery("SELECT * FROM `Levels`");
            while (res.next()) {
                Level tempClasse = new Level(res.getInt(1), res.getString(2), res.getString(3));
                output.add(tempClasse);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        closeDB(sql);
        return output;
    }

    public static List<Exercice> getExercices() {

        Connection sql = null;
        List<Exercice> output = new ArrayList<>();
        try {
            sql = connect();
            Statement req = sql.createStatement();
            ResultSet res = req.executeQuery("SELECT * FROM `Exercices`");
            while (res.next()) {
                Exercice tempClasse = new Exercice(res.getString(2), res.getString(3), res.getInt(4));
                output.add(tempClasse);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        closeDB(sql);
        return output;
    }

    public static List<Group> getGroupsByLevel(int id) {

        Connection sql = null;
        List<Group> output = new ArrayList<>();
        try {
            sql = connect();
            Statement req = sql.createStatement();
            ResultSet res = req.executeQuery("SELECT * FROM `Groups` WHERE LevelID = " + id + ";");
            while (res.next()) {
                Group tempGroup = new Group(res.getInt(1), res.getString(3), res.getString(2), res.getString(4),
                        res.getInt(5), res.getString(6));
                output.add(tempGroup);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        closeDB(sql);
        return output;
    }

    public static Group getStudent(int id) {

        Connection sql = null;
        Group output = null;
        try {
            sql = connect();
            Statement req = sql.createStatement();
            ResultSet res = req.executeQuery("SELECT * FROM `Groups` WHERE GroupID = " + id + ";");
            while (res.next()) {
                Group tempGroup = new Group(res.getInt(1), res.getString(3), res.getString(2), res.getString(4),
                        res.getInt(5), res.getString(6));
                try {
                    sql = connect();
                    Statement req2 = sql.createStatement();
                    ResultSet res2 = req2.executeQuery("SELECT * FROM VW_Inscription where GroupID = " + id + ";");
                    while (res2.next()) {

                        tempGroup.Students.add(new Student(res2.getInt(2), res2.getString(3) + " " + res2.getString(4),
                                res.getInt(5)));
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                output = tempGroup;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        closeDB(sql);
        return output;
    }

    public static Group getGroup(int id) {

        Connection sql = null;
        Group output = null;
        try {
            sql = connect();
            Statement req = sql.createStatement();
            ResultSet res = req.executeQuery("SELECT * FROM `Groups` WHERE GroupID = " + id + ";");
            while (res.next()) {
                Group tempGroup = new Group(res.getInt(1), res.getString(3), res.getString(2), res.getString(4),
                        res.getInt(5), res.getString(6));
                try {
                    sql = connect();
                    Statement req2 = sql.createStatement();
                    ResultSet res2 = req2.executeQuery("SELECT * FROM VW_Inscription where GroupID = " + id + ";");
                    while (res2.next()) {
                        tempGroup.Students.add(new Student(res2.getInt(1), res2.getString(2) + " " + res2.getString(3),
                                res2.getInt(4)));
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                output = tempGroup;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        closeDB(sql);
        return output;
    }

    public static List<Exercice> getExercicesByLevel(int id) {
        Connection sql = null;
        List<Exercice> output = new ArrayList<>();
        try {
            sql = connect();
            Statement req = sql.createStatement();
            ResultSet res = req.executeQuery("SELECT * FROM `Exercices` WHERE `LevelID` = " + id + ";");
            while (res.next()) {
                Exercice tempClasse = new Exercice(res.getString(2), res.getString(3), res.getInt(4));
                output.add(tempClasse);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        closeDB(sql);
        return output;
    }

    public static JsonElement getStudentsByLevel(int id) {
        return null;
    }

    public static List<Group> getDowngrade(int studentID) {
        Connection sql = null;
        List<Group> output = new ArrayList<>();
        try {
            sql = connect();
            Statement req = sql.createStatement();
            ResultSet res = req.executeQuery("SELECT * FROM `Groups` WHERE GroupID = " + studentID + ";");
            while (res.next()) {
                Group tempClasse = new Group(res.getInt(1), res.getString(3), res.getString(2), res.getString(4),
                        res.getInt(5), res.getString(6));
                output.add(tempClasse);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        closeDB(sql);
        return output;
    }

    public static boolean updateStatus(StatusDTO s) {

        Connection sql = null;
        PreparedStatement stmt = null;
        boolean output = false;
        try {

            sql = connect();

            stmt = sql.prepareStatement("UPDATE `StudentGroup` SET `Status`= ? WHERE `StudentID` = ?");

            stmt.setInt(1, s.status);
            stmt.setInt(2, s.studentID);
            stmt.executeUpdate();
            closeDB(sql);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            output = true;
        }
        closeDB(sql);
        return output;
    }

    public static List<SearchResponse> search(String id) {
        String[] input = id.toUpperCase().split("_");
        List<SearchResponse> out = new ArrayList<>();
        Connection sql = null;
        try {
            sql = connect();
            Statement req = sql.createStatement();
            ResultSet res;
            if (input.length == 2){
                 res = req.executeQuery("SELECT * FROM `VW_Inscription` WHERE `Name` LIKE '"+input[0]+"%' OR `FirstName` LIKE '"+input[1]+"%' OR `Name` LIKE '"+input[1]+"%' OR `FirstName` LIKE '"+input[0]+"%' ");
            }
            else{
                res = req.executeQuery("SELECT * FROM `VW_Inscription` WHERE `Name` LIKE '"+input[0]+"%' OR `FirstName` LIKE '"+input[0]+"%'");
            }
            req.toString();
            while (res.next()) {
                out.add(new SearchResponse(new Student(res.getInt(1),res.getString(2)+" "+res.getString(3),res.getInt(4)),getGroup(res.getInt(5))));
            }} catch (Exception e) {System.out.println(e.getMessage());}
        closeDB(sql);
        return out;
    }

    public static void closeDB(Connection sql) {
        if (sql != null) {
            try {
                sql.close();
            } catch (Exception e) {
            }
        }
    }

}