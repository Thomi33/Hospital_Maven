package modelo;

import java.util.Collections;
import java.util.List;

/**
 * Resultado de una consulta SQL compleja (JOIN / GROUP BY / agregaciones)
 * pensado para ser volcado directamente en un JTable.
 */
public record Informe(String nombre, List<String> columnas, List<Object[]> filas) {

    public Informe {
        columnas = Collections.unmodifiableList(columnas);
        filas = Collections.unmodifiableList(filas);
    }

    public int cantidadDeFilas() {
        return filas.size();
    }
}
