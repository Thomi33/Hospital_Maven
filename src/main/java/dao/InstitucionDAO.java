package dao;

import conexion.ConexionBD;
import modelo.Institucion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla INSTITUCIONES (RF008 - CRUD opcional, tabla obligatoria).
 */
public class InstitucionDAO extends DAOBase {

    public List<Institucion> listar() throws SQLException {
        List<Institucion> instituciones = new ArrayList<>();
        String sql = "SELECT id_institucion, nombre, direccion, telefono, ciudad, id_pais "
                + "FROM instituciones ORDER BY nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                instituciones.add(new Institucion(rs.getInt("id_institucion"), rs.getString("nombre"),
                        rs.getString("direccion"), rs.getString("telefono"), rs.getString("ciudad"),
                        rs.getInt("id_pais")));
            }
        }
        return instituciones;
    }

    /** Listado para la grilla con el nombre del pais (JOIN). */
    public List<Object[]> listarFilas() throws SQLException {
        List<Object[]> filas = new ArrayList<>();
        String sql = "SELECT i.id_institucion, i.nombre, i.direccion, i.telefono, i.ciudad, p.nombre "
                + "FROM instituciones i JOIN paises p ON p.id_pais = i.id_pais ORDER BY i.nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                filas.add(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getString(6)});
            }
        }
        return filas;
    }

    public List<Object[]> buscar(String texto) throws SQLException {
        List<Object[]> filas = new ArrayList<>();
        String sql = "SELECT i.id_institucion, i.nombre, i.direccion, i.telefono, i.ciudad, p.nombre "
                + "FROM instituciones i JOIN paises p ON p.id_pais = i.id_pais "
                + "WHERE i.nombre LIKE ? OR i.ciudad LIKE ? OR p.nombre LIKE ? ORDER BY i.nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");
            ps.setString(3, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filas.add(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getString(5), rs.getString(6)});
                }
            }
        }
        return filas;
    }

    public void insertar(Institucion institucion) throws SQLException {
        String sql = "INSERT INTO instituciones (nombre, direccion, telefono, ciudad, id_pais) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, institucion.nombre());
            ps.setString(2, institucion.direccion());
            ps.setString(3, institucion.telefono());
            ps.setString(4, institucion.ciudad());
            ps.setInt(5, institucion.idPais());
            ps.executeUpdate();
        }
    }

    public void actualizar(Institucion institucion) throws SQLException {
        String sql = "UPDATE instituciones SET nombre = ?, direccion = ?, telefono = ?, ciudad = ?, "
                + "id_pais = ? WHERE id_institucion = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, institucion.nombre());
            ps.setString(2, institucion.direccion());
            ps.setString(3, institucion.telefono());
            ps.setString(4, institucion.ciudad());
            ps.setInt(5, institucion.idPais());
            ps.setInt(6, institucion.idInstitucion());
            ps.executeUpdate();
        }
    }

    public void eliminar(int idInstitucion) throws SQLException {
        String sql = "DELETE FROM instituciones WHERE id_institucion = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idInstitucion);
            ps.executeUpdate();
        }
    }

    public boolean existeNombre(String nombre, Integer idExcluido) throws SQLException {
        String sql = "SELECT COUNT(*) FROM instituciones WHERE nombre = ? AND id_institucion <> ?";
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
