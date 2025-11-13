package DAO_bancoDados;

import java.util.List;

public interface InterfaceDao {
    void salva(Contato contrato) throws DAOException;
    Contato buscarPorID(int id) throws DAOException;
    List<Contato> buscarTodos() throws DAOException;
    void atualizar(Contato contrato) throws DAOException;
    void deletar(int id) throws DAOException;
}
