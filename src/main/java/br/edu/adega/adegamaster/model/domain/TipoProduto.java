package br.edu.adega.adegamaster.model.domain;

public class TipoProduto {

    private int id;
    private String nomeTipo;

    // Construtor padrão (obrigatório p/ frameworks, leitura de banco, etc.)
    public TipoProduto() {}

    // Construtor completo
    public TipoProduto(int id, String nomeTipo) {
        this.id = id;
        this.nomeTipo = nomeTipo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Conveniência: os sistemas geralmente pedem getNome(), não getNomeTipo()
    public String getNome() {
        return nomeTipo;
    }

    public void setNome(String nomeTipo) {
        this.nomeTipo = nomeTipo;
    }

    @Override
    public String toString() {
        return nomeTipo;
    }
}
