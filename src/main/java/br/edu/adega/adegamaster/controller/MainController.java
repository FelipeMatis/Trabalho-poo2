package br.edu.adega.adegamaster.controller;

import br.edu.adega.adegamaster.model.dao.CategoriaDAO;
import br.edu.adega.adegamaster.model.dao.ExceptionDAO; // << NOVO: Importa a exceção customizada
import br.edu.adega.adegamaster.model.dao.ProdutoDAO;
import br.edu.adega.adegamaster.model.domain.Categoria;
import br.edu.adega.adegamaster.model.domain.Produto;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    @FXML private TableView<Produto> tableProdutos;
    @FXML private TableColumn<Produto, Integer> columnId;
    @FXML private TableColumn<Produto, String> columnNome;
    @FXML private TableColumn<Produto, String> columnDescricao;
    @FXML private TableColumn<Produto, BigDecimal> columnPreco;
    @FXML private TableColumn<Produto, String> columnTipo; // mostrará o nome da categoria

    @FXML private TextField txtNome;
    @FXML private TextField txtPreco;
    @FXML private TextArea txtDescricao;
    @FXML private ComboBox<Categoria> comboTipo;
    @FXML private TextField txtBusca;

    private final ObservableList<Produto> produtosObs = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ... (configurações de colunas e listeners permanecem as mesmas) ...
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        columnDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        columnPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));

        // Categoria (mostra apenas o nome)
        columnTipo.setCellValueFactory(cell -> {
            Produto p = cell.getValue();
            if (p == null) return new SimpleStringProperty("");
            Categoria c = p.getCategoria();
            String nome = (c != null && c.getNome() != null) ? c.getNome() : "";
            return new SimpleStringProperty(nome);
        });

        // Inicializa combo e tabela com tratamento de erro
        try {
            carregarCategorias();
        } catch (ExceptionDAO e) { // << ATUALIZADO: Captura ExceptionDAO
            System.err.println("Erro ao carregar categorias no initialize: " + e.getMessage());
            e.printStackTrace();
            comboTipo.setItems(FXCollections.observableArrayList());
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Inicialização", "Falha ao carregar categorias: " + e.getMessage());
        }

        try {
            carregarTabelaProdutos();
        } catch (ExceptionDAO e) { // << ATUALIZADO: Captura ExceptionDAO
            System.err.println("Erro ao carregar produtos no initialize: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Inicialização", "Falha ao carregar produtos: " + e.getMessage());
        }

        // Preencher campos ao selecionar produto da tabela
        if (tableProdutos != null) {
            tableProdutos.getSelectionModel().selectedItemProperty()
                    .addListener((obs, oldItem, newItem) -> preencherCampos(newItem));
        }
    }

    private void carregarCategorias() throws ExceptionDAO { // << NOVO: Propaga ExceptionDAO
        List<Categoria> lista = categoriaDAO.listar();
        if (lista == null) lista = List.of();

        // Debugs
        System.out.println("DEBUG: carregarCategorias() -> quantidade = " + lista.size());
        for (Categoria c : lista) {
            System.out.println("DEBUG: Categoria -> id=" + c.getId() + " nome=" + c.getNome());
        }

        // popula o ComboBox
        ObservableList<Categoria> obs = FXCollections.observableArrayList(lista);
        comboTipo.setItems(obs);
        comboTipo.setPromptText(obs.isEmpty() ? "Nenhuma categoria cadastrada" : "Selecione a categoria...");

        // Força o ComboBox a exibir o nome do objeto
        comboTipo.setConverter(new StringConverter<Categoria>() {
            @Override
            public String toString(Categoria categoria) {
                return categoria == null ? "" : (categoria.getNome() == null ? "" : categoria.getNome());
            }
            @Override
            public Categoria fromString(String string) {
                return null;
            }
        });
        comboTipo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Categoria item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : (item.getNome() == null ? "" : item.getNome()));
            }
        });

        comboTipo.getSelectionModel().clearSelection();
    }

    private void carregarTabelaProdutos() throws ExceptionDAO { // << NOVO: Propaga ExceptionDAO
        List<Produto> lista = produtoDAO.listar();
        if (lista == null) lista = List.of();
        System.out.println("DEBUG: carregarTabelaProdutos() -> produtos retornados = " + lista.size());
        produtosObs.setAll(lista);
        if (tableProdutos != null) {
            tableProdutos.setItems(produtosObs);
        }
    }

    @FXML
    private void handleNovo() {
        limparCampos();
        if (tableProdutos != null) tableProdutos.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleSalvar() {
        String nome = txtNome.getText() != null ? txtNome.getText().trim() : "";
        String precoStr = txtPreco.getText() != null ? txtPreco.getText().trim() : "";
        String descricao = txtDescricao.getText() != null ? txtDescricao.getText().trim() : "";
        Categoria categoria = comboTipo.getValue();

        if (nome.isEmpty() || categoria == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validação", "Nome e Categoria são obrigatórios.");
            return;
        }

        BigDecimal preco = BigDecimal.ZERO;
        if (!precoStr.isEmpty()) {
            try {
                preco = new BigDecimal(precoStr.replace(",", "."));
            } catch (NumberFormatException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Preço inválido. Use formato numérico (ex: 45.50).");
                return;
            }
        }

        Produto selecionado = tableProdutos.getSelectionModel().getSelectedItem();

        try { // << NOVO: Bloco try/catch para a ExceptionDAO
            if (selecionado == null) {
                // inserir novo
                Produto p = new Produto();
                p.setNome(nome);
                p.setPreco(preco);
                p.setDescricao(descricao);
                p.setCategoria(categoria);
                p.setQuantidade(0);

                // O DAO agora lança ExceptionDAO, então não precisa checar o boolean 'ok'
                produtoDAO.inserir(p);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Produto cadastrado com sucesso!");

            } else {
                // atualizar existente
                selecionado.setNome(nome);
                selecionado.setPreco(preco);
                selecionado.setDescricao(descricao);
                selecionado.setCategoria(categoria);

                // O DAO agora lança ExceptionDAO, então não precisa checar o boolean 'ok'
                produtoDAO.atualizar(selecionado);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Produto atualizado com sucesso!");
            }

            carregarTabelaProdutos();
            limparCampos();

        } catch (ExceptionDAO e) { // << NOVO: Captura a falha do DAO
            System.err.println("Erro na operação Salvar: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Persistência", e.getMessage());
        }
    }

    @FXML
    private void handleEditar() {
        Produto selecionado = tableProdutos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleção", "Selecione um produto para editar.");
            return;
        }
        preencherCampos(selecionado);
    }

    @FXML
    private void handleExcluir() {
        Produto selecionado = tableProdutos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleção", "Selecione um produto para excluir.");
            return;
        }

        boolean ok = confirmar("Confirmação", "Deseja realmente excluir o produto selecionado?");

        if (ok) {
            try { // << NOVO: Bloco try/catch para a ExceptionDAO
                // O DAO agora lança ExceptionDAO, então não precisa checar o boolean
                produtoDAO.excluir(selecionado.getId());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Produto excluído com sucesso!");

                carregarTabelaProdutos();
                limparCampos();

            } catch (ExceptionDAO e) { // << NOVO: Captura a falha do DAO
                System.err.println("Erro na operação Excluir: " + e.getMessage());
                e.printStackTrace();
                mostrarAlerta(Alert.AlertType.ERROR, "Erro de Persistência", e.getMessage());
            }
        }
    }

    @FXML
    private void handleBuscar() {
        // ... (código de busca permanece o mesmo) ...
        String termo = txtBusca.getText() == null ? "" : txtBusca.getText().trim().toLowerCase();

        try { // << ATUALIZADO: Usamos o try/catch no handleBuscar para tratar erros no carregarTabelaProdutos
            if (termo.isEmpty()) {
                carregarTabelaProdutos(); // Recarrega tudo se o termo estiver vazio
                return;
            }
        } catch (ExceptionDAO e) {
            System.err.println("Erro na operação Buscar: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Persistência", "Não foi possível carregar a lista de produtos.");
            return; // Sai do método se falhar
        }

        // Mantemos a lógica de filtro na lista em memória (produtosObs)
        ObservableList<Produto> filtrado = FXCollections.observableArrayList();
        for (Produto p : produtosObs) {
            boolean matchNome = p.getNome() != null && p.getNome().toLowerCase().contains(termo);
            boolean matchCategoria = p.getCategoria() != null && p.getCategoria().getNome() != null &&
                    p.getCategoria().getNome().toLowerCase().contains(termo);
            if (matchNome || matchCategoria) {
                filtrado.add(p);
            }
        }
        tableProdutos.setItems(filtrado);
    }

    private void preencherCampos(Produto selecionado) {
        if (selecionado == null) {
            limparCampos();
            return;
        }
        txtNome.setText(selecionado.getNome());
        txtPreco.setText(selecionado.getPreco() != null ? selecionado.getPreco().toPlainString() : "");
        txtDescricao.setText(selecionado.getDescricao() != null ? selecionado.getDescricao() : "");
        comboTipo.setValue(selecionado.getCategoria());
    }

    private void limparCampos() {
        txtNome.clear();
        txtPreco.clear();
        txtDescricao.clear();
        comboTipo.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensagem);
        a.showAndWait();
    }

    private boolean confirmar(String titulo, String mensagem) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensagem);
        return a.showAndWait().filter(b -> b == ButtonType.OK).isPresent();
    }
}