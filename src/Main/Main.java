package Main;

import Helper.Generate256;
import Helper.GenerateAES;
import Helper.GenerateRSA;

public class Main {
    public static void showHelp() {
        System.out.println("Secure Key Manager");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("-e <Path/To/keys>   encrypt your key into keys");
        System.out.println("-d <Path/To/keys>   decrypt all your key in keys");
        System.out.println("-gen256   generate strong password");
        System.out.println("-genAES   deprecated, same as -gen256");
        System.out.println("-genRSA   generate RSA public and private keys");
        System.out.println("-del <Path/To/keys> <number>   delete key numbered in keys");
        System.out.println("-h   show help");
        System.out.println("-info   show information");
    }
    public static void showInfo() {
        System.out.println("Secure Key Manager by William");
        System.out.println("Password is hashed by SCrypt and passed as the key for decrypting keys stored in AES256 encryption");
        System.out.println();
        System.out.println("SCrypt Specification:");
        System.out.println("Salt: seasalt");
        System.out.println("Iterations count: 1048576");
        System.out.println("Block size: 8");
        System.out.println("Parallelism factor: 1");
    }
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            Main.showHelp();
        } else {
            switch (args[0]) {
                case "-e":
                    if (args.length == 1) {
                        System.err.println("No file path specified!");
                    } else {
                        Util.filePath = args[1];
                        Encrypt.Main();
                    }
                    break;
                case "-d":
                    if (args.length == 1) {
                        System.err.println("No file path specified!");
                    } else {
                        Util.filePath = args[1];
                        Decrypt.Main();
                    }
                    break;
                case "-gen256":
                    Generate256.Main(); break;
                case "-genAES":
                    GenerateAES.Main(); break;
                case "-genRSA":
                    GenerateRSA.Main(); break;
                case "-del":
                    if (args.length == 1) {
                        System.err.println("No file path specified!");
                    } else if (args.length == 2) {
                        System.err.println("No number specified!");
                    } else {
                        Util.filePath = args[1];
                        Util.deleteKey(Integer.parseInt(args[2]));
                    }
                    break;
                case "-h":
                    Main.showHelp(); break;
                case "-info":
                    Main.showInfo(); break;
            }
        }
    }
}
