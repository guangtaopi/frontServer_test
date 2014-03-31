package com.v5.test.worker.ssl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by piguangtao on 14-3-17.
 */
public class SSLUtil implements InitializingBean {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Value("${client.ssl.key.config}")
    private String keyConfig;

    @Value("${client.ssl.trust.key.config}")
    private String trustKeyConfig;

    private List<ClientKeyConfig> keyConfigList;

    private List<ClientKeyConfig> trustConfigList;

    ObjectMapper objectMapper = new ObjectMapper();

    private SSLContext sslContext = null;

    @Override
    public void afterPropertiesSet() {
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        if (null != keyConfig) {
            try {
                keyConfigList = objectMapper.readValue(keyConfig, new TypeReference<List<ClientKeyConfig>>() {
                });
            } catch (IOException e) {
                LOGGER.error("fails to parse keyconfig.keyConfig:{}", keyConfig, e);
            }
        }

        if (null != trustKeyConfig) {
            try {
                trustConfigList = objectMapper.readValue(trustKeyConfig, new TypeReference<List<ClientKeyConfig>>() {
                });
            } catch (IOException e) {
                LOGGER.error("fails to parse keyconfig.trustKeyConfig:{}", trustKeyConfig, e);
            }
        }
        sslContext = generateSSLContext();

    }
    public  SSLContext newSSLContext(){
        return sslContext;
    }

    public  SSLContext generateSSLContext() {
        SSLContext sslc = null;
        try {
            sslc = SSLContext.getInstance("TLS");
            List<KeyManager> keyManagers = new ArrayList<>();
            List<TrustManager> trustKeyManagers = new ArrayList<>();
            if(null != keyConfigList){
               for(ClientKeyConfig clientKeyConfig:keyConfigList){
                   KeyStore ks = KeyStore.getInstance(clientKeyConfig.getType());
                   InputStream keyStoreInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(clientKeyConfig.getKeyFile());
                   ks.load(keyStoreInputStream, clientKeyConfig.getKeyPassword().toCharArray());

                   KeyManagerFactory kmf = KeyManagerFactory.getInstance(clientKeyConfig.getAlgorithm());
                   kmf.init(ks, clientKeyConfig.getKeyPassword().toCharArray());
                   keyManagers.add(kmf.getKeyManagers()[0]);
               }
            }

            if(null != trustConfigList){
                for(ClientKeyConfig clientKeyConfig:trustConfigList){
                    KeyStore trustKs = KeyStore.getInstance(clientKeyConfig.getType());
                    InputStream keyStoreInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(clientKeyConfig.getKeyFile());
                    trustKs.load(keyStoreInputStream, clientKeyConfig.getKeyPassword().toCharArray());
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(clientKeyConfig.getAlgorithm());
                    tmf.init(trustKs);
                    trustKeyManagers.add(tmf.getTrustManagers()[0]);
                }
            }
            sslc.init(keyManagers.toArray(new KeyManager[]{}), trustKeyManagers.toArray(new TrustManager[]{}), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return sslc;
    }


    private static class ClientKeyConfig {
        String keyFile;
        String keyPassword;
        String type;
        String algorithm;

        public String getKeyFile() {
            return keyFile;
        }

        public void setKeyFile(String keyFile) {
            this.keyFile = keyFile;
        }

        public String getKeyPassword() {
            return keyPassword;
        }

        public void setKeyPassword(String keyPassword) {
            this.keyPassword = keyPassword;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAlgorithm() {
            return algorithm;
        }

        public void setAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }

        @Override
        public String toString() {
            return "ClientKeyConfig{" +
                    "keyFile='" + keyFile + '\'' +
                    ", keyPassword='" + keyPassword + '\'' +
                    ", type='" + type + '\'' +
                    ", algorithm='" + algorithm + '\'' +
                    '}';
        }
    }


}
