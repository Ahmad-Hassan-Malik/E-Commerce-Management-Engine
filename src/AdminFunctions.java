import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class AdminFunctions {
    Scanner scanner = new Scanner(System.in);
    AccountManager ac = new AccountManager();

    private HashMap<String, Seller> sellerMap;
    private HashMap<String, Customer> customerMap;

    private HashMap<String, TreeNode> categoryMap = new HashMap<>();

    public AdminFunctions() {
        this.sellerMap = ac.getSellerMap();
        this.customerMap = ac.getCustomerMap();
    }

    public void adminDashboard() {
        System.out.println("=================================");
        System.out.println("\t\tAdmin Dashboard");
        System.out.println("=================================\n");

        System.out.println("1. Register New Seller");
        System.out.println("2. Register New Customer");
        System.out.println("3. Delete a Seller");
        System.out.println("4. Delete a Customer");
        System.out.println("5. View all Users (Seller & Customer)");
        System.out.println("6. Manage Product Categories");
        System.out.println("0. Logout\n");

        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                ac.registerSeller();
                adminDashboard();
                break;
            case 2:
                ac.registerCustomer();
                adminDashboard();
                break;
            case 3:
                deleteSeller();
                break;
            case 4:
                deleteCustomer();
                break;
            case 5:
                viewAllUsers();
                break;
            case 6:
                manageProductCategory();
                break;
            case 0:
                ac.welcomeFunction();
                break;
            default:
                break;
        }
    }



    public void deleteSeller() {
        System.out.println("=================================");
        System.out.println("\t\tDelete Seller");
        System.out.println("=================================\n");

        System.out.print("Enter userName: ");
        String userName = scanner.nextLine();
        System.out.print("Enter SellerID: ");
        String userID = scanner.nextLine();

        if (sellerMap.containsKey(userName)) {
            Seller seller = sellerMap.get(userName);
            if (seller.sellerID.equals(userID)) {
                sellerMap.remove(userName);
                System.out.println("✅ Seller " + seller.userName +  " Deleted successfully");
            }
        } else {
            System.out.println("❌ Seller not Found");
        }

        ac.saveUsersToFile("Seller", "Seller.txt");
        adminDashboard();
    }

    public void deleteCustomer() {
        System.out.println("=================================");
        System.out.println("\t\tDelete Customer");
        System.out.println("=================================\n");

        System.out.print("Enter userName: ");
        String userName = scanner.nextLine();
        System.out.print("Enter CustomerID: ");
        String userID = scanner.nextLine();

        if (customerMap.containsKey(userName)) {
            Customer customer = customerMap.get(userName);
            if (customer.customerID.equals(userID)) {
                customerMap.remove(userName);
                System.out.println("✅ Customer " + customer.userName +  " Deleted successfully");
            }
        } else {
            System.out.println("❌ Customer not Found");
        }
        ac.saveUsersToFile("Customer", "Customer.txt");
        adminDashboard();
    }

    public void viewAllUsers() {
        System.out.println("=================================");
        System.out.println("\t\tView All User");
        System.out.println("=================================\n");

        System.out.println("1. View Seller");
        System.out.println("2. View Customer");
        System.out.println("0. Back to Dashboard");
        System.out.println("Press any Key to exit\n");

        System.out.print("Enter Your Choice: ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                view("sellers");
                break;
            case 2:
                view("customers");
                break;
            case 0:
                adminDashboard();
                break;
            default:
                break;
        }
    }
    public void view(String user) {

        if (user.equalsIgnoreCase("sellers")) {
            if (sellerMap.isEmpty()) {
                System.out.println("❌ No Sellers Found.");
            } else {
                System.out.println("-----------------------------------------------------------------------------------------");
                System.out.printf("| %-15s | %-20s | %-20s | %-20s |\n", "Seller ID", "Name", "Email", "Shop Name");
                System.out.println("-----------------------------------------------------------------------------------------");
                for (Seller s : sellerMap.values()) {
                    System.out.printf("| %-15s | %-20s | %-20s | %-20s |\n",
                            s.sellerID, s.userName, s.email, s.storeName);
                }
                System.out.println("-----------------------------------------------------------------------------------------");
            }
        }
        if (user.equalsIgnoreCase("customers")) {
            if (customerMap.isEmpty()) {
                System.out.println("❌ No Customers Found.");
            } else {
                System.out.println("---------------------------------------------------------------------");
                System.out.printf("| %-15s | %-20s | %-20s |\n", "Customer ID", "Name", "Email");
                System.out.println("---------------------------------------------------------------------");
                for (Customer c : customerMap.values()) {
                    System.out.printf("| %-15s | %-20s | %-20s |\n",
                            c.customerID, c.userName, c.email);
                }
                System.out.println("---------------------------------------------------------------------");
            }
        }
        System.out.println("\nWhat would you like to do?");
        System.out.println("0. Back to Dashboard");
        System.out.println("1. Logout");
        System.out.println("Press any Key to exit\n");

        System.out.print("Enter Your Choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 0:
                adminDashboard();
                break;
            case 1:
                ac.welcomeFunction();
                break;
            default:
                break;
        }
    }


    public void manageProductCategory() {
        loadMapFromFile();
        System.out.println("=================================");
        System.out.println("\tManage Product Category");
        System.out.println("=================================\n");

        System.out.println("1. Add Category");
        System.out.println("2. Delete Category");
        System.out.println("3. View all categories");
        System.out.println("0. Back to Dashboard");
        System.out.println("Press any key to Exit\n");

        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                addCategory();
                break;
            case 2:
                deleteCategory();
                break;
            case 3:
                viewAllCategories();
                break;
            case 0:
                adminDashboard();
                break;
            default:
                break;
        }
    }


    public void addCategory() {
        System.out.println("=================================");
        System.out.println("\t\tAdd Category");
        System.out.println("=================================\n");

        System.out.print("Enter Category name: ");
        String newCategory = scanner.nextLine();

        if(!categoryMap.containsKey(newCategory)) {
            categoryMap.put(newCategory,new TreeNode());
            saveCategoriesToFile("category.txt");
            System.out.println("✅ Category added Successfully");
        } else {
            System.out.println("This category already exist.");
        }
        adminDashboard();
    }
    public void deleteCategory() {
        System.out.println("=================================");
        System.out.println("\t\tDelete Category");
        System.out.println("=================================\n");

        System.out.print("Enter category name to delete: ");
        String deleteCategory = scanner.nextLine();

        if (categoryMap.containsKey(deleteCategory)) {
            categoryMap.remove(deleteCategory);
            saveCategoriesToFile("category.txt");
            new SellerFunctions().deleteCategoryFromTreeMap(deleteCategory);
            new SellerFunctions().deleteCategoryProductsFromSellerProductsMap(deleteCategory);
            System.out.println("✅ Category deleted successfully.");
        } else {
            System.out.println("❌ This category does not exist.");
        }
        adminDashboard();
    }
    public void viewAllCategories() {
        if (categoryMap.isEmpty()) {
            System.out.println("❌ No category exists");
            adminDashboard();
            return;
        }
        System.out.println("All Categories");
        int counter = 1;
        for (String category : categoryMap.keySet()) {
            System.out.println(counter++ + ". " + category);
        }
        adminDashboard();
    }
    public void saveCategoriesToFile(String fileName) {
        try {
            FileWriter writer = new FileWriter(fileName);
            for (String categoryName : categoryMap.keySet()) {
                writer.write(categoryName + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMapFromFile() {
        try {
            File file = new File("category.txt");
            if (!file.exists()) {
                return;
            }

            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String  line = fileScanner.nextLine();
                categoryMap.put(line,new TreeNode());
            }
            fileScanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, TreeNode> getCategoryMap() {
        loadMapFromFile();
        return categoryMap;
    }
}
