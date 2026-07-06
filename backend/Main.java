import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Email: ");
        String email = sc.nextLine();

        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        LoginService loginService = new LoginService();

        boolean success = loginService.loginStudent(email, password);

        if(success){
            System.out.println("Opening Dashboard...");
        }

        sc.close();
    }
}