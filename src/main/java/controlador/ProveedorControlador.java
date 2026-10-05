package controlador;

import dao.AuditoriaDAO;
import dao.DAOBase;
import dao.ProveedorDAO;
import modelo.Auditoria;
import modelo.Proveedor;
import utilidades.Validaciones;

import java.sql.SQLException;
import java.util.List;

/** Controlador del CRUD de proveedores (RF011). */
public class ProveedorControlador {

    private final ProveedorDAO dao = new ProveedorDAO();
    private final AuditoriaDAO auditoria = new AuditoriaDAO();

    public List<Proveedor> listar() {
        try {
            return dao.listar();
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    public List<Proveedor> buscar(String texto) {
        try {
            return dao.buscar(texto);
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    private String validar(String nombre, String direccion, String telefono, String email) {
        return Validaciones.primerError(
                Validaciones.requerido("Nombre", nombre),
                Validaciones.longitudMaxima("Nombre", nombre, 120),
                Validaciones.requerido("Dirección", direccion),
                Validaciones.longitudMaxima("Dirección", direccion, 150),
                Validaciones.telefono("Teléfono", telefono),
                Validaciones.email("Email", email),
                Validaciones.longitudMaxima("Email", email, 100));
    }

    public String guardar(String nombre, String direccion, String telefono, String email) {
        String error = validar(nombre, direccion, telefono, email);
        if (error != null) {
            return error;
        }
        try {
            if (dao.existeNombre(nombre.trim(), null)) {
                return "Ya existe un proveedor con el nombre \"" + nombre.trim() + "\".";
            }
            if (dao.existeEmail(email.trim(), null)) {
                return "Ya existe un proveedor con ese email.";
            }
            dao.insertar(new Proveedor(null, nombre.trim(), direccion.trim(),
                    telefono.trim(), email.trim()));
            auditoria.registrar(Auditoria.ALTA, "proveedores",
                    "Proveedor creado: " + nombre.trim());
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    public String modificar(Integer idProveedor, String nombre, String direccion,
                            String telefono, String email) {
        if (idProveedor == null) {
            return "Seleccione un proveedor de la lista antes de modificar.";
        }
        String error = validar(nombre, direccion, telefono, email);
        if (error != null) {
            return error;
        }
        try {
            if (dao.existeNombre(nombre.trim(), idProveedor)) {
                return "Ya existe otro proveedor con el nombre \"" + nombre.trim() + "\".";
            }
            if (dao.existeEmail(email.trim(), idProveedor)) {
                return "Ya existe otro proveedor con ese email.";
            }
            dao.actualizar(new Proveedor(idProveedor, nombre.trim(), direccion.trim(),
                    telefono.trim(), email.trim()));
            auditoria.registrar(Auditoria.MODIFICACION, "proveedores",
                    "Proveedor #" + idProveedor + " modificado: " + nombre.trim());
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    public String eliminar(Integer idProveedor) {
        if (idProveedor == null) {
            return "Seleccione un proveedor de la lista antes de eliminar.";
        }
        try {
            dao.eliminar(idProveedor);
            auditoria.registrar(Auditoria.BORRADO, "proveedores",
                    "Proveedor #" + idProveedor + " eliminado");
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }
}
