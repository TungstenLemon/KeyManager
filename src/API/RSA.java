package API;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.crypto.ntru.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.DestroyFailedException;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;

public class RSA {
    public RSA() {
        Security.addProvider(new BouncyCastleProvider());
    }
    public void generate() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, DestroyFailedException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
        SecureRandom random = SecureRandom.getInstanceStrong();
        generator.initialize(4096, random);
        KeyPair keypair = generator.generateKeyPair();
        Encryptor en = new Encryptor();
        en.init("RSA",keypair.getPrivate().getEncoded());
        en.encrypt();
        FileOutputStream fos = new FileOutputStream("RSApub");
        fos.write(keypair.getPublic().getEncoded());
        fos.close();
    }
    public byte[] encrypt(byte[] plaintext) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, NoSuchProviderException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidCipherTextException {
        byte[] pub = Files.readAllBytes(Paths.get("RSApub"));
        AsymmetricKeyParameter publicKey = (AsymmetricKeyParameter) PublicKeyFactory.createKey(pub);
        AsymmetricBlockCipher rsa = new RSAEngine();
        rsa = new OAEPEncoding(rsa);
        OAEP
        rsa.init(true, publicKey);
        rsa.
        System.out.println(rsa.getInputBlockSize());
        System.out.println(rsa.getOutputBlockSize());
        return rsa.processBlock(plaintext,0,plaintext.length);
    }
}
