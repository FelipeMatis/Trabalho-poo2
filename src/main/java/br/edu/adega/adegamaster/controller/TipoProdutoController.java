package br.edu.adega.adegamaster.controller;

import br.edu.adega.adegamaster.model.dao.ExceptionDAO; // << NOVO: Importa a exceção customizada
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

        try { // << NOVO: Tenta carregar tipos na inicialização
            carregarTipos();
        } catch (ExceptionDAO e) {
            System.err.println("Erro ao carregar tipos na inicialização: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Inicialização", "Falha ao carregar Tipos de Produto: " + e.getMessage());
        }

        // Quando selecionar na tabela, preenche o campo
        tableTipos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, novoVal) -> {
            if (novoVal != null) {
                txtNomeTipo.setText(novoVal.getNome());
            } else {
                txtNomeTipo.clear();
            }
        });
    }

    private void carregarTipos() throws ExceptionDAO { // << NOVO: Propaga ExceptionDAO
        List<TipoProduto> lista = tipoDAO.listar();
        tiposObs.setAll(lista);
        tableTipos.setItems(tiposObs);
    }

    @FXML
    private void handleNovo() {
        txtNomeTipo.clear();
        tableTipos.getSelectionModel().clearSelection();
        txtNomeTipo.requestFocus(); // << BÔNUS: Reforça a usabilidade (foco no campo)
    }

    @FXML
    private void handleSalvar() {
        String nome = txtNomeTipo.getText() != null ? txtNomeTipo.getText().trim() : "";

        if (nome.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validação", "O nome do tipo é obrigatório.");
            return;
        }

        TipoProduto selecionado = tableTipos.getSelectionModel().getSelectedItem();

        try { // << NOVO: Bloco try/catch para a ExceptionDAO
            if (selecionado == null) {
                // Inserção
                TipoProduto novo = new TipoProduto();
                novo.setNome(nome);

                // Não precisa checar novoId > 0; o DAO lança ExceptionDAO em caso de falha crítica
                tipoDAO.inserir(novo);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Tipo cadastrado com sucesso!");

            } else {
                // Atualização
                selecionado.setNome(nome);

                // Não precisa checar o boolean; o DAO lança ExceptionDAO em caso de falha crítica
                tipoDAO.atualizar(selecionado);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Tipo atualizado com sucesso!");
            }

            carregarTipos();
            handleNovo();

        } catch (ExceptionDAO e) { // << NOVO: Captura a falha do DAO
            System.err.println("Erro na operação Salvar Tipo: " + e.getMessage());
            e.printStackTrace();
            // Mostra o alerta de erro usando a mensagem específica lançada pelo DAO
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Persistência", e.getMessage());
        }
    }

    @FXML
    private void handleExcluir() {
        TipoProduto selecionado = tableTipos.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleção", "Selecione um tipo para excluir.");
            return;
        }

        if (confirmar("Confirmação", "Deseja realmente excluir o tipo selecionado?")) {
            try { // << NOVO: Bloco try/catch para a ExceptionDAO
                // Não precisa checar o boolean; o DAO lança ExceptionDAO em caso de falha crítica
                tipoDAO.excluir(selecionado.getId());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Tipo excluído com sucesso!");

                carregarTipos();
                handleNovo();

            } catch (ExceptionDAO e) { // << NOVO: Captura a falha do DAO
                System.err.println("Erro na operação Excluir Tipo: " + e.getMessage());
                e.printStackTrace();
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