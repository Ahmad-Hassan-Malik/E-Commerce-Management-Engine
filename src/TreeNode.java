import java.io.Serializable;

public class TreeNode implements Serializable {
    TreeNode left;
    TreeNode right;
    Product value;
    public int height;

    public TreeNode(Product value) {
        this.value = value;
        this.right = null;
        this.left = null;
    }
    public TreeNode() {}

    public void setHeight(int height) {
        this.height = height;
    }
}
