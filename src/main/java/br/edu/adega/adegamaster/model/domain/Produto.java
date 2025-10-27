package br.edu.adega.adegamaster.model.domain;

import java.math.BigDecimal; // Importe para o tipo NUMERIC

public class Produto {
    private int idProduto;
    private String nome;
    private BigDecimal preco;
    private int estoque;
    private String descricao;
    private TipoProduto tipoProduto; // O objeto de relacionamento!

    // CONSTRUTOR VAZIO (Obrigatório para o DAO listar!)
    public Produto() {}

    // =========================================================
    // GETTERS E SETTERS (Resolvem TODOS os erros da imagem)
    // =========================================================

    // idProduto
    public int getIdProduto() {
        return idProduto;
    }
    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    // nome
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    // preco
    public BigDecimal getPreco() {
        return preco;
    }
    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    // estoque
    public int getEstoque() {
        return estoque;
    }
    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    // descricao
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    // tipoProduto (Relacionamento)
    public TipoProduto getTipoProduto() {
        return tipoProduto;
    }
    public void setTipoProduto(TipoProduto tipoProduto) {
        this.tipoProduto = tipoProduto;
    }
}