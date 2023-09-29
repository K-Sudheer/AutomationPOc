package com.example.service;

public class DBQuery {

    public static String TABLE1_QRY="SELECT id, sal, email_id, name FROM employees WHERE id = ? and upper(name)=? and sal = ? and upper(email_id) = ?";
}