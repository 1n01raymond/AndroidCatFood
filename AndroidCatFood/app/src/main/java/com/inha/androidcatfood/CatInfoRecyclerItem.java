package com.inha.androidcatfood;

public class CatInfoRecyclerItem {
    String imageUrl;
    String title;
    int gender;
    boolean natural;

    String getImage(){
        return this.imageUrl;
    }
    String getTitle(){
        return this.title;
    }

    int getGender(){
        return gender;
    }

    boolean getNatrual(){
        return natural;
    }

    CatInfoRecyclerItem(String imageUrl, String title, int gender, boolean natural){
        this.imageUrl = imageUrl;
        this.title = title;
        this.gender = gender;
        this.natural = natural;
    }
}