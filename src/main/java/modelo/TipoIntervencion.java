package modelo;

/**
 * Tipo de intervencion: preventiva, correctiva, instalacion, etc. (RF014)
 */
public record TipoIntervencion(Integer idTipoIntervencion, String nombre, String descripcion) {

    @Override
    public String toString() {
        return nombre;
    }
}
