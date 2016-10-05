package whatsapp.android.com.whatsapp.model;

public class Message {

    private String userID;
    private String message;

    public Message() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
