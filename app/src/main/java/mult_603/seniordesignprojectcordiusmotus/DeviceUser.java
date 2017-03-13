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
//    private ArrayList<Contact> contactList;
//    private LocationHolder locationHolder;

    // Initializer
    public DeviceUser(){ }

    public DeviceUser(String email, String userName){
        this.email = email;
        this.userName = userName;
    }

    // Getters
    public String getUserImage(){ return this.userImage; }
    public String getEmail(){
        return this.email;
    }
    public String getUserName(){
        return this.email;
    }
    public String getUuid(){
        return this.uuid;
    }
//    public ArrayList<Contact> getContactList(){
//        return this.contactList;
//    }
//    public LocationHolder getLocationHolder(){ return this.locationHolder; }

    // Add contact to device user's contact list
//    public void addContactToDeviceUsersList(Contact c){
//        this.contactList.add(c);
//    }

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
//    public void setContactList(ArrayList<Contact> cList){
//        this.contactList = cList;
//    }
//    public void setLocationHolder(LocationHolder lh){
//        this.locationHolder = lh;
//    }

    public String toString(){
        return "Email: " + this.email +"\n"
                + "User Name: " + this.userName + "\n"
                + "UUID: " + this.uuid;
    }
}
