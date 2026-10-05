package controlador;

import dao.AuditoriaDAO;
import dao.DAOBase;
import dao.FuncionalidadDAO;
import modelo.Auditoria;
import modelo.Funcionalidad;
import utilidades.Validaciones;

import java.sql.SQLException;
import java.util.List;

/** Controlador del CRUD de funcionalidades (RF003). */
public class FuncionalidadControlador {

    private final FuncionalidadDAO dao = new FuncionalidadDAO();
    private final AuditoriaDAO auditoria = new AuditoriaDAO();

    public List<Funcionalidad> listar() {
        try {
            return dao.listar();
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    public List<Funcionalidad> listarActivas() {
        try {
            return dao.listarActivas();
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    public List<Funcionalidad> buscar(String texto) {
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
                Validaciones.requerido("Descripción", descripcion),
                Validaciones.longitudMaxima("Descripción", descripcion, 150));
        if (error != null) {
            return error;
        }
        try {
            if (dao.existeNombre(nombre.trim(), null)) {
                return "Ya existe una funcionalidad con el nombre \"" + nombre.trim() + "\".";
            }
            dao.insertar(new Funcionalidad(null, nombre.trim(), descripcion.trim(),
                    Funcionalidad.ACTIVO));
            auditoria.registrar(Auditoria.ALTA, "funcionalidades",
                    "Funcionalidad creada: " + nombre.trim());
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    public String modificar(Integer idFuncionalidad, String nombre, String descripcion,
                            String estado) {
        if (idFuncionalidad == null) {
            return "Seleccione una funcionalidad de la lista antes de modificar.";
        }
        String error = Validaciones.primerError(
                Validaciones.requerido("Nombre", nombre),
                Validaciones.longitudMaxima("Nombre", nombre, 80),
                Validaciones.requerido("Descripción", descripcion),
                Validaciones.longitudMaxima("Descripción", descripcion, 150));
        if (error != null) {
            return error;
        }
        try {
            if (dao.existeNombre(nombre.trim(), idFuncionalidad)) {
                return "Ya existe otra funcionalidad con el nombre \"" + nombre.trim() + "\".";
            }
            dao.actualizar(new Funcionalidad(idFuncionalidad, nombre.trim(),
                    descripcion.trim(), estado));
            auditoria.registrar(Auditoria.MODIFICACION, "funcionalidades",
                    "Funcionalidad #" + idFuncionalidad + " modificada: " + nombre.trim());
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    /** Baja logica de la funcionalidad (RF003). */
    public String eliminar(Integer idFuncionalidad) {
        if (idFuncionalidad == null) {
            return "Seleccione una funcionalidad de la lista antes de dar de baja.";
        }
        try {
            dao.cambiarEstado(idFuncionalidad, Funcionalidad.INACTIVO);
            auditoria.registrar(Auditoria.BAJA, "funcionalidades",
                    "Funcionalidad #" + idFuncionalidad + " dada de baja");
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }
}
