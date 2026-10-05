package dao;

import conexion.ConexionBD;
import modelo.Marca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla MARCAS (RF013 - CRUD completo).
 */
public class MarcaDAO extends DAOBase {

    public List<Marca> listar() throws SQLException {
        List<Marca> marcas = new ArrayList<>();
        String sql = "SELECT id_marca, nombre FROM marcas ORDER BY nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                marcas.add(new Marca(rs.getInt("id_marca"), rs.getString("nombre")));
            }
        }
        return marcas;
    }

    public List<Marca> buscar(String texto) throws SQLException {
        List<Marca> marcas = new ArrayList<>();
        String sql = "SELECT id_marca, nombre FROM marcas WHERE nombre LIKE ? ORDER BY nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    marcas.add(new Marca(rs.getInt("id_marca"), rs.getString("nombre")));
                }
            }
        }
        return marcas;
    }

    public void insertar(Marca marca) throws SQLException {
        String sql = "INSERT INTO marcas (nombre) VALUES (?)";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, marca.nombre());
            ps.executeUpdate();
        }
    }

    public void actualizar(Marca marca) throws SQLException {
        String sql = "UPDATE marcas SET nombre = ? WHERE id_marca = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, marca.nombre());
            ps.setInt(2, marca.idMarca());
            ps.executeUpdate();
        }
    }

    public void eliminar(int idMarca) throws SQLException {
        String sql = "DELETE FROM marcas WHERE id_marca = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMarca);
            ps.executeUpdate();
        }
    }

    public boolean existeNombre(String nombre, Integer idExcluido) throws SQLException {
        String sql = "SELECT COUNT(*) FROM marcas WHERE nombre = ? AND id_marca <> ?";
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
