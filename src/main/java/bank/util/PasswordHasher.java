package bank.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    private PasswordHasher(){}

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

}
