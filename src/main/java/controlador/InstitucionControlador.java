package controlador;

import dao.AuditoriaDAO;
import dao.DAOBase;
import dao.InstitucionDAO;
import modelo.Auditoria;
import modelo.Institucion;
import utilidades.Validaciones;

import java.sql.SQLException;
import java.util.List;

/** Controlador del CRUD de instituciones (RF008). */
public class InstitucionControlador {

    private final InstitucionDAO dao = new InstitucionDAO();
    private final AuditoriaDAO auditoria = new AuditoriaDAO();

    public List<Institucion> listar() {
        try {
            return dao.listar();
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    /** Filas para la grilla con el pais (JOIN). */
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

    private String validar(String nombre, String direccion, String telefono, String ciudad,
                           Integer idPais) {
        String error = Validaciones.primerError(
                Validaciones.requerido("Nombre", nombre),
                Validaciones.longitudMaxima("Nombre", nombre, 120),
                Validaciones.requerido("Dirección", direccion),
                Validaciones.longitudMaxima("Dirección", direccion, 150),
                Validaciones.telefono("Teléfono", telefono),
                Validaciones.requerido("Ciudad", ciudad),
                Validaciones.longitudMaxima("Ciudad", ciudad, 80));
        if (error != null) {
            return error;
        }
        return idPais == null ? "Debe seleccionar el país de la institución." : null;
    }

    public String guardar(String nombre, String direccion, String telefono, String ciudad,
                          Integer idPais) {
        String error = validar(nombre, direccion, telefono, ciudad, idPais);
        if (error != null) {
            return error;
        }
        try {
            if (dao.existeNombre(nombre.trim(), null)) {
                return "Ya existe una institución con el nombre \"" + nombre.trim() + "\".";
            }
            dao.insertar(new Institucion(null, nombre.trim(), direccion.trim(),
                    telefono.trim(), ciudad.trim(), idPais));
            auditoria.registrar(Auditoria.ALTA, "instituciones",
                    "Institución creada: " + nombre.trim());
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    public String modificar(Integer idInstitucion, String nombre, String direccion,
                            String telefono, String ciudad, Integer idPais) {
        if (idInstitucion == null) {
            return "Seleccione una institución de la lista antes de modificar.";
        }
        String error = validar(nombre, direccion, telefono, ciudad, idPais);
        if (error != null) {
            return error;
        }
        try {
            if (dao.existeNombre(nombre.trim(), idInstitucion)) {
                return "Ya existe otra institución con el nombre \"" + nombre.trim() + "\".";
            }
            dao.actualizar(new Institucion(idInstitucion, nombre.trim(), direccion.trim(),
                    telefono.trim(), ciudad.trim(), idPais));
            auditoria.registrar(Auditoria.MODIFICACION, "instituciones",
                    "Institución #" + idInstitucion + " modificada: " + nombre.trim());
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    public String eliminar(Integer idInstitucion) {
        if (idInstitucion == null) {
            return "Seleccione una institución de la lista antes de eliminar.";
        }
        try {
            dao.eliminar(idInstitucion);
            auditoria.registrar(Auditoria.BORRADO, "instituciones",
                    "Institución #" + idInstitucion + " eliminada");
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }
}
