import java.io.Serializable;
import java.util.HashMap;

public class ProductDatabase implements Serializable {
    HashMap<String, TreeNode> productTreeMap;
    int lastProductIdCounter;
}
