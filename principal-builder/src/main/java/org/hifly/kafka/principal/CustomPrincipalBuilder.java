package org.hifly.kafka.principal;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.security.auth.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSession;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomPrincipalBuilder implements KafkaPrincipalBuilder, KafkaPrincipalSerde {
    private static Logger LOGGER = LoggerFactory.getLogger(CustomPrincipalBuilder.class);

    List<String> allowedNames = new ArrayList<>(
            Arrays.asList("CN=broker", "CN=client")
    );

    @Override
    public KafkaPrincipal build(AuthenticationContext authenticationContext) {
        if (!(authenticationContext instanceof SslAuthenticationContext)) {
            throw new IllegalStateException("Not SSL!");
        }

        SSLSession session = ((SslAuthenticationContext) authenticationContext).session();
        try {
            Certificate[] certificates = session.getPeerCertificates();
            boolean notFound = true;
            if (certificates.length > 0) {
                for (Certificate certificate : certificates) {
                    if (!(certificate instanceof X509Certificate)) {
                        throw new IllegalStateException("Not a X509Certificate!");
                    }
                    X509Certificate x509 = (X509Certificate) certificate;
                    String subject = x509.getSubjectX500Principal().getName();
                    LOGGER.info("SUBJECT:" + subject);

                    String[] subjectArr = subject.split(",");
                    if(subjectArr!= null && subjectArr.length > 0) {
                        for (String prefix : allowedNames) {
                            if (subjectArr[0].equals(prefix))
                                return new KafkaPrincipal(KafkaPrincipal.USER_TYPE, subject);
                        }
                    }

                }
                if(notFound)
                    throw new IllegalStateException("No valid CN extracted!");

            } else {
                throw new IllegalStateException("No SSL certificates!");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Can't gather SSL certificates!");
        }
        return null;

    }

    @Override
    public byte[] serialize(KafkaPrincipal kafkaPrincipal) throws SerializationException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(kafkaPrincipal);
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public KafkaPrincipal deserialize(byte[] bytes) throws SerializationException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
            ObjectInputStream ois = new ObjectInputStream(bis);
            KafkaPrincipal kafkaPrincipal = (KafkaPrincipal) ois.readObject();
            return kafkaPrincipal;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}