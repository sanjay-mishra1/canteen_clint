package com.example.sanjay.canteen_clint.info;

public class adapter {

    private String Name;
    private String User_Img;
    private String Uid;
    private String Canteen;

    public adapter(String name, String user_Img, String uid) {
        Name = name;
        User_Img = user_Img;
        Uid = uid;
    }
    public adapter(){

    }

    public String getCanteen() {
        return Canteen;
    }

    public String getName() {
        return Name;
    }
    public String getUser_Img() {
        return User_Img;
    }

     public String getUid() {
        return Uid;
    }
}
