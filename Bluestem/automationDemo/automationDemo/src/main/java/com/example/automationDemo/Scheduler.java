package com.example.automationDemo;

import com.example.config.DBManager;
import com.example.service.DBQuery;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {

    // Define the path to the input file
    private static String INPUT_FILE_PATH = "D:\\OneDrive - Sonata Software\\Bluestem\\InputFile\\NotepadPoc.txt";

    // Define an HTML template for generating result files
    private static String HTMLTemplate = "<!doctype html> <!-- HTML document declaration --> <html> <head> <title>Query Results</title> <!-- Title of the page --> <!-- CSS styling for the page --> <style> .container { display: flex; justify-content: space-between; align-items: center; } .title { text-align: center; flex-grow: 1; } .date-time { text-align: right; font-size: 16px; } .styled-table { border-collapse: collapse; margin: 25px 0; font-size: 0.9em; font-family: sans-serif; min-width: 400px; box-shadow: 0 0 20px rgba(0, 0, 0, 0.15); } .styled-table thead tr { background-color: #009879; color: #ffffff; text-align: left; } .styled-table th, .styled-table td { padding: 12px 15px; width: 250px; } .styled-table tbody tr { border-bottom: 1px solid #dddddd; } .styled-table tbody tr:nth-of-type(even) { background-color: #f3f3f3; } .styled-table tbody tr:last-of-type { border-bottom: 2px solid #009879; } .styled-table tbody tr.active-row { font-weight: bold; color: #009879; } .date-label { color: brown; font-size: 22px; } .time-label { color: blue; font-size: 16px; } </style> </head> <body> <div class='container'> <div class='title'> <h1>Test Results</h1> </div> <div class='date-time'> <div class='date-label'>Test Date: @@DATE@@</div> <div class='time-label'>Test Time: @@TIME@@</div> </div> </div> <div><table class='styled-table'> <thead> <tr> <th>ID</th> <th>Name</th> <th>Salary</th> <th>Email</th> <th>Is Present In table</th> <th>Table Name</th> </tr> </thead> <tbody> @@ROW_DATA@@ </tbody> </table> </div> </body> </html>";

    public static void main(String[] args) {
        try {
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            startScheduler(scheduler);
            // the following line to stop the scheduler after 10 minutes
             Thread.sleep(10 * 60 * 1000);
             stopScheduler(scheduler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to start the scheduler
    public static void startScheduler(ScheduledExecutorService scheduler) {
//        System.out.println("Inside startScheduler");
        try {
            // Schedule the task to run every 2 minutes with an initial delay of 0 seconds.
            scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    String successRowData = "";
                    String failRowData = "";
                    String rowData = "";
                    String notepadLine = null;
                    Connection connection = null;
                    BufferedReader notepadReader = null;
                    try {
                        // Create a database connection manager
                        DBManager dbManager = new DBManager();
                        connection = dbManager.getConnection();

                        // Read data from the input file
                        notepadReader = new BufferedReader(new FileReader(INPUT_FILE_PATH));
                        List<String> fileData = new ArrayList<>();
                        boolean headerFlag = true;
                        while ((notepadLine = notepadReader.readLine()) != null) {
                            if (headerFlag) {
                                headerFlag = false;
                            } else {
                                fileData.add(notepadLine);
                            }
                        }
                        notepadReader.close();
//                        System.out.println("fileData : " + fileData.size());

                        for (int i = 0; i < fileData.size(); i++) {
                            notepadLine = fileData.get(i);

                            String sql = DBQuery.TABLE1_QRY;
                            PreparedStatement preparedStatement = connection.prepareStatement(sql);
//                            System.out.println("notepadLine : " + notepadLine);

                            String[] dataArr = notepadLine.split(",");
//                            System.out.println("dataArr[0] : " + dataArr[0]);

                            preparedStatement.setString(1, dataArr[0]);
                            preparedStatement.setString(2, dataArr[1].toUpperCase().trim());
                            preparedStatement.setString(3, dataArr[2]);
                            preparedStatement.setString(4, dataArr[3].toUpperCase().trim());
                            ResultSet resultSet = preparedStatement.executeQuery();

                            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                            String tableName = resultSetMetaData.getTableName(4);
//                            System.out.println("Name of the table : " + tableName);

                            if (resultSet.next()) {
                                // A match is found
                                String id = resultSet.getString("id");
                                String sal = resultSet.getString("sal");
                                String email_id = resultSet.getString("email_id");
                                String name = resultSet.getString("name");
                                // Perform your comparison logic here
                                System.out.println("Match found for: " + notepadLine);
                                System.out.println("Database Data: " + id + ", " + sal + ", " + email_id + ", " + name);
                                successRowData = successRowData + "<tr><td>" + resultSet.getString("id") + "</td><td>" + resultSet.getString("name") + "</td><td>" + resultSet.getString("sal") + "</td><td>" + resultSet.getString("email_id") + "</td><td style='color:green'>true</td><td>" + tableName + "</td></tr>";
                            } else {
                                // No match is found
                                System.out.println("No match found for: " + notepadLine);
                                failRowData = failRowData + "<tr><td>" + dataArr[0] + "</td><td>" + dataArr[1] + "</td><td>" + dataArr[2] + "</td><td>" + dataArr[3] + "</td></td><td style='color:red'>false</td><td>" + "Not present in table" + "</td></tr>";
                            }
                            if (resultSet != null) {
                                resultSet.close();
                                resultSet = null;
                            }
                            if (preparedStatement != null) {
                                preparedStatement.close();
                                preparedStatement = null;
                            }
                        }

                        rowData = successRowData + failRowData;

                        Date date = new Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
                        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
                        String formattedTime = timeFormatter.format(new Date());
                        HTMLTemplate = HTMLTemplate.replace("@@DATE@@", formatter.format(date));
                        HTMLTemplate = HTMLTemplate.replace("@@TIME@@", formattedTime);
                        HTMLTemplate = HTMLTemplate.replace("@@ROW_DATA@@", rowData);

                        SimpleDateFormat fileNameFormat = new SimpleDateFormat("dd-MMM-yyyy-HH-mm-ss");
                        String dayAndTime = fileNameFormat.format(new Date());
                        String resultFilePath = "D:\\OneDrive - Sonata Software\\Bluestem\\OutputFile\\Result_" + dayAndTime + ".html";
                        File file = new File(resultFilePath);
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        FileWriter myWriter = new FileWriter(file);
                        myWriter.write(HTMLTemplate);
                        myWriter.close();
                    } catch (SQLException | IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (connection != null) {
                                connection.close();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("******************* Done ***********************");
                }
            }, 0, 2, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to stop the scheduler
    public static void stopScheduler(ScheduledExecutorService scheduler) {
        System.out.println("Inside stopScheduler");
        // Shutdown the scheduler to stop it when done.
        scheduler.shutdown();
    }
}
