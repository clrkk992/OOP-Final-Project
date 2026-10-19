package entities;

import enums.Action;

public abstract class User {
    private String userID;
    private String name;
    private String contactNumber;
    private String email;
    private String password;
    private boolean blocked;

    // Constructor
    User(String userID, String name, String contactNumber, String email, String password) {
        this.userID = userID;
        this.name = name;
        this.contactNumber = contactNumber;
        this.email = email;
        this.password = password;
        this.blocked = false;
    }

    // GETTERS
    public String getUserID() { return userID; }  
    public String getName() { return name; }
    public String getContactNumber() { return contactNumber; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    //SETTERS
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }

    //ABSTRACT METHODS
    public abstract String displayRole();
    public abstract boolean checkPermission(Action action);
}