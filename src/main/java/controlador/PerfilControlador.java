package controlador;

import dao.AuditoriaDAO;
import dao.DAOBase;
import dao.FuncionalidadDAO;
import dao.PerfilDAO;
import modelo.Auditoria;
import modelo.Perfil;
import utilidades.Validaciones;

import java.sql.SQLException;
import java.util.List;

/** Controlador del CRUD de perfiles (RF002) y de sus funcionalidades. */
public class PerfilControlador {

    private final PerfilDAO dao = new PerfilDAO();
    private final FuncionalidadDAO funcionalidadDAO = new FuncionalidadDAO();
    private final AuditoriaDAO auditoria = new AuditoriaDAO();

    public List<Perfil> listar() {
        try {
            return dao.listar();
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    public List<Perfil> buscar(String texto) {
        try {
            return dao.buscar(texto);
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    public List<String> funcionalidadesDelPerfil(Integer idPerfil) {
        if (idPerfil == null) {
            return List.of();
        }
        try {
            return dao.funcionalidadesDelPerfil(idPerfil);
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    public String asignarFuncionalidad(Integer idPerfil, Integer idFuncionalidad) {
        if (idPerfil == null) {
            return "Seleccione un perfil de la lista.";
        }
        if (idFuncionalidad == null) {
            return "Seleccione una funcionalidad.";
        }
        try {
            dao.asignarFuncionalidad(idPerfil, idFuncionalidad);
            auditoria.registrar(Auditoria.MODIFICACION, "perfil_funcionalidad",
                    "Funcionalidad #" + idFuncionalidad + " asignada al perfil #" + idPerfil);
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    public String guardar(String nombre, String descripcion, String estado) {
        String error = Validaciones.primerError(
                Validaciones.requerido("Nombre", nombre),
                Validaciones.longitudMaxima("Nombre", nombre, 60),
                Validaciones.requerido("Descripción", descripcion),
                Validaciones.longitudMaxima("Descripción", descripcion, 150));
        if (error != null) {
            return error;
        }
        try {
            if (dao.existeNombre(nombre.trim(), null)) {
                return "Ya existe un perfil con el nombre \"" + nombre.trim() + "\".";
            }
            dao.insertar(new Perfil(null, nombre.trim(), descripcion.trim(), estado));
            auditoria.registrar(Auditoria.ALTA, "perfiles", "Perfil creado: " + nombre.trim());
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    public String modificar(Integer idPerfil, String nombre, String descripcion, String estado) {
        if (idPerfil == null) {
            return "Seleccione un perfil de la lista antes de modificar.";
        }
        String error = Validaciones.primerError(
                Validaciones.requerido("Nombre", nombre),
                Validaciones.longitudMaxima("Nombre", nombre, 60),
                Validaciones.requerido("Descripción", descripcion),
                Validaciones.longitudMaxima("Descripción", descripcion, 150));
        if (error != null) {
            return error;
        }
        try {
            if (dao.existeNombre(nombre.trim(), idPerfil)) {
                return "Ya existe otro perfil con el nombre \"" + nombre.trim() + "\".";
            }
            dao.actualizar(new Perfil(idPerfil, nombre.trim(), descripcion.trim(), estado));
            auditoria.registrar(Auditoria.MODIFICACION, "perfiles",
                    "Perfil #" + idPerfil + " modificado: " + nombre.trim());
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    /** Baja logica del perfil (RF002). */
    public String eliminar(Integer idPerfil) {
        if (idPerfil == null) {
            return "Seleccione un perfil de la lista antes de dar de baja.";
        }
        try {
            dao.cambiarEstado(idPerfil, Perfil.INACTIVO);
            auditoria.registrar(Auditoria.BAJA, "perfiles", "Perfil #" + idPerfil + " dado de baja");
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    /** Cantidad de funcionalidades activas (para mensajes de la interfaz). */
    public int cantidadDeFuncionalidades() {
        try {
            return funcionalidadDAO.listarActivas().size();
        } catch (SQLException e) {
            return 0;
        }
    }
}
