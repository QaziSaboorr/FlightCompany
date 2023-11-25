package implementation.backend.javaImplementation;

public class payment {

    protected creditCard card;

    public payment(creditCard card) {
        this.card = card;
    }

    public boolean processPayment() {
        // Check if credit card information is correct
        boolean isCardValid = card.isValid();

        if (isCardValid) {
            // If the card is valid, get ticket and email information from the database
            ticket ticket = Database.getTicket();
            address emailInfo = Database.getEmailInformation();0 dft

            // Check if email information is correct
            if (emailInfo.isValid()) {
                // Make a receipt for the transaction
                receipt receipt = new receipt(this);

                // Email the ticket and receipt
                emailInfo.sendEmail(ticket, receipt);

                // Go back to the home page
                return true;
            } else {
                System.out.println("Invalid email information. Transaction declined.");
            }
        } else {
            System.out.println("Invalid credit card information. Transaction declined.");
        }

        return false;
    }
}
    

