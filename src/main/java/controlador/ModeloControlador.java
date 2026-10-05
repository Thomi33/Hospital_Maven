package controlador;

import dao.AuditoriaDAO;
import dao.DAOBase;
import dao.ModeloDAO;
import modelo.Auditoria;
import modelo.Modelo;
import utilidades.Validaciones;

import java.sql.SQLException;
import java.util.List;

/** Controlador del CRUD de modelos (RF012). */
public class ModeloControlador {

    private final ModeloDAO dao = new ModeloDAO();
    private final AuditoriaDAO auditoria = new AuditoriaDAO();

    /** Filas para la grilla: id, nombre, marca (JOIN). */
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

    public String guardar(String nombre, Integer idMarca) {
        String error = Validaciones.primerError(
                Validaciones.requerido("Nombre", nombre),
                Validaciones.longitudMaxima("Nombre", nombre, 80));
        if (error != null) {
            return error;
        }
        if (idMarca == null) {
            return "Debe seleccionar la marca a la que pertenece el modelo.";
        }
        try {
            if (dao.existeNombre(nombre.trim(), idMarca, null)) {
                return "Ya existe un modelo \"" + nombre.trim() + "\" para esa marca.";
            }
            dao.insertar(new Modelo(null, nombre.trim(), idMarca));
            auditoria.registrar(Auditoria.ALTA, "modelos", "Modelo creado: " + nombre.trim());
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    public String modificar(Integer idModelo, String nombre, Integer idMarca) {
        if (idModelo == null) {
            return "Seleccione un modelo de la lista antes de modificar.";
        }
        String error = Validaciones.primerError(
                Validaciones.requerido("Nombre", nombre),
                Validaciones.longitudMaxima("Nombre", nombre, 80));
        if (error != null) {
            return error;
        }
        if (idMarca == null) {
            return "Debe seleccionar la marca a la que pertenece el modelo.";
        }
        try {
            if (dao.existeNombre(nombre.trim(), idMarca, idModelo)) {
                return "Ya existe otro modelo \"" + nombre.trim() + "\" para esa marca.";
            }
            dao.actualizar(new Modelo(idModelo, nombre.trim(), idMarca));
            auditoria.registrar(Auditoria.MODIFICACION, "modelos",
                    "Modelo #" + idModelo + " modificado: " + nombre.trim());
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    public String eliminar(Integer idModelo) {
        if (idModelo == null) {
            return "Seleccione un modelo de la lista antes de eliminar.";
        }
        try {
            dao.eliminar(idModelo);
            auditoria.registrar(Auditoria.BORRADO, "modelos", "Modelo #" + idModelo + " eliminado");
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }
}
