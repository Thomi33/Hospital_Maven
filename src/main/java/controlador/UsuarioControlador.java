package controlador;

import dao.AuditoriaDAO;
import dao.DAOBase;
import dao.UsuarioDAO;
import modelo.Auditoria;
import modelo.Usuario;
import utilidades.HashUtil;
import utilidades.Sesion;
import utilidades.Validaciones;

import java.sql.SQLException;
import java.util.List;

/** Controlador del CRUD de usuarios (RF001). */
public class UsuarioControlador {

    private final UsuarioDAO dao = new UsuarioDAO();
    private final AuditoriaDAO auditoria = new AuditoriaDAO();

    /** Filas para la grilla (10 columnas; la primera es el id). */
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

    /** Valida todos los datos del usuario excepto la contraseña. */
    private String validar(String nombre, String apellido, String cedula, String fechaNacimiento,
                           String telefono, String email, String usuario, Integer idPerfil) {
        return Validaciones.primerError(
                Validaciones.requerido("Nombre", nombre),
                Validaciones.longitudMaxima("Nombre", nombre, 60),
                Validaciones.requerido("Apellido", apellido),
                Validaciones.longitudMaxima("Apellido", apellido, 60),
                Validaciones.cedula("Cédula", cedula),
                Validaciones.fecha("Fecha de nacimiento", fechaNacimiento),
                Validaciones.telefono("Teléfono", telefono),
                Validaciones.email("Email", email),
                Validaciones.requerido("Usuario", usuario),
                Validaciones.longitudMaxima("Usuario", usuario, 40),
                idPerfil == null ? "Debe seleccionar el perfil del usuario." : null);
    }

    private String validarDuplicados(String usuario, String cedula, String email,
                                     Integer idExcluido) throws SQLException {
        if (dao.existeUsuario(usuario, idExcluido)) {
            return "Ya existe un usuario con el nombre de usuario \"" + usuario + "\".";
        }
        if (dao.existeCedula(cedula, idExcluido)) {
            return "Ya existe un usuario con la cédula " + cedula + ".";
        }
        if (dao.existeEmail(email, idExcluido)) {
            return "Ya existe un usuario con el email " + email + ".";
        }
        return null;
    }

    /** Alta de usuario. Devuelve null si la operacion salio bien. */
    public String guardar(String nombre, String apellido, String cedula, String fechaNacimiento,
                          String telefono, String email, String usuario, String contrasena,
                          Integer idPerfil) {
        String error = Validaciones.primerError(
                validar(nombre, apellido, cedula, fechaNacimiento, telefono, email, usuario, idPerfil),
                Validaciones.requerido("Contraseña", contrasena));
        if (error != null) {
            return error;
        }
        if (contrasena.trim().length() < 6) {
            return "La contraseña debe tener al menos 6 caracteres.";
        }
        try {
            String duplicado = validarDuplicados(usuario.trim(), cedula.trim(), email.trim(), null);
            if (duplicado != null) {
                return duplicado;
            }
            Usuario nuevo = new Usuario(null, nombre.trim(), apellido.trim(), cedula.trim(),
                    Validaciones.aFecha(fechaNacimiento), telefono.trim(), email.trim(),
                    usuario.trim(), HashUtil.sha256(contrasena.trim()), idPerfil, Usuario.ACTIVO);
            dao.insertar(nuevo);
            auditoria.registrar(Auditoria.ALTA, "usuarios", "Usuario creado: " + usuario.trim());
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    /** Modificacion. Si la contraseña viene vacia se conserva la actual. */
    public String modificar(Integer idUsuario, String nombre, String apellido, String cedula,
                            String fechaNacimiento, String telefono, String email, String usuario,
                            String contrasena, Integer idPerfil, String estado) {
        if (idUsuario == null) {
            return "Seleccione un usuario de la lista antes de modificar.";
        }
        String error = validar(nombre, apellido, cedula, fechaNacimiento, telefono, email,
                usuario, idPerfil);
        if (error != null) {
            return error;
        }
        if (!Validaciones.vacio(contrasena) && contrasena.trim().length() < 6) {
            return "La contraseña debe tener al menos 6 caracteres.";
        }
        try {
            Usuario actual = dao.buscarPorId(idUsuario);
            if (actual == null) {
                return "El usuario seleccionado ya no existe en la Base de Datos.";
            }
            String duplicado = validarDuplicados(usuario.trim(), cedula.trim(), email.trim(),
                    idUsuario);
            if (duplicado != null) {
                return duplicado;
            }
            String hash = Validaciones.vacio(contrasena)
                    ? actual.contrasena()
                    : HashUtil.sha256(contrasena.trim());
            Usuario editado = new Usuario(idUsuario, nombre.trim(), apellido.trim(),
                    cedula.trim(), Validaciones.aFecha(fechaNacimiento), telefono.trim(),
                    email.trim(), usuario.trim(), hash, idPerfil, estado);
            dao.actualizar(editado);
            auditoria.registrar(Auditoria.MODIFICACION, "usuarios",
                    "Usuario #" + idUsuario + " modificado: " + usuario.trim());
            if (Sesion.idUsuario() != null && Sesion.idUsuario() == idUsuario) {
                Sesion.iniciar(editado);
            }
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    /** Baja logica (RF001). */
    public String eliminar(Integer idUsuario) {
        if (idUsuario == null) {
            return "Seleccione un usuario de la lista antes de dar de baja.";
        }
        if (Sesion.idUsuario() != null && Sesion.idUsuario() == idUsuario) {
            return "No puede darse de baja a sí mismo mientras tiene la sesión abierta.";
        }
        try {
            dao.cambiarEstado(idUsuario, Usuario.INACTIVO);
            auditoria.registrar(Auditoria.BAJA, "usuarios",
                    "Usuario #" + idUsuario + " dado de baja");
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }
}
