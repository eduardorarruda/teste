package com.migracao;

import com.migracao.cli.CliController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ponto de entrada principal da aplicação de migração de dados.
 * Delega a lógica de CLI para o CliController.
 */
public class Main {
    
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
        try {
            logger.info("Iniciando aplicação de migração de dados...");
            
            CliController controller = new CliController();
            controller.start();
            
            logger.info("Aplicação finalizada com sucesso.");
            
        } catch (Exception e) {
            logger.error("Erro na aplicação", e);
            System.err.println("\n[ERRO] " + e.getMessage());
            System.exit(1);
        }
    }
}