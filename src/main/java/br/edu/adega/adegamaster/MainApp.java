package br.edu.adega.adegamaster;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox; // Ou o layout principal que você escolher
import javafx.stage.Stage;
import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Carrega o FXML da tela principal (exemplo: uma tela com o menu)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/adega/adegamaster/view/MainView.fxml"));
        VBox root = loader.load(); // Ajuste o tipo de layout se for outro (Pane, AnchorPane, etc)

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Adega Master - CRUD MVC/DAO"); // Requisito: Título da aplicação
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}