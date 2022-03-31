package Transfer;

import java.net.*;
import java.io.*;

public class Client {

    public static void main(String[] args) throws IOException {

        Socket sock = new Socket("127.0.0.1", 13267);

        //Send file
        File myFile = new File("/Users/william/Downloads/keys");
        byte[] mybytearray = new byte[(int) myFile.length()];

        FileInputStream fis = new FileInputStream(myFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        //bis.read(mybytearray, 0, mybytearray.length);

        DataInputStream dis = new DataInputStream(bis);
        dis.readFully(mybytearray, 0, mybytearray.length);

        OutputStream os = sock.getOutputStream();

        //Sending file name and file size to the server
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeUTF(myFile.getName());
        dos.writeLong(mybytearray.length);
        dos.write(mybytearray, 0, mybytearray.length);
        dos.flush();

        //Sending file data to the server
        os.write(mybytearray, 0, mybytearray.length);
        os.flush();

        //Closing socket
        os.close();
        dos.close();
        sock.close();
    }
}