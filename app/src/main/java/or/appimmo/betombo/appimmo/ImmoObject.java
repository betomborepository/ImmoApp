package or.appimmo.betombo.appimmo;

/**
 * Created by hp on 28/01/2018.
 */

public class ImmoObject
{
    private int rating;
    private String image;
    private String name;
    private String adresse;
    private String contact;
    private boolean status;
    private  String userEmail;
    private  String ID;
    private  String date;
    private String description;

    public ImmoObject() {

    }

    public ImmoObject(String ID, int rating, String image, String name, String adresse, String contact, boolean status, String userEmail, String date, String description) {
        this.rating = rating;
        this.image = image;
        this.name = name;
        this.adresse = adresse;
        this.contact = contact;
        this.status = status;
        this.userEmail = userEmail;
        this.ID = ID;
        this.date = date;
        this.setDescription(description);

    }


    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }




    public String getID() {
        return this.ID;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
