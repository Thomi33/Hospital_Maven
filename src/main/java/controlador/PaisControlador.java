package controlador;

import dao.AuditoriaDAO;
import dao.DAOBase;
import dao.PaisDAO;
import modelo.Auditoria;
import modelo.Pais;
import utilidades.Validaciones;

import java.sql.SQLException;
import java.util.List;

/** Controlador del CRUD de paises (RF009). */
public class PaisControlador {

    private final PaisDAO dao = new PaisDAO();
    private final AuditoriaDAO auditoria = new AuditoriaDAO();

    public List<Pais> listar() {
        try {
            return dao.listar();
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    public List<Pais> buscar(String texto) {
        try {
            return dao.buscar(texto);
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

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
                return "Ya existe un país con el nombre \"" + valor + "\".";
            }
            dao.insertar(new Pais(null, valor));
            auditoria.registrar(Auditoria.ALTA, "paises", "País creado: " + valor);
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    public String modificar(Integer idPais, String nombre) {
        if (idPais == null) {
            return "Seleccione un país de la lista antes de modificar.";
        }
        String error = Validaciones.primerError(
                Validaciones.requerido("Nombre", nombre),
                Validaciones.longitudMaxima("Nombre", nombre, 80));
        if (error != null) {
            return error;
        }
        String valor = Validaciones.normalizar(nombre);
        try {
            if (dao.existeNombre(valor, idPais)) {
                return "Ya existe otro país con el nombre \"" + valor + "\".";
            }
            dao.actualizar(new Pais(idPais, valor));
            auditoria.registrar(Auditoria.MODIFICACION, "paises",
                    "País #" + idPais + " modificado: " + valor);
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    public String eliminar(Integer idPais) {
        if (idPais == null) {
            return "Seleccione un país de la lista antes de eliminar.";
        }
        try {
            dao.eliminar(idPais);
            auditoria.registrar(Auditoria.BORRADO, "paises", "País #" + idPais + " eliminado");
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }
}
