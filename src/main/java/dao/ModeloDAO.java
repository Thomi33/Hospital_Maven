package dao;

import conexion.ConexionBD;
import modelo.Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla MODELOS (RF012 - CRUD completo).
 * Cada modelo esta siempre asociado a una marca (clave foranea).
 */
public class ModeloDAO extends DAOBase {

    public List<Modelo> listar() throws SQLException {
        List<Modelo> modelos = new ArrayList<>();
        String sql = "SELECT id_modelo, nombre, id_marca FROM modelos ORDER BY nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modelos.add(new Modelo(rs.getInt("id_modelo"), rs.getString("nombre"),
                        rs.getInt("id_marca")));
            }
        }
        return modelos;
    }

    /** Listado para la grilla: incluye el nombre de la marca por JOIN. */
    public List<Object[]> listarFilas() throws SQLException {
        List<Object[]> filas = new ArrayList<>();
        String sql = "SELECT mo.id_modelo, mo.nombre, ma.nombre AS marca "
                + "FROM modelos mo JOIN marcas ma ON ma.id_marca = mo.id_marca "
                + "ORDER BY ma.nombre, mo.nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                filas.add(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3)});
            }
        }
        return filas;
    }

    public List<Object[]> buscar(String texto) throws SQLException {
        List<Object[]> filas = new ArrayList<>();
        String sql = "SELECT mo.id_modelo, mo.nombre, ma.nombre AS marca "
                + "FROM modelos mo JOIN marcas ma ON ma.id_marca = mo.id_marca "
                + "WHERE mo.nombre LIKE ? OR ma.nombre LIKE ? "
                + "ORDER BY ma.nombre, mo.nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filas.add(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3)});
                }
            }
        }
        return filas;
    }

    public void insertar(Modelo modelo) throws SQLException {
        String sql = "INSERT INTO modelos (nombre, id_marca) VALUES (?, ?)";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, modelo.nombre());
            ps.setInt(2, modelo.idMarca());
            ps.executeUpdate();
        }
    }

    public void actualizar(Modelo modelo) throws SQLException {
        String sql = "UPDATE modelos SET nombre = ?, id_marca = ? WHERE id_modelo = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, modelo.nombre());
            ps.setInt(2, modelo.idMarca());
            ps.setInt(3, modelo.idModelo());
            ps.executeUpdate();
        }
    }

    public void eliminar(int idModelo) throws SQLException {
        String sql = "DELETE FROM modelos WHERE id_modelo = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idModelo);
            ps.executeUpdate();
        }
    }

    /** True si ya existe un modelo con ese nombre dentro de la marca. */
    public boolean existeNombre(String nombre, Integer idMarca, Integer idExcluido) throws SQLException {
        String sql = "SELECT COUNT(*) FROM modelos WHERE nombre = ? AND id_marca = ? AND id_modelo <> ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, idMarca);
            ps.setInt(3, idExcluido == null ? -1 : idExcluido);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
