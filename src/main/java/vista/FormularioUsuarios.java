package vista;

import controlador.PerfilControlador;
import controlador.UsuarioControlador;
import modelo.Perfil;

import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.util.List;

/**
 * Formulario de usuarios (RF001 - registro, modificacion, baja logica,
 * consulta y busqueda).
 */
public class FormularioUsuarios extends FormularioCRUD {

    private final UsuarioControlador controlador = new UsuarioControlador();
    private final PerfilControlador perfilControlador = new PerfilControlador();

    private final JTextField txtNombre = new JTextField(16);
    private final JTextField txtApellido = new JTextField(16);
    private final JTextField txtCedula = new JTextField(16);
    private final JTextField txtFechaNacimiento = new JTextField(16);
    private final JTextField txtTelefono = new JTextField(16);
    private final JTextField txtEmail = new JTextField(16);
    private final JTextField txtUsuario = new JTextField(16);
    private final JPasswordField txtContrasena = new JPasswordField(16);
    private final JComboBox<Perfil> comboPerfil = new JComboBox<>();
    private final JComboBox<String> comboEstado = new JComboBox<>(
            new String[]{Perfil.ACTIVO, Perfil.INACTIVO});

    private List<Perfil> perfiles = List.of();

    public FormularioUsuarios() {
        super(new String[]{"Nombre", "Apellido", "Cédula", "Fecha nac.", "Teléfono",
                "Email", "Usuario", "Perfil", "Estado"});
        agregarCampo("Nombre:", txtNombre);
        agregarCampo("Apellido:", txtApellido);
        agregarCampo("Cédula (0.000.000-0):", txtCedula);
        agregarCampo("Fecha nacimiento (aaaa-mm-dd):", txtFechaNacimiento);
        agregarCampo("Teléfono:", txtTelefono);
        agregarCampo("Email:", txtEmail);
        agregarCampo("Usuario:", txtUsuario);
        agregarCampo("Contraseña:", txtContrasena);
        agregarCampo("Perfil:", comboPerfil);
        agregarCampo("Estado:", comboEstado);
        cargarPerfiles();
        accionActualizar();
        estado("Contraseña: al modificar, deje el campo vacío para conservar la actual.");
    }

    private void cargarPerfiles() {
        try {
            perfiles = perfilControlador.listar();
        } catch (RuntimeException e) {
            mostrarExcepcion(e);
        }
        comboPerfil.removeAllItems();
        for (Perfil perfil : perfiles) {
            comboPerfil.addItem(perfil);
        }
    }

    private Integer idDelPerfilSeleccionado() {
        Perfil perfil = (Perfil) comboPerfil.getSelectedItem();
        return perfil == null ? null : perfil.idPerfil();
    }

    private void seleccionarPerfil(String nombre) {
        for (Perfil perfil : perfiles) {
            if (perfil.nombre().equalsIgnoreCase(nombre)) {
                comboPerfil.setSelectedItem(perfil);
                return;
            }
        }
        if (!perfiles.isEmpty()) {
            comboPerfil.setSelectedIndex(0);
        }
    }

    private String contrasenaIngresada() {
        return new String(txtContrasena.getPassword());
    }

    @Override
    protected void accionNuevo() {
        limpiarFormulario();
        txtNombre.requestFocus();
        estado("Nuevo usuario: complete todos los campos y pulse Guardar.");
    }

    @Override
    protected void accionGuardar() {
        String mensaje = controlador.guardar(txtNombre.getText(), txtApellido.getText(),
                txtCedula.getText(), txtFechaNacimiento.getText(), txtTelefono.getText(),
                txtEmail.getText(), txtUsuario.getText(), contrasenaIngresada(),
                idDelPerfilSeleccionado());
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("El usuario se guardó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionModificar() {
        Integer id = idSeleccionado();
        if (id == null) {
            advertencia("Seleccione un usuario de la lista antes de modificar.");
            return;
        }
        String mensaje = controlador.modificar(id, txtNombre.getText(), txtApellido.getText(),
                txtCedula.getText(), txtFechaNacimiento.getText(), txtTelefono.getText(),
                txtEmail.getText(), txtUsuario.getText(), contrasenaIngresada(),
                idDelPerfilSeleccionado(), String.valueOf(comboEstado.getSelectedItem()));
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("El usuario se modificó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionEliminar() {
        Integer id = idSeleccionado();
        if (id == null) {
            advertencia("Seleccione un usuario de la lista antes de dar de baja.");
            return;
        }
        if (!confirmarEliminacion(
                "¿Confirma que desea dar de baja lógica al usuario seleccionado?")) {
            return;
        }
        String mensaje = controlador.eliminar(id);
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("El usuario se dio de baja lógica correctamente.");
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
        txtApellido.setText(textoCelda(fila, 2));
        txtCedula.setText(textoCelda(fila, 3));
        txtFechaNacimiento.setText(textoCelda(fila, 4));
        txtTelefono.setText(textoCelda(fila, 5));
        txtEmail.setText(textoCelda(fila, 6));
        txtUsuario.setText(textoCelda(fila, 7));
        txtContrasena.setText("");
        seleccionarPerfil(textoCelda(fila, 8));
        comboEstado.setSelectedItem(textoCelda(fila, 9));
        estado("Usuario seleccionado: edite los campos y pulse Modificar.");
    }

    @Override
    public void limpiarFormulario() {
        txtNombre.setText("");
        txtApellido.setText("");
        txtCedula.setText("");
        txtFechaNacimiento.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        txtUsuario.setText("");
        txtContrasena.setText("");
        if (!perfiles.isEmpty()) {
            comboPerfil.setSelectedIndex(0);
        }
        comboEstado.setSelectedItem(Perfil.ACTIVO);
    }
}
