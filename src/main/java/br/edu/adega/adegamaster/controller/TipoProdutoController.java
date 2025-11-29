package br.edu.adega.adegamaster.controller;

import br.edu.adega.adegamaster.model.dao.TipoProdutoDAO;
import br.edu.adega.adegamaster.model.domain.TipoProduto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class TipoProdutoController implements Initializable {

    private final TipoProdutoDAO tipoDAO = new TipoProdutoDAO();

    @FXML private TableView<TipoProduto> tableTipos;
    @FXML private TableColumn<TipoProduto, Integer> columnId;
    @FXML private TableColumn<TipoProduto, String> columnNome;
    @FXML private TextField txtNomeTipo;

    private final ObservableList<TipoProduto> tiposObs = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Ligando colunas com atributos do modelo
        columnId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        columnNome.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nome"));

        carregarTipos();

        // Quando selecionar na tabela, preenche o campo
        tableTipos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, novoVal) -> {
            if (novoVal != null) {
                txtNomeTipo.setText(novoVal.getNome());
            } else {
                txtNomeTipo.clear();
            }
        });
    }

    private void carregarTipos() {
        List<TipoProduto> lista = tipoDAO.listar(); // confirme nome listar() no DAO
        tiposObs.setAll(lista);
        tableTipos.setItems(tiposObs);
    }

    @FXML
    private void handleNovo() {
        txtNomeTipo.clear();
        tableTipos.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleSalvar() {
        String nome = txtNomeTipo.getText() != null ? txtNomeTipo.getText().trim() : "";

        if (nome.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validação", "O nome do tipo é obrigatório.");
            return;
        }

        TipoProduto selecionado = tableTipos.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            // Inserção
            TipoProduto novo = new TipoProduto();
            novo.setNome(nome);
            try {
                int novoId = tipoDAO.inserir(novo);
                if (novoId > 0) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Tipo cadastrado com sucesso!");
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Falha ao cadastrar o tipo.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao cadastrar o tipo: " + e.getMessage());
            }
        } else {
            // Atualização
            selecionado.setNome(nome);
            try {
                if (tipoDAO.atualizar(selecionado)) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Tipo atualizado com sucesso!");
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Falha ao atualizar o tipo.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao atualizar o tipo: " + e.getMessage());
            }
        }

        carregarTipos();
        handleNovo();
    }

    @FXML
    private void handleExcluir() {
        TipoProduto selecionado = tableTipos.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleção", "Selecione um tipo para excluir.");
            return;
        }

        if (confirmar("Confirmação", "Deseja realmente excluir o tipo selecionado?")) {
            try {
                if (tipoDAO.excluir(selecionado.getId())) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Tipo excluído com sucesso!");
                    carregarTipos();
                    handleNovo();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Falha ao excluir o tipo.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao excluir o tipo: " + e.getMessage());
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
