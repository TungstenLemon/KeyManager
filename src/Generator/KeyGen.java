package Generator;

import API.Encryptor;
import API.Util;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.DestroyFailedException;
import java.io.IOException;
import java.security.*;

public class KeyGen {
    public byte[] key;
    public String label;
    public KeyGen(int bit,String label) {
        Security.addProvider(new BouncyCastleProvider());
        key = new byte[bit/8];
        this.label = label;
    }
    public void generate() throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom().getInstanceStrong();
        random.nextBytes(key);
    }
    public void write() throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, NoSuchProviderException, DestroyFailedException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Encryptor en = new Encryptor();
        System.out.println(new String(key));
        en.init(label, Util.toChars(key));
        en.encrypt();
    }
    public static void main(String[] args) throws NoSuchAlgorithmException, DestroyFailedException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, IOException, BadPaddingException, NoSuchProviderException, InvalidKeyException {
        KeyGen kg = new KeyGen(256,"AES");
        kg.generate();
        kg.write();
    }
}
