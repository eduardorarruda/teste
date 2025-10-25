package com.migracao.cli;

import com.migracao.cli.CliArgs.DatabaseConfig;
import com.migracao.config.DatabaseFactory;
import com.migracao.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Console;
import java.sql.Connection;
import java.util.Scanner;

/**
 * Controlador principal da interface de linha de comando (CLI).
 * Gerencia a interação com o usuário e orquestra o fluxo da aplicação.
 */
public class CliController {
    
    private static final Logger logger = LoggerFactory.getLogger(CliController.class);
    private final Scanner scanner;
    private final DatabaseFactory databaseFactory;
    
    public CliController() {
        // Garante que o Scanner use a codificação correta
        this.scanner = new Scanner(System.in, "UTF-8");
        this.databaseFactory = new DatabaseFactory();
    }
    
    /**
     * Inicia o processo interativo de configuração e migração
     */
    public void start() throws Exception {
        try {
            printBanner();
            
            // Coleta informações do banco de origem
            System.out.println("\n" + "=".repeat(60));
            System.out.println("CONFIGURAÇÃO DO BANCO DE DADOS DE ORIGEM");
            System.out.println("=".repeat(60));
            DatabaseConfig sourceConfig = collectDatabaseInfo("ORIGEM");
            
            // Coleta informações do banco de destino
            System.out.println("\n" + "=".repeat(60));
            System.out.println("CONFIGURAÇÃO DO BANCO DE DADOS DE DESTINO");
            System.out.println("=".repeat(60));
            DatabaseConfig targetConfig = collectDatabaseInfo("DESTINO");
            
            // Cria objeto CliArgs com as configurações
            CliArgs cliArgs = new CliArgs(sourceConfig, targetConfig);
            
            // Resumo das configurações
            printConfigurationSummary(cliArgs);
            
            // Confirmação do usuário
            if (confirmMigration()) {
                System.out.println("\n[INFO] Testando conexões...");
                
                // Testar conexões
                if (testConnections(cliArgs)) {
                    System.out.println("[SUCESSO] Conexões testadas com sucesso!");
                    
                    // Aqui você iniciará o processo de migração
                    // MigrationService migrationService = new MigrationService();
                    // migrationService.executeMigration(cliArgs);
                    
                    System.out.println("\n[INFO] Sistema pronto para iniciar a migração.");
                    System.out.println("[INFO] (Implementação da migração será adicionada em breve)");
                } else {
                    throw new Exception("Falha ao conectar aos bancos de dados.");
                }
            } else {
                System.out.println("\n[INFO] Migração cancelada pelo usuário.");
            }
            
        } finally {
            scanner.close();
        }
    }
    
