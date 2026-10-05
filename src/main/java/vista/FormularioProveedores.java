package vista;

import controlador.ProveedorControlador;
import modelo.Proveedor;

import javax.swing.JTextField;

/**
 * Formulario de proveedores (RF011 - CRUD completo).
 */
public class FormularioProveedores extends FormularioCRUD {

    private final ProveedorControlador controlador = new ProveedorControlador();
    private final JTextField txtNombre = new JTextField(22);
    private final JTextField txtDireccion = new JTextField(22);
    private final JTextField txtTelefono = new JTextField(22);
    private final JTextField txtEmail = new JTextField(22);

    public FormularioProveedores() {
        super(new String[]{"Nombre", "Dirección", "Teléfono", "Email"});
        agregarCampo("Nombre:", txtNombre);
        agregarCampo("Dirección:", txtDireccion);
        agregarCampo("Teléfono:", txtTelefono);
        agregarCampo("Email:", txtEmail);
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
        String mensaje = controlador.guardar(txtNombre.getText(), txtDireccion.getText(),
                txtTelefono.getText(), txtEmail.getText());
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("El proveedor se guardó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionModificar() {
        Integer id = idSeleccionado();
        if (id == null) {
            advertencia("Seleccione un proveedor de la lista antes de modificar.");
            return;
        }
        String mensaje = controlador.modificar(id, txtNombre.getText(), txtDireccion.getText(),
                txtTelefono.getText(), txtEmail.getText());
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("El proveedor se modificó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionEliminar() {
        Integer id = idSeleccionado();
        if (id == null) {
            advertencia("Seleccione un proveedor de la lista antes de eliminar.");
            return;
        }
        if (!confirmarEliminacion("¿Confirma que desea eliminar el proveedor seleccionado?")) {
            return;
        }
        String mensaje = controlador.eliminar(id);
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("El proveedor se eliminó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionBuscar() {
        cargarEntidades(controlador.buscar(textoDeBusqueda()),
                Proveedor::idProveedor,
                p -> new Object[]{p.nombre(), p.direccion(), p.telefono(), p.email()});
    }

    @Override
    protected void accionActualizar() {
        cargarEntidades(controlador.listar(),
                Proveedor::idProveedor,
                p -> new Object[]{p.nombre(), p.direccion(), p.telefono(), p.email()});
    }

    @Override
    protected void alSeleccionarFila(int fila) {
        txtNombre.setText(textoCelda(fila, 1));
        txtDireccion.setText(textoCelda(fila, 2));
        txtTelefono.setText(textoCelda(fila, 3));
        txtEmail.setText(textoCelda(fila, 4));
    }

    @Override
    public void limpiarFormulario() {
        txtNombre.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
    }
}
