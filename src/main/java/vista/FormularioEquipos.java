package vista;

import controlador.EquipoControlador;
import controlador.MarcaControlador;
import modelo.Equipo;
import modelo.Marca;
import modelo.Modelo;
import modelo.Pais;
import modelo.Proveedor;
import modelo.TipoEquipo;
import modelo.Ubicacion;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.util.List;

/**
 * Formulario del modulo principal de equipos (RF006).
 * Alta, baja logica, modificacion, consulta, busqueda y listado.
 */
public class FormularioEquipos extends FormularioCRUD {

    private final EquipoControlador controlador = new EquipoControlador();
    private final MarcaControlador marcaControlador = new MarcaControlador();

    private final JTextField txtNombre = new JTextField(18);
    private final JComboBox<TipoEquipo> comboTipo = new JComboBox<>();
    private final JComboBox<Marca> comboMarca = new JComboBox<>();
    private final JComboBox<Modelo> comboModelo = new JComboBox<>();
    private final JTextField txtNumeroSerie = new JTextField(18);
    private final JTextField txtGarantia = new JTextField(18);
    private final JComboBox<Pais> comboPais = new JComboBox<>();
    private final JComboBox<Proveedor> comboProveedor = new JComboBox<>();
    private final JTextField txtFechaCompra = new JTextField(18);
    private final JTextField txtCodigoInterno = new JTextField(18);
    private final JComboBox<Ubicacion> comboUbicacion = new JComboBox<>();
    private final JComboBox<String> comboEstado = new JComboBox<>(
            new String[]{Equipo.ACTIVO, Equipo.INACTIVO});

    private List<TipoEquipo> tipos = List.of();
    private List<Marca> marcas = List.of();
    private List<Pais> paises = List.of();
    private List<Proveedor> proveedores = List.of();
    private List<Ubicacion> ubicaciones = List.of();

    public FormularioEquipos() {
        super(new String[]{"Nombre", "Tipo", "Marca", "Modelo", "N° de serie",
                "Garantía (meses)", "País origen", "Proveedor", "Fecha compra",
                "Código interno", "Ubicación", "Estado"});
        agregarCampo("Nombre:", txtNombre);
        agregarCampo("Tipo de equipo:", comboTipo);
        agregarCampo("Marca:", comboMarca);
        agregarCampo("Modelo:", comboModelo);
        agregarCampo("N° de serie:", txtNumeroSerie);
        agregarCampo("Garantía (meses):", txtGarantia);
        agregarCampo("País de origen:", comboPais);
        agregarCampo("Proveedor:", comboProveedor);
        agregarCampo("Fecha compra (aaaa-mm-dd):", txtFechaCompra);
        agregarCampo("Código interno:", txtCodigoInterno);
        agregarCampo("Ubicación:", comboUbicacion);
        agregarCampo("Estado:", comboEstado);

        comboMarca.addActionListener(e -> recargarModelosDeLaMarca());
        cargarListas();
        accionActualizar();
    }

    private void cargarListas() {
        try {
            tipos = controlador.tiposDeEquipo();
            marcas = marcaControlador.listar();
            paises = controlador.paises();
            proveedores = controlador.proveedores();
            ubicaciones = controlador.ubicaciones();
        } catch (RuntimeException e) {
            mostrarExcepcion(e);
        }
        rellenar(comboTipo, tipos);
        rellenar(comboMarca, marcas);
        rellenar(comboPais, paises);
        rellenar(comboProveedor, proveedores);
        rellenar(comboUbicacion, ubicaciones);
        recargarModelosDeLaMarca();
    }

    private <T> void rellenar(JComboBox<T> combo, List<T> elementos) {
        combo.removeAllItems();
        for (T elemento : elementos) {
            combo.addItem(elemento);
        }
    }

    /** Cascada marca -> modelos (la marca se deriva del modelo). */
    private void recargarModelosDeLaMarca() {
        Marca marca = (Marca) comboMarca.getSelectedItem();
        Integer idMarca = marca == null ? null : marca.idMarca();
        try {
            rellenar(comboModelo, controlador.modelosDeLaMarca(idMarca));
        } catch (RuntimeException e) {
            mostrarExcepcion(e);
        }
    }

    private Integer idDe(JComboBox<?> combo, java.util.function.Function<Object, Integer> id) {
        Object seleccionado = combo.getSelectedItem();
        return seleccionado == null ? null : id.apply(seleccionado);
    }

    private Integer idTipoSeleccionado() {
        return idDe(comboTipo, o -> ((TipoEquipo) o).idTipoEquipo());
    }

