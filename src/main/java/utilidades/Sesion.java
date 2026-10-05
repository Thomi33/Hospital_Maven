package utilidades;

import modelo.Usuario;

/**
 * Mantiene el usuario logueado durante la sesion de la aplicacion.
 * (RF001 - Login / RF004 - Auditoria)
 */
public final class Sesion {

    private static Usuario usuarioActual;

    private Sesion() {
    }

    public static void iniciar(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static void cerrar() {
        usuarioActual = null;
    }

    public static Usuario usuarioActual() {
        return usuarioActual;
    }

    /** Nombre de usuario para la tabla de auditoria. */
    public static String nombreUsuario() {
        return usuarioActual == null ? "sistema" : usuarioActual.usuario();
    }

    /** Id del usuario para la clave foranea de auditoria. */
    public static Integer idUsuario() {
        return usuarioActual == null ? null : usuarioActual.idUsuario();
    }

    public static boolean haySesion() {
        return usuarioActual != null;
    }
}
