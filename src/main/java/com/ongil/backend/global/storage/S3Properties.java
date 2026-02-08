package com.ongil.backend.global.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "aws.s3")
@Getter
@Setter
public class S3Properties {
	private String bucketName;
	private String region;
	private String accessKey;
	private String secretKey;
}
