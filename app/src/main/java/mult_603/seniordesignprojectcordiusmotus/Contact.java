package mult_603.seniordesignprojectcordiusmotus;

/**
 * Created by artisja on 10/1/2016.
 */

public class Contact implements User{
    public final String TAG = Contact.class.getSimpleName();
    private String name,number,email,uuid;;

    // Empty constructor for Fire base
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

    @Override
    public String toString(){
        return    "Name: "    + this.name
                + " Number: " + this.number
                + " Email: "  + this.email
                + " UUID: "   + this.uuid;
    }
}

