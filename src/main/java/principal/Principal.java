package principal;

import vista.VentanaLogin;
import vista.VentanaPrincipal;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import conexion.ConexionBD;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Punto de entrada de la aplicacion (Java Swing + JDBC + MySQL).
 * La ventana de login se muestra primero y, al cerrarse, se abre la
 * ventana principal si el inicio de sesion fue correcto.
 */
public final class Principal {

    private Principal() {
    }

    public static void main(String[] args) {
        configurarAspecto();
        SwingUtilities.invokeLater(() -> {
            String errorDeConexion = ConexionBD.probarConexion();
            if (errorDeConexion != null) {
                JOptionPane.showMessageDialog(null,
                        "No se pudo establecer la conexión con MySQL.\n\n" + errorDeConexion
                                + "\n\nLa aplicación se abrirá de todos modos: "
                                + "verifique la conexión antes de iniciar sesión.",
                        "Advertencia de conexión", JOptionPane.WARNING_MESSAGE);
            }

            VentanaLogin login = new VentanaLogin();
            login.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent evento) {
                    if (login.ingresoCorrecto()) {
                        new VentanaPrincipal().setVisible(true);
                    } else {
                        System.exit(0);
                    }
                }
            });
            login.setVisible(true);
        });
    }

    private static void configurarAspecto() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Se mantiene el aspecto por defecto de Java Swing.
        }
    }
}
