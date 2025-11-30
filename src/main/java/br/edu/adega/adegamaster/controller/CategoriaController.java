package br.edu.adega.adegamaster.controller;

import br.edu.adega.adegamaster.model.dao.CategoriaDAO;
import br.edu.adega.adegamaster.model.dao.ExceptionDAO;
import br.edu.adega.adegamaster.model.domain.Categoria;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CategoriaController implements Initializable {

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    @FXML private TableView<Categoria> tableCategorias;
    @FXML private TableColumn<Categoria, Integer> columnId;
    @FXML private TableColumn<Categoria, String> columnNome;
    @FXML private TextField txtNomeCategoria;
    @FXML private TextArea txtDescricaoCategoria; // Adicionado para a Descrição

    private final ObservableList<Categoria> categoriasObs = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Liga as colunas com os atributos do modelo
        columnId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        columnNome.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nome"));

        try {
            // 2. Carrega as categorias na inicialização
            carregarCategorias();
        } catch (ExceptionDAO e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Inicialização", "Falha ao carregar Categorias: " + e.getMessage());
        }

        // 3. Listener para preencher os campos ao selecionar na tabela
        tableCategorias.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, novoVal) -> {
            if (novoVal != null) {
                txtNomeCategoria.setText(novoVal.getNome());
                txtDescricaoCategoria.setText(novoVal.getDescricao());
            } else {
                txtNomeCategoria.clear();
                txtDescricaoCategoria.clear();
            }
        });
    }

    private void carregarCategorias() throws ExceptionDAO {
        List<Categoria> lista = categoriaDAO.listar();
        categoriasObs.setAll(lista);
        tableCategorias.setItems(categoriasObs);
    }

    @FXML
    private void handleNovo() {
        txtNomeCategoria.clear();
        txtDescricaoCategoria.clear();
        tableCategorias.getSelectionModel().clearSelection();
        txtNomeCategoria.requestFocus();
    }

    @FXML
    private void handleSalvar() {
        String nome = txtNomeCategoria.getText() != null ? txtNomeCategoria.getText().trim() : "";
        String descricao = txtDescricaoCategoria.getText() != null ? txtDescricaoCategoria.getText().trim() : "";

        if (nome.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validação", "O nome da categoria é obrigatório.");
            return;
        }

        Categoria selecionada = tableCategorias.getSelectionModel().getSelectedItem();

        try {
            if (selecionada == null) {
                // Inserção
                Categoria nova = new Categoria();
                nova.setNome(nome);
                nova.setDescricao(descricao);

                categoriaDAO.inserir(nova);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Categoria cadastrada com sucesso!");

            } else {
                // Atualização
                selecionada.setNome(nome);
                selecionada.setDescricao(descricao);

                categoriaDAO.atualizar(selecionada);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Categoria atualizada com sucesso!");
            }

            carregarCategorias();
            handleNovo();

        } catch (ExceptionDAO e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Persistência", e.getMessage());
        }
    }

    @FXML
    private void handleExcluir() {
        Categoria selecionada = tableCategorias.getSelectionModel().getSelectedItem();

        if (selecionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleção", "Selecione uma categoria para excluir.");
            return;
        }

        if (confirmar("Confirmação", "Deseja realmente excluir a categoria selecionada?")) {
            try {
                categoriaDAO.excluir(selecionada.getId());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Categoria excluída com sucesso!");

                carregarCategorias();
                handleNovo();

            } catch (ExceptionDAO e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro de Persistência", e.getMessage());
            }
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private boolean confirmar(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}