    private Integer idModeloSeleccionado() {
        return idDe(comboModelo, o -> ((Modelo) o).idModelo());
    }

    private Integer idPaisSeleccionado() {
        return idDe(comboPais, o -> ((Pais) o).idPais());
    }

    private Integer idProveedorSeleccionado() {
        return idDe(comboProveedor, o -> ((Proveedor) o).idProveedor());
    }

    private Integer idUbicacionSeleccionada() {
        return idDe(comboUbicacion, o -> ((Ubicacion) o).idUbicacion());
    }

    private <T> void seleccionarPorNombre(JComboBox<T> combo, String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            T item = combo.getItemAt(i);
            if (item != null && nombre.equalsIgnoreCase(item.toString())) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    @Override
    protected void accionNuevo() {
        limpiarFormulario();
        txtNombre.requestFocus();
        estado("Nuevo equipo: complete los datos y pulse Guardar.");
    }

    @Override
    protected void accionGuardar() {
        String mensaje = controlador.guardar(txtNombre.getText(), idTipoSeleccionado(),
                idModeloSeleccionado(), txtNumeroSerie.getText(), txtGarantia.getText(),
                idPaisSeleccionado(), idProveedorSeleccionado(), txtFechaCompra.getText(),
                txtCodigoInterno.getText(), idUbicacionSeleccionada(),
                String.valueOf(comboEstado.getSelectedItem()));
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("El equipo se guardó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionModificar() {
        Integer id = idSeleccionado();
        if (id == null) {
            advertencia("Seleccione un equipo de la lista antes de modificar.");
            return;
        }
        String mensaje = controlador.modificar(id, txtNombre.getText(), idTipoSeleccionado(),
                idModeloSeleccionado(), txtNumeroSerie.getText(), txtGarantia.getText(),
                idPaisSeleccionado(), idProveedorSeleccionado(), txtFechaCompra.getText(),
                txtCodigoInterno.getText(), idUbicacionSeleccionada(),
                String.valueOf(comboEstado.getSelectedItem()));
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("El equipo se modificó correctamente.");
        limpiarFormulario();
        accionActualizar();
    }

    @Override
    protected void accionEliminar() {
        Integer id = idSeleccionado();
        if (id == null) {
            advertencia("Seleccione un equipo de la lista antes de dar de baja.");
            return;
        }
        if (!confirmarEliminacion(
                "¿Confirma que desea dar de baja lógica al equipo seleccionado?\n"
                        + "El registro no se elimina: pasará a estado INACTIVO.")) {
            return;
        }
        String mensaje = controlador.eliminar(id);
        if (mensaje != null) {
            error(mensaje);
            return;
        }
        informacion("El equipo se dio de baja lógica correctamente.");
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
        seleccionarPorNombre(comboTipo, textoCelda(fila, 2));
        seleccionarPorNombre(comboMarca, textoCelda(fila, 3));
        recargarModelosDeLaMarca();
        seleccionarPorNombre(comboModelo, textoCelda(fila, 4));
        txtNumeroSerie.setText(textoCelda(fila, 5));
        txtGarantia.setText(textoCelda(fila, 6));
        seleccionarPorNombre(comboPais, textoCelda(fila, 7));
        seleccionarPorNombre(comboProveedor, textoCelda(fila, 8));
        txtFechaCompra.setText(textoCelda(fila, 9));
        txtCodigoInterno.setText(textoCelda(fila, 10));
        seleccionarPorNombre(comboUbicacion, textoCelda(fila, 11));
        comboEstado.setSelectedItem(textoCelda(fila, 12));
        estado("Equipo seleccionado: edite los campos y pulse Modificar.");
    }

    @Override
    public void limpiarFormulario() {
        txtNombre.setText("");
        txtNumeroSerie.setText("");
        txtGarantia.setText("");
        txtFechaCompra.setText("");
        txtCodigoInterno.setText("");
        if (!tipos.isEmpty()) {
            comboTipo.setSelectedIndex(0);
        }
        if (!marcas.isEmpty()) {
            comboMarca.setSelectedIndex(0);
        }
        recargarModelosDeLaMarca();
        if (!paises.isEmpty()) {
            comboPais.setSelectedIndex(0);
        }
        if (!proveedores.isEmpty()) {
            comboProveedor.setSelectedIndex(0);
        }
        if (!ubicaciones.isEmpty()) {
            comboUbicacion.setSelectedIndex(0);
        }
        comboEstado.setSelectedItem(Equipo.ACTIVO);
    }
}
