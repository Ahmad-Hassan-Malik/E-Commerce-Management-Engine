public class Customer extends User {
    String customerID;
    static int idCounter = 1;

    public Customer(String userName, String password, String email) {
        super(userName, password, email);
        this.customerID = String.format("C%03d", idCounter++);
    }

    public String toString() {
        return customerID + "," + userName + "," + password + "," + email;
    }

    public boolean checkPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }
}
