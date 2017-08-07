package mult_603.seniordesignprojectcordiusmotus;

import java.util.ArrayList;

/**
 * Created by Wes on 2/4/17.
 * This class represents the Patient or device wearer that is using our application.
 * This class is used to provide information on who has what vitals and which emergency contacts
 * This helps us understand which user in our database is storing what information
 * Also the short hash is important for sending to emergency contacts
 */
public class DeviceUser {
    private String email;
    private String userName;
    private String uuid;
    private String userImage;
    private String shortHash;
    private boolean isApproved;
    private static UserTypes deviceType = UserTypes.PATIENT;

    // Empty constructor
    public DeviceUser(){
    }

    /**
     * @return
     */
    public UserTypes getDeviceType() {
        return deviceType;
    }

    public static void setDeviceType(UserTypes deviceType) {
        DeviceUser.deviceType = deviceType;
    }

    /**
     * Get the user's image associated with their profile
     * @return
     */
    public String getUserImage(){ return this.userImage; }

    /**
     * Get the user's email address associated with their account
     * @return
     */
    public String getEmail(){
        return this.email;
    }

    /**
     * Get the user's username
     * @return
     */
    public String getUserName(){
        return this.userName;
    }

    /**
     * Get the user's unique identifier that is used to store their information in our datbase
     * @return
     */
    public String getUuid(){
        return this.uuid;
    }

    /**
     * Get the user's short hash code. It is currently 4 digits long
     * @return
     */
    public String getShortHash(){return this.shortHash;}

    /**
     *
     * @return whether doctor is approved
     */
    public boolean isApproved() {
        return isApproved;
    }

    /**
     * Set user's profile image
     * @param userImage
     */
    public void setUserImage(String userImage){ this.userImage = userImage; }

    /**
     * Set user's email address
     * @param email
     */
    public void setEmail(String email){
        this.email = email;
    }

    /**
     * Set user's username
     * @param userName
     */
    public void setUserName(String userName){
        this.userName = userName;
    }

    /**
     * Set user's unique identifier
     * @param uuid
     */
    public void setUuid(String uuid){
        this.uuid = uuid;
    }

    /**
     * Set user's short hash code
     * @param shortHash
     */
    public void setShortHash(String shortHash){this.shortHash = shortHash;}

    /**
     * Print out the user's username, email, short hash and unique identifier
     * Mainly for debugging purposes
     * @return
     */
    @Override
    public String toString(){
        return  "User Name: " + this.userName + "\n"
                + "Email: "   + this.email +"\n"
                + "Short Hash: " + this.shortHash + "\n"
                + "UUID: " + this.uuid;
    }
}
