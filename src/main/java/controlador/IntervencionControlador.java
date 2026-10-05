package controlador;

import dao.AuditoriaDAO;
import dao.DAOBase;
import dao.EquipoDAO;
import dao.IntervencionDAO;
import dao.TipoIntervencionDAO;
import modelo.Auditoria;
import modelo.Equipo;
import modelo.Intervencion;
import modelo.TipoIntervencion;
import utilidades.Validaciones;

import java.sql.SQLException;
import java.util.List;

/** Controlador del modulo de intervenciones (RF005). */
public class IntervencionControlador {

    private final IntervencionDAO dao = new IntervencionDAO();
    private final EquipoDAO equipoDAO = new EquipoDAO();
    private final TipoIntervencionDAO tipoDAO = new TipoIntervencionDAO();
    private final AuditoriaDAO auditoria = new AuditoriaDAO();

    /** Filas para la grilla (7 columnas). */
    public List<Object[]> listar() {
        try {
            return dao.listarFilas();
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    public List<Object[]> buscar(String texto) {
        try {
            return dao.buscar(texto);
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    /** Equipos activos, para el combo del formulario de intervenciones. */
    public List<Equipo> equipos() {
        try {
            return equipoDAO.listarEquipos();
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    public List<TipoIntervencion> tiposDeIntervencion() {
        try {
            return tipoDAO.listar();
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    /** Alta de una intervencion (RF005). */
    public String guardar(Integer idEquipo, Integer idTipoIntervencion, String fecha,
                          String tecnico, String observaciones) {
        String error = Validaciones.primerError(
                idEquipo == null ? "Debe seleccionar el equipo intervenido." : null,
                idTipoIntervencion == null ? "Debe seleccionar el tipo de intervención." : null,
                Validaciones.fecha("Fecha", fecha),
                Validaciones.requerido("Técnico responsable", tecnico),
                Validaciones.longitudMaxima("Técnico responsable", tecnico, 120),
                Validaciones.requerido("Observaciones", observaciones),
                Validaciones.longitudMaxima("Observaciones", observaciones, 500));
        if (error != null) {
            return error;
        }
        try {
            Intervencion nueva = new Intervencion(null, idEquipo, idTipoIntervencion,
                    Validaciones.aFecha(fecha), tecnico.trim(), observaciones.trim());
            dao.insertar(nueva);
            auditoria.registrar(Auditoria.ALTA, "intervenciones",
                    "Intervención registrada sobre el equipo #" + idEquipo
                            + " el " + fecha.trim());
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }
}
