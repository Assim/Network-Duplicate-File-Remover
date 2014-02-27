package ndfr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This is a utility class with some common functions that are used
 */
public class Utilities {
	
	/**
	 * This is for getting the IP address.
	 * 
	 * @return The IP address.
	 */
	public static String getIpAddress() {
		InetAddress ip;
		try {
			ip = InetAddress.getLocalHost();
			// Return the IP address if found
			return ip.getHostAddress();
		} catch (UnknownHostException e) {
			// Return the string "Unknown" if no IP found.
			return "Unknown";
		}
	}
	
	/**
	 * This will create the checksum by getting the bytes from the file, it doesn't need to be called
	 * It will be called automatically by the getChecksum method.
	 * 
	 * @param filename The file for calculating the MD5 checksum.
	 * @return 
	 * @throws FileNotFoundException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
    private static byte[] createChecksum(String filename)
            throws FileNotFoundException, NoSuchAlgorithmException, IOException {
        InputStream fis =  new FileInputStream(filename);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;
        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);
        fis.close();
        return complete.digest();
   }
    
    /**
     * This function is the function that will be used to calculate the checksum, pass a file, and it'll return the checksum.
     * 
     * @param filename The file for calculating the MD5 checksum.
     * @return The checksum.
     * @throws FileNotFoundException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static String getChecksum(String filename)
            throws FileNotFoundException, NoSuchAlgorithmException, IOException {
        byte[] b = createChecksum(filename);
        String result = "";
        for (int i=0; i<b.length; i++) {
            result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring(1);
        }
        return result;
    }
  }