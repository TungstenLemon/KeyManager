package API;

import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Decryptor {
    public Console console;
    public Cipher cipher;
    public byte[] salt;
    public char[] label;
    public char[] toReturn;
    public boolean mode;
    public ArrayList<byte[ ] > sorted;
    public ArrayList<byte[ ] > encoding;
    public Decryptor() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        Security.addProvider(new BouncyCastleProvider());
        console = System.console();
        if (console == null) {
            System.out.println("Couldn't get Console instance");
            System.exit(0);
        }
        cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
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
    public void init(boolean manual,char[] label) {
        if (!manual) {
            mode = false;
            this.label = label;
        } else {
            System.err.println("Additional param found! Wrong method usage");
        }
    }

    public char[] getResult() {
        return toReturn;
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
                cipher.init(Cipher.DECRYPT_MODE,masterKey,param);
                char[] decrypted = Util.toChars(cipher.doFinal(sorted.get(0)));
                System.out.println(decrypted);
                sorted.remove(0);
                encoding.remove(0);
                Arrays.fill(decrypted, '\u0000');
            }
        } else {
            ArrayList<Character> toReturnArraylist = new ArrayList<>();
            while (sorted.size()!=0) {
                if (toReturnArraylist.size()!= 0) {
                    System.err.println("Multiple label match found!");
                    break;
                }
                AlgorithmParameters param = AlgorithmParameters.getInstance("GCM","BC");
                param.init(encoding.get(0));
                cipher.init(Cipher.DECRYPT_MODE,masterKey,param);
                char[] decrypted = Util.toChars(cipher.doFinal(sorted.get(0)));
                int seperatorPos = 0;
                for(int i = 0; i<decrypted.length; i++){
                    if(':' == decrypted[i]){
                        seperatorPos = i;
                        break;
                    }
                }
                char[] toCheck = new char[seperatorPos];
                System.arraycopy(decrypted,0,toCheck,0,seperatorPos);
                if(Arrays.equals(toCheck,label)) {
                    for (int j = 0; j<decrypted.length-seperatorPos-2;j++) {
                        toReturnArraylist.add(decrypted[j+seperatorPos+2]);
                    }
                }
                sorted.remove(0);
                encoding.remove(0);
                Arrays.fill(decrypted, '\u0000');
                Arrays.fill(toCheck, '\u0000');
            }
            toReturn = new char[toReturnArraylist.size()];
            for (int i = 0; i < toReturn.length; i++) {
                toReturn[i]=toReturnArraylist.get(i);
            }
            toReturnArraylist.clear();
        }
        sorted.clear();
        encoding.clear();
        System.gc();
    }
}
