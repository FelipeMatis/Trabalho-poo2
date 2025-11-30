import br.edu.adega.adegamaster.model.dao.CategoriaDAO;
import br.edu.adega.adegamaster.model.dao.ExceptionDAO;
import br.edu.adega.adegamaster.model.domain.Categoria;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CategoriaDAOTeste {

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    @Test
    void testInserir_CategoriaValida_DeveRetornarIdPositivo() {
        // ARRANGE (Preparar)
        Categoria novaCategoria = new Categoria();
        novaCategoria.setNome("Teste JUnit");
        novaCategoria.setDescricao("Categoria criada para teste de unidade.");

        int idRetornado = -1;

        try {
            // ACT (Executar)
            idRetornado = categoriaDAO.inserir(novaCategoria);

            // ASSERT (Verificar)
            Assertions.assertTrue(idRetornado > 0, "A inserção de categoria válida deve retornar um ID positivo.");

            Assertions.assertEquals(idRetornado, novaCategoria.getId(), "O ID retornado deve ser o mesmo setado no objeto Categoria.");

        } catch (ExceptionDAO e) {
            // Se a ExceptionDAO for lançada (erro de banco), o teste falha
            Assertions.fail("O teste falhou devido a uma exceção de persistência: " + e.getMessage());
        } finally {
            // CLEANUP (Limpar)
            // O método excluir será chamado apenas se idRetornado for > 0
            if (idRetornado > 0) {
                categoriaDAO.excluir(idRetornado);
            }
        }
    }
    @Test
    void testBuscarPorId_IdInexistente_DeveRetornarNull() {
        // Usamos um ID que é garantido ser inválido ou não usado (como -1 ou um número muito alto).
        final int ID_INEXISTENTE = -999;

        Categoria categoriaBuscada;

        try {
            // ACT (Executar Busca)
            // 1. Tenta buscar uma categoria que não existe no banco.
            categoriaBuscada = categoriaDAO.buscarPorId(ID_INEXISTENTE);

            // ASSERT (Verificar)
            // 2. O objeto retornado deve ser nulo.
            Assertions.assertNull(categoriaBuscada, "Buscar por um ID que não existe deve retornar null.");

        } catch (ExceptionDAO e) {
            // Se a ExceptionDAO for lançada, o teste falha. A busca não deve lançar exceção se não encontrar nada.
            Assertions.fail("O teste falhou devido a uma exceção de persistência inesperada: " + e.getMessage());
        }
    }

    @Test
    void testAtualizar_NomeAlterado_DeveRefletirNoBanco() {
        Categoria categoriaOriginal = new Categoria();
        categoriaOriginal.setNome("Nome Antigo");
        categoriaOriginal.setDescricao("Descrição Original");

        int idInserido = -1;
        final String NOVO_NOME = "Nome Atualizado Teste";
        Categoria categoriaAtualizada = null;

        try {
            // ACT 1: Insere a categoria para ter algo para atualizar
            idInserido = categoriaDAO.inserir(categoriaOriginal);

            // ASSERT 1: Verificação de Segurança
            Assertions.assertTrue(idInserido > 0, "Falha na preparação: não conseguiu inserir o dado.");

            // ACT 2: Altera o objeto e executa a atualização
            categoriaOriginal.setNome(NOVO_NOME);
            boolean sucessoAtualizacao = categoriaDAO.atualizar(categoriaOriginal);

            // ACT 3: Busca o registro de volta para verificar a alteração
            categoriaAtualizada = categoriaDAO.buscarPorId(idInserido);

            // ASSERT 2: Verificar o resultado
            Assertions.assertTrue(sucessoAtualizacao, "A atualização deve retornar true para sucesso.");
            Assertions.assertNotNull(categoriaAtualizada, "O objeto buscado após a atualização não deve ser nulo.");
            Assertions.assertEquals(NOVO_NOME, categoriaAtualizada.getNome(), "O nome no banco de dados deve ser igual ao novo nome setado.");

        } catch (ExceptionDAO e) {
            Assertions.fail("O teste falhou devido a uma exceção de persistência: " + e.getMessage());
        } finally {
            // CLEANUP (Limpar)
            if (idInserido > 0) {
                categoriaDAO.excluir(idInserido);
            }
        }
    }
    @Test
    void testExcluir_CategoriaExistente_DeveRetornarTrueECategoriaNaoDeveSerEncontrada() {
        // ARRANGE (Preparar)
        Categoria categoriaParaExcluir = new Categoria();
        categoriaParaExcluir.setNome("Exclusao Teste");

        int idParaExcluir = -1;
        boolean sucessoExclusao = false;

        try {
            // ACT 1: Insere a categoria para garantir que ela existe
            idParaExcluir = categoriaDAO.inserir(categoriaParaExcluir);

            // ASSERT 1: Verificação de Segurança
            Assertions.assertTrue(idParaExcluir > 0, "Falha na preparação: não conseguiu inserir o dado.");

            // ACT 2: Executa a exclusão
            sucessoExclusao = categoriaDAO.excluir(idParaExcluir);

            // ACT 3: Tenta buscar o registro novamente após a exclusão
            Categoria categoriaExcluida = categoriaDAO.buscarPorId(idParaExcluir);

            // ASSERT 2: Verificar o resultado
            Assertions.assertTrue(sucessoExclusao, "A exclusão deve retornar true para sucesso.");
            Assertions.assertNull(categoriaExcluida, "O registro deve ser nulo após a exclusão do banco de dados.");

        } catch (ExceptionDAO e) {
            Assertions.fail("O teste falhou devido a uma exceção de persistência: " + e.getMessage());
        } finally {
            // CLEANUP (Limpar)
            // O cleanup tenta excluir novamente por segurança, caso a exclusão no ACT tenha falhado.
            if (!sucessoExclusao && idParaExcluir > 0) {
                categoriaDAO.excluir(idParaExcluir);
            }
        }
    }

}