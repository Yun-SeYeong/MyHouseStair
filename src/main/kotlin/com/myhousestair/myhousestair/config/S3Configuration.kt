package com.myhousestair.myhousestair.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class S3Configuration(
    @Value("\${aws.s3.access-key}")
    private val accessKey: String,
    @Value("\${aws.s3.secret-key}")
    private val secretKey: String,
) {
    @Bean
    fun s3Client(): S3Client = S3Client.builder()
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                    accessKey,
                    secretKey
                )
            )
        )
        .region(Region.AP_NORTHEAST_2)
        .build()
}