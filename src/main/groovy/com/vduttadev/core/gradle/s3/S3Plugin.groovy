package com.vduttadev.core.gradle.s3

import com.amazonaws.event.ProgressEvent
import com.amazonaws.event.ProgressListener
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.transfer.Transfer
import com.amazonaws.services.s3.transfer.TransferManagerBuilder
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

import java.text.DecimalFormat
import org.gradle.api.logging.Logger

class S3Extension {

    String region
    String bucket
    String accessKey
    String accessSecret
    String keyPrefix
    String destDir
}


abstract class S3Task extends DefaultTask {

    @Input
    String bucket

    @Internal
    AmazonS3 getS3Client() {
            AWSCredentials credentials = new BasicAWSCredentials(project.s3.accessKey, project.s3.accessSecret)
            AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
            String region = project.s3.region
            if (region) {
                builder.withRegion(region)
            }
            builder.build()
    }
}

class S3Download extends S3Task {

    @Input
    String key


    @Input
    String file


    @Input
    String keyPrefix


    @Input
    String destDir

    @TaskAction
    def task() {

        Transfer transfer

        if (!bucket) {
            throw new GradleException('Invalid parameters: [bucket] was not provided and/or a default was not set')
        }
        final AmazonS3 s3Client = getS3Client()

        // directory download
        if (keyPrefix && destDir) {
            if (key || file) {
                throw new GradleException('Invalid parameters: [key, file] are not valid for S3 Download recursive')
            }
            logger.quiet("S3 Download recursive s3://${bucket}/${keyPrefix} → ${project.file(destDir)}/")
            transfer = TransferManagerBuilder.newInstance().withS3Client(s3Client).build()
                            .downloadDirectory(bucket, keyPrefix, project.file(destDir))
        }

        // single file download
        else if (key && file) {
            if (keyPrefix || destDir) {
                throw new GradleException('Invalid parameters: [keyPrefix, destDir] are not valid for S3 Download single file')
            }
            logger.quiet("S3 Download s3://${bucket}/${key} → ${file}")
            File f = new File(file)
            f.parentFile.mkdirs()
            transfer = TransferManagerBuilder.newInstance().withS3Client(s3Client).build()
                            .download(bucket, key, f)
        }

        else {
            throw new GradleException('Invalid parameters: one of [key, file] or [keyPrefix, destDir] pairs must be specified for S3 Download')
        }

        def listener = new S3Listener(transfer, logger)
        transfer.addProgressListener(listener)
        transfer.waitForCompletion()
    }
}


class S3Listener implements ProgressListener {

    DecimalFormat df = new DecimalFormat("#0.0")
    Transfer transfer
    Logger logger

    S3Listener(Transfer transfer, Logger logger) {
        this.transfer = transfer
        this.logger = logger
    }

    void progressChanged(ProgressEvent e) {
        logger.info("${df.format(transfer.progress.percentTransferred)}%")
    }
}


class S3Plugin implements Plugin<Project> {

    void apply(Project target) {
        target.extensions.create('s3', S3Extension)
    }
}
