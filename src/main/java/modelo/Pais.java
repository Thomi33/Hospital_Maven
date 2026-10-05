package modelo;

/**
 * Pais de origen de los equipos y de las instituciones (RF009).
 */
public record Pais(Integer idPais, String nombre) {

    @Override
    public String toString() {
        return nombre;
    }
}
