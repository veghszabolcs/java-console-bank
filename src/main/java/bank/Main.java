package bank;

import bank.exception.*;
import bank.model.Account;
import bank.model.User;
import bank.service.AccountService;
import bank.service.UserService;

import java.util.List;
import java.util.Scanner;

public class Main {

    //helper methods
    public static void listAccounts(){
        for (int i = 0; i < accounts.size(); i++) {
            System.out.println("["+(i+1)+"] "+accounts.get(i));
        }
    }

    public static boolean checkNoAccount(){
        if (noOfAccounts <= 0) {
            System.out.println("You have no accounts! ");
            return true;
        }
        return false;
    }

    public static boolean checkIndexOkay(int index){
        if (index < 0 || index >= accounts.size()) {
            System.out.println("No account found!");
            return false;
        }
        return true;
    }


    //session data
    static List<Account> accounts;
    static int noOfAccounts = -1;
    static User user = null;

    public static void main(String[] args){
        boolean exit = false;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Command line banking application\n");

        while(!exit){
            if(user == null){
                System.out.println("""
                        Choose an option:
                        [1] Create new user
                        [2] Log in to existing user
                        [9] Exit""");

                String option = scanner.nextLine();
                String email;
                String password;

                switch(option){
                    case "1":
                        System.out.println("\nCreating a new user:");

                        System.out.println("Enter email address: ");
                        email = scanner.nextLine();
                        System.out.println("Enter password: ");
                        password = scanner.nextLine();

                        try{
                            UserService.createUser(email, password);
                            System.out.println("User created successfully, try logging in");
                        } catch (UserAlreadyExistsException e) {
                            System.err.println("User with this email already exists!");
                        } catch (AuthenticationServiceException e) {
                            throw new RuntimeException("Server error, try again later");
                        }

                        break;
                    case "2":
                        System.out.println("\nLog in to existing user:");
                        System.out.println("Enter email address: ");
                        email = scanner.nextLine();
                        System.out.println("Enter password: ");
                        password = scanner.nextLine();
                        try{
                            user = UserService.loginUser(email, password);
                            System.out.println("User logged in successfully");
                        } catch (InvalidCredentialsException e) {
                            System.err.println("Invalid credentials!");
                        } catch (AuthenticationServiceException e) {
                            throw new RuntimeException("Server error, try again later");
                        }
                        break;
                    case "9":
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid option, try again!");
                }
            }else {

                try {
                    accounts = AccountService.getAllAccounts(user);
                    noOfAccounts = accounts.size();
                } catch (AuthenticationServiceException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("""
                        \nChoose an option:
                        [1] List accounts
                        [2] Transfer money
                        [3] Deposit money
                        [4] Withdraw money
                        [5] Create account
                        [6] Delete account
                        [7] Log out
                        [9] Exit""");
                String option = scanner.nextLine();

                switch(option){
                    case "1":
                        System.out.println("\nList of accounts:");

                        if (checkNoAccount()) break;

                        listAccounts();
                        break;
                    case "2":
                        System.out.println("\nTransfer money:");
                        if (checkNoAccount()) break;

                        System.out.println("Choose an account to transfer from:");
                        listAccounts();

                        int fromAccountIndex = Integer.parseInt(scanner.nextLine()) - 1;
                        if (!checkIndexOkay(fromAccountIndex)) break;

                        System.out.println("Enter the id of the destination account:");
                        int toAccountId = Integer.parseInt(scanner.nextLine());

                        System.out.println("Enter the amount to transfer:");
                        double amount = Double.parseDouble(scanner.nextLine());

                        try{
                            AccountService.transferMoney(accounts.get(fromAccountIndex), toAccountId, amount);
                            System.out.println("Transfer successful");
                        } catch (AccountNotFoundException e) {
                            System.err.println("Invalid account selected, please try again");
                        } catch (AuthenticationServiceException e) {
                            throw new RuntimeException("Server error, try again later");
                        }
                        break;
                    case "3":
                        System.out.println("\nDeposit money:");

                        if (checkNoAccount()) break;

                        System.out.println("Choose an account to deposit to: ");
                        listAccounts();
                        int depositAccountIndex = Integer.parseInt(scanner.nextLine()) - 1;

                        if (!checkIndexOkay(depositAccountIndex)) break;

                        System.out.println("Enter amount to deposit:");
                        double depositAmount = Double.parseDouble(scanner.nextLine());
                        try {
                            AccountService.depositMoney(accounts.get(depositAccountIndex), depositAmount);
                            System.out.println("Successfully deposited money");
                        } catch (AccountNotFoundException e) {
                            System.err.println("Invalid account selected, please try again");
                        } catch (AuthenticationServiceException e) {
                            throw new RuntimeException("Server error, try again later");
                        }
                        break;

                    case "4":
                        System.out.println("\nWithdraw money:");
                        System.out.println("Choose an account to withdraw: ");

                        if (checkNoAccount()) break;

                        listAccounts();
                        int withdrawAccountIndex = Integer.parseInt(scanner.nextLine()) - 1;

                        if (!checkIndexOkay(withdrawAccountIndex)) break;

                        System.out.println("Enter amount to withdraw:");
                        double withdrawAmount = Double.parseDouble(scanner.nextLine());
                        try {
                            AccountService.withdrawMoney(accounts.get(withdrawAccountIndex), withdrawAmount);
                            System.out.println("Successfully withdrawn money");
                        } catch (AccountNotFoundException e) {
                            System.err.println("Invalid account selected, please try again");
                        } catch (AuthenticationServiceException e) {
                            throw new RuntimeException("Server error, try again later");
                        }
                        break;
                    case "5":
                        System.out.println("\nCreate account:");
                        System.out.println("Are you sure you want to create a new account? [Y/N]");

                        String answer = scanner.nextLine();

                        if (answer.equals("Y")) {
                            try {
                                AccountService.createAccount(user);
                                System.out.println("Account created successfully");
                            } catch (AuthenticationServiceException e) {
                                throw new RuntimeException("Server error, try again later");
                            }
                        }
                        break;

                    case "6":
                        System.out.println("\nDelete account:");
                        System.out.println("Which account do you want to delete?");
                        listAccounts();
                        int deleteAccountIndex = Integer.parseInt(scanner.nextLine()) - 1;
                        System.out.println("Are you sure you want to delete the account? [Y/N]");
                        String answer2 = scanner.nextLine();
                        if (answer2.equalsIgnoreCase("y")) {
                            try{
                                AccountService.deleteAccount(accounts.get(deleteAccountIndex).getId());
                                System.out.println("Account deleted successfully");
                            }catch (BusinessRuleViolationException e) {
                                System.err.println("Account balance must be 0 in order to delete account");
                            } catch (AccountNotFoundException e) {
                                System.err.println("No such account");
                            } catch (AuthenticationServiceException e) {
                                throw new RuntimeException("Server error, try again later");
                            }
                        }else {
                            System.out.println("Delete cancelled");
                        }
                        break;
                    case "9":
                        exit = true;
                        break;
                }

            }

        }

    }
}
