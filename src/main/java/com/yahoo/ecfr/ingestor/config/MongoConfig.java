package com.yahoo.ecfr.ingestor.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteConcernResolver;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Value("${spring.data.mongodb.connection-pool-size:100}")
    private int connectionPoolSize;

    @Value("${spring.data.mongodb.connection-timeout-ms:30000}")
    private int connectionTimeoutMs;

    @Value("${spring.data.mongodb.socket-timeout-ms:30000}")
    private int socketTimeoutMs;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(mongoUri);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .applyToConnectionPoolSettings(builder -> 
                    builder.maxSize(connectionPoolSize)
                           .minSize(10)
                           .maxConnectionIdleTime(30000, java.util.concurrent.TimeUnit.MILLISECONDS))
                .applyToSocketSettings(builder -> 
                    builder.connectTimeout(connectionTimeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS)
                           .readTimeout(socketTimeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS))
                .applyToServerSettings(builder -> 
                    builder.heartbeatFrequency(10000, java.util.concurrent.TimeUnit.MILLISECONDS))
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public WriteConcernResolver writeConcernResolver() {
        return action -> {
            if (action.getCollectionName().equals("ecfrDocuments")) {
                return WriteConcern.MAJORITY;
            }
            return WriteConcern.ACKNOWLEDGED;
        };
    }

    @Override
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = super.mongoTemplate();
        mongoTemplate.setWriteConcernResolver(writeConcernResolver());
        return mongoTemplate;
    }

    @Override
    protected void configureConverters(MongoCustomConversions.MongoConverterConfigurationAdapter adapter) {
        List<?> converters = new ArrayList<>();
        adapter.registerConverters(converters);
    }

    @Override
    protected void configureMappingContext(MongoMappingContext context) {
        context.setAutoIndexCreation(true);
    }
} 