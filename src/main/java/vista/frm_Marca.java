package vista;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Formulario de marcas que extiende JInternalFrame (RNF013).
 */
public class frm_Marca extends JInternalFrame {

    private JTextField txtNombre;
    private JButton btnGuardar;
    private JButton btnCancelar;

    public frm_Marca() {
        setTitle("Gestión de Marcas");
        setSize(400, 300);
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);

        JPanel panel = new JPanel(new BorderLayout());

        // Panel superior con campos
        JPanel campoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblNombre = new JLabel("Nombre:");
        txtNombre = new JTextField(20);
        campoPanel.add(lblNombre);
        campoPanel.add(txtNombre);

        panel.add(campoPanel, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel botonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        botonPanel.add(btnGuardar);
        botonPanel.add(btnCancelar);

        panel.add(botonPanel, BorderLayout.SOUTH);

        add(panel);
    }

    // Getters y setters para acceso desde controladores
    public JTextField getTxtNombre() {
        return txtNombre;
    }

    public void setTxtNombre(JTextField txtNombre) {
        this.txtNombre = txtNombre;
    }

    public JButton getBtnGuardar() {
        return btnGuardar;
    }

    public void setBtnGuardar(JButton btnGuardar) {
        this.btnGuardar = btnGuardar;
    }

    public JButton getBtnCancelar() {
        return btnCancelar;
    }

    public void setBtnCancelar(JButton btnCancelar) {
        this.btnCancelar = btnCancelar;
    }

    // Método para limpiar el formulario
    public void limpiarFormulario() {
        txtNombre.setText("");
    }

    // Método para obtener el nombre del formulario
    public String obtenerNombre() {
        return txtNombre.getText();
    }

    // Listener para el botón de guardar
    public void addActionGuardar(ActionListener listener) {
        btnGuardar.addActionListener(listener);
    }

    // Listener para el botón de cancelar
    public void addActionCancelar(ActionListener listener) {
        btnCancelar.addActionListener(listener);
    }
}