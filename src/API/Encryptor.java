package API;

import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.security.auth.DestroyFailedException;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.util.Arrays;

public class Encryptor {
    public Console console;
    public Cipher cipher;
    public byte[] salt;
    public byte[] iv;
    public byte[] concatenated;
    public Encryptor() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        Security.addProvider(new BouncyCastleProvider());
        console = System.console();
        if (console == null) {
            System.out.println("Couldn't get Console instance");
            System.exit(0);
        }
        cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
        SecureRandom random = new SecureRandom().getInstanceStrong();
        iv = new byte[64];
        random.nextBytes(iv);
        salt = new byte[64];
        File file = new File(Util.filePath);
        if (!file.isFile()) {
            file.createNewFile();
        }
        byte[] text = Files.readAllBytes(file.toPath());
        if (text.length < 64) {
            System.err.println("Malformed header! regenerating...");
            FileOutputStream saltWriter = new FileOutputStream(Util.filePath);
            saltWriter.write(("").getBytes());
            random.nextBytes(salt);
            saltWriter.write(salt);
            saltWriter.close();
        } else {
            System.arraycopy(text,0,salt,0,64);
        }
    }
    public void init(boolean manual) {
        if (manual) {
            byte[] label = console.readLine("Enter Label:").getBytes(StandardCharsets.UTF_8);
            byte[] key = Util.toBytes(console.readPassword("Enter Key:"));
            concatenated = new byte[label.length+key.length+1];
            System.arraycopy(key,0,concatenated,label.length+1,key.length);
            Arrays.fill(key, (byte)0);
            concatenated[0] = (byte)label.length;
            System.arraycopy(label,0,concatenated,1,label.length);
        } else {
            System.err.println("No param found! Wrong method usage");
        }
    }
    public void init(String label,char[] key) {
        byte[] Label = label.getBytes(StandardCharsets.UTF_8);
        byte[] Key = Util.toBytes(key);
        concatenated = new byte[Label.length+Key.length+1];
        System.arraycopy(Key,0,concatenated,Label.length+1,Key.length);
        Arrays.fill(Key, (byte)0);
        concatenated[0] = (byte)Label.length;
        System.arraycopy(Label,0,concatenated,1,Label.length);
    }
    public void init(String label,byte[] key) {
        byte[] Label = label.getBytes(StandardCharsets.UTF_8);
        concatenated = new byte[Label.length+key.length+1];
        System.arraycopy(key,0,concatenated,Label.length+1,key.length);
        Arrays.fill(key, (byte)0);
        concatenated[0] = (byte)Label.length;
        System.arraycopy(Label,0,concatenated,1,Label.length);
    }
    public void encrypt() throws DestroyFailedException, IOException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        char[] password = console.readPassword("Enter password:");
        byte[] hashedPass = SCrypt.generate(Util.toBytes(password), salt, 1048576, 8, 1, 32);
        SecretKey masterKey = new DestroyableSecretKeySpec(hashedPass, 0, hashedPass.length, "AES");
        Arrays.fill(hashedPass, (byte)0);
        cipher.init(Cipher.ENCRYPT_MODE, masterKey, new GCMParameterSpec(128, iv));
        byte[] encrypted = cipher.doFinal(concatenated);
        masterKey.destroy();
        byte[] param = cipher.getParameters().getEncoded();
        byte[] length = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(encrypted.length).array();
        FileOutputStream fos = new FileOutputStream(Util.filePath,true);
        byte[] toWrite = new byte[encrypted.length+length.length+71];
        System.arraycopy(length,0,toWrite,0,length.length);
        System.arraycopy(encrypted,0,toWrite,length.length,encrypted.length);
        System.arraycopy(param,0,toWrite,length.length+encrypted.length,71);
        fos.write(toWrite);
        fos.close();
    }
}
