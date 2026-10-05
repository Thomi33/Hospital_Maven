package modelo;

/**
 * Modelo de equipo, siempre asociado a una marca (RF012).
 */
public record Modelo(Integer idModelo, String nombre, Integer idMarca) {

    @Override
    public String toString() {
        return nombre;
    }
}
