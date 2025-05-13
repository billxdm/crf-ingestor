package com.ecfr.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.event.CommandListener;
import com.mongodb.event.CommandStartedEvent;
import com.mongodb.event.CommandSucceededEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import jakarta.annotation.PostConstruct;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import com.ecfr.dto.ecfrxml.EcfrDTO;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@Profile("!test")
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "com.ecfr.repository")
public class MongoConfig {
    private static final Logger log = LoggerFactory.getLogger(MongoConfig.class);

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.connection-pool-size:100}")
    private int connectionPoolSize;

    @Value("${spring.data.mongodb.connection-timeout:30000}")
    private int connectionTimeout;

    @Value("${spring.data.mongodb.max-connection-idle-time:300000}")
    private int maxConnectionIdleTime;

    @Value("${spring.data.mongodb.max-connection-life-time:1800000}")
    private int maxConnectionLifeTime;

    @Bean
    public CommandListener mongoCommandListener() {
        return new CommandListener() {
            @Override
            public void commandStarted(CommandStartedEvent event) {
                log.debug("MongoDB Command Started: {} - {}", event.getCommandName(), event.getCommand());
            }

            @Override
            public void commandSucceeded(CommandSucceededEvent event) {
                long elapsedTimeMs = event.getElapsedTime(TimeUnit.MILLISECONDS);
                log.info("MongoDB command succeeded: {} ({} ms)", event.getCommandName(), elapsedTimeMs);
            }
        };
    }

    @Bean
    public MongoClient mongoClient(@Qualifier("mongoCommandListener") CommandListener commandListener) {
        mongoUri = mongoUri.trim();
        if (mongoUri == null || mongoUri.isEmpty() || mongoUri.contains(" ")) {
            throw new IllegalArgumentException("mongoUri is invalid: '" + mongoUri + "'");
        }
        log.info("MongoDB URI used for connection: '{}'", mongoUri);
        ConnectionString connectionString = new ConnectionString(mongoUri);
        
        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .writeConcern(WriteConcern.MAJORITY)
            .readPreference(ReadPreference.secondaryPreferred())
            .applyToConnectionPoolSettings(builder -> 
                builder.maxSize(connectionPoolSize)
                       .minSize(10)
                       .maxConnectionIdleTime(maxConnectionIdleTime, TimeUnit.MILLISECONDS)
                       .maxConnectionLifeTime(maxConnectionLifeTime, TimeUnit.MILLISECONDS)
                       .maintenanceFrequency(60, TimeUnit.SECONDS))
            .applyToSocketSettings(builder ->
                builder.connectTimeout(connectionTimeout, TimeUnit.MILLISECONDS)
                       .readTimeout(connectionTimeout, TimeUnit.MILLISECONDS))
            .applyToServerSettings(builder ->
                builder.heartbeatFrequency(10000, TimeUnit.MILLISECONDS))
            .addCommandListener(commandListener)
            .build();

        return MongoClients.create(settings);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "ecfr");
        try {
            if (!mongoTemplate.collectionExists("ecfr_documents")) {
                mongoTemplate.createCollection("ecfr_documents");
                log.info("Created ecfr_documents collection");
            }
            createIndexes(mongoTemplate);
        } catch (Exception e) {
            log.error("Failed to initialize MongoDB", e);
            throw new RuntimeException("Failed to initialize MongoDB", e);
        }
        return mongoTemplate;
    }

    private void createIndexes(MongoTemplate mongoTemplate) {
        try {
            MongoMappingContext mappingContext = (MongoMappingContext) mongoTemplate.getConverter().getMappingContext();
            IndexOperations indexOps = mongoTemplate.indexOps(EcfrDTO.class);
            IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);
            resolver.resolveIndexFor(EcfrDTO.class).forEach(indexOps::ensureIndex);
            log.info("Successfully created MongoDB indexes");
        } catch (Exception e) {
            log.error("Failed to create MongoDB indexes", e);
            throw e;
        }
    }
} 