package br.edu.adega.adegamaster.model.dao; // Ou o pacote adequado

/**
 * Exceção customizada para erros na camada de acesso a dados (DAO).
 * É uma RuntimeException para simplificar o tratamento nos Controllers.
 */
public class ExceptionDAO extends RuntimeException {

    // Construtor que aceita uma mensagem
    public  ExceptionDAO(String message) {
        super(message);
    }

    // Construtor que aceita uma mensagem e a exceção original (chaining)
    public  ExceptionDAO(String message, Throwable cause) {
        super(message, cause);
    }
}