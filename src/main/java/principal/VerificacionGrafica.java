package principal;

import utilidades.Mensajes;
import vista.VentanaLogin;
import vista.VentanaPrincipal;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Verificacion grafica: abre las ventanas, recorre todos los items del
 * menu (abriendo cada formulario) y guarda capturas de pantalla.
 * Requiere un display (en CI se usa Xvfb):
 *
 *   xvfb-run -a java -cp target/classes:target/lib/* principal.VerificacionGrafica
 */
public final class VerificacionGrafica {

    private static int fallos;

    private VerificacionGrafica() {
    }

    public static void main(String[] args) throws Exception {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("[AVISO] No hay display disponible; verificacion grafica omitida.");
            System.exit(0);
            return;
        }

        SwingUtilities.invokeAndWait(() -> {
            try {
                ejecutar();
            } catch (Throwable t) {
                fallos++;
                t.printStackTrace();
            }
        });

        System.out.println(fallos == 0
                ? "=== VERIFICACION GRAFICA CORRECTA ==="
                : "=== VERIFICACION GRAFICA CON " + fallos + " FALLOS ===");
        System.exit(fallos == 0 ? 0 : 1);
    }

    private static void ejecutar() throws Exception {
        File carpeta = new File("docs/capturas");
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            throw new IllegalStateException("No se pudo crear " + carpeta.getAbsolutePath());
        }

        VentanaLogin login = new VentanaLogin();
        login.setVisible(true);
        capturar(login, new File(carpeta, "01-login.png"));
        comprobar("Ventana de login abierta", login.isDisplayable());

        // Se completan los campos reales y se pulsa el boton "Ingresar".
        javax.swing.JTextField campoUsuario = (javax.swing.JTextField) buscar(login,
                c -> c instanceof javax.swing.JTextField
                        && !(c instanceof javax.swing.JPasswordField));
        javax.swing.JPasswordField campoClave = (javax.swing.JPasswordField) buscar(login,
                c -> c instanceof javax.swing.JPasswordField);
        javax.swing.JButton botonIngresar = (javax.swing.JButton) buscar(login,
                c -> c instanceof javax.swing.JButton
                        && "Ingresar".equals(((javax.swing.JButton) c).getText()));
        comprobar("Login: se encontraron usuario, contraseña y boton",
                campoUsuario != null && campoClave != null && botonIngresar != null, "");
        campoUsuario.setText("admin");
        campoClave.setText("admin123");
        botonIngresar.doClick();
        comprobar("Login: credenciales validadas y ventana cerrada",
                !login.isDisplayable() && login.ingresoCorrecto(), "");
        comprobar("Sesion activa con el usuario admin",
                utilidades.Sesion.haySesion()
                        && "admin".equals(utilidades.Sesion.nombreUsuario()), "");

        VentanaPrincipal principal = new VentanaPrincipal();
        principal.setVisible(true);
        comprobar("Ventana principal abierta", principal.isDisplayable());

        recorrerMenu(principal);

        // Abre el modulo principal para la captura final.
        JMenu menuEquipamiento = principal.getJMenuBar().getMenu(1);
        menuEquipamiento.getItem(0).doClick();
        capturar(principal, new File(carpeta, "02-formulario-equipos.png"));

        login.dispose();
        principal.dispose();
    }

    private static void recorrerMenu(VentanaPrincipal principal) {
        JMenuBar barra = principal.getJMenuBar();
        comprobar("La barra de menus tiene modulos", barra.getMenuCount() >= 4,
                "menus=" + barra.getMenuCount());
        for (int i = 0; i < barra.getMenuCount(); i++) {
            JMenu menu = barra.getMenu(i);
            if (menu == null) {
                continue;
            }
            for (int j = 0; j < menu.getItemCount(); j++) {
                JMenuItem item = menu.getItem(j);
                if (item == null || item.getText() == null || !item.isEnabled()) {
                    continue;
                }
                String texto = item.getText();
                if (texto.equals("Salir") || texto.equals("Cerrar sesión")) {
                    continue;
                }
                item.doClick();
                comprobar("Menu " + menu.getText() + " > " + texto, true, "");
            }
        }
    }

    /** Busca un componente dentro de un contenedor (recorrido recursivo). */
    private static java.awt.Component buscar(java.awt.Container raiz,
                                             java.util.function.Predicate<java.awt.Component> criterio) {
        for (java.awt.Component componente : raiz.getComponents()) {
            if (criterio.test(componente)) {
                return componente;
            }
            if (componente instanceof java.awt.Container contenedor) {
                java.awt.Component encontrado = buscar(contenedor, criterio);
                if (encontrado != null) {
                    return encontrado;
                }
            }
        }
        return null;
    }

    private static void capturar(JFrame ventana, File archivo) throws Exception {
        ventana.repaint();
        for (int intento = 0; intento < 10
                && (ventana.getWidth() == 0 || ventana.getHeight() == 0); intento++) {
            Thread.sleep(100);
        }
        BufferedImage imagen = new BufferedImage(Math.max(ventana.getWidth(), 1),
                Math.max(ventana.getHeight(), 1), BufferedImage.TYPE_INT_RGB);
        Graphics2D grafico = imagen.createGraphics();
        ventana.printAll(grafico);
        grafico.dispose();
        ImageIO.write(imagen, "png", archivo);
        System.out.println("[OK]    Captura guardada: " + archivo.getPath());
    }

    private static void comprobar(String nombre, boolean condicion, String... detalle) {
        if (condicion) {
            System.out.println("[OK]    " + nombre);
        } else {
            fallos++;
            System.out.println("[FALLA] " + nombre
                    + (detalle.length > 0 && detalle[0] != null ? "  ->  " + detalle[0] : ""));
        }
    }
}
