package entities;

//CHANGED: removed isBlocked() and setBlocked() methods, as well as the blocked field.

import enums.Action;

public abstract class User {
    private String userID;
    private String name;
    private String email;
    private String password;

    // CONSTRUCTOR
    protected User(String userID, String name, String email, String password) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // GETTERS
    public String getUserID() { return userID; }  
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    //SETTERS
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    //ABSTRACT METHODS
    public abstract String displayRole();
    public abstract boolean checkPermission(Action action);
}