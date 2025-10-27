package br.edu.adega.adegamaster.model.domain;

public class TipoProduto {
    private int idTipoProduto;
    private String nomeTipo;

    // 1. Construtor Vazio
    public TipoProduto() {}

    // 2. Construtor Completo
    public TipoProduto(int idTipoProduto, String nomeTipo) {
        this.idTipoProduto = idTipoProduto;
        this.nomeTipo = nomeTipo;
    }

    // 3. Getters e Setters (Clique direito > Generate > Getters e Setters)
    public int getIdTipoProduto() { return idTipoProduto; }
    public void setIdTipoProduto(int idTipoProduto) { this.idTipoProduto = idTipoProduto; }
    public String getNomeTipo() { return nomeTipo; }
    public void setNomeTipo(String nomeTipo) { this.nomeTipo = nomeTipo; }

    // 4. Método para exibição na interface
    @Override
    public String toString() {
        return nomeTipo;
    }
}