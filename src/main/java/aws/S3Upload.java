package aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class S3Upload {

	public static void uploadString(String str, String keyName) {
		Regions clientRegion = Regions.US_EAST_1;
        String bucketName = "trader-interact";

        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .build();

            s3Client.putObject(bucketName, keyName, str);
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
        
        System.out.println("Object uploaded to " + keyName);
	}

}
