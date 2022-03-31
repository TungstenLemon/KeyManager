package API;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.crypto.ntru.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.DestroyFailedException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;

public class NTRU {
    public NTRU() {
        Security.addProvider(new BouncyCastleProvider());
    }
    public void generate() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, DestroyFailedException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        NTRUEncryptionKeyPairGenerator Nekpg = new NTRUEncryptionKeyPairGenerator();
        NTRUEncryptionKeyGenerationParameters Nekgp = NTRUEncryptionKeyGenerationParameters.APR2011_743;
        Nekpg.init(Nekgp);
        AsymmetricCipherKeyPair kp = Nekpg.generateKeyPair();
        NTRUEncryptionPublicKeyParameters Nepubkp = (NTRUEncryptionPublicKeyParameters) kp.getPublic();
        byte[] encoded = Nepubkp.getEncoded();
        FileOutputStream fos = new FileOutputStream("NTRUPub");
        fos.write(encoded);
        Encryptor en = new Encryptor();
        NTRUEncryptionPrivateKeyParameters Neprvkp = (NTRUEncryptionPrivateKeyParameters) kp.getPrivate();
        byte[] key = Neprvkp.getEncoded();
        en.init("NTRU",key);
        en.encrypt();
        Arrays.fill(key,(byte)0);
    }
    public byte[] encrypt(byte[] plaintext) throws InvalidCipherTextException, IOException {
        NTRUEngine Ne = new NTRUEngine();
        byte[] pub = Files.readAllBytes(Paths.get("NTRUPub"));
        NTRUEncryptionPublicKeyParameters Nepubkp = new NTRUEncryptionPublicKeyParameters(pub,NTRUEncryptionKeyGenerationParameters.APR2011_743.getEncryptionParameters());
        Ne.init(true,Nepubkp);
        boolean cont = false;
        int length = plaintext.length;
        ArrayList<byte[]> processedArraylist = new ArrayList<>();
        while (!cont) {
            if (length>105) {
                processedArraylist.add(Ne.processBlock(plaintext,plaintext.length-length,105));
                length = length-105;
            } else {
                processedArraylist.add(Ne.processBlock(plaintext,plaintext.length-length,length));
                cont = true;
                length = 0;
            }
        }
        for (byte[] bytes : processedArraylist) {
            length = length + bytes.length;
        }
        byte[] toReturn = new byte[length];
        for (byte[] array : processedArraylist) {
            System.arraycopy(array, 0, toReturn, toReturn.length - length, array.length);
            length = length - array.length;
        }
        return toReturn;
    }
    public byte[] decrypt(byte[] ciphertext) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, NoSuchProviderException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidCipherTextException {
        NTRUEngine Ne = new NTRUEngine();
        Decryptor de = new Decryptor();
        de.init("NTRU");
        de.decrypt();
        NTRUEncryptionPrivateKeyParameters Neprvkp = new NTRUEncryptionPrivateKeyParameters(de.getResult(),NTRUEncryptionKeyGenerationParameters.APR2011_743.getEncryptionParameters());
        Ne.init(false,Neprvkp);
        int length = ciphertext.length;
        ArrayList<byte[]> processedArraylist = new ArrayList<>();
        while(length != 0) {
            processedArraylist.add(Ne.processBlock(ciphertext,ciphertext.length-length,1022));
            length = length-1022;
        }
        for (byte[] bytes : processedArraylist) {
            length = length + bytes.length;
        }
        byte[] toReturn = new byte[length];
        for (byte[] array : processedArraylist) {
            System.arraycopy(array, 0, toReturn, toReturn.length - length, array.length);
            length = length - array.length;
        }
        return toReturn;
    }
}
