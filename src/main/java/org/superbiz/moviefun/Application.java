package org.superbiz.moviefun;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.superbiz.moviefun.albums.AlbumsUpdateScheduler;
import org.superbiz.moviefun.blobstore.BlobStore;
import org.superbiz.moviefun.blobstore.S3Store;

import javax.sql.DataSource;

@SpringBootApplication
public class Application {

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }

    @Value("${s3.endpointUrl}") String s3EndpointUrl;
    @Value("${s3.accessKey}") String s3AccessKey;
    @Value("${s3.secretKey}") String s3SecretKey;
    @Value("${s3.bucketName}") String s3BucketName;

    @Bean
    public BlobStore blobStore() {
        AWSCredentials credentials = new BasicAWSCredentials(s3AccessKey, s3SecretKey);
        AmazonS3Client s3Client = new AmazonS3Client(credentials);
        s3Client.setEndpoint(s3EndpointUrl);

        return new S3Store(s3Client, s3BucketName);
    }

    @Bean
    public AlbumsUpdaterRepo getUpdaterRepo(DataSource dataSource) {
        return new AlbumsUpdaterRepo(dataSource);
    }

    @Bean
    public AlbumUpdaterTxManager getTxManager(DataSource dataSource) {
        AlbumUpdaterTxManager manager = new AlbumUpdaterTxManager(dataSource);
        return manager;
    }
}
