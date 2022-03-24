package Helper;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.KeyGenerator;

public class GenerateAES {

	public static void Main() throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(256); 
		System.out.println(Base64.getEncoder().encodeToString(keyGen.generateKey().getEncoded())); 
	}

}
