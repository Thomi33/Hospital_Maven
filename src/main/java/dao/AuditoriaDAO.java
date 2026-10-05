package dao;

import conexion.ConexionBD;
import utilidades.Sesion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla AUDITORIA (RF004).
 * Cada operacion relevante realizada desde la interfaz deja un registro
 * con usuario, fecha, hora y operacion realizada.
 */
public class AuditoriaDAO extends DAOBase {

    /** Registra una operacion usando el usuario de la sesion actual. */
    public void registrar(String operacion, String tabla, String detalle) throws SQLException {
        String sql = "INSERT INTO auditoria (id_usuario, fecha, hora, operacion, tabla, detalle) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            Integer idUsuario = Sesion.idUsuario();
            if (idUsuario == null) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, idUsuario);
            }
            ps.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            ps.setTime(3, java.sql.Time.valueOf(LocalTime.now().withNano(0)));
            ps.setString(4, operacion);
            ps.setString(5, tabla);
            ps.setString(6, detalle);
            ps.executeUpdate();
        }
    }

    /** Listado para la grilla: 7 columnas (la primera es el id oculto). */
    public List<Object[]> listarFilas() throws SQLException {
        return consultar(sqlBase(), new String[0]);
    }

    /**
     * Busqueda por texto libre y/o por tipo de operacion.
     * Si operacion es "TODAS" solo se aplica el texto.
     */
    public List<Object[]> buscar(String texto, String operacion) throws SQLException {
        boolean todas = operacion == null || operacion.isBlank() || "TODAS".equals(operacion);
        String sql = sqlBase() + (todas
                ? "WHERE a.detalle LIKE ? OR u.usuario LIKE ? OR a.tabla LIKE ? "
                : "WHERE a.operacion = ? AND (a.detalle LIKE ? OR u.usuario LIKE ?) ");
        String patron = "%" + texto + "%";
        return consultar(sql, todas
                ? new String[]{patron, patron, patron}
                : new String[]{operacion, patron, patron});
    }

    private String sqlBase() {
        return "SELECT a.id_auditoria, a.fecha, a.hora, u.usuario, a.operacion, a.tabla, a.detalle "
                + "FROM auditoria a LEFT JOIN usuarios u ON u.id_usuario = a.id_usuario ";
    }

    private List<Object[]> consultar(String sql, String[] parametros) throws SQLException {
        List<Object[]> filas = new ArrayList<>();
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setString(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filas.add(new Object[]{rs.getLong(1),
                            String.valueOf(rs.getDate(2).toLocalDate()),
                            rs.getTime(3).toLocalTime().withNano(0).toString(),
                            rs.getString(4) == null ? "(sin usuario)" : rs.getString(4),
                            rs.getString(5), rs.getString(6), rs.getString(7)});
                }
            }
        }
        return filas;
    }

    /** Cantidad total de registros de auditoria. */
    public long total() throws SQLException {
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement("SELECT COUNT(*) FROM auditoria");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        }
    }
}
