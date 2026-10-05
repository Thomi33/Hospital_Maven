package vista;

import controlador.TipoIntervencionControlador;
import modelo.TipoIntervencion;

import javax.swing.JTextField;

/**
 * Formulario de tipos de intervencion (RF014 - CRUD opcional).
 */
public class FormularioTipoIntervencion extends FormularioCRUD {

    private final TipoIntervencionControlador controlador = new TipoIntervencionControlador();
    private final JTextField txtNombre = new JTextField(22);
    private final JTextField txtDescripcion = new JTextField(22);

    public FormularioTipoIntervencion() {
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
        informacion("El tipo de intervención se guardó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionModificar() {
        Integer id = idSeleccionado();
        if (id == null) {
            advertencia("Seleccione un tipo de intervención de la lista antes de modificar.");
            return;
        }
        String mensaje = controlador.modificar(id, txtNombre.getText(), txtDescripcion.getText());
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("El tipo de intervención se modificó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionEliminar() {
        Integer id = idSeleccionado();
        if (id == null) {
            advertencia("Seleccione un tipo de intervención de la lista antes de eliminar.");
            return;
        }
        if (!confirmarEliminacion("¿Confirma que desea eliminar el tipo de intervención?")) {
            return;
        }
        String mensaje = controlador.eliminar(id);
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("El tipo de intervención se eliminó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionBuscar() {
        cargarEntidades(controlador.buscar(textoDeBusqueda()),
                TipoIntervencion::idTipoIntervencion,
                t -> new Object[]{t.nombre(), t.descripcion()});
    }

    @Override
    protected void accionActualizar() {
        cargarEntidades(controlador.listar(),
                TipoIntervencion::idTipoIntervencion,
                t -> new Object[]{t.nombre(), t.descripcion()});
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
