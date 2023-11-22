package implementation.backend.javaImplementation;

public class person {

    protected emailAddress eAddress;
    protected phoneNumber phone;
    protected address address;
    protected birthday dateOfBirth;
    protected name name;

    public person(emailAddress eaAddress, phoneNumber phone, address address, 
        birthday dateOfBirth, name name) {
            this.eAddress = eaAddress;
            this.phone = phone;
            this.address = address;
            this.dateOfBirth = dateOfBirth;
            this.name = name;
        }
    
}
