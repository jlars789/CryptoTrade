package aws;

import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import notifications.ErrorLogger;

public class S3Upload {

	public static void uploadString(String str, String keyName) {
		Regions clientRegion = Regions.US_EAST_1;
        String bucketName = "trader-interact";
        
        System.out.println("Attempting to upload object to " + keyName);
        
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .build();

            s3Client.putObject(bucketName, keyName, str);
        } catch (SdkClientException e) {
        	ErrorLogger.logException(e);
        	System.out.println("Failed to upload object due to " + e.toString());
        }
        
        System.out.println("Object successfully uploaded");
	}

}
