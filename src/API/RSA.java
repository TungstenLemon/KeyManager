package API;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
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
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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
    public byte[] encrypt(byte[] plaintext) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, NoSuchProviderException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidCipherTextException, InvalidKeySpecException {
        Key pub = KeyFactory.getInstance("RSA","BC").generatePublic(new X509EncodedKeySpec(Files.readAllBytes(Paths.get("RSApub"))));
        Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING","BC");
        SecureRandom random = SecureRandom.getInstanceStrong();
        rsa.init(Cipher.ENCRYPT_MODE,pub,random);
        return rsa.doFinal(plaintext);
    }
    public byte[] decrypt(byte[] ciphertext) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, NoSuchProviderException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidCipherTextException, InvalidKeySpecException {
        Decryptor de = new Decryptor();
        de.init("RSA");
        de.decrypt();
        Key prv = KeyFactory.getInstance("RSA","BC").generatePrivate(new PKCS8EncodedKeySpec(de.getResult()));
        de.destruct();
        Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING","BC");
        SecureRandom random = SecureRandom.getInstanceStrong();
        rsa.init(Cipher.DECRYPT_MODE,prv,random);
        return rsa.doFinal(ciphertext);
    }
}
