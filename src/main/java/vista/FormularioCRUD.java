package vista;

import utilidades.Mensajes;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

/**
 * Base de todos los formularios del sistema.
 * Proporciona los componentes minimos exigidos (JLabel, JTextField,
 * JComboBox, JTable, JScrollPane y JButton) y los botones Nuevo,
 * Guardar, Modificar, Eliminar, Buscar, Actualizar y Salir.
 * La primera columna del modelo de la grilla es el id oculto.
 */
public abstract class FormularioCRUD extends JPanel {

    protected final DefaultTableModel modeloTabla;
    protected final JTable tabla = new JTable();
    protected final JTextField txtBuscar = new JTextField(18);
    protected final JComboBox<String> comboCriterio;
    protected final JButton btnNuevo = new JButton("Nuevo");
    protected final JButton btnGuardar = new JButton("Guardar");
    protected final JButton btnModificar = new JButton("Modificar");
    protected final JButton btnEliminar = new JButton("Eliminar");
    protected final JButton btnBuscar = new JButton("Buscar");
    protected final JButton btnActualizar = new JButton("Actualizar");
    protected final JButton btnSalir = new JButton("Salir");

    private final JPanel panelCampos = new JPanel(new GridBagLayout());
    private final JLabel lblEstado = new JLabel(" ");
    private Runnable accionDeSalir = () -> { };

    private int indiceCampo = 0;

    protected FormularioCRUD(String[] columnasVisibles) {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        String[] todas = new String[columnasVisibles.length + 1];
        todas[0] = "ID";
        System.arraycopy(columnasVisibles, 0, todas, 1, columnasVisibles.length);

        modeloTabla = new DefaultTableModel(todas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columna) {
                return Object.class;
            }
        };
        tabla.setModel(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);
        // El id se guarda en el modelo pero no se muestra en la pantalla.
        tabla.getColumnModel().removeColumn(tabla.getColumnModel().getColumn(0));

        String[] opciones = new String[todas.length];
        opciones[0] = "Todas las columnas";
        System.arraycopy(columnasVisibles, 0, opciones, 1, columnasVisibles.length);
        comboCriterio = new JComboBox<>(opciones);

