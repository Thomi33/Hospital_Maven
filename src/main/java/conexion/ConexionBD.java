package conexion;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Capa de conexion a MySQL mediante JDBC (RNF002 / RNF003).
 * La configuracion se lee del archivo src/main/resources/conexion.properties
 * y siempre entrega una conexion nueva que el DAO cierra con try-with-resources.
 */
public final class ConexionBD {

    private static final Logger LOG = Logger.getLogger(ConexionBD.class.getName());
    private static final String RECURSO = "/conexion.properties";

    private static String driver = "com.mysql.cj.jdbc.Driver";
    private static String url = "jdbc:mysql://127.0.0.1:3306/hospital"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    private static String usuario = "hospital";
    private static String contrasena = "hospital123";

    static {
        cargarConfiguracion();
    }

    private ConexionBD() {
    }

    private static void cargarConfiguracion() {
        try (InputStream entrada = ConexionBD.class.getResourceAsStream(RECURSO)) {
            if (entrada == null) {
                LOG.warning("No se encontro " + RECURSO + ". Se usan los valores por defecto.");
                return;
            }
            Properties propiedades = new Properties();
            propiedades.load(entrada);
            driver = propiedades.getProperty("driver", driver).trim();
            url = propiedades.getProperty("url", url).trim();
            usuario = propiedades.getProperty("usuario", usuario).trim();
            contrasena = propiedades.getProperty("contrasena", contrasena);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error al leer la configuracion de conexion.", e);
        }
    }

    /** Abre una conexion nueva a la Base de Datos. El llamado debe cerrarla. */
    public static Connection obtenerConexion() throws SQLException {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC no encontrado: " + driver, e);
        }
        return DriverManager.getConnection(url, usuario, contrasena);
    }

    /**
     * Prueba la conexion y devuelve null si esta correcta, o un mensaje
     * claro para el usuario si no se pudo conectar.
     */
    public static String probarConexion() {
        try (Connection conexion = obtenerConexion()) {
            if (conexion == null || !conexion.isValid(5)) {
                return "La conexion con la Base de Datos fue rechazada por el servidor.";
            }
            return null;
        } catch (SQLException e) {
            return "No se pudo conectar con MySQL (" + e.getErrorCode() + "). "
                    + "Verifique que el servidor este activo y que exista la base \"hospital\".\n"
                    + "Detalle: " + e.getMessage();
        }
    }

    /** Describe la conexion sin exponer la contrasena (para mensajes). */
    public static String resumen() {
        return "Servidor: " + url.substring(0, url.indexOf('?')) + " | Usuario: " + usuario;
    }
}
