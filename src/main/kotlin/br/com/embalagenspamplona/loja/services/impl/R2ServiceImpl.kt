package br.com.embalagenspamplona.loja.services.impl

import br.com.embalagenspamplona.loja.services.R2Service
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class R2ServiceImpl: R2Service {

    // Substitua pelos seus dados do painel Cloudflare
    private val endpoint: String = "https://<SEU_ACCOUNT_ID>.r2.cloudflarestorage.com";
    private val accessKey: String = "<SUA_ACCESS_KEY>";
    private val secretKey: String = "<SUA_SECRET_KEY>";
    private val bucketName: String = "seu-bucket-r2";

    private val minioClient = MinioClient.builder()
        .endpoint(endpoint)
        .credentials(accessKey, secretKey)
        .build();

    override fun generatePresignedUploadUrl(fileName: String): String? {
        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.PUT)
                    .bucket(bucketName)
                    .`object`(fileName)
                    .expiry(15, TimeUnit.MINUTES)
                    .build()
            )
        } catch (e: Exception) {
            throw RuntimeException("Erro ao gerar URL do R2", e)
        }
    }

    override fun deleteFile(fileName: String) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(fileName)
                    .build()
            )
        } catch (e: Exception) {
            throw RuntimeException("Erro ao deletar arquivo do R2", e)
        }
    }
}