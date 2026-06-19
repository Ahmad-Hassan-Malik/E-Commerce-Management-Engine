import java.io.*;
import java.util.*;

public class SellerFunctions {

    Scanner scanner = new Scanner(System.in);
    AdminFunctions adf = new AdminFunctions();
    AccountManager acm = new AccountManager();

    HashMap<String, TreeNode> productTreeMap;
    HashMap<String, ArrayList<Product>> sellerProductsMap = new HashMap<>();
    HashMap<String, ArrayList<Product>> allProductsMap = new HashMap<>();
    HashMap<String, Queue<Order>> sellerOrdersMap;
    HashMap<String, TreeNode> tempMap;
    HashMap<String, Seller> tempSellerMap;

    Seller currentSeller;

    public SellerFunctions() {
        tempMap = adf.getCategoryMap();
        tempSellerMap = acm.getSellerMap();
        updateTreeMapCategories();
        updateSellerProductMap();
    }

    public void sellerDashboard(Seller seller) {
        if(seller != null) {
            currentSeller = seller;
        }
        System.out.println("=================================");
        System.out.println("\t\tSeller Dashboard");
        System.out.println("=================================\n");

        System.out.println("1. Add Product");
        System.out.println("2. Delete Product");
        System.out.println("3. View My Products");
        System.out.println("4. View Orders");
        System.out.println("5. Search product");
        System.out.println("0. Logout\n");

        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                addProduct();
                break;
            case 2:
                deleteProduct();
                break;
            case 3:
                viewMyProducts();
                break;
            case 4:
                viewOrders();
                break;
            case 5:
                searchMyProduct();
                break;
            case 0:
                acm.welcomeFunction();
                break;
            default:
                break;
        }
    }

    public void addProduct() {
        loadProductsFromFile();
        int counter = 1;
        System.out.println("===============================");
        System.out.println("\t\tAdd Product");
        System.out.println("===============================\n");

        System.out.println("select Category to add the Product:");
        for (String category : productTreeMap.keySet()) {
            System.out.println(counter++ + ". " + category);
        }
        System.out.println("0. Back to Dashboard");

        System.out.print("\nEnter category name: ");
        String choice = scanner.nextLine();
        if (choice.equals("0")) {
            sellerDashboard(null);
        }
        add(choice);
    }
    public void add(String category) {
        System.out.println("===============================");
        System.out.println("\t\tAdd Product");
        System.out.println("===============================\n");

        System.out.print("Enter Product Name: ");
        String pName = scanner.nextLine();
        System.out.print("Enter Product Price: ");
        double pPrice = scanner.nextDouble();
        System.out.print("Enter Product Amount: ");
        int pAmount = scanner.nextInt();

        Product product = new Product(pName,currentSeller.userName,currentSeller.sellerID,3.0,category,pAmount,pPrice);
        if (productTreeMap.containsKey(category)) {
            TreeNode root = productTreeMap.get(category);
            TreeNode updatedTree = insertInTree(product,root);
            productTreeMap.put(category,updatedTree);
            System.out.println("✅ Product added Successfully");
            saveProductsInFile();         //   All products by category
            saveSellerProducts(product); //  specific seller products
            storeProductInMap(product);  // store products in map by name for efficient search by name
        } else {
            System.out.println(category + "category does not exist");
        }
        sellerDashboard(null);
    }

    public void saveSellerProducts(Product product) {
        loadSellerProductsFromFile();
        if (sellerProductsMap.containsKey(currentSeller.userName)) {
            ArrayList<Product> list = sellerProductsMap.get(currentSeller.userName);
            list.add(product);
            sellerProductsMap.put(currentSeller.userName, list);
        } else {
            ArrayList<Product> list = new ArrayList<>();
            list.add(product);
            sellerProductsMap.put(currentSeller.userName,list);
        }
        saveSellerProductMapInFile();
    }
    public void saveSellerProductMapInFile() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("sellerProducts.ser"));
            oos.writeObject(sellerProductsMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loadSellerProductsFromFile() {
        File file = new File("sellerProducts.ser");
        if (file.exists() && file.length() > 0) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                Object data = ois.readObject();
                if (data instanceof HashMap) {
                    sellerProductsMap = (HashMap<String, ArrayList<Product>>) data;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public TreeNode insertInTree(Product product, TreeNode node) {
        if (node == null) {
            return new TreeNode(product);
        }

        if (node.value == null) {
            node.value = product;
            return node;
        }

        if (node.value.pPrice > product.pPrice) {
            node.left = insertInTree(product,node.left);
        } else {
            node.right = insertInTree(product,node.right);
        }
            node.height = Math.max(height(node.left),height(node.right)) + 1;
        return rotate(node);
    }
    public TreeNode rotate(TreeNode node) {
        if (height(node.left) - height(node.right) > 1) {
            // Left Heavy
            if (height(node.left.left) - height(node.left.right) > 0) { //  Left Left Case
                return rightRotation(node);
            }
            if (height(node.left.left) - height(node.left.right) < 0) { // Left Right Case
                node.left = leftRotation(node.left);
                return rightRotation(node);
            }
        } else if (height(node.left) - height(node.right) < -1) {
            // Right Heavy
            if (height(node.right.left) - height(node.right.right) < 0) { //  Right Right Case
                return leftRotation(node);
            }
            if (height(node.right.left) - height(node.right.right) > 0) { // Right Left Case
                node.right = rightRotation(node.right);
                return leftRotation(node);
            }
        }
        return node;
    }
    public TreeNode rightRotation(TreeNode node) {
        TreeNode child = node.left;
        TreeNode t = child.right;

        child.right = node;
        node.left = t;
        node.height = Math.max(height(node.left), height(node.right) + 1);
        child.height = Math.max(height(child.left), height(child.right) + 1);

        return child;
    }
    public TreeNode leftRotation(TreeNode node) {
        TreeNode child = node.right;
        TreeNode t = child.left;

        child.left = node;
        node.right = t;
        node.height = Math.max(height(node.left), height(node.right) + 1);
        child.height = Math.max(height(child.left), height(child.right) + 1);

        return child;
    }
    public int height(TreeNode node) {
        if (node == null) {
            return -1;
        }
        return node.height;
    }
//    public void saveProductsInFile(String fileName) {
//        try {
//            ObjectOutputStream object = new ObjectOutputStream(new FileOutputStream(fileName));
//            object.writeObject(productTreeMap);
//            object.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    public void saveProductsInFile() {
        try (ObjectOutputStream object = new ObjectOutputStream(new FileOutputStream("products.ser"))) {
            ProductDatabase database = new ProductDatabase();
            database.productTreeMap = productTreeMap;
            database.lastProductIdCounter = Product.getpCounter(); // Save current counter
            object.writeObject(database);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public void loadProductsFromFile() {
//        try {
//            ObjectInputStream object = new ObjectInputStream(new FileInputStream("products.ser"));
//            productTreeMap = (HashMap<String, Tree>) object.readObject();
//            object.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    public void loadProductsFromFile() {
        try (ObjectInputStream object = new ObjectInputStream(new FileInputStream("products.ser"))) {
            Object loadedData = object.readObject();

            if (loadedData instanceof ProductDatabase) {
                // New format with counter
                ProductDatabase database = (ProductDatabase) loadedData;
                productTreeMap = database.productTreeMap;
                Product.setpCounter(database.lastProductIdCounter);
            } else {
                // Old format (just HashMap)
                productTreeMap = (HashMap<String, TreeNode>) loadedData;
                // Scan products to find max ID
                int maxId = productTreeMap.values().stream()
                        .flatMap(tree -> getAllProducts(tree).stream())
                        .mapToInt(p -> Integer.parseInt(p.pID.substring(1)))
                        .max()
                        .orElse(0); // Default to 0 if no products
                Product.setpCounter(maxId + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private List<Product> getAllProducts(TreeNode tree) {
        List<Product> products = new ArrayList<>();
        if (tree != null) {
            products.addAll(getAllProducts(tree.left));
            if (tree.value != null) products.add(tree.value);
            products.addAll(getAllProducts(tree.right));
        }
        return products;
    }

    public void printMap() {
        loadProductsFromFile();
        for (String category : productTreeMap.keySet()) {
            System.out.println("=== Category: " + category + " ===");
            printTree(productTreeMap.get(category));
            System.out.println("=============================");
        }
    }
    public void printTree(TreeNode node) {     //    Inorder Print
        if (node != null) {
            printTree(node.left);
            if (node.value != null) {
                System.out.println(node.value.toString());
            }
            printTree(node.right);
        }
    }

    public void deleteProduct() {
        loadProductsFromFile();
        int a = 1;
        System.out.println("===============================");
        System.out.println("\t\tDelete Product");
        System.out.println("===============================\n");

        System.out.println("select Category to delete the Product:");
        for (String category : productTreeMap.keySet()) {
            System.out.println(a++ + ". " + category);
        }
        System.out.println("0. Back to Dashboard\n");

        System.out.print("Enter category Name: ");
        String category = scanner.nextLine();
        System.out.print("Enter Product ID: ");
        String pID = scanner.nextLine();
        System.out.print("Enter Product Name: ");
        String pName = scanner.nextLine();

        if (productTreeMap.containsKey(category)) {
            TreeNode root = productTreeMap.get(category);
            TreeNode node = delete(pID,pName,root); // return the root of new Tree
            node.height = Math.max(height(node.left),height(node.right)) + 1;
            node = rotate(node);
            productTreeMap.put(category,node);
            saveProductsInFile();
            deleteProductFromSellerMap(pID,pName);
        } else {
            System.out.println("❌ Category not found.");
        }
        sellerDashboard(null);

    }
    public TreeNode delete(String pID, String pName, TreeNode node) {
        if (node == null) {
            return null;
        }
            node.left = delete(pID,pName,node.left);

        if (node.value.pID.equals(pID) && node.value.pName.equalsIgnoreCase(pName)) {
            if (node.left == null && node.right == null) {
                return null;
            }
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }

            TreeNode successor = findSuitableSuccessor(node.right);
            node.value = successor.value;
            node.right = delete(successor.value.pID,successor.value.pName,node.right);
        }
        node.right = delete(pID,pName,node.right);
        return node;
    }
    public TreeNode findSuitableSuccessor(TreeNode node) {
        if (node == null) {
            return null;
        }
        if (node.left == null) {
            return node;
        }
        return findSuitableSuccessor(node.left);
    }
    public void deleteProductFromSellerMap(String pID, String pName) {
        loadSellerProductsFromFile();
        List<Product> list = sellerProductsMap.get(currentSeller.userName);
        if (list.isEmpty()) {
            System.out.println("❌ No Products found for this Seller");
            sellerDashboard(null);
            return;
        }
        Iterator<Product> iterator = list.iterator();
        boolean found = false;
        while (iterator.hasNext()) {
            Product p = iterator.next();
            if (p.pID.equals(pID) && p.pName.equalsIgnoreCase(pName)) {
                iterator.remove();
                found = true;
                System.out.println("✅ Product removed successfully.");
                break;
            }
        }
        if (!found) {
            System.out.println("❌ Product not found.");
        }
        saveSellerProductMapInFile();
    }

    public void deleteCategoryFromTreeMap(String category) {
        loadProductsFromFile();
        String keyToDelete = null;
        for (String a: productTreeMap.keySet()) {
            if (a.equalsIgnoreCase(category)) {
                keyToDelete = a;
                break;
            }
        }
        if (keyToDelete != null) {
            productTreeMap.remove(keyToDelete);
//            System.out.println("✅ Category \"" + keyToDelete + "\" removed successfully.");
            saveProductsInFile();
        } else {
            System.out.println("❌ Category \"" + category + "\" not found.");
        }
    }

    public void deleteCategoryProductsFromSellerProductsMap(String category) {
        loadSellerProductsFromFile();
        for (String sellerName : sellerProductsMap.keySet()) {
            ArrayList<Product> products = sellerProductsMap.get(sellerName);

            if (products == null || products.isEmpty()) {
                continue;
            }
            Iterator<Product> iterator = products.iterator();
            while (iterator.hasNext()) {
                Product p = iterator.next();
                if (p.pCategory.equalsIgnoreCase(category)) {
                    iterator.remove();
                }
            }

            // Optional: update the map entry with the modified list
            sellerProductsMap.put(sellerName, products);
        }
//        System.out.println("✅ All products from category \"" + category + "\" removed from seller products.");
        saveSellerProductMapInFile();
    }

    public void viewMyProducts() {    //  All Product of Seller
        loadSellerProductsFromFile();
        if (sellerProductsMap.containsKey(currentSeller.userName)) {
            ArrayList<Product> products = sellerProductsMap.get(currentSeller.userName);
            if (products.isEmpty()) {
                System.out.println("❌ No products found for this seller.");
                sellerDashboard(null);
                return;
            }
            System.out.println("\n📦 Your Listed Products:");
            System.out.println("----------------------------------------------------------------------------------------");
            System.out.printf("| %-10s | %-20s | %-10s | %-15s | %-8s | %-6s |\n",
                    "Product ID", "Product Name", "Price", "Category", "Stock", "Rating");
            System.out.println("----------------------------------------------------------------------------------------");
            for (Product p : products) {
                System.out.printf("| %-10s | %-20s | $%-9.2f | %-15s | %-8d | %-5.1f★ |\n",
                        p.pID, p.pName, p.pPrice, p.pCategory, p.pStock, p.pRatings);
            }
            System.out.println("----------------------------------------------------------------------------------------");
        } else {
            System.out.println("❌ Seller not Found");
        }
        sellerDashboard(null);
    }
    public void searchMyProduct() {
        loadSellerProductsFromFile();
        System.out.println("===============================");
        System.out.println("\t\tSearch Product");
        System.out.println("===============================\n");

        System.out.print("Enter the Product Name: ");
        String pName = scanner.nextLine();
        System.out.print("Enter the Product ID: ");
        String pID = scanner.nextLine();

        if (sellerProductsMap.containsKey(currentSeller.userName)) {
            ArrayList<Product> listOfSellerProduct = sellerProductsMap.get(currentSeller.userName);
           // Product searchProduct;
            boolean found = false;

            for (Product product : listOfSellerProduct) {
                if (product.pName.equalsIgnoreCase(pName) && product.pID.equals(pID)) {
                    System.out.println("\n✅ Product Found:");
                    System.out.println("-------------------------------------------------------------------------------------");
                    System.out.printf("| %-10s | %-20s | %-10s | %-12s | %-8s | %-6s |\n",
                            "Product ID", "Product Name", "Price", "Category", "Stock", "Rating");
                    System.out.println("-------------------------------------------------------------------------------------");
                    System.out.printf("| %-10s | %-20s | $%-9.2f | %-12s | %-8d | %-5.1f★ |\n",
                            product.pID, product.pName, product.pPrice, product.pCategory, product.pStock, product.pRatings);
                    System.out.println("-------------------------------------------------------------------------------------");
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("❌ Product not found");
            }
        } else {
            System.out.println("❌ Seller not found");
        }
        sellerDashboard(null);
    }


    public HashMap<String, TreeNode> getProductTreeMap() {
        loadProductsFromFile();
        return productTreeMap;
    }
    public HashMap<String, ArrayList<Product>> getSellerProductsMap() {
        loadSellerProductsFromFile();
        return sellerProductsMap;
    }
    public HashMap<String, ArrayList<Product>> getAllProductsMap() {
        loadallProductsMapFromFile();
        return allProductsMap;
    }


    public void storeProductInMap(Product product) {
        loadallProductsMapFromFile();
        allProductsMap.computeIfAbsent(product.pName, k -> new ArrayList<>()).add(product);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("productsByName.ser"));
            oos.writeObject(allProductsMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loadallProductsMapFromFile() {
        File file = new File("productsByName.ser");
        if (file.exists() && file.length() > 0) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                Object data = ois.readObject();
                if (data instanceof HashMap) {
                    allProductsMap = (HashMap<String, ArrayList<Product>>) data;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void viewOrders() {
        CustomerFunctions cfm = new CustomerFunctions(null);
        sellerOrdersMap = cfm.getSellerOrderMap();
        Queue<Order> queueOfProducts = sellerOrdersMap.get(currentSeller.userName);

        view(queueOfProducts);
    }
    public void view(Queue<Order> queueOfProducts) {
        if (queueOfProducts == null || queueOfProducts.isEmpty()) {
            System.out.println("❌ No Order available");
            sellerDashboard(null);
            return;
        }
        int counter = 1;
        for (Order order : queueOfProducts) {
            System.out.println("\n🧾 Order " + counter++ + " Details:");
            System.out.println("Customer: " + order.customer);
            System.out.println("City: " + order.city);
            System.out.println("Address: " + order.address);
            System.out.println("Status: " + order.status);

            System.out.println("-------------------------------------------------------------------------------------");
            System.out.printf("| %-10s | %-20s | %-10s | %-12s | %-8s | %-6s |\n",
                    "Product ID", "Product Name", "Price", "Seller", "Stock", "Rating");
            System.out.println("-------------------------------------------------------------------------------------");

            double total = 0;
            for (Product p : order.listOfProducts) {
                System.out.printf("| %-10s | %-20s | $%-9.2f | %-12s | %-8d | %-5.1f★ |\n",
                        p.pID, p.pName, p.pPrice, p.pSellerName, p.pStock, p.pRatings);
                total += p.pPrice;
            }

            System.out.println("-------------------------------------------------------------------------------------");
            System.out.printf("🧾 Total Price: $%.2f\n", total);
            System.out.println();
        }

        System.out.print("\nDo you want to process order (yes/no): ");
        String response = scanner.nextLine().trim();
        if (response.equalsIgnoreCase("yes")) {
            processOrder(queueOfProducts);
        } else if (response.equalsIgnoreCase("no")) {
            saveUpdatedSellerOrderMapInFile("Seller-Orders.ser");
            sellerDashboard(null);
        } else {
            System.out.println("❌ Invalid input. Try again.");
            view(queueOfProducts); // 🔁 ask again
        }
    }
    public void processOrder(Queue<Order> queueOfProducts){
        Order processedOrder = queueOfProducts.remove();
        System.out.println("✅ Order for " + processedOrder.customer + " is processed successfully");
        System.out.println("\nThe remaining Orders are given below");
        sellerOrdersMap.put(currentSeller.userName, queueOfProducts);
        view(queueOfProducts);
    }

    public void saveUpdatedSellerOrderMapInFile(String fileName) {
        File file = new File(fileName);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(sellerOrdersMap);
        } catch (Exception e) {
            System.out.println("❌ Error saving Seller orders: " + e.getMessage());
        }
    }


    public void updateTreeMapCategories() {     //   adds newly added categories in product tree map
        loadProductsFromFile();
        for (String key : tempMap.keySet()) {
            if (!productTreeMap.containsKey(key)) {
                productTreeMap.put(key, new TreeNode());  // Add with empty TreeNode
            }
        }
        saveProductsInFile();
    }
    public void updateSellerProductMap() {   //   adds newly added seller in seller product map
        loadSellerProductsFromFile();

        for (String sellerUsername : tempSellerMap.keySet()) {
            if (!sellerProductsMap.containsKey(sellerUsername)) {
                sellerProductsMap.put(sellerUsername, new ArrayList<>()); // Add with empty product list
            }
        }

        saveSellerProductMapInFile();
    }




    public void updateProduct() {}



}
