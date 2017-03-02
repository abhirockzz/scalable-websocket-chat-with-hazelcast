package io.gitbooks.abhirockzz.jwah.chat.model;

import java.io.Serializable;

public class LogOutNotification implements Serializable{

    private final String user;

    public LogOutNotification(String user) {
        this.user = user;
    }
    
    public String getLogoutMsg(){
        return "User "+this.user+ " has logged out";
    }
   
}
