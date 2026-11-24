package com.bijlipay.Config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Configures datasources:
 * - Primary: used by JPA/Hibernate (from spring.datasource.* properties)
 * - Switch: used for raw JDBC queries to analytics DB (lazy, won't block startup)
 */
@Configuration
public class SwitchDataSourceConfig {

    @Value("${switch.datasource.url}")
    private String url;

    @Value("${switch.datasource.username}")
    private String username;

    @Value("${switch.datasource.password}")
    private String password;

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "dataSource")
    public DataSource primaryDataSource() {
        return primaryDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    /**
     * Secondary datasource for the switch/analytics DB.
     * @Lazy — won't initialize until first actual use.
     * initializationFailTimeout(-1) — HikariCP won't fail-fast if DB is unreachable.
     */
    @Lazy
    @Bean(name = "switchDataSource")
    public DataSource switchDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setPoolName("switch-pool");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(600000);
        config.setInitializationFailTimeout(-1);
        return new HikariDataSource(config);
    }
}

