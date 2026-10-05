package dao;

import conexion.ConexionBD;
import modelo.Funcionalidad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla FUNCIONALIDADES (RF003 - alta, baja, modificacion,
 * consulta).
 */
public class FuncionalidadDAO extends DAOBase {

    public List<Funcionalidad> listar() throws SQLException {
        List<Funcionalidad> lista = new ArrayList<>();
        String sql = "SELECT id_funcionalidad, nombre, descripcion, estado "
                + "FROM funcionalidades ORDER BY nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Funcionalidad(rs.getInt("id_funcionalidad"), rs.getString("nombre"),
                        rs.getString("descripcion"), rs.getString("estado")));
            }
        }
        return lista;
    }

    public List<Funcionalidad> listarActivas() throws SQLException {
        List<Funcionalidad> lista = new ArrayList<>();
        String sql = "SELECT id_funcionalidad, nombre, descripcion, estado FROM funcionalidades "
                + "WHERE estado = 'ACTIVO' ORDER BY nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Funcionalidad(rs.getInt("id_funcionalidad"), rs.getString("nombre"),
                        rs.getString("descripcion"), rs.getString("estado")));
            }
        }
        return lista;
    }

    public List<Funcionalidad> buscar(String texto) throws SQLException {
        List<Funcionalidad> lista = new ArrayList<>();
        String sql = "SELECT id_funcionalidad, nombre, descripcion, estado FROM funcionalidades "
                + "WHERE nombre LIKE ? OR descripcion LIKE ? ORDER BY nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Funcionalidad(rs.getInt("id_funcionalidad"), rs.getString("nombre"),
                            rs.getString("descripcion"), rs.getString("estado")));
                }
            }
        }
        return lista;
    }

    public void insertar(Funcionalidad funcionalidad) throws SQLException {
        String sql = "INSERT INTO funcionalidades (nombre, descripcion, estado) VALUES (?, ?, ?)";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, funcionalidad.nombre());
            ps.setString(2, funcionalidad.descripcion());
            ps.setString(3, funcionalidad.estado());
            ps.executeUpdate();
        }
    }

    public void actualizar(Funcionalidad funcionalidad) throws SQLException {
        String sql = "UPDATE funcionalidades SET nombre = ?, descripcion = ?, estado = ? "
                + "WHERE id_funcionalidad = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, funcionalidad.nombre());
            ps.setString(2, funcionalidad.descripcion());
            ps.setString(3, funcionalidad.estado());
            ps.setInt(4, funcionalidad.idFuncionalidad());
            ps.executeUpdate();
        }
    }

    /** Baja logica de la funcionalidad. */
    public void cambiarEstado(int idFuncionalidad, String estado) throws SQLException {
        String sql = "UPDATE funcionalidades SET estado = ? WHERE id_funcionalidad = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idFuncionalidad);
            ps.executeUpdate();
        }
    }

    public boolean existeNombre(String nombre, Integer idExcluido) throws SQLException {
        String sql = "SELECT COUNT(*) FROM funcionalidades WHERE nombre = ? "
                + "AND id_funcionalidad <> ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, idExcluido == null ? -1 : idExcluido);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
