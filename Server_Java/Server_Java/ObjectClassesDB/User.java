package Server_Java.ObjectClassesDB;

public class User {

    private int userID;
    private String userName;
    private String password;
    private String status;

    public User(int userID, String userName, String password, String status) {
        this.userID = userID;
        this.userName = userName;
        this.password = password;
        this.status = status;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
