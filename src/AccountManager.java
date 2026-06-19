import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class AccountManager {

    Scanner scanner = new Scanner(System.in);
    private HashMap<String, Seller> sellerMap = new HashMap<>();
    private HashMap<String, Customer> customerMap = new HashMap<>();
    HashMap<String, Admin> adminMap = new HashMap<>();


    public void welcomeFunction() {
        loadSellersFromFile();
        loadCustomerFromFile();
        System.out.println("=========================================");
        System.out.println("\t\tWelcome to E-Commerce App");
        System.out.println("=========================================\n");

        System.out.println("1. Signup");
        System.out.println("2. Login");
        System.out.println("0. Exit\n");

        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();

        switch (choice) {

            case 1:
                signup();
                break;
            case 2:
                login();
                break;
            case 0:
                break;
            default:
                System.out.println("Invalid Choice!!!");
                welcomeFunction();
        }

    }

    public void loadSellersFromFile() {
        try {
            File file = new File("Seller.txt");
            if (!file.exists()) {
                return;
            }

            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    String[] parts = line.split(",");

                    String userName = parts[1];
                    String password = parts[2];
                    String email = parts[3];
                    String storeName = parts[4];

                    Seller seller = new Seller(userName, password, email, storeName);
                    sellerMap.put(userName, seller);
            }
            fileScanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loadCustomerFromFile() {
        try {
            File file = new File("Customer.txt");
            if (!file.exists()) {
                return;
            }
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");

                String userName = parts[1];
                String password = parts[2];
                String email = parts[3];

                Customer customer = new Customer(userName, password, email);
                customerMap.put(userName, customer);
            }
            fileScanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void signup() {

        System.out.println("=================================");
        System.out.println("\t\tSign Up Form");
        System.out.println("=================================\n");

        System.out.println("1. Signup as Seller");
        System.out.println("2. Signup as Customer");
        System.out.println("0. Exit\n");

        System.out.print("Enter Your choice: ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                registerSeller();
                break;
            case 2:
                registerCustomer();
                break;
            case 0:
                break;
            default:
                System.out.println("Invalid Choice!");
                System.out.println("Enter Again\n");
                signup();
        }

    }

    public void registerSeller() {
        loadSellersFromFile();

        System.out.println("=================================");
        System.out.println("\t\tSeller Signup");
        System.out.println("=================================\n");

        String userName;

        while (true) {
            System.out.print("Enter User Name: ");
            userName = scanner.nextLine();

            if (sellerMap.containsKey(userName)) {
                System.out.println("❌ This userName already exists. Please choose different username.");
            } else {
                break;
            }

        }

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        System.out.print("Enter E-mail: ");
        String email = scanner.nextLine();

        System.out.print("Store Name: ");
        String storeName = scanner.nextLine();

        Seller seller = new Seller(userName,password,email,storeName);

        sellerMap.put(userName,seller);

        System.out.println("\n✅ Seller Registered Successfully.");

        saveUsersToFile("Seller","Seller.txt");

    }


    public void saveUsersToFile(String user, String fileName) {
        try {
            FileWriter writer = new FileWriter(fileName);

            if (user.equals("Seller")) {
                for (Seller seller : sellerMap.values()) {
                    writer.write(seller.toString() + "\n");
                }
            }
            if (user.equals("Customer")) {
                for (Customer customer : customerMap.values()) {
                    writer.write(customer.toString() + "\n");
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerCustomer() {
        loadCustomerFromFile();

        System.out.println("=================================");
        System.out.println("\t\tCustomer Signup");
        System.out.println("=================================\n");

        String userName;

        while (true) {
            System.out.print("Enter User Name: ");
            userName = scanner.nextLine();

            if (customerMap.containsKey(userName)) {
                System.out.println("❌ This userName already exists. Please choose different username.");
            } else {
                break;
            }
        }
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        System.out.print("Enter E-mail: ");
        String email = scanner.nextLine();

        Customer customer = new Customer(userName,password,email);
        customerMap.put(userName,customer);
        System.out.println("\n✅ Customer Registered Successfully.");

        saveUsersToFile("Customer", "Customer.txt");
    }



    public void login() {

        System.out.println("===============================");
        System.out.println("\t\tLogin Form");
        System.out.println("===============================\n");

        System.out.println("Login as:");
        System.out.println("1. Admin");
        System.out.println("2. Seller");
        System.out.println("3. Customer");
        System.out.println("0. Exit\n");

        System.out.print("Enter Your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                adminLogin();
                break;
            case 2:
                sellerLogin();
                break;
            case 3:
                customerLogin();
                break;
            case 0:
                break;
            default:
                System.out.println("Invalid Choice!");
                System.out.println("Enter Again\n");
                login();
        }

    }

    public void adminLogin() {
        loadAdmin();
        System.out.println("=================================");
        System.out.println("\t\tAdmin Login");
        System.out.println("=================================\n");


        System.out.print("Enter UserName: ");
        String userName = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        System.out.print("Enter code: ");
        String adminCode = scanner.nextLine();

        if (adminMap.containsKey(userName)) {
            Admin admin = adminMap.get(userName);
            if (admin.checkPasswordAndAdminCode(password,adminCode)) {
                AdminFunctions adf = new AdminFunctions();
                adf.adminDashboard();
            } else {
                System.out.println("❌ Incorrect Password or Admin code");
                adminLogin();
            }
        } else {
            System.out.println("❌ No Admin with username [" + userName + "]");
        }
    }

    public void loadAdmin() {
        adminMap.put("admin1", new Admin("admin1","pass123","admin1@gmail.com","A001"));
        adminMap.put("admin2", new Admin("admin2","secure123","admin12@gmail.com","A002"));
    }

    public void sellerLogin() {
        System.out.println("=================================");
        System.out.println("\t\tSeller Login");
        System.out.println("=================================\n");

        System.out.print("Enter UserName: ");
        String userName = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        if (sellerMap.containsKey(userName)) {
            Seller seller = sellerMap.get(userName);
            if (seller.checkPassword(password)) {
                SellerFunctions slf = new SellerFunctions();
                slf.sellerDashboard(seller);
            } else {
                System.out.println("❌ Incorrect Password");
                sellerLogin();
            }
        } else {
            System.out.println("❌ No Seller with username [" + userName + "]");
        }
    }

    public void customerLogin() {
        System.out.println("=================================");
        System.out.println("\t\tCustomer Login");
        System.out.println("=================================\n");

        System.out.print("Enter UserName: ");
        String userName = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        if (customerMap.containsKey(userName)) {
            Customer customer = customerMap.get(userName);
            if (customer.checkPassword(password)) {
                CustomerFunctions cfm = new CustomerFunctions();
                cfm.customerDashboard(customer);
            } else {
                System.out.println("❌ Incorrect Password");
                customerLogin();
            }
        } else {
            System.out.println("❌ No Customer with username [" + userName + "]");
        }
    }

    public HashMap<String, Seller> getSellerMap() {
        loadSellersFromFile();
        return sellerMap;
    }
    public HashMap<String, Customer> getCustomerMap() {
        loadCustomerFromFile();
        return customerMap;
    }




}
