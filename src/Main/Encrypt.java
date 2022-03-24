package Main;

import java.io.BufferedWriter;
import java.io.Console;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Encrypt {
	private static byte[] AESKey = null;
    public static String encrypt(String plain) throws Exception {
        byte[] getBytes = plain.getBytes(StandardCharsets.UTF_8);
		SecretKey secret = new SecretKeySpec(AESKey, "AES");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        byte[] encryptedText = cipher.doFinal(getBytes);
        byte[] iv = cipher.getIV();
        byte[] message = new byte[12 + getBytes.length + 16];
        System.arraycopy(iv, 0, message, 0, 12); 
        System.arraycopy(encryptedText, 0, message, 12, encryptedText.length);
 	    return Base64.getEncoder().encodeToString(message);
    }
    public static void Main() throws Exception {
		Console console = System.console();
    	Scanner scanner = new Scanner(System.in);
    	AESKey = SCrypt.scrypt(console.readPassword("Enter Key:"), "seasalt", 1048576, 8, 1, 32);
    	System.out.println("Enter label:");
    	String input = scanner.nextLine();
    	input = input.concat(": ");
		System.out.println("Enter key to store:");
		input = input.concat(scanner.nextLine());
    	scanner.close();
    	String encrypted = Encrypt.encrypt(input);
    	BufferedWriter out = new BufferedWriter(new FileWriter(Util.filePath, true));
    	out.append(encrypted).append("\n");
    	out.close();
    	System.out.println("Finished");
    }
}
