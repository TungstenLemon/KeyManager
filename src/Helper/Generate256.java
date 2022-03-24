package Helper;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;

public class Generate256 {

	public static void Main() {
		SecureRandom ran = new SecureRandom();
		BigInteger password = new BigInteger(512, ran);
		System.out.println(Base64.getEncoder().encodeToString(password.toByteArray()));
	}

}
