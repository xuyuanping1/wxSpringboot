package com.couple.app.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileService {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    private OSS ossClient;

    @PostConstruct
    public void init() {
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    public String upload(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        String ext = originalName != null && originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf("."))
                : ".jpg";
        String objectName = "photo/" + UUID.randomUUID().toString().replace("-", "") + ext;

        try (InputStream inputStream = file.getInputStream()) {
            ossClient.putObject(bucketName, objectName, inputStream);
        }

        return "https://" + bucketName + "." + endpoint + "/" + objectName;
    }

    public String generateThumbnail(String imageUrl) {
        // 利用 OSS 图片处理，直接在 URL 后拼接缩略参数，无需额外上传
        if (imageUrl == null || imageUrl.isEmpty()) {
            return "";
        }
        // SVG 等不支持 OSS 图片处理的格式，直接返回原图
        String lower = imageUrl.toLowerCase();
        if (lower.endsWith(".svg") || lower.endsWith(".gif") || lower.endsWith(".webp")) {
            return imageUrl;
        }
        return imageUrl + "?x-oss-process=image/resize,w_300,h_300,m_fill";
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return;
        // 从 URL 中提取 objectName
        String prefix = "https://" + bucketName + "." + endpoint + "/";
        if (!fileUrl.startsWith(prefix)) return;
        String objectName = fileUrl.substring(prefix.length());
        // 去掉可能的查询参数
        if (objectName.contains("?")) {
            objectName = objectName.substring(0, objectName.indexOf("?"));
        }
        try {
            ossClient.deleteObject(bucketName, objectName);
        } catch (Exception ignored) {
        }
    }
}
