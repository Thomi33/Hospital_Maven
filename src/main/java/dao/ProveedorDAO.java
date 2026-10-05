package dao;

import conexion.ConexionBD;
import modelo.Proveedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla PROVEEDORES (RF011 - CRUD completo).
 */
public class ProveedorDAO extends DAOBase {

    public List<Proveedor> listar() throws SQLException {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT id_proveedor, nombre, direccion, telefono, email "
                + "FROM proveedores ORDER BY nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                proveedores.add(new Proveedor(rs.getInt("id_proveedor"), rs.getString("nombre"),
                        rs.getString("direccion"), rs.getString("telefono"), rs.getString("email")));
            }
        }
        return proveedores;
    }

    public List<Proveedor> buscar(String texto) throws SQLException {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT id_proveedor, nombre, direccion, telefono, email FROM proveedores "
                + "WHERE nombre LIKE ? OR email LIKE ? OR direccion LIKE ? ORDER BY nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");
            ps.setString(3, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    proveedores.add(new Proveedor(rs.getInt("id_proveedor"), rs.getString("nombre"),
                            rs.getString("direccion"), rs.getString("telefono"), rs.getString("email")));
                }
            }
        }
        return proveedores;
    }

    public void insertar(Proveedor proveedor) throws SQLException {
        String sql = "INSERT INTO proveedores (nombre, direccion, telefono, email) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, proveedor.nombre());
            ps.setString(2, proveedor.direccion());
            ps.setString(3, proveedor.telefono());
            ps.setString(4, proveedor.email());
            ps.executeUpdate();
        }
    }

    public void actualizar(Proveedor proveedor) throws SQLException {
        String sql = "UPDATE proveedores SET nombre = ?, direccion = ?, telefono = ?, email = ? "
                + "WHERE id_proveedor = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, proveedor.nombre());
            ps.setString(2, proveedor.direccion());
            ps.setString(3, proveedor.telefono());
            ps.setString(4, proveedor.email());
            ps.setInt(5, proveedor.idProveedor());
            ps.executeUpdate();
        }
    }

    public void eliminar(int idProveedor) throws SQLException {
        String sql = "DELETE FROM proveedores WHERE id_proveedor = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            ps.executeUpdate();
        }
    }

    public boolean existeNombre(String nombre, Integer idExcluido) throws SQLException {
        String sql = "SELECT COUNT(*) FROM proveedores WHERE nombre = ? AND id_proveedor <> ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, idExcluido == null ? -1 : idExcluido);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public boolean existeEmail(String email, Integer idExcluido) throws SQLException {
        String sql = "SELECT COUNT(*) FROM proveedores WHERE email = ? AND id_proveedor <> ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, idExcluido == null ? -1 : idExcluido);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
