package com.example.ComparingData;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CreateJavaFile {
        public static void main(String[] args) {
            // Specify the file path
            String filePath = "D:\\OneDrive - Sonata Software\\ComparingNotepadData\\employee_data.txt";

            try {
                // Create a FileWriter object
                FileWriter fileWriter = new FileWriter(filePath);
                // Create a BufferedWriter object to write efficiently
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                // Employee data
                String[] employees = {
                "1, ram, 20000, ram@gmail.com",
                "2, Bhim, 25000, bhim@gmail.com",
                "3, Krish, 22000, krish@gmail.com",
                "4, Anji, 23000, anji@gmail.com",
                "5, Ravan, 18000, ravan@gmail.com",
                "6, Dharma, 25000, dharma@gmail.com",
                "7, Arjun, 21000, arjun@gmail.com",
                "8, Lava, 17000, lava@gmail.com",
                "9, Kusa, 18000, kusa@gmail.com",
                "10, Sita, 28000, sita@gmail.com",
                "11, Satya, 12000, staya@gmail.com",
                "12, bama, 23000, bhama@gmail.com",
                "13, Ruk, 25000, ruk@gmail.com"

                };
                // Write employee data to the file
                for (String employee : employees) {
                    bufferedWriter.write(employee);
                    bufferedWriter.newLine(); // Add a new line for each employee
                }
                // Close the BufferedWriter
                bufferedWriter.close();
                System.out.println("Employee data has been written to " + filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}
