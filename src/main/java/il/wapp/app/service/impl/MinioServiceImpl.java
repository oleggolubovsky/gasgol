package il.wapp.app.service.impl;

import il.wapp.app.service.*;
import il.wapp.app.validation.*;
import io.minio.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.io.*;
import java.util.*;

@Service
public class MinioServiceImpl implements MinioService {

	@Autowired
	MinioClient minioClient;

	@Autowired
	private MinioValidator minioValidator;

	@Value("${minio.bucket.contacts.name}")
	String contactsBucketName;

	@Value("${minio.bucket.campaigns.name}")
	String campaignsBucketName;

	@Override
	public String uploadFile(String name, byte[] content) throws Exception {
		minioValidator.validateFile(name);
		PutObjectOptions options = new PutObjectOptions(content.length, -1);
		String fileName = UUID.randomUUID().toString() + "_" + name;
		ByteArrayInputStream bios = new ByteArrayInputStream(content);
		minioClient.putObject(contactsBucketName, fileName, bios, options);
		return fileName;
	}

	@Override
	public String uploadCampaignFile(String name, byte[] content) throws Exception {
		PutObjectOptions options = new PutObjectOptions(content.length, -1);
		String fileName = UUID.randomUUID().toString() + "_" + name;
		ByteArrayInputStream bios = new ByteArrayInputStream(content);
		if (!minioClient.bucketExists(campaignsBucketName)) {
			minioClient.makeBucket(campaignsBucketName);
		}
		minioClient.putObject(campaignsBucketName, fileName, bios, options);
		Map<String, String> reqParams = new HashMap<>();
		reqParams.put("response-content-type", "image/png");
		return minioClient.presignedGetObject(campaignsBucketName, fileName, 1, reqParams);
	}

	@Override
	public InputStream getFile(String fileName) throws Exception {
		return minioClient.getObject(contactsBucketName, fileName);
	}
}
