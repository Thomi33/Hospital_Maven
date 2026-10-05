package vista;

import controlador.IntervencionControlador;
import modelo.Equipo;
import modelo.TipoIntervencion;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.util.List;

/**
 * Formulario de intervenciones (RF005 - alta y consulta).
 * Los registros historicos no se modifican.
 */
public class FormularioIntervenciones extends FormularioCRUD {

    private final IntervencionControlador controlador = new IntervencionControlador();
    private final JComboBox<Equipo> comboEquipo = new JComboBox<>();
    private final JComboBox<TipoIntervencion> comboTipo = new JComboBox<>();
    private final JTextField txtFecha = new JTextField(14);
    private final JTextField txtTecnico = new JTextField(20);
    private final JTextField txtObservaciones = new JTextField(30);

    private List<Equipo> equipos = List.of();
    private List<TipoIntervencion> tipos = List.of();

    public FormularioIntervenciones() {
        super(new String[]{"Fecha", "Equipo", "Código", "Tipo", "Técnico responsable",
                "Observaciones"});
        agregarCampo("Equipo:", comboEquipo);
        agregarCampo("Tipo de intervención:", comboTipo);
        agregarCampo("Fecha (aaaa-mm-dd):", txtFecha);
        agregarCampo("Técnico responsable:", txtTecnico);
        agregarCampo("Observaciones:", txtObservaciones);
        cargarCombos();
        accionActualizar();
    }

    private void cargarCombos() {
        try {
            equipos = controlador.equipos();
            tipos = controlador.tiposDeIntervencion();
        } catch (RuntimeException e) {
            mostrarExcepcion(e);
        }
        comboEquipo.removeAllItems();
        for (Equipo equipo : equipos) {
            comboEquipo.addItem(equipo);
        }
        comboTipo.removeAllItems();
        for (TipoIntervencion tipo : tipos) {
            comboTipo.addItem(tipo);
        }
    }

    private Integer idDelEquipoSeleccionado() {
        Equipo equipo = (Equipo) comboEquipo.getSelectedItem();
        return equipo == null ? null : equipo.idEquipo();
    }

    private Integer idDelTipoSeleccionado() {
        TipoIntervencion tipo = (TipoIntervencion) comboTipo.getSelectedItem();
        return tipo == null ? null : tipo.idTipoIntervencion();
    }

    private void seleccionarEquipoPorCodigo(String codigo) {
        for (Equipo equipo : equipos) {
            if (codigo.equalsIgnoreCase(equipo.codigoInterno())) {
                comboEquipo.setSelectedItem(equipo);
                return;
            }
        }
        if (!equipos.isEmpty()) {
            comboEquipo.setSelectedIndex(0);
        }
    }

    private void seleccionarTipoPorNombre(String nombre) {
        for (TipoIntervencion tipo : tipos) {
            if (nombre.equalsIgnoreCase(tipo.nombre())) {
                comboTipo.setSelectedItem(tipo);
                return;
            }
        }
        if (!tipos.isEmpty()) {
            comboTipo.setSelectedIndex(0);
        }
    }

    @Override
    protected void accionNuevo() {
        limpiarFormulario();
        comboEquipo.requestFocus();
        estado("Nueva intervención: seleccione el equipo y complete los datos.");
    }

    @Override
    protected void accionGuardar() {
        String mensaje = controlador.guardar(idDelEquipoSeleccionado(), idDelTipoSeleccionado(),
                txtFecha.getText(), txtTecnico.getText(), txtObservaciones.getText());
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("La intervención se registró correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionModificar() {
        advertencia("RF005: los registros históricos de intervenciones no se modifican.\n"
                + "Si corresponde, registre una nueva intervención.");
    }

    @Override
    protected void accionEliminar() {
        advertencia("RF005: los registros históricos de intervenciones no se eliminan.");
    }

    @Override
    protected void accionBuscar() {
        cargarFilas(controlador.buscar(textoDeBusqueda()));
    }

    @Override
    protected void accionActualizar() {
        cargarFilas(controlador.listar());
    }

    @Override
    protected void alSeleccionarFila(int fila) {
        seleccionarEquipoPorCodigo(textoCelda(fila, 3));
        seleccionarTipoPorNombre(textoCelda(fila, 4));
        txtFecha.setText(textoCelda(fila, 1));
        txtTecnico.setText(textoCelda(fila, 5));
        txtObservaciones.setText(textoCelda(fila, 6));
        estado("Intervención seleccionada (solo consulta).");
    }

    @Override
    public void limpiarFormulario() {
        if (!equipos.isEmpty()) {
            comboEquipo.setSelectedIndex(0);
        }
        if (!tipos.isEmpty()) {
            comboTipo.setSelectedIndex(0);
        }
        txtFecha.setText(java.time.LocalDate.now().toString());
        txtTecnico.setText("");
        txtObservaciones.setText("");
    }
}
