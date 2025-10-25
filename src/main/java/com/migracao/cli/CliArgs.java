package com.migracao.cli;

/**
 * POJO (Plain Old Java Object) para armazenar os argumentos
 * e configurações coletadas via CLI.
 */
public class CliArgs {
    
    private final DatabaseConfig sourceConfig;
    private final DatabaseConfig targetConfig;
    
    public CliArgs(DatabaseConfig sourceConfig, DatabaseConfig targetConfig) {
        this.sourceConfig = sourceConfig;
        this.targetConfig = targetConfig;
    }
    
    public DatabaseConfig getSourceConfig() {
        return sourceConfig;
    }
    
    public DatabaseConfig getTargetConfig() {
        return targetConfig;
    }
    
    /**
     * Classe interna para armazenar configurações de banco de dados
     */
    public static class DatabaseConfig {
        private DatabaseType type;
        private String host;
        private int port;
        private String database;
        private String username;
        private String password;
        private String charset;
        
        /**
         * Enum para tipos de banco de dados suportados
         */
        public enum DatabaseType {
            FIREBIRD("Firebird"),
            POSTGRESQL("PostgreSQL"),
            MYSQL("MySQL");
            
            private final String displayName;
            
            DatabaseType(String displayName) {
                this.displayName = displayName;
            }
            
            @Override
            public String toString() {
                return displayName;
            }
        }
        
        // Getters e Setters
        public DatabaseType getType() { return type; }
        public void setType(DatabaseType type) { this.type = type; }
        
        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        
        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
        
        public String getDatabase() { return database; }
        public void setDatabase(String database) { this.database = database; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getCharset() { return charset; }
        public void setCharset(String charset) { this.charset = charset; }
        
        /**
         * Gera a URL JDBC apropriada para o tipo de banco
         */
        public String getJdbcUrl() {
            return switch (type) {
                case FIREBIRD -> String.format("jdbc:firebirdsql://%s:%d/%s?encoding=%s", 
                    host, port, database, charset != null ? charset : "UTF8");
                case POSTGRESQL -> String.format("jdbc:postgresql://%s:%d/%s", 
                    host, port, database);
                case MYSQL -> String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC", 
                    host, port, database);
            };
        }
        
        /**
         * Retorna o driver JDBC apropriado
         */
        public String getDriverClass() {
            return switch (type) {
                case FIREBIRD -> "org.firebirdsql.jdbc.FBDriver";
                case POSTGRESQL -> "org.postgresql.Driver";
                case MYSQL -> "com.mysql.cj.jdbc.Driver";
            };
        }
    }
} 
