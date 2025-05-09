package com.app.server.infra.cloud_storage

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.ResourceUtils
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.util.*

@Component
class CloudStorageUtil (
    @Value("\${spring.cloud.gcp.storage.credentials.location}")
    private val keyFileName: String,
    @Value("\${spring.cloud.gcp.storage.bucket}")
    private val bucketName: String,
){

    fun uploadImageToCloudStorage(image: MultipartFile, userChallengeId: Long): String {

        val ext = image.originalFilename
            ?.substringAfterLast('.', missingDelimiterValue = "")
            ?.lowercase()
            ?: ""
        val keyFile: InputStream = ResourceUtils.getURL(keyFileName).openStream()
        val uuid: String = UUID.randomUUID().toString()
        val storage: Storage = StorageOptions.newBuilder()
            .setCredentials(GoogleCredentials.fromStream(keyFile))
            .build()
            .service

        val contentType = when (ext.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            else -> "application/octet-stream"
        }

        val prefixBucket = "https://storage.googleapis.com/$bucketName"
        val imagePath = "user/certification/$userChallengeId/$uuid.$ext"

        val blobInfo = BlobInfo.newBuilder(bucketName, imagePath)
            .setContentType(contentType).build()

        storage.create(blobInfo, image.bytes)

        return "$prefixBucket/$imagePath"
    }
}