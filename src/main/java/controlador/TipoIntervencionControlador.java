package controlador;

import dao.AuditoriaDAO;
import dao.DAOBase;
import dao.TipoIntervencionDAO;
import modelo.Auditoria;
import modelo.TipoIntervencion;
import utilidades.Validaciones;

import java.sql.SQLException;
import java.util.List;

/** Controlador del CRUD de tipos de intervencion (RF014). */
public class TipoIntervencionControlador {

    private final TipoIntervencionDAO dao = new TipoIntervencionDAO();
    private final AuditoriaDAO auditoria = new AuditoriaDAO();

    public List<TipoIntervencion> listar() {
        try {
            return dao.listar();
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    public List<TipoIntervencion> buscar(String texto) {
        try {
            return dao.buscar(texto);
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    public String guardar(String nombre, String descripcion) {
        String error = Validaciones.primerError(
                Validaciones.requerido("Nombre", nombre),
                Validaciones.longitudMaxima("Nombre", nombre, 60),
                Validaciones.longitudMaxima("Descripción", descripcion, 150));
        if (error != null) {
            return error;
        }
        String detalle = Validaciones.normalizar(descripcion);
        try {
            if (dao.existeNombre(nombre.trim(), null)) {
                return "Ya existe un tipo de intervención con el nombre \"" + nombre.trim() + "\".";
            }
            dao.insertar(new TipoIntervencion(null, nombre.trim(),
                    detalle.isEmpty() ? null : detalle));
            auditoria.registrar(Auditoria.ALTA, "tipo_intervencion",
                    "Tipo de intervención creado: " + nombre.trim());
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    public String modificar(Integer idTipo, String nombre, String descripcion) {
        if (idTipo == null) {
            return "Seleccione un tipo de intervención de la lista antes de modificar.";
        }
        String error = Validaciones.primerError(
                Validaciones.requerido("Nombre", nombre),
                Validaciones.longitudMaxima("Nombre", nombre, 60),
                Validaciones.longitudMaxima("Descripción", descripcion, 150));
        if (error != null) {
            return error;
        }
        String detalle = Validaciones.normalizar(descripcion);
        try {
            if (dao.existeNombre(nombre.trim(), idTipo)) {
                return "Ya existe otro tipo de intervención con el nombre \"" + nombre.trim() + "\".";
            }
            dao.actualizar(new TipoIntervencion(idTipo, nombre.trim(),
                    detalle.isEmpty() ? null : detalle));
            auditoria.registrar(Auditoria.MODIFICACION, "tipo_intervencion",
                    "Tipo de intervención #" + idTipo + " modificado: " + nombre.trim());
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    public String eliminar(Integer idTipo) {
        if (idTipo == null) {
            return "Seleccione un tipo de intervención de la lista antes de eliminar.";
        }
        try {
            dao.eliminar(idTipo);
            auditoria.registrar(Auditoria.BORRADO, "tipo_intervencion",
                    "Tipo de intervención #" + idTipo + " eliminado");
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }
}
