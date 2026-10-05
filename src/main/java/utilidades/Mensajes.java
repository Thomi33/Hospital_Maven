package utilidades;

import javax.swing.JOptionPane;
import java.awt.Component;

/**
 * Mensajes standard para el usuario (RNF005 / RNF006):
 * toda comunicacion con el usuario pasa por aqui para que los textos
 * sean claros y comprensibles.
 */
public final class Mensajes {

    private Mensajes() {
    }

    public static void info(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void advertencia(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }

    public static void error(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Confirma una accion destructiva. Devuelve true si el usuario acepta. */
    public static boolean confirmar(Component padre, String mensaje) {
        int respuesta = JOptionPane.showConfirmDialog(padre, mensaje, "Confirmación",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return respuesta == JOptionPane.YES_OPTION;
    }

    /**
     * Muestra una excepcion en terminos comprensibles para el usuario
     * (RNF005: mensajes claros / RNF006: nunca cerrar la aplicacion).
     */
    public static void excepcion(Component padre, Exception e) {
        excepcion(padre, "Se produjo un error inesperado.", e);
    }

    public static void excepcion(Component padre, String mensajeUsuario, Exception e) {
        String detalle = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
        String texto = mensajeUsuario + "\n\nDetalle técnico: " + detalle
                + "\n\nVerifique los datos ingresados y la conexión con la Base de Datos.";
        JOptionPane.showMessageDialog(padre, texto, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
