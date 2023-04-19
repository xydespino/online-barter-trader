package ca.dal.cs.onlinebartertrader;

import java.io.Serializable;
 /**
 * Represents the current user
 */

public class User  implements Serializable {
    private String username;
    private String emailAddress;
    private String password;
    private String firstName;
    private String lastName;

    public User(){
    }

     /**
     * Building method to create a new User Object
     * @param username: Username of the current user
     * @param emailAddress: email address of the current user
     * @param password: password of the current user
     * @param firstName: first name of the current user
     * @param lastName: last name of the current user
     */
    public User(String firstName, String lastName, String username, String emailAddress, String password){
        this.username = username;
        this.emailAddress = emailAddress;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public void setEmailAddress(String emailAddress){
        this.emailAddress = emailAddress;
    }
    public void setPassword(String password){
        this.password = password;
    }

    // Getters
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getUsername() {
        return username;
    }
    public String getEmailAddress() {
        return emailAddress;
    }
    public String getPassword() {
        return password;
    }
}
