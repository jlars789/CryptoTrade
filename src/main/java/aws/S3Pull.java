package aws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class S3Pull {

	public static String readObject(String key) {
        Regions clientRegion = Regions.US_EAST_1;
        String bucketName = "trader-interact";
        String data = "";
        S3Object fullObject = null;
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .build();

            fullObject = s3Client.getObject(new GetObjectRequest(bucketName, key));
            data = getTextInputStream(fullObject.getObjectContent());
            if (fullObject != null) {
                fullObject.close();
            }
        } catch (SdkClientException | IOException e) {
            e.printStackTrace();
        }
        
        return data;
    }

    private static String getTextInputStream(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = "";
        String temp = null;
        while ((temp = reader.readLine()) != null) {
        	line+=temp;
        }
        return line;
    }

}
