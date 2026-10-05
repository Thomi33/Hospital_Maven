package controlador;

import dao.AuditoriaDAO;
import dao.DAOBase;
import dao.UbicacionDAO;
import modelo.Auditoria;
import modelo.Ubicacion;
import utilidades.Validaciones;

import java.sql.SQLException;
import java.util.List;

/** Controlador del CRUD de ubicaciones (RF007). */
public class UbicacionControlador {

    private final UbicacionDAO dao = new UbicacionDAO();
    private final AuditoriaDAO auditoria = new AuditoriaDAO();

    public List<Ubicacion> listar() {
        try {
            return dao.listar();
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    /** Filas para la grilla con la institucion (JOIN). */
    public List<Object[]> listarFilas() {
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

    private String validar(String nombre, String descripcion, Integer idInstitucion) {
        String error = Validaciones.primerError(
                Validaciones.requerido("Nombre", nombre),
                Validaciones.longitudMaxima("Nombre", nombre, 80),
                Validaciones.longitudMaxima("Descripción", descripcion, 150));
        if (error != null) {
            return error;
        }
        return idInstitucion == null ? "Debe seleccionar la institución." : null;
    }

    public String guardar(String nombre, String descripcion, Integer idInstitucion) {
        String error = validar(nombre, descripcion, idInstitucion);
        if (error != null) {
            return error;
        }
        String detalle = Validaciones.normalizar(descripcion);
        try {
            if (dao.existeNombre(nombre.trim(), null)) {
                return "Ya existe una ubicación con el nombre \"" + nombre.trim() + "\".";
            }
            dao.insertar(new Ubicacion(null, nombre.trim(),
                    detalle.isEmpty() ? null : detalle, idInstitucion));
            auditoria.registrar(Auditoria.ALTA, "ubicaciones",
                    "Ubicación creada: " + nombre.trim());
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    public String modificar(Integer idUbicacion, String nombre, String descripcion,
                            Integer idInstitucion) {
        if (idUbicacion == null) {
            return "Seleccione una ubicación de la lista antes de modificar.";
        }
        String error = validar(nombre, descripcion, idInstitucion);
        if (error != null) {
            return error;
        }
        String detalle = Validaciones.normalizar(descripcion);
        try {
            if (dao.existeNombre(nombre.trim(), idUbicacion)) {
                return "Ya existe otra ubicación con el nombre \"" + nombre.trim() + "\".";
            }
            dao.actualizar(new Ubicacion(idUbicacion, nombre.trim(),
                    detalle.isEmpty() ? null : detalle, idInstitucion));
            auditoria.registrar(Auditoria.MODIFICACION, "ubicaciones",
                    "Ubicación #" + idUbicacion + " modificada: " + nombre.trim());
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    public String eliminar(Integer idUbicacion) {
        if (idUbicacion == null) {
            return "Seleccione una ubicación de la lista antes de eliminar.";
        }
        try {
            dao.eliminar(idUbicacion);
            auditoria.registrar(Auditoria.BORRADO, "ubicaciones",
                    "Ubicación #" + idUbicacion + " eliminada");
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }
}
