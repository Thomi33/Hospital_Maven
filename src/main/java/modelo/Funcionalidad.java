package modelo;

/**
 * Funcionalidad (permiso) disponible en el sistema (RF003).
 */
public record Funcionalidad(Integer idFuncionalidad, String nombre, String descripcion,
                            String estado) {

    public static final String ACTIVO = "ACTIVO";
    public static final String INACTIVO = "INACTIVO";

    @Override
    public String toString() {
        return nombre;
    }
}
