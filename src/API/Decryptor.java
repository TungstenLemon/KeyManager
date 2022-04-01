package API;

import org.bouncycastle.crypto.generators.SCrypt;

import javax.crypto.*;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Decryptor {
    public Console console;
    public Cipher aes;
    public byte[] salt;
    public byte[] label;
    public byte[] toReturn;
    public boolean mode;
    public ArrayList<byte[]> sorted;
    public ArrayList<byte[]> encoding;
    public Decryptor() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        console = System.console();
        if (console == null) {
            System.out.println("Couldn't get Console instance");
            System.exit(0);
        }
        aes = Cipher.getInstance("AES/GCM/NoPadding","BC");

        File file = new File(Util.filePath);
        if (!file.isFile()) {
            System.err.println("No file found!!");
            System.exit(0);
        }
        salt = new byte[64];
        byte[] text = Files.readAllBytes(file.toPath());
        byte[] content = new byte[Math.max(text.length-64,0)];
        if (text.length < 64) {
            System.err.println("Malformed header! quitting...");
            System.exit(0);
        } else {
            System.arraycopy(text,0,salt,0,64);
            System.arraycopy(text,64,content,0,text.length-64);
        }
        sorted = new ArrayList<>();
        encoding = new ArrayList<>();
        int control = content.length;
        while (control != 0 ) {
            byte[] readLength = new byte[4];
            System.arraycopy(content,content.length-control,readLength,0,4);
            int length = ByteBuffer.wrap(readLength).order(ByteOrder.LITTLE_ENDIAN).getInt();
            byte[] chunk = new byte[length];
            byte[] encodedParam = new byte[71];
            System.arraycopy(content,content.length-control+4,chunk,0,length);
            System.arraycopy(content,content.length-control+4+length,encodedParam,0,71);
            sorted.add(chunk);
            encoding.add(encodedParam);
            control = control-4-length-71;
        }
    }
    public void init(boolean manual) {
        if (manual) {
            mode = true;
        } else {
            System.err.println("No param found! Wrong method usage");
        }
    }
    public void init(String label) {
        mode = false;
        this.label = label.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getResult() {
        return toReturn;
    }

    public void destruct() {
        Arrays.fill(toReturn,(byte)32);
    }

    public void decrypt() throws NoSuchAlgorithmException, NoSuchProviderException, IOException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        char[] password = console.readPassword("Enter password:");
        byte[] hashedPass = SCrypt.generate(Util.toBytes(password), salt, 1048576, 8, 1, 32);
        SecretKey masterKey = new DestroyableSecretKeySpec(hashedPass, 0, hashedPass.length, "AES");
        Arrays.fill(hashedPass, (byte)0);
        if (mode) {
            while (sorted.size()!=0) {
                AlgorithmParameters param = AlgorithmParameters.getInstance("GCM","BC");
                param.init(encoding.get(0));
                aes.init(Cipher.DECRYPT_MODE,masterKey,param);
                byte[] decrypted = aes.doFinal(sorted.get(0));
                byte[] toPrint = new byte[decrypted.length+1];
                int labelLength = decrypted[0];
                System.arraycopy(decrypted,1,toPrint,0,labelLength);
                toPrint[labelLength] = 0x3a;
                toPrint[labelLength+1] = 0x20;
                System.arraycopy(decrypted,labelLength+1,toPrint,labelLength+2,decrypted.length-labelLength-1);
                System.out.println(new String(toPrint));
                sorted.remove(0);
                encoding.remove(0);
                Arrays.fill(decrypted, (byte)0x10);
            }
        } else {
            while (sorted.size()!=0) {
                AlgorithmParameters param = AlgorithmParameters.getInstance("GCM","BC");
                param.init(encoding.get(0));
                aes.init(Cipher.DECRYPT_MODE,masterKey,param);
                byte[] decrypted = aes.doFinal(sorted.get(0));
                int labelLength = decrypted[0];
                byte[] toCheck = new byte[labelLength];
                System.arraycopy(decrypted,1,toCheck,0,labelLength);
                if(Arrays.equals(toCheck,label)) {
                    toReturn = new byte[decrypted.length-labelLength-1];
                    System.arraycopy(decrypted,labelLength+1,toReturn,0,decrypted.length-labelLength-1);
                    break;
                }
                sorted.remove(0);
                encoding.remove(0);
                Arrays.fill(decrypted, (byte)0);
                Arrays.fill(toCheck, (byte)0);
            }
        }
        sorted.clear();
        encoding.clear();
        System.gc();
    }
}
