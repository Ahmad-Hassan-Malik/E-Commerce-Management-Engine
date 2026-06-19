import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {
    String oID;
    String customer;
    String city;
    String address;
    String status;
    List<Product> listOfProducts;
    int idCounter = 0;

    public Order(String customer, String city, String address, List<Product> listOfProducts){
        this.oID = String.format("S%03d",idCounter++);
        this.customer = customer;
        this.city = city;
        this.address = address;
        this.listOfProducts = listOfProducts;
        this.status = "Pending";
    }

    public Order(String customer,String city, String address, List<Product> sublist, String a) {
        this.customer = customer;
        this.address = address;
        this.city = city;
        this.listOfProducts = sublist;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("🧾 Order ID: ").append(oID).append("\n");
        sb.append("👤 Customer: ").append(customer).append("\n");
        sb.append("🏠 Address: ").append(address).append(", ").append(city).append("\n");
        sb.append("📦 Status: ").append(status == null ? "Pending" : status).append("\n");
        sb.append("🛒 Products:\n");
        sb.append("-------------------------------------------------------------------------------------\n");
        sb.append(String.format("| %-10s | %-20s | %-10s | %-12s | %-8s | %-6s |\n",
                "Product ID", "Product Name", "Price", "Seller", "Stock", "Rating"));
        sb.append("-------------------------------------------------------------------------------------\n");

        double total = 0;
        for (Product p : listOfProducts) {
            sb.append(String.format("| %-10s | %-20s | $%-9.2f | %-12s | %-8d | %-5.1f★ |\n",
                    p.pID, p.pName, p.pPrice, p.pSellerName, p.pStock, p.pRatings));
            total += p.pPrice;
        }

        sb.append("-------------------------------------------------------------------------------------\n");
        sb.append(String.format("💰 Total Price: $%.2f\n", total));

        return sb.toString();
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
