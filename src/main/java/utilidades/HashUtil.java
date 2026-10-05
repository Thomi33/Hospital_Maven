package utilidades;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad de contrasenas: hash SHA-256 en hexadecimal (64 caracteres),
 * el mismo que produce la funcion SHA2(texto, 256) de MySQL.
 */
public final class HashUtil {

    private HashUtil() {
    }

    public static String sha256(String texto) {
        if (texto == null) {
            texto = "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("El algoritmo SHA-256 no está disponible en la JVM.", e);
        }
    }

    public static boolean coincide(String texto, String hashGuardado) {
        if (texto == null || hashGuardado == null) {
            return false;
        }
        return sha256(texto).equalsIgnoreCase(hashGuardado.trim());
    }
}
