package com.travelquest.travelquest.database_handler;

public class User {

    private int id;
    private String first_name, last_name, gender, mail;



    public User(int id, String first_name, String last_name, String gender, String mail) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.gender = gender;
        this.mail = mail;

    }

    public int getId() {
        return id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getGender() {
        return gender;
    }

    public String getMail() {
        return mail;
    }

}
