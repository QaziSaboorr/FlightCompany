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
    
}
