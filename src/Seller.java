import java.util.ArrayList;

public class Seller extends User {
    String storeName;
    String sellerID;
    static int idCounter = 1;
    public Seller(String userName, String password, String email, String storeName) {
        super(userName, password, email);
        this.sellerID = String.format("S%03d", idCounter++);
        this.storeName = storeName;
    }

    @Override
    public String toString() {
        return sellerID + "," + userName + "," + password + "," + email + "," + storeName;
    }

    public boolean checkPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }
}
