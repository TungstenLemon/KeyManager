package Test;

import API.*;
import Generator.KeyGen;
import org.bouncycastle.crypto.InvalidCipherTextException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.DestroyFailedException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, DestroyFailedException, InvalidCipherTextException, InvalidKeySpecException {
        //SignalHandler sh = new SignalHandler();
        Util.filePath = "/Users/will/Downloads/keys";
        //Encryptor en = new Encryptor();
        //en.init("test","testkey".getBytes(StandardCharsets.UTF_8));
        //en.encrypt();
        //KeyGen kg = new KeyGen(256,"AES");
        //kg.generate();
        //kg.write();
        //System.out.println(new String(de.getResult()));
        //NTRU ntru = new NTRU();
        //ntru.generate();
        //System.out.println(new String(ntru.decrypt(ntru.encrypt("bruh".getBytes(StandardCharsets.UTF_8)))));
        //Decryptor de = new Decryptor();
        //de.init(true);
        //de.decrypt();
        //System.out.println(new String(de.getResult()));
        RSA rsa = new RSA();
        rsa.generate();
        System.out.println(new String(rsa.decrypt(rsa.encrypt("test".getBytes(StandardCharsets.UTF_8)))));
    }
}
