package modelo;

/**
 * Institucion de salud propietaria del equipamiento (RF008).
 */
public record Institucion(Integer idInstitucion, String nombre, String direccion,
                          String telefono, String ciudad, Integer idPais) {

    @Override
    public String toString() {
        return nombre;
    }
}
