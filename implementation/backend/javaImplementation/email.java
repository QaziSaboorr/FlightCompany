package implementation.backend.javaImplementation;

import java.util.ArrayList;

public class email {
    protected ArrayList<ticket> tickets;
    protected emailAddress eAddress;
    protected receipt receipt;

    public email(ArrayList<ticket> tickets, emailAddress eAddress, receipt receipt) {
        this.tickets = tickets;
        this.eAddress = eAddress;
        this.receipt = receipt;
    }

    // Getters
    public ArrayList<ticket> getTickets() {
        return this.tickets;
    }

    public emailAddress getEmailAddress() {
        return this.eAddress;
    }

    public receipt getReceipt() {
        return this.receipt;
    }

    // Setters
    public void setTickets(ArrayList<ticket> t) {
        this.tickets = t;
    } 

    public void setEmailAddress(emailAddress email) {
        this.eAddress = email;
    }

    public void setReceipt(receipt r) {
        this.receipt = r;
    }

    // Other methods
    public void print() {

    }
    
}
