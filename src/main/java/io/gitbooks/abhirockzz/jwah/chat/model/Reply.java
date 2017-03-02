package io.gitbooks.abhirockzz.jwah.chat.model;


public class Reply {

    private String msg;
    private String from;

    public Reply(String msg, String from, boolean isPrivate) {
        String msgContent = isPrivate ? msg.substring(msg.lastIndexOf("]") + 1, msg.length()) : msg; 
        String privateWarning = isPrivate ? "(privately) " : "";

        this.msg = privateWarning + "[From: " + from + "] " + msgContent;
        
    }
    
    public Reply(String msg, boolean isPrivate) {
        String msgContent = isPrivate ? msg.substring(msg.lastIndexOf("]") + 1, msg.length()) : msg; 
        String privateWarning = isPrivate ? "(privately) " : "";
        
        this.msg = privateWarning + "[From: " + from + "] " + msgContent;
        
    }
    
    public void from(String from){
        this.from = from;
    }

    public String getMsg() {
        return this.msg;
    }

  
}
