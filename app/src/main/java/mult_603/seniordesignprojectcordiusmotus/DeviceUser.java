package mult_603.seniordesignprojectcordiusmotus;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Wes on 2/4/17.
 */
public class DeviceUser {
    private String email;
    private String userName;
    private String uuid;
    private ArrayList<Contact> contactList;
    private LocationHolder locationHolder;

    public DeviceUser(){

    }

    public DeviceUser(String email, String userName){
        this.email = email;
        this.userName = userName;
    }

    // Getters

    public String getEmail(){
        return this.email;
    }

    public String getUserName(){
        return this.email;
    }

    public String getUuid(){
        return this.uuid;
    }

    public ArrayList<Contact> getContactList(){
        return this.contactList;
    }

    public LocationHolder getLocationHolder(){
        return this.locationHolder;
    }

    // Setters

    public void setEmail(String email){
        this.email = email;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public void setUuid(String uuid){
        this.uuid = uuid;
    }

    public void setContactList(ArrayList<Contact> cList){
        this.contactList = cList;
    }

    public void setLocationHolder(LocationHolder lh){
        this.locationHolder = lh;
    }

    public String toString(){
        return "Email: " + this.email +"\n"
                + "User Name: " + this.userName + "\n"
                + "UUID: " + this.uuid;
    }
}