        tabla.getSelectionModel().addListSelectionListener(this::filaSeleccionadaCambiada);

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 3));
        panelBusqueda.add(new JLabel("Buscar:"));
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(new JLabel("Criterio:"));
        panelBusqueda.add(comboCriterio);

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(panelCampos, BorderLayout.CENTER);
        panelNorte.add(panelBusqueda, BorderLayout.SOUTH);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        for (JButton boton : new JButton[]{btnNuevo, btnGuardar, btnModificar, btnEliminar,
                btnBuscar, btnActualizar, btnSalir}) {
            panelBotones.add(boton);
        }

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.add(panelBotones, BorderLayout.CENTER);
        panelSur.add(lblEstado, BorderLayout.SOUTH);

        add(panelNorte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(panelSur, BorderLayout.SOUTH);

        lblEstado.setBorder(BorderFactory.createEmptyBorder(4, 6, 2, 6));

        btnNuevo.addActionListener(e -> ejecutar(this::accionNuevo));
        btnGuardar.addActionListener(e -> ejecutar(this::accionGuardar));
        btnModificar.addActionListener(e -> ejecutar(this::accionModificar));
        btnEliminar.addActionListener(e -> ejecutar(this::accionEliminar));
        btnBuscar.addActionListener(e -> ejecutar(this::accionBuscar));
        btnActualizar.addActionListener(e -> ejecutar(this::accionActualizar));
        btnSalir.addActionListener(e -> accionSalir());
    }

    /** Agrega un JLabel + componente al panel de campos (2 columnas). */
    protected <T extends JComponent> T agregarCampo(String etiqueta, T componente) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 6, 3, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        int fila = indiceCampo / 2;
        int columna = (indiceCampo % 2) * 2;

        c.gridx = columna;
        c.gridy = fila;
        c.weightx = 0;
        panelCampos.add(new JLabel(etiqueta), c);

        c.gridx = columna + 1;
        c.weightx = 1;
        panelCampos.add(componente, c);

        indiceCampo++;
        return componente;
    }

    /** Ejecuta una accion sin dejar que la aplicacion se cierre por un error. */
    private void ejecutar(Accion accion) {
        try {
            accion.ejecutar();
        } catch (RuntimeException e) {
            mostrarExcepcion(e);
        }
    }

    @FunctionalInterface
    private interface Accion {
        void ejecutar();
    }

    // -----------------------------------------------------------------
    // Grilla
    // -----------------------------------------------------------------

    /**
     * Carga filas cuya primera posicion es el id del registro.
     * Aplica el criterio de busqueda elegido en el combo.
     */
    protected void cargarFilas(List<Object[]> filas) {
        vaciarTabla();
        for (Object[] fila : filas) {
            if (coincideConCriterio(fila)) {
                modeloTabla.addRow(fila);
            }
        }
        estado("Registros: " + modeloTabla.getRowCount());
    }

    /**
     * Carga entidades de negocio (el id primero y luego sus columnas).
     * Se usa en los catalogos simples que devuelven objetos del modelo.
     */
    protected <T> void cargarEntidades(List<T> entidades,
                                      java.util.function.Function<T, Object> id,
                                      java.util.function.Function<T, Object[]> columnas) {
        vaciarTabla();
        for (T entidad : entidades) {
            Object[] columnasDeEntidad = columnas.apply(entidad);
            Object[] fila = new Object[columnasDeEntidad.length + 1];
            fila[0] = id.apply(entidad);
            System.arraycopy(columnasDeEntidad, 0, fila, 1, columnasDeEntidad.length);
            if (coincideConCriterio(fila)) {
                modeloTabla.addRow(fila);
            }
        }
        estado("Registros: " + modeloTabla.getRowCount());
    }

    /** Filtra la fila segun el texto y el criterio elegidos. */
    private boolean coincideConCriterio(Object[] fila) {
        String texto = textoDeBusqueda().toLowerCase();
        int columna = columnaDelCriterio();
        if (texto.isEmpty() || columna == 0 || columna >= fila.length) {
            return true;
        }
        Object valor = fila[columna];
        return valor != null && valor.toString().toLowerCase().contains(texto);
    }

    /** Agrega una columna al modelo que no se muestra en pantalla. */
    protected void agregarColumnaOculta(String nombre) {
        modeloTabla.addColumn(nombre);
        int ultima = tabla.getColumnModel().getColumnCount() - 1;
        tabla.getColumnModel().removeColumn(tabla.getColumnModel().getColumn(ultima));
    }

    /**
     * Reemplaza las columnas de la grilla (la primera siempre es el id
     * oculto). Se usa en el modulo de reportes, cuyos resultados cambian.
     */
    protected void definirColumnas(String[] columnasVisibles) {
        String[] todas = new String[columnasVisibles.length + 1];
        todas[0] = "ID";
        System.arraycopy(columnasVisibles, 0, todas, 1, columnasVisibles.length);
        modeloTabla.setColumnIdentifiers(todas);
        tabla.getColumnModel().removeColumn(tabla.getColumnModel().getColumn(0));
        modeloTabla.setRowCount(0);

        String[] opciones = new String[todas.length];
        opciones[0] = "Todas las columnas";
        System.arraycopy(columnasVisibles, 0, opciones, 1, columnasVisibles.length);
        comboCriterio.removeAllItems();
        for (String opcion : opciones) {
            comboCriterio.addItem(opcion);
        }
    }

    protected void vaciarTabla() {
        modeloTabla.setRowCount(0);
    }

    /** Fila seleccionada en el modelo, o -1 si no hay seleccion. */
    protected int filaSeleccionada() {
        return tabla.getSelectedRow();
    }

    /** Id del registro seleccionado, o null si no hay seleccion. */
    protected Integer idSeleccionado() {
        int fila = filaSeleccionada();
        if (fila < 0) {
            return null;
        }
        return aEntero(modeloTabla.getValueAt(fila, 0));
    }

    protected Integer aEntero(Object valor) {
        if (valor instanceof Number numero) {
            return numero.intValue();
        }
        if (valor == null) {
            return null;
        }
        try {
            return Integer.parseInt(valor.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected Object valorCelda(int fila, int columna) {
        return modeloTabla.getValueAt(fila, columna);
    }

    protected String textoCelda(int fila, int columna) {
        Object valor = valorCelda(fila, columna);
        return valor == null ? "" : valor.toString();
    }

    // -----------------------------------------------------------------
    // Busqueda, mensajes y acciones
    // -----------------------------------------------------------------

    protected String textoDeBusqueda() {
        return txtBuscar.getText() == null ? "" : txtBuscar.getText().trim();
    }

    protected String criterioSeleccionado() {
        Object valor = comboCriterio.getSelectedItem();
        return valor == null ? "" : valor.toString();
    }

    /** Indice en el modelo de la columna elegida en el combo (0 = todas). */
    protected int columnaDelCriterio() {
        return Math.max(0, comboCriterio.getSelectedIndex());
    }

    /** Se invoca cuando el usuario cambia de fila en la grilla. */
    protected void alSeleccionarFila(int filaEnModelo) {
        // Las subclases cargan los campos del formulario desde la grilla.
    }

    private void filaSeleccionadaCambiada(javax.swing.event.ListSelectionEvent evento) {
        if (!evento.getValueIsAdjusting()) {
            int fila = filaSeleccionada();
            if (fila >= 0) {
                alSeleccionarFila(fila);
            }
        }
    }

    protected void estado(String texto) {
        lblEstado.setText(" " + texto);
    }

    protected void informacion(String mensaje) {
        Mensajes.info(this, mensaje);
    }

    protected void error(String mensaje) {
        Mensajes.error(this, mensaje);
    }

    protected void advertencia(String mensaje) {
        Mensajes.advertencia(this, mensaje);
    }

    /** Pide confirmacion antes de eliminar (seccion 13). */
    protected boolean confirmarEliminacion(String mensaje) {
        return Mensajes.confirmar(this, mensaje);
    }

    protected void mostrarExcepcion(Exception e) {
        Mensajes.excepcion(this, e);
    }

    /** Accion del boton Salir: por defecto vuelve al menu principal. */
    protected void accionSalir() {
        accionDeSalir.run();
    }

    public void setAccionDeSalir(Runnable accion) {
        this.accionDeSalir = accion;
    }

    protected abstract void accionNuevo();

    protected abstract void accionGuardar();

    protected abstract void accionModificar();

    protected abstract void accionEliminar();

    protected abstract void accionBuscar();

    protected abstract void accionActualizar();

    /** Limpia los campos del formulario. */
    public abstract void limpiarFormulario();
}
