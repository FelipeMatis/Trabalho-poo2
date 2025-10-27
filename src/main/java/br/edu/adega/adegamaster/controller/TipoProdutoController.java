package br.edu.adega.adegamaster.controller;

import br.edu.adega.adegamaster.dao.ProdutoDAO;
import br.edu.adega.adegamaster.dao.TipoProdutoDAO;
import br.edu.adega.adegamaster.domain.Produto;
import br.edu.adega.adegamaster.domain.TipoProduto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList; // NOVO
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;

public class ProdutoController implements Initializable {

    @FXML private TextField txtNome;
    @FXML private TextField txtPreco;
    @FXML private TextArea txtDescricao;
    @FXML private ComboBox<TipoProduto> cbTipoProduto;
    @FXML private TableView<Produto> tableProdutos;
    @FXML private TableColumn<Produto, Integer> colId;
    @FXML private TableColumn<Produto, String> colNome;
    @FXML private TableColumn<Produto, Double> colPreco;
    @FXML private TableColumn<Produto, TipoProduto> colTipo;

    // NOVO CAMPO PARA BUSCA
    @FXML private TextField txtBusca;

    private ProdutoDAO produtoDAO = new ProdutoDAO();
    private TipoProdutoDAO tipoProdutoDAO = new TipoProdutoDAO();

    private ObservableList<Produto> listaProdutos;
    private FilteredList<Produto> listaFiltrada; // NOVO

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoProduto"));

        carregarTabelaProdutos();
        carregarComboBoxTipos();

        tableProdutos.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> preencherCampos(newValue));

        // NOVO: Adicionar listener para o campo de busca
        txtBusca.textProperty().addListener((observable, oldValue, newValue) -> filtrarProdutos(newValue));
    }

    private void carregarTabelaProdutos() {
        listaProdutos = FXCollections.observableArrayList(produtoDAO.listarTodos());
        listaFiltrada = new FilteredList<>(listaProdutos, p -> true); // Inicializa com todos
        tableProdutos.setItems(listaFiltrada);
    }

    // NOVO MÉTODO DE FILTRO
    private void filtrarProdutos(String texto) {
        String lowerCaseFilter = texto.toLowerCase();
        listaFiltrada.setPredicate(produto -> {
            // Se o campo de busca está vazio, exibe tudo
            if (texto == null || texto.isEmpty()) {
                return true;
            }

            // Compara o nome do produto com o texto de busca
            if (produto.getNome().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }
            // Compara o nome do tipo de produto com o texto de busca
            if (produto.getTipoProduto() != null && produto.getTipoProduto().getNome().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }
            return false; // Não houve correspondência
        });
    }


    private void carregarComboBoxTipos() {
        ObservableList<TipoProduto> listaTipos = FXCollections.observableArrayList(tipoProdutoDAO.listarTodos());
        cbTipoProduto.setItems(listaTipos);
    }

    private void preencherCampos(Produto produto) {
        if (produto != null) {
            txtNome.setText(produto.getNome());
            txtPreco.setText(String.valueOf(produto.getPreco()));
            txtDescricao.setText(produto.getDescricao());
            cbTipoProduto.getSelectionModel().select(produto.getTipoProduto());
        } else {
            limparCampos();
        }
    }

    private void limparCampos() {
        txtNome.clear();
        txtPreco.clear();
        txtDescricao.clear();
        cbTipoProduto.getSelectionModel().clearSelection();
        tableProdutos.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleSalvar() {
        // ... (Lógica de Salvar/Atualizar, mantida) ...
        Produto produto = new Produto();
        try {
            if (txtNome.getText().isEmpty() || txtPreco.getText().isEmpty() || cbTipoProduto.getSelectionModel().isEmpty()) {
                exibirAlerta(Alert.AlertType.WARNING, "Campos Obrigatórios", "Nome, Preço e Tipo de Produto são obrigatórios.");
                return;
            }

            produto.setNome(txtNome.getText());
            produto.setPreco(Double.parseDouble(txtPreco.getText()));
            produto.setDescricao(txtDescricao.getText());
            produto.setTipoProduto(cbTipoProduto.getSelectionModel().getSelectedItem());

            Produto selecionado = tableProdutos.getSelectionModel().getSelectedItem();
            if (selecionado != null) {
                produto.setId(selecionado.getId());
                produtoDAO.atualizar(produto);
                exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Produto atualizado com sucesso!");
            } else {
                produtoDAO.inserir(produto);
                exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Produto inserido com sucesso!");
            }
        } catch (NumberFormatException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Formato", "Preço deve ser um número válido.");
        }

        carregarTabelaProdutos();
        limparCampos();
    }

    @FXML
    private void handleEditar() {
        Produto selecionado = tableProdutos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Seleção Necessária", "Selecione um produto na tabela para editar.");
        }
    }

    @FXML
    private void handleExcluir() {
        Produto selecionado = tableProdutos.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmação de Exclusão");
            alert.setHeaderText("Tem certeza que deseja excluir " + selecionado.getNome() + "?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                try {
                    produtoDAO.excluir(selecionado.getId());
                    exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Produto excluído com sucesso.");
                    carregarTabelaProdutos();
                    limparCampos();
                } catch (Exception e) {
                    exibirAlerta(Alert.AlertType.ERROR, "Erro de Exclusão", "Não foi possível excluir o produto. Verifique se ele não está em uso.");
                }
            }
        } else {
            exibirAlerta(Alert.AlertType.WARNING, "Seleção Necessária", "Selecione um produto na tabela para excluir.");
        }
    }

    // MÉTODO DE ALERTA COMPARTILHADO
    private void exibirAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}