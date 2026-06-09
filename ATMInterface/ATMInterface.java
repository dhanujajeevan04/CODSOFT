import java.util.ArrayList;
import java.util.Scanner;

public class ATMInterface {

    public static void main(String[] args) {
        BankAccount account = new BankAccount("Rahul Sharma", "ACC-20481", 1234, 15000.00);
        ATMMachine atm = new ATMMachine(account);
        atm.start();
    }
}

class BankAccount {
    private String holderName;
    private String accountNumber;
    private double balance;
    private ArrayList<String> transactionLog;

    public BankAccount(String holderName, String accountNumber, int pin, double initialBalance) {
        this.holderName = holderName;
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.transactionLog = new ArrayList<>();
        transactionLog.add("Account opened. Initial balance: ₹" + String.format("%.2f", initialBalance));
    }

    public double getBalance() { return balance; }
    public String getHolderName() { return holderName; }
    public String getAccountNumber() { return accountNumber; }

    public boolean deposit(double amount) {
        if (amount <= 0) return false;
        balance += amount;
        transactionLog.add("CREDIT  +₹" + String.format("%.2f", amount) + "  | Balance: ₹" + String.format("%.2f", balance));
        return true;
    }

    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance) return false;
        balance -= amount;
        transactionLog.add("DEBIT   -₹" + String.format("%.2f", amount) + "  | Balance: ₹" + String.format("%.2f", balance));
        return true;
    }

    public ArrayList<String> getRecentTransactions(int count) {
        int size = transactionLog.size();
        return new ArrayList<>(transactionLog.subList(Math.max(0, size - count), size));
    }
}

class ATMMachine {
    private BankAccount account;
    private Scanner input;
    private static final int CORRECT_PIN = 1234;
    private static final int MAX_PIN_TRIES = 3;
    private static final double WITHDRAWAL_LIMIT = 20000.00;

    public ATMMachine(BankAccount account) {
        this.account = account;
        this.input = new Scanner(System.in);
    }

    public void start() {
        printWelcome();
        if (!authenticateUser()) {
            System.out.println("\n  🔒 Card blocked after too many wrong PINs. Contact your bank.");
            input.close();
            return;
        }
        showDashboard();
        mainMenu();
        System.out.println("\n  Thank you for using our ATM. Goodbye, " + account.getHolderName() + "!");
        input.close();
    }

    private boolean authenticateUser() {
        System.out.println("\n  Please enter your 4-digit PIN:");
        for (int attempt = 1; attempt <= MAX_PIN_TRIES; attempt++) {
            System.out.print("  PIN: ");
            try {
                int pin = Integer.parseInt(input.nextLine().trim());
                if (pin == CORRECT_PIN) {
                    System.out.println("  ✅ PIN accepted.");
                    return true;
                }
            } catch (NumberFormatException ignored) {}
            System.out.println("  ❌ Wrong PIN. " + (MAX_PIN_TRIES - attempt) + " attempt(s) left.");
        }
        return false;
    }

    private void showDashboard() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("  Welcome, " + account.getHolderName());
        System.out.println("  Account: " + account.getAccountNumber());
        System.out.println("╚══════════════════════════════════════╝");
    }

    private void mainMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n┌─────────────────────────────┐");
            System.out.println("│         ATM MENU            │");
            System.out.println("│  1. Check Balance           │");
            System.out.println("│  2. Deposit Cash            │");
            System.out.println("│  3. Withdraw Cash           │");
            System.out.println("│  4. Mini Statement          │");
            System.out.println("│  5. Exit                    │");
            System.out.println("└─────────────────────────────┘");
            System.out.print("  Select option: ");

            switch (input.nextLine().trim()) {
                case "1": checkBalance(); break;
                case "2": depositCash(); break;
                case "3": withdrawCash(); break;
                case "4": miniStatement(); break;
                case "5": running = false; break;
                default: System.out.println("  ⚠ Invalid option. Please choose 1–5.");
            }
        }
    }

    private void checkBalance() {
        System.out.println("\n  💰 Available Balance: ₹" + String.format("%.2f", account.getBalance()));
    }

    private void depositCash() {
        System.out.print("\n  Enter deposit amount (₹): ");
        double amount = readPositiveDouble();
        if (account.deposit(amount)) {
            System.out.println("  ✅ ₹" + String.format("%.2f", amount) + " deposited successfully.");
            System.out.println("  New Balance: ₹" + String.format("%.2f", account.getBalance()));
        } else {
            System.out.println("  ❌ Invalid deposit amount.");
        }
    }

    private void withdrawCash() {
        System.out.print("\n  Enter withdrawal amount (₹): ");
        double amount = readPositiveDouble();

        if (amount > WITHDRAWAL_LIMIT) {
            System.out.println("  ❌ Exceeds single withdrawal limit of ₹" + String.format("%.2f", WITHDRAWAL_LIMIT));
        } else if (amount > account.getBalance()) {
            System.out.println("  ❌ Insufficient balance. Available: ₹" + String.format("%.2f", account.getBalance()));
        } else if (account.withdraw(amount)) {
            System.out.println("  ✅ ₹" + String.format("%.2f", amount) + " dispensed. Please collect your cash.");
            System.out.println("  Remaining Balance: ₹" + String.format("%.2f", account.getBalance()));
        }
    }

    private void miniStatement() {
        System.out.println("\n  📄 Last 5 Transactions:");
        System.out.println("  ─────────────────────────────────────────");
        for (String txn : account.getRecentTransactions(5)) {
            System.out.println("  " + txn);
        }
        System.out.println("  ─────────────────────────────────────────");
    }

    private double readPositiveDouble() {
        while (true) {
            try {
                double val = Double.parseDouble(input.nextLine().trim());
                if (val > 0) return val;
                System.out.print("  ⚠ Amount must be positive: ");
            } catch (NumberFormatException e) {
                System.out.print("  ⚠ Invalid input. Enter a number: ");
            }
        }
    }

    private void printWelcome() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║      🏧  WELCOME TO JAVA BANK  🏧   ║");
        System.out.println("║       ATM — Secure & Simple          ║");
        System.out.println("╚══════════════════════════════════════╝");
    }
}
