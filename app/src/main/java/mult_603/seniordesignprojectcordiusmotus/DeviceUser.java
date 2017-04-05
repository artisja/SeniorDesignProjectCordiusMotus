package mult_603.seniordesignprojectcordiusmotus;

import java.util.ArrayList;

/**
 * Created by Wes on 2/4/17.
 */
public class DeviceUser {
    private String email;
    private String userName;
    private String uuid;
    private String userImage;
    private String shortHash;

    // Empty constructor
    public DeviceUser(){ }

    // Getters
    public String getUserImage(){ return this.userImage; }
    public String getEmail(){
        return this.email;
    }
    public String getUserName(){
        return this.userName;
    }
    public String getUuid(){
        return this.uuid;
    }
    public String getShortHash(){return this.shortHash;}

    // Setters
    public void setUserImage(String userImage){ this.userImage = userImage; }
    public void setEmail(String email){
        this.email = email;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }
    public void setUuid(String uuid){
        this.uuid = uuid;
    }
    public void setShortHash(String shortHash){this.shortHash = shortHash;}

    public String toString(){
        return  "User Name: " + this.userName + "\n"
                + "Email: "   + this.email +"\n"
                + "Short Hash: " + this.shortHash + "\n"
                + "UUID: " + this.uuid;
    }
}
