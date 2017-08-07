package mult_603.seniordesignprojectcordiusmotus;

/**
 * Created by artisja on 10/1/2016.
 * This is a custom object that allows us to store the object in Firebase
 * This class represents an emergency contact for our patient
 */

public class Contact{
    public final String TAG = Contact.class.getSimpleName();
    private String name,number,email,uuid;
    private Boolean isApproved;
    private static UserTypes contactType = UserTypes.CONTACT;

    // Empty constructor for Firebase
    public Contact(){

    }

    /**
     *
     * @return contact enum
     */
    public UserTypes getContactType(){
        return contactType;
    }


    /**
     * Initialize the contact with a name number and email address
     * @param newName
     * @param newNumber
     * @param newEmail
     */
    public Contact(String newName,String newNumber, String newEmail){
        this.name = newName;
        this.number = newNumber;
        this.email = newEmail;
    }

    /**
     *
     * @return contact's name
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @return contact's number
     */
    public String getNumber() {
        return this.number;
    }

    /**
     *
     * @return contact's email address
     */
    public String getEmail(){
        return this.email;
    }

    /**
     *
     * @return if member approved of app
     */
    public Boolean getApproved() {
        return isApproved;
    }

    /**
     * Set the contact's name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the contact's phone number
     * @param number
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * Set the contact's email address
     * @param email
     */
    public void setEmail(String email){
        this.email = email;
    }

    /**
     *
     * @return contact's unique identifier
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the contact's unique identifier
     * @param id
     */
    public void setUuid(String id) {
        uuid=id;
    }

    /**
     * Set the approval to true if contact accepts
     * @param approved
     */
    public void setApproved(Boolean approved) {
        isApproved = approved;
    }

    /**
     * print out the contacts name, number, email, and unique identifier
     * @return
     */
    @Override
    public String toString(){
        return    "Name: "    + this.name
                + " Number: " + this.number
                + " Email: "  + this.email
                + " UUID: "   + this.uuid;
    }
}

