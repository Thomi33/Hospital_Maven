package dao;

import conexion.ConexionBD;
import modelo.Perfil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla PERFILES (RF002) y a la relacion N:N con
 * funcionalidades (perfil_funcionalidad).
 */
public class PerfilDAO extends DAOBase {

    public List<Perfil> listar() throws SQLException {
        List<Perfil> perfiles = new ArrayList<>();
        String sql = "SELECT id_perfil, nombre, descripcion, estado FROM perfiles ORDER BY nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                perfiles.add(aPerfil(rs));
            }
        }
        return perfiles;
    }

    /** Solo los perfiles activos, para el combo del formulario de usuarios. */
    public List<Perfil> listarActivos() throws SQLException {
        List<Perfil> perfiles = new ArrayList<>();
        String sql = "SELECT id_perfil, nombre, descripcion, estado FROM perfiles "
                + "WHERE estado = 'ACTIVO' ORDER BY nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                perfiles.add(aPerfil(rs));
            }
        }
        return perfiles;
    }

    public List<Perfil> buscar(String texto) throws SQLException {
        List<Perfil> perfiles = new ArrayList<>();
        String sql = "SELECT id_perfil, nombre, descripcion, estado FROM perfiles "
                + "WHERE nombre LIKE ? OR descripcion LIKE ? ORDER BY nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    perfiles.add(aPerfil(rs));
                }
            }
        }
        return perfiles;
    }

    private Perfil aPerfil(ResultSet rs) throws SQLException {
        return new Perfil(rs.getInt("id_perfil"), rs.getString("nombre"),
                rs.getString("descripcion"), rs.getString("estado"));
    }

    public void insertar(Perfil perfil) throws SQLException {
        String sql = "INSERT INTO perfiles (nombre, descripcion, estado) VALUES (?, ?, ?)";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, perfil.nombre());
            ps.setString(2, perfil.descripcion());
            ps.setString(3, perfil.estado());
            ps.executeUpdate();
        }
    }

    public void actualizar(Perfil perfil) throws SQLException {
        String sql = "UPDATE perfiles SET nombre = ?, descripcion = ?, estado = ? WHERE id_perfil = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, perfil.nombre());
            ps.setString(2, perfil.descripcion());
            ps.setString(3, perfil.estado());
            ps.setInt(4, perfil.idPerfil());
            ps.executeUpdate();
        }
    }

    /** Baja logica: solo cambia el estado del perfil. */
    public void cambiarEstado(int idPerfil, String estado) throws SQLException {
        String sql = "UPDATE perfiles SET estado = ? WHERE id_perfil = ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idPerfil);
            ps.executeUpdate();
        }
    }

    public boolean existeNombre(String nombre, Integer idExcluido) throws SQLException {
        String sql = "SELECT COUNT(*) FROM perfiles WHERE nombre = ? AND id_perfil <> ?";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, idExcluido == null ? -1 : idExcluido);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // -----------------------------------------------------------------
    // Relacion N:N con funcionalidades
    // -----------------------------------------------------------------

    /** Nombres de las funcionalidades asignadas al perfil. */
    public List<String> funcionalidadesDelPerfil(int idPerfil) throws SQLException {
        List<String> nombres = new ArrayList<>();
        String sql = "SELECT f.nombre FROM funcionalidades f "
                + "JOIN perfil_funcionalidad pf ON pf.id_funcionalidad = f.id_funcionalidad "
                + "WHERE pf.id_perfil = ? ORDER BY f.nombre";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPerfil);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    nombres.add(rs.getString(1));
                }
            }
        }
        return nombres;
    }

    public void asignarFuncionalidad(int idPerfil, int idFuncionalidad) throws SQLException {
        String sql = "INSERT IGNORE INTO perfil_funcionalidad (id_perfil, id_funcionalidad) "
                + "VALUES (?, ?)";
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPerfil);
            ps.setInt(2, idFuncionalidad);
            ps.executeUpdate();
        }
    }
}
