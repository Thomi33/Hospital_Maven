package vista;

import controlador.LoginControlador;
import utilidades.Mensajes;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Ventana de inicio de sesion (seccion 9.1 / RF001).
 * Valida usuario y contraseña contra la tabla usuarios.
 */
public class VentanaLogin extends JFrame {

    private final LoginControlador controlador = new LoginControlador();
    private final JTextField txtUsuario = new JTextField(20);
    private final JPasswordField txtContrasena = new JPasswordField(20);
    private boolean ingresoCorrecto = false;

    public VentanaLogin() {
        super("Hospital - Inicio de sesión");
        construir();
    }

    private void construir() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel titulo = new JLabel("Sistema de Gestión de Equipamiento Clínico Hospitalario",
                SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));
        JLabel subtitulo = new JLabel("Ingrese su usuario y contraseña", SwingConstants.CENTER);
        subtitulo.setForeground(Color.DARK_GRAY);

        JPanel cabecera = new JPanel(new BorderLayout(5, 5));
        cabecera.add(titulo, BorderLayout.NORTH);
        cabecera.add(subtitulo, BorderLayout.SOUTH);
        panel.add(cabecera, BorderLayout.NORTH);

        JPanel campos = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0;
        c.gridy = 0;
        campos.add(new JLabel("Usuario:"), c);
        c.gridx = 1;
        campos.add(txtUsuario, c);

        c.gridx = 0;
        c.gridy = 1;
        campos.add(new JLabel("Contraseña:"), c);
        c.gridx = 1;
        campos.add(txtContrasena, c);
        panel.add(campos, BorderLayout.CENTER);

        JButton btnIngresar = new JButton("Ingresar");
        JButton btnSalir = new JButton("Salir");
        JPanel botones = new JPanel();
        botones.add(btnIngresar);
        botones.add(btnSalir);
        panel.add(botones, BorderLayout.SOUTH);

        btnIngresar.addActionListener(e -> ingresar());
        btnSalir.addActionListener(e -> salirDelSistema());
        getRootPane().setDefaultButton(btnIngresar);
        txtContrasena.addActionListener(e -> ingresar());

        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
    }

    private void ingresar() {
        String contrasena = new String(txtContrasena.getPassword());
        String error = controlador.ingresar(txtUsuario.getText(), contrasena);
        if (error != null) {
            Mensajes.error(this, error);
            txtContrasena.setText("");
            txtContrasena.requestFocus();
            return;
        }
        ingresoCorrecto = true;
        dispose();
    }

    private void salirDelSistema() {
        controlador.salir();
        ingresoCorrecto = false;
        dispose();
    }

    public boolean ingresoCorrecto() {
        return ingresoCorrecto;
    }
}
