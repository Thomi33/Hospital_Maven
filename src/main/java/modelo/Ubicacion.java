package modelo;

/**
 * Ubicacion fisica dentro de una institucion: CTI, Emergencia, etc. (RF007)
 */
public record Ubicacion(Integer idUbicacion, String nombre, String descripcion,
                        Integer idInstitucion) {

    @Override
    public String toString() {
        return nombre;
    }
}
