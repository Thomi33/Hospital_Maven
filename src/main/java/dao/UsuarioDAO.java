package dao;

import conexion.ConexionBD;
import modelo.Usuario;
import utilidades.HashUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla USUARIOS (RF001 - CRUD y login).
 */
public class UsuarioDAO extends DAOBase {

    /** Filas para la grilla: incluye el nombre del perfil (JOIN). */
    public List<Object[]> listarFilas() throws SQLException {
        String sql = "SELECT u.id_usuario, u.nombre, u.apellido, u.cedula, u.fecha_nacimiento, "
                + "u.telefono, u.email, u.usuario, p.nombre AS perfil, u.estado "
                + "FROM usuarios u JOIN perfiles p ON p.id_perfil = u.id_perfil "
                + "ORDER BY u.apellido, u.nombre";
        return consultarFilas(sql, null, null, null, null, null);
    }

    public List<Object[]> buscar(String texto) throws SQLException {
        String sql = "SELECT u.id_usuario, u.nombre, u.apellido, u.cedula, u.fecha_nacimiento, "
                + "u.telefono, u.email, u.usuario, p.nombre AS perfil, u.estado "
                + "FROM usuarios u JOIN perfiles p ON p.id_perfil = u.id_perfil "
                + "WHERE u.nombre LIKE ? OR u.apellido LIKE ? OR u.usuario LIKE ? "
                + "OR u.cedula LIKE ? OR u.email LIKE ? ORDER BY u.apellido, u.nombre";
        String patron = "%" + texto + "%";
        return consultarFilas(sql, patron, patron, patron, patron, patron);
    }

    private List<Object[]> consultarFilas(String sql, String p1, String p2, String p3,
                                          String p4, String p5) throws SQLException {
        List<Object[]> filas = new ArrayList<>();
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            if (p1 != null) {
                ps.setString(1, p1);
                ps.setString(2, p2);
                ps.setString(3, p3);
                ps.setString(4, p4);
                ps.setString(5, p5);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filas.add(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), String.valueOf(rs.getDate(5).toLocalDate()),
                            rs.getString(6), rs.getString(7), rs.getString(8),
                            rs.getString(9), rs.getString(10)});
                }
            }
        }
        return filas;
    }

    /** Busca un usuario por su identificador (para editar). */
    public Usuario buscarPorId(int idUsuario) throws SQLException {
        String sql = "SELECT id_usuario, nombre, apellido, cedula, fecha_nacimiento, telefono, "
                + "email, usuario, contrasena, id_perfil, estado FROM usuarios WHERE id_usuario = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? aUsuario(rs) : null;
            }
        }
    }

    /**
     * Valida usuario y contrasena contra la tabla usuarios (RF001).
     * Devuelve el usuario activo o null si las credenciales no son validas.
     */
    public Usuario validarLogin(String usuario, String contrasena) throws SQLException {
        String sql = "SELECT id_usuario, nombre, apellido, cedula, fecha_nacimiento, telefono, "
                + "email, usuario, contrasena, id_perfil, estado FROM usuarios "
                + "WHERE usuario = ? AND contrasena = ? AND estado = 'ACTIVO'";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, HashUtil.sha256(contrasena));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? aUsuario(rs) : null;
            }
        }
    }

    public void insertar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, apellido, cedula, fecha_nacimiento, telefono, "
                + "email, usuario, contrasena, id_perfil, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, usuario.nombre());
            ps.setString(2, usuario.apellido());
            ps.setString(3, usuario.cedula());
            ps.setDate(4, java.sql.Date.valueOf(usuario.fechaNacimiento()));
            ps.setString(5, usuario.telefono());
            ps.setString(6, usuario.email());
            ps.setString(7, usuario.usuario());
            ps.setString(8, usuario.contrasena());
            ps.setInt(9, usuario.idPerfil());
            ps.setString(10, usuario.estado());
            ps.executeUpdate();
        }
    }

    public void actualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nombre = ?, apellido = ?, cedula = ?, fecha_nacimiento = ?, "
                + "telefono = ?, email = ?, usuario = ?, contrasena = ?, id_perfil = ?, estado = ? "
                + "WHERE id_usuario = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, usuario.nombre());
            ps.setString(2, usuario.apellido());
            ps.setString(3, usuario.cedula());
            ps.setDate(4, java.sql.Date.valueOf(usuario.fechaNacimiento()));
            ps.setString(5, usuario.telefono());
            ps.setString(6, usuario.email());
            ps.setString(7, usuario.usuario());
            ps.setString(8, usuario.contrasena());
            ps.setInt(9, usuario.idPerfil());
            ps.setString(10, usuario.estado());
            ps.setInt(11, usuario.idUsuario());
            ps.executeUpdate();
        }
    }

    /** Baja logica: el usuario sigue existiendo pero no puede ingresar. */
    public void cambiarEstado(int idUsuario, String estado) throws SQLException {
        String sql = "UPDATE usuarios SET estado = ? WHERE id_usuario = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        }
    }

    public boolean existeUsuario(String usuario, Integer idExcluido) throws SQLException {
        return contar("SELECT COUNT(*) FROM usuarios WHERE usuario = ? AND id_usuario <> ?",
                usuario, idExcluido);
    }

    public boolean existeCedula(String cedula, Integer idExcluido) throws SQLException {
        return contar("SELECT COUNT(*) FROM usuarios WHERE cedula = ? AND id_usuario <> ?",
                cedula, idExcluido);
    }

    public boolean existeEmail(String email, Integer idExcluido) throws SQLException {
        return contar("SELECT COUNT(*) FROM usuarios WHERE email = ? AND id_usuario <> ?",
                email, idExcluido);
    }

    private boolean contar(String sql, String valor, Integer idExcluido) throws SQLException {
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, valor);
            ps.setInt(2, idExcluido == null ? -1 : idExcluido);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private Usuario aUsuario(ResultSet rs) throws SQLException {
        return new Usuario(rs.getInt("id_usuario"), rs.getString("nombre"), rs.getString("apellido"),
                rs.getString("cedula"), fecha(rs, "fecha_nacimiento"), rs.getString("telefono"),
                rs.getString("email"), rs.getString("usuario"), rs.getString("contrasena"),
                rs.getInt("id_perfil"), rs.getString("estado"));
    }
}
