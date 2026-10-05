package modelo;

/**
 * Perfil de acceso del sistema: nombre, descripcion y estado (RF002).
 */
public record Perfil(Integer idPerfil, String nombre, String descripcion, String estado) {

    public static final String ACTIVO = "ACTIVO";
    public static final String INACTIVO = "INACTIVO";

    public boolean activo() {
        return ACTIVO.equals(estado);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
