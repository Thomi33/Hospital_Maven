package modelo;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Registro de auditoria del sistema (RF004).
 * El usuario se guarda como clave foranea y se resuelve por JOIN (3FN).
 */
public record Auditoria(Long idAuditoria, Integer idUsuario, LocalDate fecha, LocalTime hora,
                        String operacion, String tabla, String detalle) {

    public static final String LOGIN = "LOGIN";
    public static final String ALTA = "ALTA";
    public static final String MODIFICACION = "MODIFICACION";
    public static final String BAJA = "BAJA";
    public static final String BORRADO = "BORRADO";
    public static final String CONSULTA = "CONSULTA";
}
