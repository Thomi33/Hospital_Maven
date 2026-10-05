package modelo;

import java.time.LocalDate;

/**
 * Equipo clinico - modulo principal del sistema (RF006).
 * La marca no se almacena: se deriva del modelo para respetar la 3FN.
 */
public record Equipo(Integer idEquipo, String nombre, Integer idTipoEquipo, Integer idModelo,
                     String numeroSerie, Integer garantiaMeses, Integer idPais,
                     Integer idProveedor, LocalDate fechaCompra, String codigoInterno,
                     Integer idUbicacion, String estado) {

    public static final String ACTIVO = "ACTIVO";
    public static final String INACTIVO = "INACTIVO";

    public boolean activo() {
        return ACTIVO.equals(estado);
    }

    @Override
    public String toString() {
        return codigoInterno + " - " + nombre;
    }
}
