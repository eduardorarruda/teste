package com.migracao.config;

import com.migracao.cli.CliArgs.DatabaseConfig;
import com.migracao.exception.ConfigException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Factory para criar conexões com bancos de dados.
 * Utiliza HikariCP para pool de conexões.
 */
public class DatabaseFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseFactory.class);
    
    /**
     * Cria uma conexão com o banco de dados baseado na configuração fornecida
     */
    public Connection createConnection(DatabaseConfig config) throws ConfigException {
        try {
            logger.info("Criando conexão para banco: {} em {}:{}", 
                config.getType(), config.getHost(), config.getPort());
            
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(config.getJdbcUrl());
            hikariConfig.setUsername(config.getUsername());
            hikariConfig.setPassword(config.getPassword());
            hikariConfig.setDriverClassName(config.getDriverClass());
            
            // Configurações do pool
            hikariConfig.setMaximumPoolSize(5);
            hikariConfig.setMinimumIdle(1);
            hikariConfig.setConnectionTimeout(30000); // 30 segundos
            hikariConfig.setIdleTimeout(600000); // 10 minutos
            hikariConfig.setMaxLifetime(1800000); // 30 minutos
            
            try (HikariDataSource dataSource = new HikariDataSource(hikariConfig)) {
                return dataSource.getConnection();
            }
            
        } catch (SQLException e) {
            logger.error("Erro ao criar conexão com o banco de dados", e);
            throw new ConfigException("Falha ao conectar ao banco: " + e.getMessage(), e);
        }
    }
}