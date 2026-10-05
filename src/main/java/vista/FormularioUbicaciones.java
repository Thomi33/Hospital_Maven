package vista;

import controlador.InstitucionControlador;
import controlador.UbicacionControlador;
import modelo.Institucion;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.util.List;

/**
 * Formulario de ubicaciones (RF007 - CRUD completo).
 */
public class FormularioUbicaciones extends FormularioCRUD {

    private final UbicacionControlador controlador = new UbicacionControlador();
    private final InstitucionControlador institucionControlador = new InstitucionControlador();
    private final JTextField txtNombre = new JTextField(22);
    private final JTextField txtDescripcion = new JTextField(22);
    private final JComboBox<Institucion> comboInstitucion = new JComboBox<>();

    private List<Institucion> instituciones = List.of();

    public FormularioUbicaciones() {
        super(new String[]{"Nombre", "Descripción", "Institución"});
        agregarColumnaOculta("ID Institución");
        agregarCampo("Nombre:", txtNombre);
        agregarCampo("Descripción:", txtDescripcion);
        agregarCampo("Institución:", comboInstitucion);
        cargarInstituciones();
        accionActualizar();
    }

    private void cargarInstituciones() {
        try {
            instituciones = institucionControlador.listar();
        } catch (RuntimeException e) {
            mostrarExcepcion(e);
        }
        comboInstitucion.removeAllItems();
        for (Institucion institucion : instituciones) {
            comboInstitucion.addItem(institucion);
        }
    }

    private Integer idDeLaInstitucionSeleccionada() {
        Institucion institucion = (Institucion) comboInstitucion.getSelectedItem();
        return institucion == null ? null : institucion.idInstitucion();
    }

    private void seleccionarInstitucion(String nombre) {
        for (Institucion institucion : instituciones) {
            if (institucion.nombre().equalsIgnoreCase(nombre)) {
                comboInstitucion.setSelectedItem(institucion);
                return;
            }
        }
        if (!instituciones.isEmpty()) {
            comboInstitucion.setSelectedIndex(0);
        }
    }

    @Override
    protected void accionNuevo() {
        limpiarFormulario();
        txtNombre.requestFocus();
        estado("Nuevo registro: complete los datos y pulse Guardar.");
    }

    @Override
    protected void accionGuardar() {
        String mensaje = controlador.guardar(txtNombre.getText(), txtDescripcion.getText(),
                idDeLaInstitucionSeleccionada());
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("La ubicación se guardó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionModificar() {
        Integer id = idSeleccionado();
        if (id == null) {
            advertencia("Seleccione una ubicación de la lista antes de modificar.");
            return;
        }
        String mensaje = controlador.modificar(id, txtNombre.getText(), txtDescripcion.getText(),
                idDeLaInstitucionSeleccionada());
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("La ubicación se modificó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionEliminar() {
        Integer id = idSeleccionado();
        if (id == null) {
            advertencia("Seleccione una ubicación de la lista antes de eliminar.");
            return;
        }
        if (!confirmarEliminacion("¿Confirma que desea eliminar la ubicación seleccionada?")) {
            return;
        }
        String mensaje = controlador.eliminar(id);
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("La ubicación se eliminó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionBuscar() {
        cargarFilas(controlador.buscar(textoDeBusqueda()));
    }

    @Override
    protected void accionActualizar() {
        cargarFilas(controlador.listarFilas());
    }

    @Override
    protected void alSeleccionarFila(int fila) {
        txtNombre.setText(textoCelda(fila, 1));
        txtDescripcion.setText(textoCelda(fila, 2));
        seleccionarInstitucion(textoCelda(fila, 3));
    }

    @Override
    public void limpiarFormulario() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        if (!instituciones.isEmpty()) {
            comboInstitucion.setSelectedIndex(0);
        }
    }
}
