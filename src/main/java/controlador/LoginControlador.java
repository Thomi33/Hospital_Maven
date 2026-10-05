package controlador;

import conexion.ConexionBD;
import dao.AuditoriaDAO;
import dao.UsuarioDAO;
import modelo.Auditoria;
import modelo.Usuario;
import utilidades.Sesion;
import utilidades.Validaciones;

import java.sql.SQLException;

/**
 * Controlador del login (RF001 / seccion 9.1).
 * Valida usuario y contrasena contra la tabla usuarios y deja el
 * registro de auditoria del inicio de sesion.
 */
public class LoginControlador {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    /** Devuelve null si el inicio de sesion fue correcto. */
    public String ingresar(String usuario, String contrasena) {
        String error = Validaciones.primerError(
                Validaciones.requerido("Usuario", usuario),
                Validaciones.requerido("Contraseña", contrasena));
        if (error != null) {
            return error;
        }
        String errorConexion = ConexionBD.probarConexion();
        if (errorConexion != null) {
            return errorConexion;
        }
        try {
            Usuario encontrado = usuarioDAO.validarLogin(usuario.trim(), contrasena);
            if (encontrado == null) {
                return "Usuario o contraseña incorrectos, o el usuario está dado de baja.";
            }
            Sesion.iniciar(encontrado);
            auditoriaDAO.registrar(Auditoria.LOGIN, "usuarios",
                    "Inicio de sesión correcto de " + encontrado.usuario()
                            + " (" + encontrado.nombreCompleto() + ")");
            return null;
        } catch (SQLException e) {
            return dao.DAOBase.mensajeError(e);
        }
    }

    /** Cierra la sesion y registra la salida. */
    public void salir() {
        try {
            if (Sesion.haySesion()) {
                auditoriaDAO.registrar("LOGOUT", "usuarios",
                        "Cierre de sesión de " + Sesion.nombreUsuario());
            }
        } catch (SQLException e) {
            // La auditoria nunca debe impedir salir del sistema.
        }
        Sesion.cerrar();
    }
}
