package modelo;

/**
 * Proveedor del equipamiento clinico (RF011).
 */
public record Proveedor(Integer idProveedor, String nombre, String direccion,
                        String telefono, String email) {

    @Override
    public String toString() {
        return nombre;
    }
}
