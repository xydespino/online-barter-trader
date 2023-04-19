package ca.dal.cs.onlinebartertrader.MessagePages;

import java.io.Serializable;

/**
 * Logic for a message object
 */
public class Message implements Serializable{
    private String messageText;
    private String user;


    public Message(){
    }

    /**
     * Creates a message
     * @param message updates the object's message variable from input
     * @param user updattes the object's user variable from input
     */
    public Message(String message, String user){
        this.messageText = message;
        this.user = user;
    }

    // Setters
    public void setMessage(String message){
        this.messageText = message;
    }
    public void setUser(String user){ this.user = user; }

    // Getters
    public String getUser() {
        return user;
    }
    public String getMessage() { return messageText; }


}
