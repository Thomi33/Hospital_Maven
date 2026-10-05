package modelo;

import java.time.LocalDate;

/**
 * Intervencion (mantenimiento, calibracion, etc.) realizada sobre un
 * equipo (RF005).
 */
public record Intervencion(Integer idIntervencion, Integer idEquipo, Integer idTipoIntervencion,
                           LocalDate fecha, String tecnicoResponsable, String observaciones) {

    @Override
    public String toString() {
        return "#" + idIntervencion;
    }
}
