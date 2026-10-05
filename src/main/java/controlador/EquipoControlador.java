package controlador;

import dao.AuditoriaDAO;
import dao.DAOBase;
import dao.EquipoDAO;
import dao.MarcaDAO;
import dao.ModeloDAO;
import dao.PaisDAO;
import dao.ProveedorDAO;
import dao.TipoEquipoDAO;
import dao.UbicacionDAO;
import modelo.Auditoria;
import modelo.Equipo;
import modelo.Marca;
import modelo.Modelo;
import modelo.Pais;
import modelo.Proveedor;
import modelo.TipoEquipo;
import modelo.Ubicacion;
import utilidades.Validaciones;

import java.sql.SQLException;
import java.util.List;

/** Controlador del modulo principal de equipos (RF006). */
public class EquipoControlador {

    private final EquipoDAO dao = new EquipoDAO();
    private final TipoEquipoDAO tipoDAO = new TipoEquipoDAO();
    private final MarcaDAO marcaDAO = new MarcaDAO();
    private final ModeloDAO modeloDAO = new ModeloDAO();
    private final PaisDAO paisDAO = new PaisDAO();
    private final ProveedorDAO proveedorDAO = new ProveedorDAO();
    private final UbicacionDAO ubicacionDAO = new UbicacionDAO();
    private final AuditoriaDAO auditoria = new AuditoriaDAO();

    public List<Object[]> listar() {
        try {
            return dao.listarFilas();
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    public List<Object[]> buscar(String texto) {
        try {
            return dao.buscar(texto);
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    public Equipo equipoPorId(Integer idEquipo) {
        if (idEquipo == null) {
            return null;
        }
        try {
            return dao.buscarPorId(idEquipo);
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    // --- datos para los combos del formulario ---

    public List<TipoEquipo> tiposDeEquipo() {
        try {
            return tipoDAO.listar();
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    public List<Marca> marcas() {
        try {
            return marcaDAO.listar();
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    public List<Modelo> modelosDeLaMarca(Integer idMarca) {
        if (idMarca == null) {
            return List.of();
        }
        try {
            List<Modelo> filtrados = new java.util.ArrayList<>();
            for (Modelo m : modeloDAO.listar()) {
                if (idMarca.equals(m.idMarca())) {
                    filtrados.add(m);
                }
            }
            return filtrados;
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    public List<Pais> paises() {
        try {
            return paisDAO.listar();
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    public List<Proveedor> proveedores() {
        try {
            return proveedorDAO.listar();
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    public List<Ubicacion> ubicaciones() {
        try {
            return ubicacionDAO.listar();
        } catch (SQLException e) {
            throw new RuntimeException(DAOBase.mensajeError(e), e);
        }
    }

    private String validar(String nombre, Integer idTipo, Integer idModelo, String numeroSerie,
                           String garantiaMeses, Integer idPais, Integer idProveedor,
                           String fechaCompra, String codigoInterno, Integer idUbicacion) {
        return Validaciones.primerError(
                Validaciones.requerido("Nombre", nombre),
                Validaciones.longitudMaxima("Nombre", nombre, 120),
                idTipo == null ? "Debe seleccionar el tipo de equipo." : null,
                idModelo == null ? "Debe seleccionar la marca y el modelo." : null,
                Validaciones.requerido("Número de serie", numeroSerie),
                Validaciones.longitudMaxima("Número de serie", numeroSerie, 60),
                Validaciones.entero("Garantía (meses)", garantiaMeses, 0, 120),
                idPais == null ? "Debe seleccionar el país de origen." : null,
                idProveedor == null ? "Debe seleccionar el proveedor." : null,
                Validaciones.fecha("Fecha de compra", fechaCompra),
                Validaciones.requerido("Código interno", codigoInterno),
                Validaciones.longitudMaxima("Código interno", codigoInterno, 40),
                idUbicacion == null ? "Debe seleccionar la ubicación." : null);
    }

    /** Alta de equipo (RF006). */
    public String guardar(String nombre, Integer idTipo, Integer idModelo, String numeroSerie,
                          String garantiaMeses, Integer idPais, Integer idProveedor,
                          String fechaCompra, String codigoInterno, Integer idUbicacion,
                          String estado) {
        String error = validar(nombre, idTipo, idModelo, numeroSerie, garantiaMeses, idPais,
                idProveedor, fechaCompra, codigoInterno, idUbicacion);
        if (error != null) {
            return error;
        }
        try {
            String duplicado = validarDuplicados(numeroSerie.trim(), codigoInterno.trim(), null);
            if (duplicado != null) {
                return duplicado;
            }
            Equipo equipo = new Equipo(null, nombre.trim(), idTipo, idModelo,
                    numeroSerie.trim(), Integer.valueOf(garantiaMeses.trim()), idPais,
                    idProveedor, Validaciones.aFecha(fechaCompra), codigoInterno.trim(),
                    idUbicacion, estado);
            dao.insertar(equipo);
            auditoria.registrar(Auditoria.ALTA, "equipos",
                    "Equipo creado: " + codigoInterno.trim() + " - " + nombre.trim());
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    /** Modificacion de un equipo. */
    public String modificar(Integer idEquipo, String nombre, Integer idTipo, Integer idModelo,
                            String numeroSerie, String garantiaMeses, Integer idPais,
                            Integer idProveedor, String fechaCompra, String codigoInterno,
                            Integer idUbicacion, String estado) {
        if (idEquipo == null) {
            return "Seleccione un equipo de la lista antes de modificar.";
        }
        String error = validar(nombre, idTipo, idModelo, numeroSerie, garantiaMeses, idPais,
                idProveedor, fechaCompra, codigoInterno, idUbicacion);
        if (error != null) {
            return error;
        }
        try {
            String duplicado = validarDuplicados(numeroSerie.trim(), codigoInterno.trim(), idEquipo);
            if (duplicado != null) {
                return duplicado;
            }
            Equipo equipo = new Equipo(idEquipo, nombre.trim(), idTipo, idModelo,
                    numeroSerie.trim(), Integer.valueOf(garantiaMeses.trim()), idPais,
                    idProveedor, Validaciones.aFecha(fechaCompra), codigoInterno.trim(),
                    idUbicacion, estado);
            dao.actualizar(equipo);
            auditoria.registrar(Auditoria.MODIFICACION, "equipos",
                    "Equipo #" + idEquipo + " modificado: " + codigoInterno.trim());
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }

    private String validarDuplicados(String serie, String codigo, Integer idExcluido)
            throws SQLException {
        if (dao.existeNumeroSerie(serie, idExcluido)) {
            return "Ya existe un equipo con el número de serie \"" + serie + "\".";
        }
        if (dao.existeCodigoInterno(codigo, idExcluido)) {
            return "Ya existe un equipo con el código interno \"" + codigo + "\".";
        }
        return null;
    }

    /** Baja logica del equipo (RF006): el registro no se elimina. */
    public String eliminar(Integer idEquipo) {
        if (idEquipo == null) {
            return "Seleccione un equipo de la lista antes de dar de baja.";
        }
        try {
            dao.bajaLogica(idEquipo);
            auditoria.registrar(Auditoria.BAJA, "equipos",
                    "Equipo #" + idEquipo + " dado de baja lógica");
            return null;
        } catch (SQLException e) {
            return DAOBase.mensajeError(e);
        }
    }
}
