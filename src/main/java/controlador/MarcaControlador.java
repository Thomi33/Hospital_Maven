package controlador;

import dao.AuditoriaDAO;
import dao.DAOBase;
import dao.MarcaDAO;
import modelo.Auditoria;
import modelo.Marca;
import utilidades.Validaciones;

import java.sql.SQLException;
import java.util.List;

/** Controlador del CRUD de marcas (RF013). */
public class MarcaControlador {

    private final MarcaDAO dao = new MarcaDAO();
    private final AuditoriaDAO auditoria = new AuditoriaDAO();

    public List<Marca> listar() {
        try {
            return dao.listar();
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    public List<Marca> buscar(String texto) {
        try {
            return dao.buscar(texto);
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    /** Alta. Devuelve null si todo salio bien. */
    public String guardar(String nombre) {
        String error = Validaciones.primerError(
                Validaciones.requerido("Nombre", nombre),
                Validaciones.longitudMaxima("Nombre", nombre, 80));
        if (error != null) {
            return error;
        }
        String valor = Validaciones.normalizar(nombre);
        try {
            if (dao.existeNombre(valor, null)) {
                return "Ya existe una marca con el nombre \"" + valor + "\".";
            }
            dao.insertar(new Marca(null, valor));
            auditoria.registrar(Auditoria.ALTA, "marcas", "Marca creada: " + valor);
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    /** Modificacion. */
    public String modificar(Integer idMarca, String nombre) {
        if (idMarca == null) {
            return "Seleccione una marca de la lista antes de modificar.";
        }
        String error = Validaciones.primerError(
                Validaciones.requerido("Nombre", nombre),
                Validaciones.longitudMaxima("Nombre", nombre, 80));
        if (error != null) {
            return error;
        }
        String valor = Validaciones.normalizar(nombre);
        try {
            if (dao.existeNombre(valor, idMarca)) {
                return "Ya existe otra marca con el nombre \"" + valor + "\".";
            }
            dao.actualizar(new Marca(idMarca, valor));
            auditoria.registrar(Auditoria.MODIFICACION, "marcas",
                    "Marca #" + idMarca + " modificada: " + valor);
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    /** Eliminacion fisica (con confirmacion previa en la interfaz). */
    public String eliminar(Integer idMarca) {
        if (idMarca == null) {
            return "Seleccione una marca de la lista antes de eliminar.";
        }
        try {
            dao.eliminar(idMarca);
            auditoria.registrar(Auditoria.BORRADO, "marcas", "Marca #" + idMarca + " eliminada");
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }
}
