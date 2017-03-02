package io.gitbooks.abhirockzz.jwah.chat.model;

import java.io.Serializable;


public class NewJoineeNotification implements Serializable{
    private final String newJoinee;
    
    

    public NewJoineeNotification(String newJoinee) {
        this.newJoinee = newJoinee;
    }
    
    public String getNewJoineeMessage(){
        return "New user joined --- " + this.newJoinee;
    }
    
    public String getNewJoinee(){
        return newJoinee;
    }
}
