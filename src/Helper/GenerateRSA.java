package Helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.NoSuchPaddingException;
import javax.security.auth.DestroyFailedException;

public class GenerateRSA {
	public static void setReadOnly(String location) {
	    File file = new File(location);
	    file.setWritable(false);
	    System.out.println("Set file to read-only");
	}
	public static void setWritable(String location) {
	    File file = new File(location);
	    file.setWritable(true);
	}
	public static void writeFile(String location, String content) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(location);
		out.println(content);
		out.close();
	} 
	public static void Main() throws NoSuchAlgorithmException, CertificateException, InvalidKeySpecException, IOException, NoSuchPaddingException, KeyStoreException, DestroyFailedException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(8192);
		KeyPair keypair = generator.generateKeyPair();
		PublicKey publickey = keypair.getPublic();
		PrivateKey privatekey = keypair.getPrivate();
		GenerateRSA.setWritable("pubkey");
		GenerateRSA.writeFile("pubkey", Base64.getEncoder().encodeToString(publickey.getEncoded()));
		GenerateRSA.setReadOnly("pubkey");
		System.out.println(Base64.getEncoder().encodeToString(privatekey.getEncoded()));
	}
}
