package vista;

import controlador.FuncionalidadControlador;
import controlador.PerfilControlador;
import modelo.Funcionalidad;
import modelo.Perfil;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.util.List;

/**
 * Formulario de perfiles (RF002) y de sus funcionalidades asociadas
 * (relacion N:N con la tabla perfil_funcionalidad).
 */
public class FormularioPerfiles extends FormularioCRUD {

    private final PerfilControlador controlador = new PerfilControlador();
    private final FuncionalidadControlador funcionalidadControlador =
            new FuncionalidadControlador();
    private final JTextField txtNombre = new JTextField(22);
    private final JTextField txtDescripcion = new JTextField(22);
    private final JComboBox<String> comboEstado =
            new JComboBox<>(new String[]{Perfil.ACTIVO, Perfil.INACTIVO});
    private final JComboBox<Funcionalidad> comboFuncionalidad = new JComboBox<>();
    private final JButton btnAsignar = new JButton("Asignar funcionalidad");
    private final JLabel lblPermisos = new JLabel("Permisos: (seleccione un perfil)");

    private List<Funcionalidad> funcionalidades = List.of();

    public FormularioPerfiles() {
        super(new String[]{"Nombre", "Descripción", "Estado"});
        agregarCampo("Nombre:", txtNombre);
        agregarCampo("Descripción:", txtDescripcion);
        agregarCampo("Estado:", comboEstado);
        agregarCampo("Funcionalidad:", comboFuncionalidad);
        agregarCampo("", btnAsignar);
        agregarCampo("Permisos del perfil:", lblPermisos);
        cargarFuncionalidades();
        btnAsignar.addActionListener(e -> asignarFuncionalidad());
        accionActualizar();
    }

    private void cargarFuncionalidades() {
        try {
            funcionalidades = funcionalidadControlador.listarActivas();
        } catch (RuntimeException e) {
            mostrarExcepcion(e);
        }
        comboFuncionalidad.removeAllItems();
        for (Funcionalidad funcionalidad : funcionalidades) {
            comboFuncionalidad.addItem(funcionalidad);
        }
    }

    private void asignarFuncionalidad() {
        Integer idPerfil = idSeleccionado();
        Funcionalidad funcionalidad = (Funcionalidad) comboFuncionalidad.getSelectedItem();
        Integer idFuncionalidad = funcionalidad == null ? null : funcionalidad.idFuncionalidad();
        String mensaje = controlador.asignarFuncionalidad(idPerfil, idFuncionalidad);
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("Funcionalidad asignada al perfil.");
        mostrarPermisos(idPerfil);
    }

    private void mostrarPermisos(Integer idPerfil) {
        if (idPerfil == null) {
            lblPermisos.setText("Permisos: (seleccione un perfil)");
            return;
        }
        List<String> permisos = controlador.funcionalidadesDelPerfil(idPerfil);
        lblPermisos.setText("Permisos: "
                + (permisos.isEmpty() ? "(sin funcionalidades asignadas)" : String.join(", ", permisos)));
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
                String.valueOf(comboEstado.getSelectedItem()));
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("El perfil se guardó correctamente. Selecciónnelo para asignarle "
                + "funcionalidades.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionModificar() {
        Integer id = idSeleccionado();
        if (id == null) {
            advertencia("Seleccione un perfil de la lista antes de modificar.");
            return;
        }
        String mensaje = controlador.modificar(id, txtNombre.getText(), txtDescripcion.getText(),
                String.valueOf(comboEstado.getSelectedItem()));
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("El perfil se modificó correctamente.");
        accionActualizar();
        mostrarPermisos(id);
    }

    @Override
    protected void accionEliminar() {
        Integer id = idSeleccionado();
        if (id == null) {
            advertencia("Seleccione un perfil de la lista antes de dar de baja.");
            return;
        }
        if (!confirmarEliminacion("¿Confirma que desea dar de baja el perfil seleccionado?")) {
            return;
        }
        String mensaje = controlador.eliminar(id);
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("El perfil se dio de baja correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionBuscar() {
        cargarEntidades(controlador.buscar(textoDeBusqueda()),
                Perfil::idPerfil, p -> new Object[]{p.nombre(), p.descripcion(), p.estado()});
    }

    @Override
    protected void accionActualizar() {
        cargarEntidades(controlador.listar(),
                Perfil::idPerfil, p -> new Object[]{p.nombre(), p.descripcion(), p.estado()});
    }

    @Override
    protected void alSeleccionarFila(int fila) {
        txtNombre.setText(textoCelda(fila, 1));
        txtDescripcion.setText(textoCelda(fila, 2));
        comboEstado.setSelectedItem(textoCelda(fila, 3));
        mostrarPermisos(idSeleccionado());
    }

    @Override
    public void limpiarFormulario() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        comboEstado.setSelectedItem(Perfil.ACTIVO);
        lblPermisos.setText("Permisos: (seleccione un perfil)");
    }
}
