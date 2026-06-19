public class Admin extends User {
    String adminCode;
    public Admin(String userName, String password, String email, String adminCode) {
        super(userName, password, email);
        this.adminCode = adminCode;
    }

    public boolean checkPasswordAndAdminCode(String inputPassword, String inputCode) {
        return this.password.equals(inputPassword) && this.adminCode.equals(inputCode);
    }
}
