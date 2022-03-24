package Main;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Decrypt {
	private static byte[] AESKey = null;
    public static String decrypt(String encrypted) throws NoSuchPaddingException, NoSuchAlgorithmException {
		SecretKey secret = new SecretKeySpec(AESKey, "AES");
		byte[] decodedString = Base64.getDecoder().decode(encrypted.getBytes(StandardCharsets.UTF_8));
        Cipher cipher;
        cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec params = new GCMParameterSpec(128, decodedString, 0, 12);
        try {
            cipher.init(Cipher.DECRYPT_MODE, secret, params);
        } catch (InvalidKeyException e) {
            System.err.println("Key failure!!");
            System.exit(0);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        byte[] decryptedText = new byte[0];
        try {
            decryptedText = cipher.doFinal(decodedString, 12, decodedString.length - 12);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            System.err.println("Key failure!!");
            System.exit(0);
        }
        return new String(decryptedText);
    }
    public static void Main() throws Exception {
        Console console = System.console();
    	AESKey = SCrypt.scrypt(console.readPassword("Enter Key:"), "seasalt", 1048576, 8, 1, 32);
    	BufferedReader br = new BufferedReader(new FileReader(Util.filePath));
    	for(String line; (line = br.readLine()) != null; ) {
            System.out.println(Decrypt.decrypt(line));
        }
    	br.close();
    }
}