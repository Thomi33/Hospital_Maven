package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;

/**
 * Base comun de los DAO: traduccion de errores SQL a mensajes claros
 * para el usuario (RNF005) y utilidades de mapeo de ResultSet.
 */
public abstract class DAOBase {

    protected DAOBase() {
    }

    /**
     * Traduce una excepcion de Base de Datos a un mensaje comprensible
     * para el usuario final.
     */
    public static String mensajeError(Exception e) {
        if (e instanceof SQLIntegrityConstraintViolationException) {
            return "La operacion no se pudo completar porque viola una restriccion de integridad "
                    + "de la Base de Datos: el registro esta relacionado con otros datos "
                    + "o ya existe un valor identico.\nDetalle: " + e.getMessage();
        }
        if (e instanceof SQLException sq) {
            int codigo = sq.getErrorCode();
            if (codigo == 1062) {
                return "Ya existe un registro con ese valor (dato duplicado).";
            }
            if (codigo == 1451) {
                return "No se puede eliminar: otros registros dependen de este. "
                        + "Modifique o elimine primero esos registros.";
            }
            if (codigo == 1452) {
                return "No se puede guardar: la referencia seleccionada no existe en la Base de Datos.";
            }
            if (codigo == 1045) {
                return "Acceso denegado: usuario o contrasena de Base de Datos incorrectos.";
            }
            if (codigo == 0 || codigo == 1049) {
                return "No se pudo conectar con la Base de Datos \"hospital\". "
                        + "Verifique que el servidor MySQL este activo.\nDetalle: " + sq.getMessage();
            }
            return "Error de Base de Datos (" + codigo + "): " + sq.getMessage();
        }
        return "Error inesperado: " + e.getMessage();
    }

    /** Lee una columna DATE como LocalDate (devuelve null si es NULL). */
    protected static LocalDate fecha(ResultSet rs, String columna) throws SQLException {
        java.sql.Date valor = rs.getDate(columna);
        return valor == null ? null : valor.toLocalDate();
    }
}
