package modelo;

/**
 * Marca de un equipo clinico (RF013).
 */
public record Marca(Integer idMarca, String nombre) {

    @Override
    public String toString() {
        return nombre;
    }
}
