package controlador;

import dao.DAOBase;
import dao.ReporteDAO;
import modelo.Informe;

import java.sql.SQLException;
import java.util.List;

/** Controlador de los reportes con JOIN, GROUP BY y agregaciones. */
public class ReporteControlador {

    private final ReporteDAO dao = new ReporteDAO();

    public List<String> nombresDeInformes() {
        return dao.nombresDeInformes();
    }

    /** Ejecuta la consulta elegida; devuelve null si no hay informe. */
    public Informe ejecutar(String nombreDeInforme) {
        try {
            return dao.ejecutar(nombreDeInforme);
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }
}
