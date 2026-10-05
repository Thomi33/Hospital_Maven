package vista;

import controlador.FuncionalidadControlador;
import modelo.Funcionalidad;

import javax.swing.JComboBox;
import javax.swing.JTextField;

/**
 * Formulario de funcionalidades (RF003 - alta, baja, modificacion,
 * consulta).
 */
public class FormularioFuncionalidades extends FormularioCRUD {

    private final FuncionalidadControlador controlador = new FuncionalidadControlador();
    private final JTextField txtNombre = new JTextField(22);
    private final JTextField txtDescripcion = new JTextField(22);
    private final JComboBox<String> comboEstado = new JComboBox<>(new String[]{
            Funcionalidad.ACTIVO, Funcionalidad.INACTIVO});

    public FormularioFuncionalidades() {
        super(new String[]{"Nombre", "Descripción", "Estado"});
        agregarCampo("Nombre:", txtNombre);
        agregarCampo("Descripción:", txtDescripcion);
        agregarCampo("Estado:", comboEstado);
        accionActualizar();
    }

    @Override
    protected void accionNuevo() {
        limpiarFormulario();
        txtNombre.requestFocus();
        estado("Nuevo registro: complete los datos y pulse Guardar.");
    }

    @Override
    protected void accionGuardar() {
        String mensaje = controlador.guardar(txtNombre.getText(), txtDescripcion.getText());
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("La funcionalidad se guardó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionModificar() {
        Integer id = idSeleccionado();
        if (id == null) {
            advertencia("Seleccione una funcionalidad de la lista antes de modificar.");
            return;
        }
        String mensaje = controlador.modificar(id, txtNombre.getText(), txtDescripcion.getText(),
                String.valueOf(comboEstado.getSelectedItem()));
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("La funcionalidad se modificó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionEliminar() {
        Integer id = idSeleccionado();
        if (id == null) {
            advertencia("Seleccione una funcionalidad de la lista antes de dar de baja.");
            return;
        }
        if (!confirmarEliminacion(
                "¿Confirma que desea dar de baja la funcionalidad seleccionada?")) {
            return;
        }
        String mensaje = controlador.eliminar(id);
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("La funcionalidad se dio de baja correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionBuscar() {
        cargarEntidades(controlador.buscar(textoDeBusqueda()),
                Funcionalidad::idFuncionalidad,
                f -> new Object[]{f.nombre(), f.descripcion(), f.estado()});
    }

    @Override
    protected void accionActualizar() {
        cargarEntidades(controlador.listar(),
                Funcionalidad::idFuncionalidad,
                f -> new Object[]{f.nombre(), f.descripcion(), f.estado()});
    }

    @Override
    protected void alSeleccionarFila(int fila) {
        txtNombre.setText(textoCelda(fila, 1));
        txtDescripcion.setText(textoCelda(fila, 2));
        comboEstado.setSelectedItem(textoCelda(fila, 3));
    }

    @Override
    public void limpiarFormulario() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        comboEstado.setSelectedItem(Funcionalidad.ACTIVO);
    }
}
