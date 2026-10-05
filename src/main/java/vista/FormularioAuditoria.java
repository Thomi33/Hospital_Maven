package vista;

import controlador.AuditoriaControlador;

import javax.swing.JButton;
import javax.swing.JComboBox;
import java.util.List;

/**
 * Consulta de la tabla de auditoria (RF004).
 * La aplicacion solo consulta: el registro lo realizan los demas modulos.
 */
public class FormularioAuditoria extends FormularioCRUD {

    private final AuditoriaControlador controlador = new AuditoriaControlador();
    private final JComboBox<String> comboOperacion = new JComboBox<>(new String[]{
            "TODAS", "LOGIN", "LOGOUT", "ALTA", "MODIFICACION", "BAJA", "BORRADO", "CONSULTA"});
    private final JButton btnPorOperacion = new JButton("Buscar por operación");

    public FormularioAuditoria() {
        super(new String[]{"Fecha", "Hora", "Usuario", "Operación", "Tabla", "Detalle"});
        agregarCampo("Operación:", comboOperacion);
        agregarCampo("", btnPorOperacion);

        // RF004: la auditoria no admite altas ni modificaciones desde la interfaz.
        btnNuevo.setEnabled(false);
        btnGuardar.setEnabled(false);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnNuevo.setToolTipText("RF004: la auditoría solo se consulta desde la interfaz.");
        btnGuardar.setToolTipText("RF004: la auditoría solo se consulta desde la interfaz.");
        btnModificar.setToolTipText("RF004: la auditoría solo se consulta desde la interfaz.");
        btnEliminar.setToolTipText("RF004: la auditoría solo se consulta desde la interfaz.");

        btnPorOperacion.addActionListener(e -> buscarPorOperacion());
        accionActualizar();
    }

    private void buscarPorOperacion() {
        try {
            String operacion = String.valueOf(comboOperacion.getSelectedItem());
            cargarFilas(controlador.buscar(textoDeBusqueda(), operacion));
            estado("Operación " + operacion + " - registros: " + modeloTabla.getRowCount()
                    + " de " + controlador.totalDeRegistros() + " en total.");
        } catch (RuntimeException e) {
            mostrarExcepcion(e);
        }
    }

    @Override
    protected void accionNuevo() {
        advertencia("RF004: la auditoría no permite altas desde la interfaz.\n"
                + "Cada operación del sistema deja su registro automáticamente.");
    }

    @Override
    protected void accionGuardar() {
        accionNuevo();
    }

    @Override
    protected void accionModificar() {
        advertencia("RF004: los registros de auditoría no se modifican.");
    }

    @Override
    protected void accionEliminar() {
        advertencia("RF004: los registros de auditoría no se eliminan.");
    }

    @Override
    protected void accionBuscar() {
        cargarFilas(controlador.buscar(textoDeBusqueda(), "TODAS"));
    }

    @Override
    protected void accionActualizar() {
        List<Object[]> filas = controlador.listar();
        cargarFilas(filas);
        estado("Registros: " + modeloTabla.getRowCount() + " de "
                + controlador.totalDeRegistros() + " en la tabla auditoría.");
    }

    @Override
    protected void alSeleccionarFila(int fila) {
        estado("Registro #" + textoCelda(fila, 0) + " | " + textoCelda(fila, 1) + " "
                + textoCelda(fila, 2) + " | " + textoCelda(fila, 4) + " | "
                + textoCelda(fila, 5));
    }

    @Override
    public void limpiarFormulario() {
        txtBuscar.setText("");
        comboOperacion.setSelectedIndex(0);
    }
}
