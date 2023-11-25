package implementation.backend.javaImplementation;

public class creditCard {
    
    protected int cardNumber;
    protected int ccv;
    protected int expiryDate;

    public creditCard(int cardNumber, int ccv, int expiryDate) {
        this.cardNumber = cardNumber;
        this.ccv = ccv;
        this.expiryDate = expiryDate;
    }

    public boolean isValid() {
        return false;
    }
    
}
