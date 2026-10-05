package vista;

import controlador.LoginControlador;
import utilidades.Mensajes;
import utilidades.Sesion;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;
import java.util.function.Supplier;

/**
 * Ventana principal del sistema: menu con todos los modulos y contenedor
 * de los formularios.
 */
public class VentanaPrincipal extends JFrame {

    private final LoginControlador loginControlador = new LoginControlador();
    private final JPanel contenedor = new JPanel(new CardLayout());
    private static final String TARJETA_INICIO = "INICIO";

    public VentanaPrincipal() {
        super("Sistema de Gestion de Equipamiento Clinico Hospitalario");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 780);
        setMinimumSize(new java.awt.Dimension(980, 640));
        setLocationRelativeTo(null);

        setJMenuBar(construirMenu());
        contenedor.add(panelInicio(), TARJETA_INICIO);
        setContentPane(contenedor);
    }

    private JPanel panelInicio() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel texto = new JLabel("<html><div style='text-align:center'>"
                + "<h2>Bienvenido/a al Sistema de Gestión de Equipamiento Clínico</h2>"
                + "<p>Usuario: " + Sesion.nombreUsuario() + "</p>"
                + "<p>Utilice el menú superior para ingresar a cada módulo.</p>"
                + "<p>Base de datos: hospital | Java Swing + JDBC + MySQL</p>"
                + "</div></html>", SwingConstants.CENTER);
        texto.setFont(texto.getFont().deriveFont(Font.PLAIN, 15f));
        panel.add(texto, BorderLayout.CENTER);
        return panel;
    }

    private JMenuBar construirMenu() {
        JMenuBar barra = new JMenuBar();

        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemInicio = new JMenuItem("Inicio");
        JMenuItem itemCerrarSesion = new JMenuItem("Cerrar sesión");
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemInicio.addActionListener(e -> mostrarInicio());
        itemCerrarSesion.addActionListener(e -> cerrarSesion());
        itemSalir.addActionListener(e -> salir());
        menuArchivo.add(itemInicio);
        menuArchivo.addSeparator();
        menuArchivo.add(itemCerrarSesion);
        menuArchivo.add(itemSalir);

        JMenu menuEquipamiento = new JMenu("Equipamiento");
        agregarItem(menuEquipamiento, "Equipos", FormularioEquipos::new);
        agregarItem(menuEquipamiento, "Tipos de equipo", FormularioTipoEquipo::new);
        agregarItem(menuEquipamiento, "Intervenciones", FormularioIntervenciones::new);
        agregarItem(menuEquipamiento, "Tipos de intervención", FormularioTipoIntervencion::new);

        JMenu menuCatalogos = new JMenu("Catálogos");
        agregarItem(menuCatalogos, "Marcas", FormularioMarcas::new);
        agregarItem(menuCatalogos, "Modelos", FormularioModelos::new);
        agregarItem(menuCatalogos, "Proveedores", FormularioProveedores::new);
        agregarItem(menuCatalogos, "Países", FormularioPaises::new);
        agregarItem(menuCatalogos, "Ubicaciones", FormularioUbicaciones::new);
        agregarItem(menuCatalogos, "Instituciones", FormularioInstituciones::new);

        JMenu menuSistema = new JMenu("Sistema");
        agregarItem(menuSistema, "Usuarios", FormularioUsuarios::new);
        agregarItem(menuSistema, "Perfiles", FormularioPerfiles::new);
        agregarItem(menuSistema, "Funcionalidades", FormularioFuncionalidades::new);
        agregarItem(menuSistema, "Auditoría", FormularioAuditoria::new);

        JMenu menuReportes = new JMenu("Reportes");
        agregarItem(menuReportes, "Consultas e informes", FormularioReportes::new);

        barra.add(menuArchivo);
        barra.add(menuEquipamiento);
        barra.add(menuCatalogos);
        barra.add(menuSistema);
        barra.add(menuReportes);
        return barra;
    }

    private void agregarItem(JMenu menu, String titulo, Supplier<FormularioCRUD> fabrica) {
        JMenuItem item = new JMenuItem(titulo);
        item.addActionListener(e -> mostrar(titulo, fabrica));
        menu.add(item);
    }

    /** Muestra el formulario indicado en el contenedor central. */
    private void mostrar(String titulo, Supplier<FormularioCRUD> fabrica) {
        try {
            FormularioCRUD formulario = fabrica.get();
            formulario.setAccionDeSalir(this::mostrarInicio);
            contenedor.removeAll();
            contenedor.add(formulario, titulo);
            ((CardLayout) contenedor.getLayout()).show(contenedor, titulo);
            setTitle("Sistema de Gestion de Equipamiento Clinico Hospitalario - " + titulo);
            contenedor.revalidate();
            contenedor.repaint();
        } catch (RuntimeException e) {
            Mensajes.excepcion(this, "No se pudo abrir el módulo " + titulo + ".", e);
        }
    }

    private void mostrarInicio() {
        contenedor.removeAll();
        contenedor.add(panelInicio(), TARJETA_INICIO);
        ((CardLayout) contenedor.getLayout()).show(contenedor, TARJETA_INICIO);
        setTitle("Sistema de Gestion de Equipamiento Clinico Hospitalario");
        contenedor.revalidate();
        contenedor.repaint();
    }

    private void cerrarSesion() {
        if (!Mensajes.confirmar(this, "¿Desea cerrar la sesión actual?")) {
            return;
        }
        loginControlador.salir();
        dispose();
        new VentanaLogin().setVisible(true);
    }

    private void salir() {
        if (!Mensajes.confirmar(this, "¿Desea salir del sistema?")) {
            return;
        }
        loginControlador.salir();
        dispose();
        System.exit(0);
    }
}
