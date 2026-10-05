package controlador;

import dao.AuditoriaDAO;
import dao.DAOBase;
import modelo.Informe;

import java.sql.SQLException;
import java.util.List;

/** Controlador de la consulta de auditoria (RF004). */
public class AuditoriaControlador {

    private final AuditoriaDAO dao = new AuditoriaDAO();

    /** Filas para la grilla (7 columnas; la primera es el id). */
    public List<Object[]> listar() {
        try {
            return dao.listarFilas();
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    /** Busca por texto libre y opcionalmente por tipo de operacion. */
    public List<Object[]> buscar(String texto, String operacion) {
        try {
            return dao.buscar(texto, operacion);
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    public long totalDeRegistros() {
        try {
            return dao.total();
        } catch (SQLException e) {
            return 0;
        }
    }
}
