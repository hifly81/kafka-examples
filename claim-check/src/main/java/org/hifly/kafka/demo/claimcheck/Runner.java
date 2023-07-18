package org.hifly.kafka.demo.claimcheck;

import io.minio.*;
import io.minio.http.Method;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.hifly.kafka.demo.claimcheck.model.Item;
import org.hifly.kafka.demo.producer.RecordMetadataUtil;
import org.hifly.kafka.demo.producer.serializer.json.JsonProducer;

import java.io.FileInputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Runner {

    private static final String TOPIC = "items";
    private static final String BUCKET_NAME = "test";
    private static final String OBJECT_NAME = "README.pdf";
    private static final String FILE_PATH = "./README.pdf";


    public static void main (String [] args) throws Exception {


        MinioClient client =
                MinioClient.builder()
                        .endpoint("http://127.0.0.1:9000")
                        .credentials("admin", "minioadmin")
                        .build();

        boolean found =
                client.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).build());
        if (!found) {
            client.makeBucket(
                    MakeBucketArgs
                            .builder()
                            .bucket(BUCKET_NAME)
                            .build());
        }

        try(FileInputStream fis = new FileInputStream(FILE_PATH)) {
            client.putObject(PutObjectArgs
                    .builder()
                    .bucket(BUCKET_NAME)
                    .object(OBJECT_NAME)
                    .stream(fis, fis.getChannel().size(), -1).build());
        }

        String objUrl = client.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(BUCKET_NAME)
                        .object(OBJECT_NAME)
                        .expiry(1, TimeUnit.DAYS)
                        .method(Method.GET)
                        .build());

        Item item = new Item();
        item.setId(UUID.randomUUID().toString());
        item.setUrl(objUrl);

        JsonProducer<Item> jsonProducer = new JsonProducer<>("org.hifly.kafka.demo.claimcheck.model.ItemJsonSerializer");
        jsonProducer.start();
        RecordMetadata lastRecord = jsonProducer.produceSync(new ProducerRecord<>(TOPIC, item.getId(), item));
        RecordMetadataUtil.prettyPrinter(lastRecord);
        jsonProducer.stop();






    }

}
