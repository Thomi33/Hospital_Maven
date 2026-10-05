package controlador;

import dao.AuditoriaDAO;
import dao.DAOBase;
import dao.TipoEquipoDAO;
import modelo.Auditoria;
import modelo.TipoEquipo;
import utilidades.Validaciones;

import java.sql.SQLException;
import java.util.List;

/** Controlador del CRUD de tipos de equipo (RF010). */
public class TipoEquipoControlador {

    private final TipoEquipoDAO dao = new TipoEquipoDAO();
    private final AuditoriaDAO auditoria = new AuditoriaDAO();

    public List<TipoEquipo> listar() {
        try {
            return dao.listar();
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    public List<TipoEquipo> buscar(String texto) {
        try {
            return dao.buscar(texto);
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    public String guardar(String nombre, String descripcion) {
        String error = Validaciones.primerError(
                Validaciones.requerido("Nombre", nombre),
                Validaciones.longitudMaxima("Nombre", nombre, 80),
                Validaciones.longitudMaxima("Descripción", descripcion, 150));
        if (error != null) {
            return error;
        }
        String valor = Validaciones.normalizar(nombre);
        String detalle = Validaciones.normalizar(descripcion);
        try {
            if (dao.existeNombre(valor, null)) {
                return "Ya existe un tipo de equipo con el nombre \"" + valor + "\".";
            }
            dao.insertar(new TipoEquipo(null, valor, detalle.isEmpty() ? null : detalle));
            auditoria.registrar(Auditoria.ALTA, "tipo_equipo", "Tipo de equipo creado: " + valor);
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    public String modificar(Integer idTipo, String nombre, String descripcion) {
        if (idTipo == null) {
            return "Seleccione un tipo de equipo de la lista antes de modificar.";
        }
        String error = Validaciones.primerError(
                Validaciones.requerido("Nombre", nombre),
                Validaciones.longitudMaxima("Nombre", nombre, 80),
                Validaciones.longitudMaxima("Descripción", descripcion, 150));
        if (error != null) {
            return error;
        }
        String valor = Validaciones.normalizar(nombre);
        String detalle = Validaciones.normalizar(descripcion);
        try {
            if (dao.existeNombre(valor, idTipo)) {
                return "Ya existe otro tipo de equipo con el nombre \"" + valor + "\".";
            }
            dao.actualizar(new TipoEquipo(idTipo, valor, detalle.isEmpty() ? null : detalle));
            auditoria.registrar(Auditoria.MODIFICACION, "tipo_equipo",
                    "Tipo de equipo #" + idTipo + " modificado: " + valor);
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    public String eliminar(Integer idTipo) {
        if (idTipo == null) {
            return "Seleccione un tipo de equipo de la lista antes de eliminar.";
        }
        try {
            dao.eliminar(idTipo);
            auditoria.registrar(Auditoria.BORRADO, "tipo_equipo",
                    "Tipo de equipo #" + idTipo + " eliminado");
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }
}
