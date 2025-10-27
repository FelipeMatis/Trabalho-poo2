package br.edu.adega.adegamaster;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Carrega uma view existente (ProdutoView.fxml) — você pode trocar para MainView.fxml se criar/popular esse arquivo
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/adega/adegamaster/ProdutoView.fxml"));
        Parent root = loader.load(); // Parent evita ClassCastException se o root não for VBox

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Adega Master - CRUD MVC/DAO");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
