import java.io.Serializable;

public class Product implements Serializable {
    String pName;
    String pID;
    String pSellerName;
    String pSellerID;
    double pRatings;
    double pPrice;
    String pCategory;
    int pStock;
    private static int pCounter = 1;

    public Product(String pName, String pSellerName, String pSellerID, double pRatings, String pCategory, int pStock, double pPrice) {
        this.pID = String.format("P%03d", pCounter++);
        this.pName = pName;
        this.pSellerName = pSellerName;
        this.pSellerID = pSellerID;
        this.pRatings = pRatings;
        this.pCategory = pCategory;
        this.pStock = pStock;
        this.pPrice = pPrice;
    }

    @Override
    public String toString() {
        return pID + ", " + pName + ", " + pSellerName + ", " + pSellerID + ", " + pCategory + ", " + pPrice + ", " + pStock + ", " + pRatings;
    }

    public static int getpCounter() {
        return pCounter;
    }

    public static void setpCounter(int pCounter) {
        Product.pCounter = pCounter;
    }
}
