package dao;

import conexion.ConexionBD;
import modelo.Pais;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla PAISES (RF009 - CRUD completo).
 */
public class PaisDAO extends DAOBase {

    private static final String SQL_LISTAR =
            "SELECT id_pais, nombre FROM paises ORDER BY nombre";

    public List<Pais> listar() throws SQLException {
        List<Pais> paises = new ArrayList<>();
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                paises.add(new Pais(rs.getInt("id_pais"), rs.getString("nombre")));
            }
        }
        return paises;
    }

    public List<Pais> buscar(String texto) throws SQLException {
        List<Pais> paises = new ArrayList<>();
        String sql = "SELECT id_pais, nombre FROM paises WHERE nombre LIKE ? ORDER BY nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    paises.add(new Pais(rs.getInt("id_pais"), rs.getString("nombre")));
                }
            }
        }
        return paises;
    }

    public void insertar(Pais pais) throws SQLException {
        String sql = "INSERT INTO paises (nombre) VALUES (?)";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, pais.nombre());
            ps.executeUpdate();
        }
    }

    public void actualizar(Pais pais) throws SQLException {
        String sql = "UPDATE paises SET nombre = ? WHERE id_pais = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, pais.nombre());
            ps.setInt(2, pais.idPais());
            ps.executeUpdate();
        }
    }

    public void eliminar(int idPais) throws SQLException {
        String sql = "DELETE FROM paises WHERE id_pais = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPais);
            ps.executeUpdate();
        }
    }

    public boolean existeNombre(String nombre, Integer idExcluido) throws SQLException {
        String sql = "SELECT COUNT(*) FROM paises WHERE nombre = ? AND id_pais <> ?";
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
