package io.gitbooks.abhirockzz.jwah.chat.model;

import java.io.Serializable;


public class WelcomeMessage implements Serializable{

    private final String forUser;
    
    public WelcomeMessage(String forUser) {
        this.forUser = forUser;
    }
    
    public String getWelcomeMessage(){
        return "Welcome " + forUser;
    }
    
}
