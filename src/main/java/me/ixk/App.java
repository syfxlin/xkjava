package me.ixk;

import me.ixk.utils.Base64;
import me.ixk.utils.Crypt;

public class App {

    public static void main(String[] args) {
        try {
            Crypt crypt = new Crypt(
                Base64.decode("8nv1uWkiIVuSVyQyPnMEOTpzOeZ3CmoqyLA2ZLsfiMM=")
            );
            String encrypt = crypt.encrypt("123");
            System.out.println(encrypt);
            System.out.println(crypt.decrypt(encrypt));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