    /**
     * Imprime o banner da aplicação
     */
    private void printBanner() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  " + Constants.APP_NAME + " - " + Constants.APP_VERSION);
        System.out.println("  Migração entre diferentes bancos de dados");
        System.out.println("=".repeat(60));
    }
    
    /**
     * Coleta informações de configuração do banco de dados
     */
    private DatabaseConfig collectDatabaseInfo(String tipo) {
        DatabaseConfig config = new DatabaseConfig();
        
        // Tipo de banco de dados
        System.out.println("\nTipo de banco de dados:");
        System.out.println("1. Firebird");
        System.out.println("2. PostgreSQL");
        System.out.println("3. MySQL");
        System.out.print("Escolha o tipo [1-3]: ");
        System.out.flush(); // Força a exibição do prompt
        
        int dbType = readInt(1, 3);
        config.setType(mapDatabaseType(dbType));
        
        // Host
        System.out.print("Host/IP (padrão: localhost): ");
        String host = scanner.nextLine().trim();
        config.setHost(host.isEmpty() ? "localhost" : host);
             
        // Porta
        int defaultPort = getDefaultPort(config.getType());
        config.setPort(readIntWithDefault("Porta (padrão: " + defaultPort + "): ", defaultPort));
        
        // Nome do banco / Caminho (para Firebird)
        if (config.getType() == DatabaseConfig.DatabaseType.FIREBIRD) {
            System.out.print("Caminho completo do arquivo .fdb: ");
        } else {
            System.out.print("Nome do banco de dados: ");
        }
        config.setDatabase(scanner.nextLine().trim());
        
        // Usuário
        System.out.print("Usuário (padrão: " + getDefaultUser(config.getType()) + "): ");
        String user = scanner.nextLine().trim();
        config.setUsername(user.isEmpty() ? getDefaultUser(config.getType()) : user);
        
        // Senha
        Console console = System.console();
        if (console != null) {
            char[] passwordArray = console.readPassword("Senha: ");
            config.setPassword(new String(passwordArray));
        } else {
            System.out.print("Senha: ");
            config.setPassword(scanner.nextLine());
        }
        
        // Charset (importante para Firebird)
        if (config.getType() == DatabaseConfig.DatabaseType.FIREBIRD) {
            System.out.print("Charset (padrão: UTF8): ");
            String charset = scanner.nextLine().trim();
            config.setCharset(charset.isEmpty() ? "UTF8" : charset);
        }
        
        return config;
    }
    
    /**
     * Lê um número inteiro dentro de um intervalo
     */
    private int readInt(int min, int max) {
        while (true) {
            try {
                // Garante que o scanner está pronto para ler
                if (!scanner.hasNextLine()) {
                    System.err.println("[ERRO] Scanner não tem próxima linha disponível");
                    throw new IllegalStateException("Scanner não disponível");
                }
                
                String input = scanner.nextLine().trim();
                
                // Debug: mostra o que foi lido
                System.out.println("[DEBUG] Entrada lida: '" + input + "' (length: " + input.length() + ")");
                
                if (input.isEmpty()) {
                    System.out.print("Entrada vazia. Digite um número entre " + min + " e " + max + ": ");
                    System.out.flush();
                    continue;
                }
                
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.print("Por favor, digite um número entre " + min + " e " + max + ": ");
                System.out.flush();
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Digite um número entre " + min + " e " + max + ": ");
                System.out.flush();
            }
        }
    }

    /**
     * Lê um número inteiro com um valor padrão.
     * Se o usuário apenas pressionar Enter, retorna o padrão.
     * Se digitar algo inválido, pede novamente.
     */
    private int readIntWithDefault(String prompt, int defaultValue) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    return defaultValue;
                }
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Digite um número ou deixe em branco para " + defaultValue + ": ");
            }
        }
    }
    
    /**
     * Mapeia o número escolhido para o tipo de banco
     */
    private DatabaseConfig.DatabaseType mapDatabaseType(int choice) {
        return switch (choice) {
            case 1 -> DatabaseConfig.DatabaseType.FIREBIRD;
            case 2 -> DatabaseConfig.DatabaseType.POSTGRESQL;
            case 3 -> DatabaseConfig.DatabaseType.MYSQL;
            default -> throw new IllegalArgumentException("Tipo de banco inválido");
        };
    }
    
    /**
     * Retorna a porta padrão para cada tipo de banco
     */
    private int getDefaultPort(DatabaseConfig.DatabaseType type) {
        return switch (type) {
            case FIREBIRD -> 3050;
            case POSTGRESQL -> 5432;
            case MYSQL -> 3306;
        };
    }
    
    /**
     * Retorna o usuário padrão para cada tipo de banco
     */
    private String getDefaultUser(DatabaseConfig.DatabaseType type) {
        return switch (type) {
            case FIREBIRD -> "SYSDBA";
            case POSTGRESQL -> "postgres";
            case MYSQL -> "root";
        };
    }
    
    /**
     * Imprime um resumo das configurações
     */
    private void printConfigurationSummary(CliArgs args) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("RESUMO DAS CONFIGURAÇÕES");
        System.out.println("=".repeat(60));
        
        DatabaseConfig source = args.getSourceConfig();
        System.out.println("\n[ORIGEM]");
        System.out.println("  Tipo:     " + source.getType());
        System.out.println("  Host:     " + source.getHost() + ":" + source.getPort());
        System.out.println("  Database: " + source.getDatabase());
        System.out.println("  Usuário:  " + source.getUsername());
        
        DatabaseConfig target = args.getTargetConfig();
        System.out.println("\n[DESTINO]");
        System.out.println("  Tipo:     " + target.getType());
        System.out.println("  Host:     " + target.getHost() + ":" + target.getPort());
        System.out.println("  Database: " + target.getDatabase());
        System.out.println("  Usuário:  " + target.getUsername());
        
        System.out.println("\n" + "=".repeat(60));
    }
    
    /**
     * Solicita confirmação do usuário para iniciar a migração
     */
    private boolean confirmMigration() {
        System.out.print("\nDeseja prosseguir com estas configurações? (S/N): ");
        String response = scanner.nextLine().trim().toUpperCase();
        return response.equals("S") || response.equals("SIM") || response.equals("Y") || response.equals("YES");
    }
    
    /**
     * Testa as conexões com os bancos de dados
     */
    private boolean testConnections(CliArgs args) {
        try {
            System.out.println("  [1/2] Testando conexão com banco de ORIGEM...");
            try (Connection sourceConn = databaseFactory.createConnection(args.getSourceConfig())) {
                if (sourceConn != null && !sourceConn.isClosed()) {
                    System.out.println("        ✓ Conexão de ORIGEM OK");
                }
            }
            
            System.out.println("  [2/2] Testando conexão com banco de DESTINO...");
            try (Connection targetConn = databaseFactory.createConnection(args.getTargetConfig())) {
                if (targetConn != null && !targetConn.isClosed()) {
                    System.out.println("        ✓ Conexão de DESTINO OK");
                }
            }
            
            return true;
        } catch (Exception e) {
            logger.error("Erro ao testar conexões", e);
            System.err.println("        ✗ Erro: " + e.getMessage());
            return false;
        }
    }
}