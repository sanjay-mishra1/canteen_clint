package com.example.sanjay.canteen_clint;

/**
 * Created by sanjay on 10/03/2018.
 */

class UserInformation {
    public String Food_name;
    public String Type;
    public int price;
    public int discount;
     public String Description;
   public String Food_Image;

    public UserInformation(String Food_name, String Type, int price,String Description,String Food_Image,int discount) {
        this.Food_name = Food_name;
        this.Type = Type;
        this.price = price;
       this.Description = Description;
        this.Food_Image = Food_Image;
        this.discount = discount;

    }
}
