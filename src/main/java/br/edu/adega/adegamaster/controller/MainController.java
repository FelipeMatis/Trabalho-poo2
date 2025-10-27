package br.edu.adega.adegamaster.controller;

import br.edu.adega.adegamaster.dao.ProdutoDAO;
import br.edu.adega.adegamaster.dao.TipoProdutoDAO;
import br.edu.adega.adegamaster.domain.Produto;
import br.edu.adega.adegamaster.domain.TipoProduto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    private ProdutoDAO produtoDAO = new ProdutoDAO();
    private TipoProdutoDAO tipoProdutoDAO = new TipoProdutoDAO();

    private ObservableList<Produto> listaProdutos;
    private ObservableList<TipoProduto> listaTipos;

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
    }

    private void carregarTabelaProdutos() {
        listaProdutos = FXCollections.observableArrayList(produtoDAO.listarTodos());
        tableProdutos.setItems(listaProdutos);
    }

    private void carregarComboBoxTipos() {
        listaTipos = FXCollections.observableArrayList(tipoProdutoDAO.listarTodos());
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
    }

    @FXML
    private void handleSalvar() {
        Produto produto = new Produto();
        produto.setNome(txtNome.getText());
        produto.setPreco(Double.parseDouble(txtPreco.getText()));
        produto.setDescricao(txtDescricao.getText());
        produto.setTipoProduto(cbTipoProduto.getSelectionModel().getSelectedItem());

        Produto selecionado = tableProdutos.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            produto.setId(selecionado.getId());
            produtoDAO.atualizar(produto);
        } else {
            produtoDAO.inserir(produto);
        }

        carregarTabelaProdutos();
        limparCampos();
    }

    @FXML
    private void handleEditar() {
        Produto selecionado = tableProdutos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            System.out.println("Selecione um produto para editar.");
            return;
        }
    }

    @FXML
    private void handleExcluir() {
        Produto selecionado = tableProdutos.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            produtoDAO.excluir(selecionado.getId());
            carregarTabelaProdutos();
            limparCampos();
        } else {
            System.out.println("Selecione um produto para excluir.");
        }
    }
}