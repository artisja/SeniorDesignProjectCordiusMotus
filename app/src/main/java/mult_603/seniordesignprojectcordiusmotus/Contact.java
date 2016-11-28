package mult_603.seniordesignprojectcordiusmotus;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by artisja on 10/1/2016.
 */

public class Contact implements User{
    public final String TAG = Contact.class.getSimpleName();
    private String name,number,email,uuid;;

    // Empty constructor for Firebase
    public Contact(){

    }

    public Contact(String newName,String newNumber, String newEmail){
        this.name = newName;
        this.number = newNumber;
        this.email = newEmail;
    }

    public String getName() {
        return this.name;
    }

    public String getNumber() {
        return this.number;
    }

    public String getEmail(){
        return this.email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setEmail(String email){
        this.email = email;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String id) {
        uuid=id;
    }

//    @Override
//    public boolean equals(Object o){
//        if(this == o){
//            return true;
//        }
//        if(o == null){
//            return false;
//        }
//        if(getClass() != o.getClass()){
//            return false;
//        }
//
//        Contact c = (Contact) o;
//        if(!(this.name.equals(c.name) && this.number.equals(c.number) && this.email.equals(c.email))){
//            return false;
//        }
//        return true;
//    }
//
//    // Be Careful as this can return nul if there is no name, number or email for the contact
//    @Override
//    public int hashCode(){
//        int result = 31 * this.name.hashCode() + this.email.hashCode() + this.number.hashCode();
//        Log.i(TAG, "Contact HashCode => " + result);
//        return result;
//    }

    @Override
    public String toString(){
        return    "Name: "    + this.name
                + " Number: " + this.number
                + " Email: "  + this.email
                + " UUID: "   + this.uuid;
    }
}

