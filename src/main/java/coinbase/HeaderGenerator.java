package coinbase;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import notifications.ErrorLogger;

public class HeaderGenerator {

    public static String getHMACHeader(String secretKey, String timestamp, String method, String requestPath, String body) {
    	String prehash = timestamp + method.toUpperCase() + requestPath;
    	
    	if(method.equals("POST") || method.equals("PUT")) {
    		prehash += body;
    	}

    	 SecretKeySpec keyspec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
    	 Mac sha256 = null;
		try {
			sha256 = (Mac) Mac.getInstance("HmacSHA256");
			sha256.init(keyspec);
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
		}
		
		String hash = Hex.encodeHexString((sha256.doFinal(prehash.getBytes())));
    	 
    	return hash;
    }

}
