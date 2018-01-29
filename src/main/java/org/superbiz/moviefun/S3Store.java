package org.superbiz.moviefun;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Optional;

public class S3Store implements BlobStore {
    private final String s3Bucket;
    private final AmazonS3Client s3Client;

    public S3Store(AmazonS3Client s3Client, String s3BucketName) {

         this.s3Bucket = s3BucketName;
         this.s3Client = s3Client;

    }

    @Override
    public void put(Blob blob) throws IOException {
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType(blob.getContentType());
        PutObjectResult result = s3Client.putObject(s3Bucket, blob.getName(), blob.getIs(), meta);
        System.out.println("put result: " + result);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        S3Object object = s3Client.getObject(new GetObjectRequest(s3Bucket, name));
        InputStream objectData = object.getObjectContent();
        objectData.close();

        Blob blob = new Blob(name, objectData, object.getObjectMetadata().getContentType());
        return Optional.of(blob);
    }

    @Override
    public void deleteAll() {

        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        try {
            ObjectListing listing = s3Client.listObjects(s3Bucket);
            while (true) {
                for (Iterator<?> iterator =
                     listing.getObjectSummaries().iterator();
                     iterator.hasNext();) {
                    S3ObjectSummary summary = (S3ObjectSummary)iterator.next();
                    s3.deleteObject(s3Bucket, summary.getKey());
                }

                // more object_listing to retrieve?
                if (listing.isTruncated()) {
                    listing = s3.listNextBatchOfObjects(listing);
                } else {
                    break;
                }
            }

        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
        }

    }
}
