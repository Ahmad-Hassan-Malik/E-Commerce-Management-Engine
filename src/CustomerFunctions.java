import java.io.*;
import java.util.*;

public class CustomerFunctions {
    AccountManager acm = new AccountManager();
    Scanner scanner = new Scanner(System.in);
    AdminFunctions admin = new AdminFunctions();
    SellerFunctions seller = new SellerFunctions();

    HashMap<String, TreeNode> productTreeMap;                 //Category wise products in tree
    HashMap<String, ArrayList<Product>> sellerProductsMap;   //  Specific Products
    HashMap<String, ArrayList<Product>> allProductsMap;     //All Products by name
    List<Product> cart = new ArrayList<>();
    HashMap<String, Queue<Order>> sellerOrdersMap = new HashMap<>();
    HashMap<String, Stack<Order>> customerOrderMap = new HashMap<>();
    List<Product> wishList = new ArrayList<>();
    HashMap<String, List<Complaints>> complaintMap = new HashMap<>();


    Customer currentCustomer;
    int b;

    public CustomerFunctions() {
        productTreeMap = seller.getProductTreeMap();
        sellerProductsMap = seller.getSellerProductsMap();
        allProductsMap = seller.getAllProductsMap();
    }
    public CustomerFunctions(String a) {
        this.b = 0;
    }
    public void customerDashboard(Customer customer) {
        if (customer != null) {
            currentCustomer = customer;
        }
        System.out.println("=================================");
        System.out.println("\t\tCustomer Dashboard");
        System.out.println("=================================\n");

        System.out.println("1. Browse or Search Product");
        System.out.println("2. View Cart");
        System.out.println("3. View Order History");
        System.out.println("4. File a Complaint to Admin");
        System.out.println("5. View Pending Order");
        System.out.println("6. View WishList");
        System.out.println("7. View your Complaints");
        System.out.println("0. Logout\n");

        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                browseProducts();
                break;
            case 2:
                viewCart();
                break;
            case 3:
                viewOrderHistory();
                break;
            case 4:
                complaintToAdmin();
                break;
            case 5:
                pendingOrder();
                customerDashboard(null);
                break;
            case 6:
                viewWishList();
                break;
            case 7:
                viewMyComplaints();
            case 0:
                acm.welcomeFunction();
                break;
            default:
                break;
        }
    }

    public void browseProducts() {
        System.out.println("=================================");
        System.out.println("\t\tSearch Product");
        System.out.println("=================================\n");

        System.out.println("1. Search by Category");
        System.out.println("2. Search by Seller");
        System.out.println("3. Search by Product Name");
        System.out.println("0. Back to Dashboard\n");

        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                searchByCategory();
                break;
            case 2:
                searchBySeller();
                break;
            case 3:
                globalSearch();
                return;
            case 0:
                customerDashboard(null);
                break;
            default:
                break;
        }
    }

    // Dashboard Functions

    public void viewCart() {
        if (cart.isEmpty()) {
            System.out.println("❌ No Products available");
            return;
        }
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.printf("| %-10s | %-20s | %-10s | %-12s | %-8s | %-6s |\n",
                "Product ID", "Product Name", "Price", "Seller", "Stock", "Rating");
        System.out.println("-------------------------------------------------------------------------------------");
        double total = 0;
        for (Product p : cart) {
            System.out.printf("| %-10s | %-20s | $%-9.2f | %-12s | %-8d | %-5.1f★ |\n",
                    p.pID, p.pName, p.pPrice, p.pSellerName, p.pStock, p.pRatings);
            total = total+p.pPrice;
        }
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.printf("🧾 Total Price: $%.2f\n\n", total);

        System.out.println("What would you like to do?");
        System.out.println("1. Remove product from cart");
        System.out.println("2. Place Order");
        System.out.println("0. Back to dashboard\n");

        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.print("Enter Product ID to remove (Separated by comma): ");
                String a = scanner.nextLine();
                String[] ids = a.split(",");
                removeProductFromCart(ids);
                break;
            case 2:
                placeOrder();
                System.out.println("✅ Order Placed Successfully");
                customerDashboard(null);
                break;
            case 0:
                customerDashboard(null);
                break;
            default:
                break;
        }
    }




    public void viewOrderHistory() {
        getCustomerOrderMapFromFile("Customer-Orders.ser");

        Stack<Order> stack = customerOrderMap.get(currentCustomer.userName);

        if (stack == null || stack.isEmpty()) {
            System.out.println("❌ No orders found for " + currentCustomer.userName + ".");
            customerDashboard(null);
        }

        for (Order order : stack) {
            System.out.println("\n🧾 Order Details:");
            System.out.println("Customer: " + order.customer);
            System.out.println("City: " + order.city);
            System.out.println("Address: " + order.address);
            System.out.println("Status: " + order.status);

            System.out.println("-------------------------------------------------------------------------------------");
            System.out.printf("| %-10s | %-20s | %-10s | %-12s | %-8s | %-6s |\n",
                    "Product ID", "Product Name", "Price", "Seller", "Stock", "Rating");
            System.out.println("-------------------------------------------------------------------------------------");

            double total = 0;
           // int counter = 0;
            for (Product p : order.listOfProducts) {
                System.out.printf("| %-10s | %-20s | $%-9.2f | %-12s | %-8d | %-5.1f★ |\n",
                        p.pID, p.pName, p.pPrice, p.pSellerName, p.pStock, p.pRatings);
                total += p.pPrice;
               // counter++;
            }

            System.out.println("-------------------------------------------------------------------------------------");
//            double deliverFee = total*100;       Future Pending
//            System.out.println("Your delivery Fee " + counter +  " × 100 = " + deliverFee);
//            total = total + deliverFee;
            System.out.printf("🧾 Total Price: $%.2f\n", total);
            System.out.println("============================================================\n");
        }
        customerDashboard(null);
    }
    public void pendingOrder() {
        getCustomerOrderMapFromFile("Customer-Orders.ser");
        Stack<Order> stack = customerOrderMap.get(currentCustomer.userName);
        if (stack == null || stack.isEmpty()) {
            System.out.println("❌ No orders found.");
            return;
        }
        Order order = stack.peek();

        if (order.status.equalsIgnoreCase("Delivered")) {
            System.out.println("📦 No pending orders. Your latest order has already been delivered.");
            return;
        }
        System.out.println("\n🧾 Order Details:");
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
        System.out.println("============================================================\n");
    }
    public void viewWishList() {
        getWishList("wishList.ser");

        if (wishList.isEmpty()) {
            System.out.println("📭 Your wish list is empty.");
            return;
        }

        System.out.println("\n📌 Your Wish List:");
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.printf("| %-10s | %-20s | %-10s | %-12s | %-8s | %-6s |\n",
                "Product ID", "Product Name", "Price", "Seller", "Stock", "Rating");
        System.out.println("-------------------------------------------------------------------------------------");

        for (Product p : wishList) {
            System.out.printf("| %-10s | %-20s | $%-9.2f | %-12s | %-8d | %-5.1f★ |\n",
                    p.pID, p.pName, p.pPrice, p.pSellerName, p.pStock, p.pRatings);
        }

        System.out.println("-------------------------------------------------------------------------------------");

        addInCart(wishList);
    }
    public void addProductInWishList(List<Product> list) {
        getWishList("wishList.ser");
        System.out.print("\nDo you want to add any product to Wish List? (yes/no): ");
        String response = scanner.nextLine();

        if (response.equalsIgnoreCase("yes")) {
            System.out.print("Enter Product ID to add (Separated by comma): ");
            String productID = scanner.nextLine();
            String[] ids = productID.split(",");

            int addedCount = 0;
            for (String id : ids) {
                String trimmedID = id.trim();
                Product selectedProduct = null;

                for (Product p : list) {
                    if (p.pID.equalsIgnoreCase(trimmedID)) {
                        selectedProduct = p;
                        break;
                    }
                }

                if (selectedProduct != null) {
                    wishList.add(selectedProduct);
                    addedCount++;
                } else {
                    System.out.println("❌ Product ID '" + trimmedID + "' not found.");
                }
            }
            if (addedCount > 0) {
                System.out.println("✅ " + addedCount + " product(s) added to wish list successfully.");
                saveWishListInFile("wishList.ser");
            } else {
                System.out.println("⚠️ No products were added to the wish list.");
            }
        } else {
            System.out.println("👍 No product added to wish list.");
        }
        customerDashboard(null);
    }
    public void saveWishListInFile(String fileName) {
        File file = new File(fileName);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(wishList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getWishList(String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.length() > 0) {
            try {
                ObjectInputStream ooi = new ObjectInputStream(new FileInputStream(file));
                wishList = (List<Product>) ooi.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("⚠️ File not found");
        }
    }


    // Helper Functions of Browse Products

    public void searchByCategory() {
        System.out.println();
        int a = 1;
        for (String category : productTreeMap.keySet()) {
            System.out.println(a++ + ". " + category);
        }

        System.out.print("\nEnter category name: ");
        scanner.nextLine();
        String choice = scanner.nextLine();
        boolean found = false;

        for (String category : productTreeMap.keySet()) {
            if (category.equalsIgnoreCase(choice)) {
                found = true;
                TreeNode tree = productTreeMap.get(category);

                System.out.println("\nWhat would you like to do?");
                System.out.println("1. View all products in this category");
                System.out.println("2. Search product by name");
                System.out.println("3. View products sorted by price");
                System.out.println("0. Back to Dashboard");

                System.out.print("Enter your choice: ");
                int option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1:
                        inorderTree(tree);
                        customerDashboard(null);
                        break;
                    case 2:
                        System.out.print("Enter Product Name to search: ");
                        String name = scanner.nextLine();
                        List<Product> list = new ArrayList<>();
                        searchProductByName(tree,name,list);
                        if (list.isEmpty()) {
                            System.out.println("❌ No Product found...!!!");
                        } else {
                            System.out.println("-------------------------------------------------------------------------------------");
                            System.out.printf("| %-10s | %-20s | %-10s | %-12s | %-8s | %-6s |\n",
                                    "Product ID", "Product Name", "Price", "Seller", "Stock", "Rating");
                            System.out.println("-------------------------------------------------------------------------------------");

                            for (Product p : list) {
                                System.out.printf("| %-10s | %-20s | %-9.2f | %-12s | %-8d | %-5.1f★ |\n",
                                        p.pID, p.pName, p.pPrice, p.pSellerName, p.pStock, p.pRatings);
                            }
                            System.out.println("-------------------------------------------------------------------------------------");
                            addToCartProduct(list);
                            customerDashboard(null);
                        }
                        break;
                    case 3:
                        sortedByPrice(tree);
                        customerDashboard(null);
                        break;
                    case 0:
                        customerDashboard(null);
                        break;
                    default:
                        System.out.println("❌ Invalid Option");
                }
                break;
            }
        }
        if (!found) {
            System.out.println("❌ Category Not found");
        }
    }
    //  Helper Function of search by category
    public void searchProductByName(TreeNode node, String name, List<Product> list) {
        if (node == null) {
            return;
        }
        if (node.value.pName.equalsIgnoreCase(name)) {
            list.add(node.value);
        }
        searchProductByName(node.left,name,list);
        searchProductByName(node.right,name,list);
    }
    public void sortedByPrice(TreeNode node) {
        System.out.println("1. Sort from low to high");
        System.out.println("2. Sort from high to low");
        System.out.println("0. Back to dashboard");

        System.out.print("\nEnter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                inorderTree(node);
                break;
            case 2:
                postorderTree(node);
                break;
            case 0:
                customerDashboard(null);
                break;
            default:
                break;
        }
    }
    public void postorderTree(TreeNode node) {
        if (node == null) {
            System.out.println("❌ No products in the tree.");
            return;
        }
        List<Product> list = new ArrayList<>();
        System.out.println("--------------------------------------------------------------------------------------");
        System.out.printf("| %-10s | %-20s | %-10s | %-15s | %-10s | %-8s |\n",
                "ProductID", "Product Name", "Price", "Seller", "In Stock", "Rating");
        System.out.println("--------------------------------------------------------------------------------------");
        printPostorderRows(node,list);
        System.out.println("--------------------------------------------------------------------------------------");
        addToCartProduct(list);
    }
    public void printPostorderRows(TreeNode node, List<Product> list) {
        if (node != null) {
            printPostorderRows(node.right,list);
            if (node.value != null) {
                Product p = node.value;
                System.out.printf("| %-10s | %-20s | %-9.2f | %-15s | %-10d | %-5.1f★ |\n",
                        p.pID, p.pName, p.pPrice, p.pSellerName, p.pStock, p.pRatings);
                list.add(p);
            }
            printPostorderRows(node.left,list);
        }
    }
    public void inorderTree(TreeNode node) {
        if (node == null) {
            System.out.println("❌ No products in the tree.");
            return;
        }
        List<Product> list = new ArrayList<>();
        System.out.println("--------------------------------------------------------------------------------------");
        System.out.printf("| %-10s | %-20s | %-10s | %-15s | %-10s | %-8s |\n",
                "ProductID", "Product Name", "Price", "Seller", "In Stock", "Rating");
        System.out.println("--------------------------------------------------------------------------------------");
        printInorderRows(node,list);
        System.out.println("--------------------------------------------------------------------------------------");
        addToCartProduct(list);
    }
    public void printInorderRows(TreeNode node, List<Product> list) {
        if (node != null) {
            printInorderRows(node.left,list);
            if (node.value != null) {
                Product p = node.value;
                System.out.printf("| %-10s | %-20s | %-9.2f | %-15s | %-10d | %-5.1f★ |\n",
                        p.pID, p.pName, p.pPrice, p.pSellerName, p.pStock, p.pRatings);
                list.add(p);
            }
            printInorderRows(node.right,list);
        }
    }


    public void searchBySeller() {
        System.out.println();
        int a = 1;
        for (String seller : sellerProductsMap.keySet()) {
            System.out.println(a++ + ". " + seller);
        }

        System.out.print("\nEnter Seller name: ");
        scanner.nextLine();
        String choice = scanner.nextLine();
        boolean found = false;

        for (String seller : sellerProductsMap.keySet()) {
            if (seller.equalsIgnoreCase(choice)) {
                found = true;
                List<Product> sellerProducts = sellerProductsMap.get(seller);

                System.out.println("\nWhat would you like to do?");
                System.out.println("1. View All Products Of Seller");
                System.out.println("2. Search Product by name");
                System.out.println("3. View Sorted Products of Seller");
                System.out.println("0. Back to dashboard");

                System.out.print("\nEnter your choice: ");
                int option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1:
                        viewAllProductsOfSpecificSeller(sellerProducts);
                        customerDashboard(null);
                        break;
                    case 2:
                        System.out.print("Enter Product name: ");
                        scanner.nextLine();
                        String name = scanner.nextLine();
                        Product p = searchProductOfSpecificSellerByName(name,sellerProducts);
                        if(p == null) {
                            System.out.println("❌ Product not found...!!!");
                        } else {
                            System.out.println(p);
                            System.out.println("\nDo you want to add it in cart: ");
                            String result = scanner.nextLine();

                            if (result.equalsIgnoreCase("yes")) {
                                cart.add(p);
                                System.out.println("✅ Product added successfully in cart");
                            } else {
                                System.out.println("👍 No Product added in cart");
                            }
                        }
                        customerDashboard(null);
                        break;
                    case 3:
                        viewSortedProductsOfSpecificSeller(sellerProducts);
                        customerDashboard(null);
                        break;
                    case 0:
                        customerDashboard(null);
                    default:
                        System.out.println("❌ Invalid Option");
                }
                break;
            }
        }
        if (!found) {
            System.out.println("❌ Seller Not found");
        }
    }
    //  Helper Function of search by seller
    public void viewAllProductsOfSpecificSeller(List<Product> list) {
        if (list.isEmpty()) {
            System.out.println("❌ No Products available");
            return;
        }
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.printf("| %-10s | %-20s | %-10s | %-12s | %-8s | %-6s |\n",
                "Product ID", "Product Name", "Price", "Seller", "Stock", "Rating");
        System.out.println("-------------------------------------------------------------------------------------");

        for (Product p : list) {
            System.out.printf("| %-10s | %-20s | $%-9.2f | %-12s | %-8d | %-5.1f★ |\n",
                    p.pID, p.pName, p.pPrice, p.pSellerName, p.pStock, p.pRatings);
        }
        System.out.println("-------------------------------------------------------------------------------------");
        addToCartProduct(list);
    }
    public Product searchProductOfSpecificSellerByName(String name, List<Product> list) {
            for (Product p : list) {
                if (p.pName.equalsIgnoreCase(name)) {
                    return p;
                }
            }
            return null;
    }
    public void viewSortedProductsOfSpecificSeller(List<Product> list) {
        System.out.println("1. Sort from low to high price");
        System.out.println("2. Sort from high to low price");
        System.out.println("0. Back to dashboard");

        System.out.print("\nEnter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                list.sort(Comparator.comparingDouble(p -> p.pPrice));
                viewAllProductsOfSpecificSeller(list);
                break;
            case 2:
                list.sort(Comparator.comparingDouble((Product p) -> p.pPrice).reversed());
                viewAllProductsOfSpecificSeller(list);
                break;
            case 0:
                customerDashboard(null);
                break;
            default:
                break;
        }
    }

    public void addToCartProduct(List<Product> list) {

        System.out.println("\n1. Add Product in Cart");
        System.out.println("2. Add Product in your WishList");
        System.out.println("0. 🔙 to Dashboard");

        System.out.print("\nEnter you choice: ");
        int option = scanner.nextInt();
        scanner.nextLine();

        switch (option) {
            case 1:
                addInCart(list);
                break;
            case 2:
                addProductInWishList(list);
                break;
            case 0:
                customerDashboard(null);
            default:
                break;
        }

    }
    public void addInCart(List<Product> list) {
        System.out.print("\nDo you want to add any product to cart? (yes/no): ");
        String response = scanner.nextLine();

        if (response.equalsIgnoreCase("yes")) {
            System.out.print("Enter Product ID to add (Separated by comma): ");
            String productID = scanner.nextLine();
            String[] ids = productID.split(",");

            int addedCount = 0;
            for (String id : ids) {
                String trimmedID = id.trim();
                Product selectedProduct = null;

                for (Product p : list) {
                    if (p.pID.equalsIgnoreCase(trimmedID)) {
                        selectedProduct = p;
                        break;
                    }
                }

                if (selectedProduct != null) {
                    cart.add(selectedProduct);
                    addedCount++;
                } else {
                    System.out.println("❌ Product ID '" + trimmedID + "' not found.");
                }
            }
            if (addedCount > 0) {
                System.out.println("✅ " + addedCount + " product(s) added to cart successfully.");
            } else {
                System.out.println("⚠️ No products were added to the cart.");
            }
        } else {
            System.out.println("👍 No product added to cart.");
        }
        customerDashboard(null);
    }




    public void removeProductFromCart(String[] ids) {
        for (String id : ids) {
            String trimmedID = id.trim();

            Iterator<Product> iterator = cart.iterator();
            while (iterator.hasNext()) {
                Product p = iterator.next();
                if(trimmedID.equalsIgnoreCase(p.pID)) {
                    iterator.remove();
                }
            }
        }
        System.out.println("✅ Products removed Successfully.");
        System.out.println("Now your updated cart is given below⬇️\n");
        viewCart();
    }
    public void placeOrder() {
        getCustomerOrderMapFromFile("Customer-Orders.ser");
        System.out.print("Enter your Address: ");
        String address = scanner.nextLine();
        System.out.print("Enter your city: ");
        String city = scanner.nextLine();
        Order order = new Order(currentCustomer.userName,city,address,cart);
        if (customerOrderMap.containsKey(currentCustomer.userName)) {
            Stack<Order> updatedStack = customerOrderMap.get(currentCustomer.userName);
            updatedStack.push(order);
            customerOrderMap.put(currentCustomer.userName,updatedStack);
        } else {
            Stack<Order> orderStack = new Stack<>();
            orderStack.push(order);
            customerOrderMap.put(currentCustomer.userName, orderStack);
        }
        saveCustomerOrderMapInFile("Customer-Orders.ser");
        processOrder(order);
    }

    public void processOrder(Order order) {
        List<Product> list = order.listOfProducts;
        Map<String, List<Product>> splittedSellerMap = new HashMap<>();
        for (Product p: list) {
            if (splittedSellerMap.containsKey(p.pSellerName)) {
                List<Product> updatedList = splittedSellerMap.get(p.pSellerName);
                updatedList.add(p);
                splittedSellerMap.put(p.pSellerName,updatedList);
            } else {
                List<Product> newList = new ArrayList<>();
                newList.add(p);
                splittedSellerMap.put(p.pSellerName, newList);
            }
        }
        makeSubOrders(splittedSellerMap,order);
    }

    public void makeSubOrders(Map<String, List<Product>> splittedSellerMap, Order order) {
        for (String key: splittedSellerMap.keySet()) {
            List<Product> list = splittedSellerMap.get(key);
            Order subOrder = new Order(order.customer, order.city, order.address, list, null);
            getSellerOrderMapFromFile("Seller-Orders.ser");
            if (sellerOrdersMap.containsKey(key)) {
                Queue<Order> orderQueue = sellerOrdersMap.get(key);
                orderQueue.add(subOrder);
              //  sellerOrdersMap.put(key,orderQueue);     works fine without it
            } else {
                Queue<Order> orderQueue = new LinkedList<>();
                orderQueue.add(subOrder);
                sellerOrdersMap.put(key,orderQueue);
            }
        }
        saveSellerOrderMapInFile("Seller-Orders.ser");
    }

    public void saveCustomerOrderMapInFile(String fileName) {
        File file = new File(fileName);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(customerOrderMap);
            } catch (IOException e) {
                System.out.println("❌ Error saving customer orders: " + e.getMessage());
            }
    }
    public void saveSellerOrderMapInFile(String fileName) {
        File file = new File(fileName);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(sellerOrdersMap);
            } catch (Exception e) {
                System.out.println("❌ Error saving Seller orders: " + e.getMessage());
            }
    }
    public void getCustomerOrderMapFromFile (String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.length() > 0) {
            try (ObjectInputStream ooi = new ObjectInputStream(new FileInputStream(file))) {
                customerOrderMap = (HashMap<String, Stack<Order>>) ooi.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        else {
//            System.out.println("⚠️ File not found");
//        }
    }
    public void getSellerOrderMapFromFile(String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.length() > 0) {
            try {
                ObjectInputStream ooi = new ObjectInputStream(new FileInputStream(file));
                sellerOrdersMap = (HashMap<String, Queue<Order>>) ooi.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        else {
//            System.out.println("⚠️ File not found");
//        }
    }

    public void globalSearch() {}
    public void complaintToAdmin() {
        System.out.println("==================================================");
        System.out.println("            ✉ Submit a Complaint to Admin        ");
        System.out.println("==================================================");

        System.out.println("\nEnter your Complaint: ");
        scanner.nextLine();
        String text = scanner.nextLine();

        Complaints complaint = new Complaints(currentCustomer.customerID,currentCustomer.userName,text);
        saveComplaintsInFile("complaintsToAdmin.ser", complaint);
        customerDashboard(null);
    }

    public void getComplaintsFromFile(String filName) {
        File file = new File(filName);
        if(file.exists() && file.length() > 0) {
            try {
                ObjectInputStream ooi = new ObjectInputStream(new FileInputStream(file));
                complaintMap = (HashMap<String, List<Complaints>>) ooi.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void saveComplaintsInFile(String fileName, Complaints complaint) {
        if (complaint == null) {
            System.out.println("❌ No Complaint added");
            return;
        }
        getComplaintsFromFile("complaintsToAdmin.ser");
        List<Complaints> list = complaintMap.get(currentCustomer.userName);
        if (list == null) {
            list = new ArrayList<>(); //  Create new list if not already present
        }
        list.add(complaint);
        complaintMap.put(currentCustomer.userName,list);
        File file = new File(fileName);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(complaintMap);
            System.out.println("✅ Complaints Submitted Successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void viewMyComplaints() {
        getComplaintsFromFile("complaintsToAdmin.ser");
        List<Complaints> listOfComplaint = complaintMap.get(currentCustomer.userName);
        if (listOfComplaint.isEmpty()) {
            System.out.println("❌ No Complaints found");
            customerDashboard(null);
        }
        int counter = 1;
        System.out.println("Your Complaints are given Below⬇️");
        for (Complaints complaint : listOfComplaint) {
            System.out.println("\t\tComplaint no: " + counter++);
            System.out.println("Complaint: " + complaint.text);
            System.out.println("Admin Response = " + complaint.response);
            System.out.println("--------------------------------------------------");
        }
        customerDashboard(null);
    }

    public HashMap<String, Queue<Order>> getSellerOrderMap() {
        getSellerOrderMapFromFile("Seller-Orders.ser");
        return sellerOrdersMap;
    }

    public void printCustomerOrders() {
        getCustomerOrderMapFromFile("Customer-Orders.ser");

        Stack<Order> stack = customerOrderMap.get("MAlik Ahmad");

        if (stack == null || stack.isEmpty()) {
            System.out.println("❌ No orders found for MAlik Ahmad.");
            return;
        }

        for (Order order : stack) {
            System.out.println("\n🧾 Order Details:");
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
            System.out.println("============================================================\n");
        }
    }



}
