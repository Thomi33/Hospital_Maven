package vista;

import controlador.ModeloControlador;
import controlador.MarcaControlador;
import modelo.Marca;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.util.List;

/**
 * Formulario de modelos (RF012 - CRUD completo).
 * Cada modelo se asocia obligatoriamente a una marca.
 */
public class FormularioModelos extends FormularioCRUD {

    private final ModeloControlador controlador = new ModeloControlador();
    private final MarcaControlador marcaControlador = new MarcaControlador();
    private final JTextField txtNombre = new JTextField(22);
    private final JComboBox<Marca> comboMarca = new JComboBox<>();

    private List<Marca> marcas = List.of();

    public FormularioModelos() {
        super(new String[]{"Nombre", "Marca"});
        agregarCampo("Nombre:", txtNombre);
        agregarCampo("Marca:", comboMarca);
        cargarMarcas();
        accionActualizar();
    }

    private void cargarMarcas() {
        try {
            marcas = marcaControlador.listar();
        } catch (RuntimeException e) {
            mostrarExcepcion(e);
        }
        comboMarca.removeAllItems();
        for (Marca marca : marcas) {
            comboMarca.addItem(marca);
        }
    }

    private Integer idDeLaMarcaSeleccionada() {
        Marca marca = (Marca) comboMarca.getSelectedItem();
        return marca == null ? null : marca.idMarca();
    }

    private void seleccionarMarca(String nombreDeMarca) {
        for (Marca marca : marcas) {
            if (marca.nombre().equalsIgnoreCase(nombreDeMarca)) {
                comboMarca.setSelectedItem(marca);
                return;
            }
        }
        comboMarca.setSelectedIndex(marcas.isEmpty() ? -1 : 0);
    }

    @Override
    protected void accionNuevo() {
        limpiarFormulario();
        txtNombre.requestFocus();
        estado("Nuevo registro: seleccione la marca y pulse Guardar.");
    }

    @Override
    protected void accionGuardar() {
        String mensaje = controlador.guardar(txtNombre.getText(), idDeLaMarcaSeleccionada());
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("El modelo se guardó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionModificar() {
        Integer id = idSeleccionado();
        if (id == null) {
            advertencia("Seleccione un modelo de la lista antes de modificar.");
            return;
        }
        String mensaje = controlador.modificar(id, txtNombre.getText(),
                idDeLaMarcaSeleccionada());
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("El modelo se modificó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionEliminar() {
        Integer id = idSeleccionado();
        if (id == null) {
            advertencia("Seleccione un modelo de la lista antes de eliminar.");
            return;
        }
        if (!confirmarEliminacion("¿Confirma que desea eliminar el modelo seleccionado?")) {
            return;
        }
        String mensaje = controlador.eliminar(id);
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("El modelo se eliminó correctamente.");
        limpiarFormulario();
        accionActualizar();
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
        txtNombre.setText(textoCelda(fila, 1));
        seleccionarMarca(textoCelda(fila, 2));
    }

    @Override
    public void limpiarFormulario() {
        txtNombre.setText("");
        if (!marcas.isEmpty()) {
            comboMarca.setSelectedIndex(0);
        }
    }
}
