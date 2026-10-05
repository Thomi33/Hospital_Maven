package vista;

import controlador.MarcaControlador;
import modelo.Marca;

import javax.swing.JTextField;

/**
 * Formulario de marcas (RF013 - CRUD completo).
 */
public class FormularioMarcas extends FormularioCRUD {

    private final MarcaControlador controlador = new MarcaControlador();
    private final JTextField txtNombre = new JTextField(22);

    public FormularioMarcas() {
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
        informacion("La marca se guardó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionModificar() {
        Integer id = idSeleccionado();
        if (id == null) {
            advertencia("Seleccione una marca de la lista antes de modificar.");
            return;
        }
        String mensaje = controlador.modificar(id, txtNombre.getText());
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("La marca se modificó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionEliminar() {
        Integer id = idSeleccionado();
        if (id == null) {
            advertencia("Seleccione una marca de la lista antes de eliminar.");
            return;
        }
        if (!confirmarEliminacion("¿Confirma que desea eliminar la marca seleccionada?")) {
            return;
        }
        String mensaje = controlador.eliminar(id);
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("La marca se eliminó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionBuscar() {
        cargarEntidades(controlador.buscar(textoDeBusqueda()),
                Marca::idMarca, m -> new Object[]{m.nombre()});
    }

    @Override
    protected void accionActualizar() {
        cargarEntidades(controlador.listar(),
                Marca::idMarca, m -> new Object[]{m.nombre()});
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
