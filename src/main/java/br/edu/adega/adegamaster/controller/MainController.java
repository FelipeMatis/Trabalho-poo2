package br.edu.adega.adegamaster.controller;

import br.edu.adega.adegamaster.MainApp;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainController {

    // Método para abrir a tela de Gerenciamento de Tipos de Produtos
    @FXML
    public void handleAbrirTipos() {
        try {
            // Cria um novo Stage (janela)
            Stage stage = new Stage();

            // Carrega o FXML do TipoProduto
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/br/edu/adega/adegamaster/view/TipoProdutoView.fxml"));
            AnchorPane root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gerenciar Tipos de Produtos");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Implementar tratamento de erro (ex: Alert) aqui.
        }
    }

    // Método para abrir a tela de Gerenciamento de Produtos (a ser implementado)
    @FXML
    public void handleAbrirProdutos() {
        // Implemente a mesma lógica do handleAbrirTipos, mas carregando o ProdutoView.fxml
        System.out.println("Abrir tela de Produtos...");
    }
}