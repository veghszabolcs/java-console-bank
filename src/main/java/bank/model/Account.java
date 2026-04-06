package bank.model;

public class Account {
    private final int id;
    private double balance;
    private int userId;

    public Account(int id, double balance, int userId) {
        this.id = id;
        this.balance = balance;
        this.userId = userId;
    }

    public Account(int id, int userId) {
        this(id,0,userId);
    }

    public int getId() {
        return id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getUser() {
        return userId;
    }

    public void setUser(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Account number: "+ id + ", balance: " + balance + ", owner: " + userId;
    }
}
