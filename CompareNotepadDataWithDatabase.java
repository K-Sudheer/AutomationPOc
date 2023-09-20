package com.example.ComparingData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class CompareNotepadDataWithDatabase {
    public static void main(String[] args) {
        // Read data from the Notepad file

        List<String> notepadData = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Sudheer.Kokolu\\Documents\\NotepadPoc.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                notepadData.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Connect to the database
        Connection connection = null;
        try {
            String dbUrl = "jdbc:mysql://localhost:3306/employee_db";
            String dbUser = "root";
            String dbPassword = "Ksudheer@21";
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Retrieve data from the database
        List<String> databaseData = new ArrayList<>();
        try {
            String sql = "SELECT id, name, sal, email_id FROM employees";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int sal = resultSet.getInt("sal");
                String email_id = resultSet.getString("email_id");

                String record = id + ", " + name + ", " + sal + ", " + email_id;
                databaseData.add(record);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Compare data
        for (String notepadLine : notepadData) {
            if (databaseData.contains(notepadLine)) {
                // Match found, perform your desired action
                System.out.println("Match found: " + notepadLine);
            } else {
                // No match found, perform your desired action
                System.out.println(" No match found for: " + notepadLine);
            }

            // Close the database connection
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }}