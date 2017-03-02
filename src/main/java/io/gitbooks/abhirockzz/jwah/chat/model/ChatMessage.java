package io.gitbooks.abhirockzz.jwah.chat.model;

import java.io.Serializable;


public class ChatMessage implements Serializable{
    private final String msg;
    private final String recepient;
    private final boolean isPrivate;
    private String sender;
    
    public ChatMessage(String msg) {
        this.msg = msg;
        this.isPrivate = msg.equals("[logout]") ? false : msg.startsWith("[") ; //e.g. [abhishek] hey abhi!
        this.recepient = this.isPrivate ? msg.substring(msg.indexOf("[") + 1, msg.lastIndexOf("]")) : "ALL";
        
    }
    
    public String getMsg(){
        return this.msg;
    }
    
    public String getRecepient(){
        return this.recepient;
    }
    
    public boolean isPrivate(){
        return this.isPrivate;
    }
    
    public void from(String from){
        this.sender = from;
    }
    
    public String from(){
        return this.sender;
    }

    @Override
    public String toString() {
        return "ChatMessage{" + "msg=" + msg + ", recepient=" + recepient + ", isPrivate=" + isPrivate + '}';
    }
    
    
  
}
