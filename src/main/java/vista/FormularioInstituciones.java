package vista;

import controlador.InstitucionControlador;
import controlador.PaisControlador;
import modelo.Pais;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.util.List;

/**
 * Formulario de instituciones (RF008 - CRUD opcional, tabla obligatoria).
 */
public class FormularioInstituciones extends FormularioCRUD {

    private final InstitucionControlador controlador = new InstitucionControlador();
    private final PaisControlador paisControlador = new PaisControlador();
    private final JTextField txtNombre = new JTextField(22);
    private final JTextField txtDireccion = new JTextField(22);
    private final JTextField txtTelefono = new JTextField(22);
    private final JTextField txtCiudad = new JTextField(22);
    private final JComboBox<Pais> comboPais = new JComboBox<>();

    private List<Pais> paises = List.of();

    public FormularioInstituciones() {
        super(new String[]{"Nombre", "Dirección", "Teléfono", "Ciudad", "País"});
        agregarCampo("Nombre:", txtNombre);
        agregarCampo("Dirección:", txtDireccion);
        agregarCampo("Teléfono:", txtTelefono);
        agregarCampo("Ciudad:", txtCiudad);
        agregarCampo("País:", comboPais);
        cargarPaises();
        accionActualizar();
    }

    private void cargarPaises() {
        try {
            paises = paisControlador.listar();
        } catch (RuntimeException e) {
            mostrarExcepcion(e);
        }
        comboPais.removeAllItems();
        for (Pais pais : paises) {
            comboPais.addItem(pais);
        }
    }

    private Integer idDelPaisSeleccionado() {
        Pais pais = (Pais) comboPais.getSelectedItem();
        return pais == null ? null : pais.idPais();
    }

    private void seleccionarPais(String nombre) {
        for (Pais pais : paises) {
            if (pais.nombre().equalsIgnoreCase(nombre)) {
                comboPais.setSelectedItem(pais);
                return;
            }
        }
        if (!paises.isEmpty()) {
            comboPais.setSelectedIndex(0);
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
        String mensaje = controlador.guardar(txtNombre.getText(), txtDireccion.getText(),
                txtTelefono.getText(), txtCiudad.getText(), idDelPaisSeleccionado());
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("La institución se guardó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionModificar() {
        Integer id = idSeleccionado();
        if (id == null) {
            advertencia("Seleccione una institución de la lista antes de modificar.");
            return;
        }
        String mensaje = controlador.modificar(id, txtNombre.getText(), txtDireccion.getText(),
                txtTelefono.getText(), txtCiudad.getText(), idDelPaisSeleccionado());
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("La institución se modificó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionEliminar() {
        Integer id = idSeleccionado();
        if (id == null) {
            advertencia("Seleccione una institución de la lista antes de eliminar.");
            return;
        }
        if (!confirmarEliminacion("¿Confirma que desea eliminar la institución seleccionada?")) {
            return;
        }
        String mensaje = controlador.eliminar(id);
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("La institución se eliminó correctamente.");
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
        txtDireccion.setText(textoCelda(fila, 2));
        txtTelefono.setText(textoCelda(fila, 3));
        txtCiudad.setText(textoCelda(fila, 4));
        seleccionarPais(textoCelda(fila, 5));
    }

    @Override
    public void limpiarFormulario() {
        txtNombre.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        txtCiudad.setText("");
        if (!paises.isEmpty()) {
            comboPais.setSelectedIndex(0);
        }
    }
}
