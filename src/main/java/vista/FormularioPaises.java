package vista;

import controlador.PaisControlador;
import modelo.Pais;

import javax.swing.JTextField;

/**
 * Formulario de paises (RF009 - CRUD completo).
 */
public class FormularioPaises extends FormularioCRUD {

    private final PaisControlador controlador = new PaisControlador();
    private final JTextField txtNombre = new JTextField(22);

    public FormularioPaises() {
        super(new String[]{"Nombre"});
        agregarCampo("Nombre:", txtNombre);
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
        String mensaje = controlador.guardar(txtNombre.getText());
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("El país se guardó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionModificar() {
        Integer id = idSeleccionado();
        if (id == null) {
            advertencia("Seleccione un país de la lista antes de modificar.");
            return;
        }
        String mensaje = controlador.modificar(id, txtNombre.getText());
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("El país se modificó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionEliminar() {
        Integer id = idSeleccionado();
        if (id == null) {
            advertencia("Seleccione un país de la lista antes de eliminar.");
            return;
        }
        if (!confirmarEliminacion("¿Confirma que desea eliminar el país seleccionado?")) {
            return;
        }
        String mensaje = controlador.eliminar(id);
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("El país se eliminó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionBuscar() {
        cargarEntidades(controlador.buscar(textoDeBusqueda()),
                Pais::idPais, p -> new Object[]{p.nombre()});
    }

    @Override
    protected void accionActualizar() {
        cargarEntidades(controlador.listar(),
                Pais::idPais, p -> new Object[]{p.nombre()});
    }

    @Override
    protected void alSeleccionarFila(int fila) {
        txtNombre.setText(textoCelda(fila, 1));
    }

    @Override
    public void limpiarFormulario() {
        txtNombre.setText("");
    }
}
