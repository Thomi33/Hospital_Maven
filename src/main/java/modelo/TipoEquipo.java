package modelo;

/**
 * Tipo o categoria de equipo (RF010).
 */
public record TipoEquipo(Integer idTipoEquipo, String nombre, String descripcion) {

    @Override
    public String toString() {
        return nombre;
    }
}
