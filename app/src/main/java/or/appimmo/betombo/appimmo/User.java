package or.appimmo.betombo.appimmo;

/**
 * Created by hp on 04/02/2018.
 */

class User {
    private String email;
    private String contact;

    public User()
    {

    }
    public User(String email, String contact) {
        this.setEmail(email);
        this.setContact(contact);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
