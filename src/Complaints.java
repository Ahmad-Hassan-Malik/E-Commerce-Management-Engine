import java.io.Serializable;

public class Complaints implements Serializable {
    String cId;
    String cName;
    String text;
    String response;

    public Complaints(String cId, String cName, String text) {
        this.cId = cId;
        this.cName = cName;
        this.text = text;
        this.response = "Pending";
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String toString() {
        return "Customer ID = " + cId + "\n"
                + "Customer Name = " + cName + "\n"
                + "Complaint:\n\t" + text + "\n"
                + "Admin Response = " + response + "\n";
    }
}
