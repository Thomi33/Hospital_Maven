package vista;

import controlador.TipoEquipoControlador;
import modelo.TipoEquipo;

import javax.swing.JTextField;

/**
 * Formulario de tipos de equipo (RF010 - CRUD completo).
 */
public class FormularioTipoEquipo extends FormularioCRUD {

    private final TipoEquipoControlador controlador = new TipoEquipoControlador();
    private final JTextField txtNombre = new JTextField(22);
    private final JTextField txtDescripcion = new JTextField(22);

    public FormularioTipoEquipo() {
        super(new String[]{"Nombre", "Descripción"});
        agregarCampo("Nombre:", txtNombre);
        agregarCampo("Descripción:", txtDescripcion);
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
        informacion("El tipo de equipo se guardó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionModificar() {
        Integer id = idSeleccionado();
        if (id == null) {
            advertencia("Seleccione un tipo de equipo de la lista antes de modificar.");
            return;
        }
        String mensaje = controlador.modificar(id, txtNombre.getText(), txtDescripcion.getText());
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("El tipo de equipo se modificó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionEliminar() {
        Integer id = idSeleccionado();
        if (id == null) {
            advertencia("Seleccione un tipo de equipo de la lista antes de eliminar.");
            return;
        }
        if (!confirmarEliminacion("¿Confirma que desea eliminar el tipo de equipo seleccionado?")) {
            return;
        }
        String mensaje = controlador.eliminar(id);
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("El tipo de equipo se eliminó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionBuscar() {
        cargarEntidades(controlador.buscar(textoDeBusqueda()),
                TipoEquipo::idTipoEquipo, t -> new Object[]{t.nombre(), t.descripcion()});
    }

    @Override
    protected void accionActualizar() {
        cargarEntidades(controlador.listar(),
                TipoEquipo::idTipoEquipo, t -> new Object[]{t.nombre(), t.descripcion()});
    }

    @Override
    protected void alSeleccionarFila(int fila) {
        txtNombre.setText(textoCelda(fila, 1));
        txtDescripcion.setText(textoCelda(fila, 2));
    }

    @Override
    public void limpiarFormulario() {
        txtNombre.setText("");
        txtDescripcion.setText("");
    }
}
