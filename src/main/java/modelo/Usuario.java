package modelo;

/**
 * Usuario del sistema (RF001). La contrasena se guarda hasheada (SHA-256).
 */
public record Usuario(Integer idUsuario, String nombre, String apellido, String cedula,
                      java.time.LocalDate fechaNacimiento, String telefono, String email,
                      String usuario, String contrasena, Integer idPerfil, String estado) {

    public static final String ACTIVO = "ACTIVO";
    public static final String INACTIVO = "INACTIVO";

    /** Nombre completo, util para la auditoria y los mensajes. */
    public String nombreCompleto() {
        return nombre + " " + apellido;
    }

    @Override
    public String toString() {
        return usuario;
    }
}
