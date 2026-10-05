package utilidades;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validaciones de datos de entrada (seccion 13 del proyecto).
 * Todos los metodos devuelven null cuando el dato es valido y, en caso
 * contrario, un mensaje claro y comprensible para el usuario.
 */
public final class Validaciones {

    /** Formato de fecha utilizado por toda la aplicacion. */
    public static final String FORMATO_FECHA = "yyyy-MM-dd";

    private static final Pattern RE_EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern RE_CEDULA = Pattern.compile("^\\d{1,2}\\.\\d{3}\\.\\d{3}-\\d$");
    private static final Pattern RE_DIGITOS = Pattern.compile("^\\d+$");

    private Validaciones() {
    }

    public static boolean vacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    /** Devuelve el primer error encontrado entre los mensajes recibidos. */
    public static String primerError(String... mensajes) {
        for (String m : mensajes) {
            if (m != null && !m.isBlank()) {
                return m;
            }
        }
        return null;
    }

    public static String requerido(String etiqueta, String valor) {
        return vacio(valor) ? "El campo \"" + etiqueta + "\" es obligatorio." : null;
    }

    public static String longitudMaxima(String etiqueta, String valor, int maximo) {
        if (valor != null && valor.trim().length() > maximo) {
            return "\"" + etiqueta + "\" no puede superar los " + maximo + " caracteres.";
        }
        return null;
    }

    public static String email(String etiqueta, String valor) {
        if (vacio(valor)) {
            return "El campo \"" + etiqueta + "\" es obligatorio.";
        }
        if (!RE_EMAIL.matcher(valor.trim()).matches()) {
            return "\"" + etiqueta + "\" debe tener un formato de correo electrónico válido (ejemplo: nombre@dominio.com).";
        }
        return null;
    }

    /** Enteros entre minimo y maximo (inclusive). */
    public static String entero(String etiqueta, String valor, int minimo, int maximo) {
        if (vacio(valor)) {
            return "El campo \"" + etiqueta + "\" es obligatorio.";
        }
        String limpio = valor.trim();
        if (!RE_DIGITOS.matcher(limpio).matches()) {
            return "\"" + etiqueta + "\" debe ser un número entero (sin letras ni signos).";
        }
        long n = Long.parseLong(limpio);
        if (n < minimo || n > maximo) {
            return "\"" + etiqueta + "\" debe estar entre " + minimo + " y " + maximo + ".";
        }
        return null;
    }

    public static String cedula(String etiqueta, String valor) {
        String error = requerido(etiqueta, valor);
        if (error != null) {
            return error;
        }
        if (!RE_CEDULA.matcher(valor.trim()).matches()) {
            return "\"" + etiqueta + "\" debe tener el formato 0.000.000-0.";
        }
        return null;
    }

    public static String telefono(String etiqueta, String valor) {
        String error = requerido(etiqueta, valor);
        if (error != null) {
            return error;
        }
        String limpio = valor.trim().replace(" ", "").replace("-", "");
        if (!RE_DIGITOS.matcher(limpio).matches() || limpio.length() < 6 || limpio.length() > 15) {
            return "\"" + etiqueta + "\" debe contener solo números (entre 6 y 15 dígitos).";
        }
        return null;
    }

    /** Valida una fecha con el formato {@link #FORMATO_FECHA} y que no sea futura. */
    public static String fecha(String etiqueta, String valor) {
        String error = requerido(etiqueta, valor);
        if (error != null) {
            return error;
        }
        LocalDate fecha = aFecha(valor);
        if (fecha == null) {
            return "\"" + etiqueta + "\" debe tener el formato " + FORMATO_FECHA + " (aaaa-mm-dd).";
        }
        if (fecha.isAfter(LocalDate.now())) {
            return "\"" + etiqueta + "\" no puede ser una fecha futura.";
        }
        return null;
    }

    /** Convierte texto a LocalDate; devuelve null si el valor es invalido. */
    public static LocalDate aFecha(String valor) {
        if (vacio(valor)) {
            return null;
        }
        try {
            return LocalDate.parse(valor.trim(), DateTimeFormatter.ofPattern(FORMATO_FECHA));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static String textoLibre(String etiqueta, String valor, int maximo) {
        String error = primerError(requerido(etiqueta, valor), longitudMaxima(etiqueta, valor, maximo));
        return error;
    }

    /** Normaliza un valor de texto ingresado por el usuario. */
    public static String normalizar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    /** Une varios mensajes de validacion en uno solo. */
    public static String unirMensajes(List<String> mensajes) {
        if (mensajes == null || mensajes.isEmpty()) {
            return null;
        }
        return String.join("\n", mensajes);
    }

    /** Devuelve una lista (puede venir vacia) para ir acumulando errores. */
    public static List<String> listaDeErrores() {
        return new ArrayList<>();
    }
}
