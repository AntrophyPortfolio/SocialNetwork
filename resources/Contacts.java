package resources;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Contacts {

    private SimpleStringProperty login;
    private SimpleStringProperty name;
    private SimpleStringProperty lastName;
    private int userID;
    private int contactID;
    private SimpleIntegerProperty ban;

    public Contacts(int userID, int contactID, String login, String name, String lastName, int ban) {
        this.login = new SimpleStringProperty(login);
        this.name = new SimpleStringProperty(name);
        this.lastName = new SimpleStringProperty(lastName);
        this.userID = userID;
        this.contactID = contactID;
        this.ban = new SimpleIntegerProperty(ban);

    }

    public String getLogin() {
        return login.get();
    }

    public void setLogin(String login) {
        this.login = new SimpleStringProperty(login);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name = new SimpleStringProperty(name);
    }

    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String lastName) {
        this.lastName = new SimpleStringProperty(lastName);
    }

    public int getAccountID() {
        return userID;
    }

    public void setAccountID(int userID) {
        this.userID = userID;
    }

    public int getContactID() {
        return contactID;
    }

    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    public int getBan() {
        return ban.get();
    }

    public void setBan(int ban) {
        this.ban = new SimpleIntegerProperty(ban);
    }
}
