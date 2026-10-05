package vista;

import controlador.ReporteControlador;
import modelo.Informe;

import javax.swing.JButton;
import javax.swing.JComboBox;
import java.util.List;

/**
 * Modulo de consultas complejas: JOIN, GROUP BY y funciones de
 * agregacion (objetivos especificos del proyecto / RNF010).
 */
public class FormularioReportes extends FormularioCRUD {

    private final ReporteControlador controlador = new ReporteControlador();
    private final JComboBox<String> comboInforme = new JComboBox<>();
    private final JButton btnEjecutar = new JButton("Ejecutar informe");

    private Informe informeActual;

    public FormularioReportes() {
        super(new String[]{"(Seleccione un informe y pulse Ejecutar)"});
        agregarCampo("Informe:", comboInforme);
        agregarCampo("", btnEjecutar);

        btnNuevo.setEnabled(false);
        btnGuardar.setEnabled(false);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnGuardar.setToolTipText("Este módulo solo ejecuta consultas de lectura.");
        btnNuevo.setToolTipText("Este módulo solo ejecuta consultas de lectura.");
        btnModificar.setToolTipText("Este módulo solo ejecuta consultas de lectura.");
        btnEliminar.setToolTipText("Este módulo solo ejecuta consultas de lectura.");

        for (String nombre : controlador.nombresDeInformes()) {
            comboInforme.addItem(nombre);
        }
        btnEjecutar.addActionListener(e -> ejecutarSeleccionado());
        btnBuscar.addActionListener(e -> ejecutarSeleccionado());
        estado("Seleccione un informe y pulse Ejecutar informe.");
    }

    private void ejecutarSeleccionado() {
        Object seleccion = comboInforme.getSelectedItem();
        if (seleccion == null) {
            advertencia("Debe seleccionar un informe.");
            return;
        }
        String criterio = criterioSeleccionado();
        try {
            Informe informe = controlador.ejecutar(seleccion.toString());
            informeActual = informe;
            definirColumnas(informe.columnas().toArray(new String[0]));
            List<Object[]> filas = new java.util.ArrayList<>();
            for (Object[] fila : informe.filas()) {
                Object[] completa = new Object[fila.length + 1];
                System.arraycopy(fila, 0, completa, 1, fila.length);
                filas.add(completa);
            }
            cargarFilas(filas);
            comboCriterio.setSelectedItem(criterio);
            estado("Informe: " + informe.nombre() + " | filas: " + informe.cantidadDeFilas());
        } catch (RuntimeException e) {
            mostrarExcepcion(e);
        }
    }

    @Override
    protected void accionNuevo() {
        advertencia("Este módulo ejecuta consultas SQL de lectura: no crea registros.");
    }

    @Override
    protected void accionGuardar() {
        accionNuevo();
    }

    @Override
    protected void accionModificar() {
        advertencia("Este módulo ejecuta consultas SQL de lectura: no modifica registros.");
    }

    @Override
    protected void accionEliminar() {
        advertencia("Este módulo ejecuta consultas SQL de lectura: no elimina registros.");
    }

    @Override
    protected void accionBuscar() {
        ejecutarSeleccionado();
    }

    @Override
    protected void accionActualizar() {
        if (informeActual == null) {
            ejecutarSeleccionado();
        } else {
            comboInforme.setSelectedItem(informeActual.nombre());
            ejecutarSeleccionado();
        }
    }

    @Override
    public void limpiarFormulario() {
        txtBuscar.setText("");
    }
}
