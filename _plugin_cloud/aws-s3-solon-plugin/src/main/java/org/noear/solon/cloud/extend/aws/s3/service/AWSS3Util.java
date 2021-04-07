//package org.noear.solon.cloud.extend.aws.s3.service;
//
//import com.amazonaws.AmazonServiceException;
//import com.amazonaws.SdkClientException;
//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
//import com.amazonaws.services.s3.model.*;
//import org.apache.commons.lang.StringUtils;
//import org.eclipse.jetty.util.StringUtil;
//import org.noear.solon.core.XFile;
//import webapp.Config;
//import webapp.dao.IDUtilEx;
//
//import java.io.IOException;
//import java.io.InputStream;
//
//public class AWSS3Util {
//
//    private static String AWS_ACCESS_KEY = Config.oss().getProperty("aws_access_key"); // 【你的 access_key】
//    private static String AWS_SECRET_KEY = Config.oss().getProperty("aws_secret_key"); // 【你的 aws_secret_key】
//
//    private static String bucketName = Config.oss().getProperty("aws_bucket_name"); // 【你 bucket 的名字】 # 首先需要保证 s3 上已经存在该存储桶
//
//    private static String image_uri = Config.oss().getProperty("aws_img_uri");
//    public static String upload(XFile file, String prefix) throws IOException {
//        String clientRegion = "ap-south-1";
//        String filename = IDUtilEx.buildGuid() + "." + file.name.replaceAll("^.*\\.", "");//ts + file.getOriginalFilename()
//        InputStream in = file.content;
//        String url = "";
//        try {
//            AWSCredentials awsCredentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
//            AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(clientRegion).withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
//            AccessControlList acls = new AccessControlList();
//            acls.grantPermission(GroupGrantee.AllUsers, Permission.Read);
//
//            ObjectMetadata metadata = new ObjectMetadata();
//            metadata.setContentType(file.contentType);
//            metadata.addUserMetadata("x-amz-meta-title", "someTitle");
//            PutObjectRequest request = new PutObjectRequest(bucketName, filename, in, metadata).withAccessControlList(acls);
//            PutObjectResult results = s3Client.putObject(request);
//            url = image_uri + filename;
//        } catch (AmazonServiceException e) {
//
//            e.printStackTrace();
//        } catch (SdkClientException e) {
//
//            e.printStackTrace();
//        }
//
//        return url;
//    }
//
//
//
//    public static String uploadText(String text,String fileName, String prefix) throws IOException {
//        InputStream stream= FileUtil.getStringStream(text);
//        InputStream in =stream;
//
//        String clientRegion = "ap-south-1";
//        String filename = (StringUtils.isNotBlank(prefix) ? prefix.replaceAll("/+$", "") + "/" : "") +(StringUtil.isBlank(fileName)?IDUtilEx.buildGuid():fileName) + "." + "txt";//ts + file.getOriginalFilename();
//        String url = "";
//        try {
//            AWSCredentials awsCredentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
//            AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(clientRegion).withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
//            AccessControlList acls = new AccessControlList();
//            acls.grantPermission(GroupGrantee.AllUsers, Permission.Read);
//
//            ObjectMetadata metadata = new ObjectMetadata();
//            metadata.setContentType("text/plain");
//            metadata.addUserMetadata("x-amz-meta-title", "someTitle");
//            PutObjectRequest request = new PutObjectRequest(bucketName, filename, in, metadata).withAccessControlList(acls);
//            PutObjectResult results = s3Client.putObject(request);
//            url = image_uri + filename;
//        } catch (AmazonServiceException e) {
//
//            e.printStackTrace();
//        } catch (SdkClientException e) {
//
//            e.printStackTrace();
//        }
//
//        return url;
//
//    }
//
//
//
//
//}
