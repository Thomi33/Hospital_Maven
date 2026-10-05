package principal;

import conexion.ConexionBD;
import controlador.AuditoriaControlador;
import controlador.EquipoControlador;
import controlador.FuncionalidadControlador;
import controlador.InstitucionControlador;
import controlador.IntervencionControlador;
import controlador.LoginControlador;
import controlador.MarcaControlador;
import controlador.ModeloControlador;
import controlador.PaisControlador;
import controlador.PerfilControlador;
import controlador.ProveedorControlador;
import controlador.ReporteControlador;
import controlador.TipoEquipoControlador;
import controlador.TipoIntervencionControlador;
import controlador.UbicacionControlador;
import controlador.UsuarioControlador;
import modelo.Informe;
import modelo.Marca;

import java.util.List;

/**
 * Verificacion automatica del proyecto capa a capa: conexion JDBC,
 * login, validaciones, CRUD, auditoria, informes SQL y formularios.
 *
 * Ejecucion:
 *   java -Djava.awt.headless=true -cp target/classes:target/lib/* principal.Verificacion
 */
public final class Verificacion {

    private static int correctos;
    private static int fallidos;

    private Verificacion() {
    }

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        System.out.println("=== VERIFICACION DEL PROYECTO HOSPITAL ===");

        String conexion = ConexionBD.probarConexion();
        comprobar("Conexion JDBC con MySQL", conexion == null, conexion);

        String login = new LoginControlador().ingresar("admin", "admin123");
        comprobar("Login con usuario admin/admin123", login == null, login);
        comprobar("Login con contraseña incorrecta rechazada",
                new LoginControlador().ingresar("admin", "otraclave") != null, "");

        verificarValidaciones();
        Integer idPais = verificarPaises();
        Integer idTipoEquipo = verificarTiposDeEquipo();
        Integer idMarca = verificarMarcas();
        Integer idModelo = verificarModelos(idMarca);
        Integer idProveedor = verificarProveedores();
        Integer idInstitucion = verificarInstituciones();
        Integer idUbicacion = verificarUbicaciones(idInstitucion);
        Integer idEquipo = verificarEquipos(idTipoEquipo, idModelo, idPais, idProveedor,
                idUbicacion);
        verificarIntervenciones(idEquipo);
        verificarUsuarios();
        verificarPerfiles();
        verificarFuncionalidades();
        verificarTipoDeIntervencion();
        verificarAuditoria();
        verificarInformes();
        verificarFormularios();

        limpiarDatosDePrueba();
        resumen();
    }

    private static void verificarValidaciones() {
        MarcaControlador marcas = new MarcaControlador();
        comprobarRechazado("Validacion: campo obligatorio vacio",
                marcas.guardar("   "), "obligatorio");
        comprobarRechazado("Validacion: longitud maxima de cadena",
                marcas.guardar("x".repeat(81)), "80 caracteres");

        UsuarioControlador usuarios = new UsuarioControlador();
        comprobarRechazado("Validacion: formato de correo invalido",
                usuarios.guardar("Prueba", "Validacion", "9.999.999-9", "1990-01-01",
                        "099111222", "no-es-un-correo", "usuario_prueba", "123456", 1),
                "válido");
        comprobarRechazado("Validacion: fecha invalida",
                usuarios.guardar("Prueba", "Validacion", "9.999.999-9", "31/12/1990",
                        "099111222", "prueba@hospital.uy", "usuario_prueba", "123456", 1),
                "aaaa-mm-dd");
        comprobarRechazado("Validacion: numero donde corresponde",
                new EquipoControlador().guardar("Equipo Prueba", 1, 1, "SERIE-PRUEBA-1",
                        "veinticuatro", 1, 1, "2024-01-01", "EQ-PRUEBA-1", 1, "ACTIVO"),
                "número entero");
    }

    private static Integer verificarPaises() {
        PaisControlador controlador = new PaisControlador();
        comprobarSinError("Paises: alta", controlador.guardar("Pais Eliminable"));
        List<modelo.Pais> encontrados = controlador.buscar("Pais Eliminable");
        comprobar("Paises: consulta y busqueda", !encontrados.isEmpty(), "");
        if (encontrados.isEmpty()) {
            return null;
        }
        Integer idEliminable = encontrados.get(0).idPais();
        comprobarSinError("Paises: modificacion",
                controlador.modificar(idEliminable, "Pais Eliminable Editado"));
        comprobar("Paises: dato duplicado rechazado",
                controlador.guardar("Pais Eliminable Editado") != null, "");
        comprobarSinError("Paises: baja", controlador.eliminar(idEliminable));

        comprobarSinError("Paises: alta para la verificacion",
                controlador.guardar("Prueba Verificacion"));
        return controlador.buscar("Prueba Verificacion").stream()
                .map(modelo.Pais::idPais).findFirst().orElse(null);
    }

    private static Integer verificarTiposDeEquipo() {
        TipoEquipoControlador controlador = new TipoEquipoControlador();
        comprobarSinError("Tipo de equipo: alta",
                controlador.guardar("Prueba Verificacion", "Tipo de prueba"));
        List<modelo.TipoEquipo> encontrados = controlador.buscar("Prueba Verificacion");
        comprobar("Tipo de equipo: consulta", !encontrados.isEmpty(), "");
        if (encontrados.isEmpty()) {
            return null;
        }
        Integer id = encontrados.get(0).idTipoEquipo();
        comprobarSinError("Tipo de equipo: modificacion",
                controlador.modificar(id, "Prueba Verificacion", "Tipo modificado"));
        comprobar("Tipo de equipo: duplicado rechazado",
                controlador.guardar("Prueba Verificacion", "") != null, "");
        return id;
    }

    private static Integer verificarMarcas() {
        MarcaControlador controlador = new MarcaControlador();
        comprobarSinError("Marcas: alta", controlador.guardar("Prueba Verificacion"));
        List<Marca> encontradas = controlador.buscar("Prueba Verificacion");
        comprobar("Marcas: consulta", !encontradas.isEmpty(), "");
        if (encontradas.isEmpty()) {
            return null;
        }
        Integer id = encontradas.get(0).idMarca();
        comprobarSinError("Marcas: modificacion",
                controlador.modificar(id, "Prueba Verificacion 2"));
        comprobar("Marcas: duplicado rechazado",
                controlador.guardar("Prueba Verificacion 2") != null, "");
        return id;
    }

    private static Integer verificarModelos(Integer idMarca) {
        if (idMarca == null) {
            comprobar("Modelos: alta", false, "no hay marca de prueba");
            return null;
        }
        ModeloControlador controlador = new ModeloControlador();
        comprobarSinError("Modelos: alta", controlador.guardar("Modelo Prueba", idMarca));
        List<Object[]> encontrados = controlador.buscar("Modelo Prueba");
        comprobar("Modelos: consulta con JOIN de marca", !encontrados.isEmpty(), "");
        if (encontrados.isEmpty()) {
            return null;
        }
        Integer id = aEntero(encontrados.get(0)[0]);
        comprobarSinError("Modelos: modificacion",
                controlador.modificar(id, "Modelo Prueba 2", idMarca));
        comprobar("Modelos: alta sin marca rechazada",
                controlador.guardar("Otro Modelo", null) != null, "");
        return id;
    }

    private static Integer verificarProveedores() {
        ProveedorControlador controlador = new ProveedorControlador();
        comprobarSinError("Proveedores: alta",
                controlador.guardar("Proveedor Prueba", "Calle Falsa 123", "099123456",
                        "prueba@proveedor.uy"));
        List<modelo.Proveedor> encontrados = controlador.buscar("Proveedor Prueba");
        comprobar("Proveedores: consulta", !encontrados.isEmpty(), "");
        if (encontrados.isEmpty()) {
            return null;
        }
        Integer id = encontrados.get(0).idProveedor();
        comprobarSinError("Proveedores: modificacion",
                controlador.modificar(id, "Proveedor Prueba", "Avenida 456", "099123456",
                        "prueba@proveedor.uy"));
        return id;
    }

    private static Integer verificarInstituciones() {
        InstitucionControlador controlador = new InstitucionControlador();
        comprobarSinError("Instituciones: alta",
                controlador.guardar("Institucion Prueba", "Direccion 1", "046221234",
                        "Rivera", idPaisDePrueba()));
        List<Object[]> encontradas = controlador.buscar("Institucion Prueba");
        comprobar("Instituciones: consulta con JOIN de pais", !encontradas.isEmpty(), "");
        return encontradas.isEmpty() ? null : aEntero(encontradas.get(0)[0]);
    }

    private static Integer idPaisDePrueba() {
        return new PaisControlador().buscar("Prueba Verificacion").stream()
                .map(modelo.Pais::idPais).findFirst().orElse(null);
    }

    private static Integer verificarUbicaciones(Integer idInstitucion) {
        if (idInstitucion == null) {
            comprobar("Ubicaciones: alta", false, "no hay institucion de prueba");
            return null;
        }
        UbicacionControlador controlador = new UbicacionControlador();
        comprobarSinError("Ubicaciones: alta",
                controlador.guardar("Prueba Verificacion", "Ubicacion de prueba", idInstitucion));
        List<Object[]> encontradas = controlador.buscar("Prueba Verificacion");
        comprobar("Ubicaciones: consulta con JOIN de institucion",
                !encontradas.isEmpty(), "");
        if (encontradas.isEmpty()) {
            return null;
        }
        Integer id = aEntero(encontradas.get(0)[0]);
        comprobarSinError("Ubicaciones: modificacion",
                controlador.modificar(id, "Prueba Verificacion", "Descripcion modificada",
                        idInstitucion));
        comprobar("Ubicaciones: validacion de institucion obligatoria",
                controlador.guardar("Sin Institucion", "", null) != null, "");
        return id;
    }

    private static Integer aEntero(Object valor) {
        return valor instanceof Number n ? n.intValue() : null;
    }

    private static Integer verificarEquipos(Integer idTipo, Integer idModelo, Integer idPais,
                                            Integer idProveedor, Integer idUbicacion) {
        if (idTipo == null || idModelo == null || idPais == null || idProveedor == null
                || idUbicacion == null) {
            comprobar("Equipos: alta", false, "faltan datos de prueba");
            return null;
        }
        EquipoControlador controlador = new EquipoControlador();
        comprobarSinError("Equipos: alta (modulo principal)",
                controlador.guardar("Equipo Prueba Verificacion", idTipo, idModelo,
                        "SERIE-PRUEBA-0001", "24", idPais, idProveedor, "2024-05-10",
                        "EQ-PRUEBA-0001", idUbicacion, "ACTIVO"));

        List<Object[]> filas = controlador.buscar("EQ-PRUEBA-0001");
        comprobar("Equipos: busqueda por codigo interno", filas.size() == 1, "");
        if (filas.isEmpty()) {
            return null;
        }
        comprobar("Equipos: listado con JOIN (marca derivada del modelo)",
                "Prueba Verificacion 2".equals(filas.get(0)[3])
                        && "Modelo Prueba 2".equals(filas.get(0)[4]),
                "marca=" + filas.get(0)[3] + " modelo=" + filas.get(0)[4]);
        comprobar("Equipos: numero de serie duplicado rechazado",
                controlador.guardar("Otro Equipo", idTipo, idModelo, "SERIE-PRUEBA-0001",
                        "12", idPais, idProveedor, "2024-05-10", "EQ-PRUEBA-0002",
                        idUbicacion, "ACTIVO") != null, "");
        comprobar("Equipos: fecha de compra futura rechazada",
                controlador.guardar("Otro Equipo", idTipo, idModelo, "SERIE-PRUEBA-0003",
                        "12", idPais, idProveedor, "2099-01-01", "EQ-PRUEBA-0003",
                        idUbicacion, "ACTIVO") != null, "");

        Integer idEquipo = aEntero(filas.get(0)[0]);
        comprobarSinError("Equipos: modificacion",
                controlador.modificar(idEquipo, "Equipo Prueba Verificacion Editado", idTipo,
                        idModelo, "SERIE-PRUEBA-0001", "36", idPais, idProveedor,
                        "2024-05-10", "EQ-PRUEBA-0001", idUbicacion, "ACTIVO"));
        comprobarSinError("Equipos: baja logica", controlador.eliminar(idEquipo));
        List<Object[]> despuesDeBaja = controlador.buscar("EQ-PRUEBA-0001");
        comprobar("Equipos: la baja logica conserva el registro con estado INACTIVO",
                !despuesDeBaja.isEmpty() && "INACTIVO".equals(despuesDeBaja.get(0)[12]), "");
        return idEquipo;
    }

    private static void verificarIntervenciones(Integer idEquipo) {
        IntervencionControlador controlador = new IntervencionControlador();
        comprobar("Intervenciones: combo de equipos activos", !controlador.equipos().isEmpty(),
                "");
        comprobar("Intervenciones: combo de tipos", !controlador.tiposDeIntervencion().isEmpty(),
                "");
        if (idEquipo == null) {
            comprobar("Intervenciones: alta", false, "sin equipo de prueba");
            return;
        }
        Integer idTipo = controlador.tiposDeIntervencion().stream()
                .filter(t -> "Preventiva".equals(t.nombre()))
                .map(modelo.TipoIntervencion::idTipoIntervencion)
                .findFirst().orElse(null);
        comprobarSinError("Intervenciones: alta",
                controlador.guardar(idEquipo, idTipo, "2025-06-15",
                        "Verificacion Automatica", "Intervencion creada por la verificacion"));
        comprobar("Intervenciones: consulta",
                controlador.listar().stream()
                        .anyMatch(f -> "Verificacion Automatica".equals(f[5])), "");
        comprobar("Intervenciones: busqueda por tecnico",
                !controlador.buscar("Verificacion Automatica").isEmpty(), "");
        comprobarRechazado("Intervenciones: fecha futura rechazada",
                controlador.guardar(idEquipo, idTipo, "2099-12-31", "Tecnico", "Observacion"),
                "futura");
        comprobarRechazado("Intervenciones: campos obligatorios",
                controlador.guardar(null, null, "", "", ""), "equipo");
    }

    private static void verificarUsuarios() {
        UsuarioControlador controlador = new UsuarioControlador();
        comprobarSinError("Usuarios: alta",
                controlador.guardar("Verificacion", "Automatica", "8.888.888-8", "1992-07-08",
                        "099888777", "verificacion@hospital.uy", "usuarionuevo", "clave123", 1));
        comprobar("Usuarios: consulta y busqueda",
                !controlador.buscar("usuarionuevo").isEmpty(), "");
        comprobarRechazado("Usuarios: nombre de usuario duplicado rechazado",
                controlador.guardar("Otro", "Usuario", "7.777.777-7", "1990-02-02", "099777666",
                        "otro@hospital.uy", "usuarionuevo", "clave123", 1),
                "ya existe");
        comprobarRechazado("Usuarios: cedula duplicada rechazada",
                controlador.guardar("Otro", "Usuario", "8.888.888-8", "1990-02-02", "099777666",
                        "otro@hospital.uy", "otrousuario", "clave123", 1),
                "cédula");

        List<Object[]> filas = controlador.buscar("usuarionuevo");
        comprobar("Usuarios: fila con perfil por JOIN", !filas.isEmpty()
                && "Administrador".equals(filas.get(0)[8]), "");
        if (filas.isEmpty()) {
            return;
        }
        Integer id = aEntero(filas.get(0)[0]);
        comprobarSinError("Usuarios: modificacion",
                controlador.modificar(id, "Verificacion", "Automatica", "8.888.888-8",
                        "1992-07-08", "099888777", "verificacion@hospital.uy", "usuarionuevo",
                        "", 1, "ACTIVO"));
        comprobar("Usuarios: el login funciona despues de la modificacion",
                new LoginControlador().ingresar("usuarionuevo", "clave123") == null, "");
        new LoginControlador().ingresar("admin", "admin123");
        comprobarSinError("Usuarios: baja logica", controlador.eliminar(id));
        comprobar("Usuarios: el usuario dado de baja no puede ingresar",
                new LoginControlador().ingresar("usuarionuevo", "clave123") != null, "");
        new LoginControlador().ingresar("admin", "admin123");
    }

    private static void verificarPerfiles() {
        PerfilControlador controlador = new PerfilControlador();
        comprobarSinError("Perfiles: alta",
                controlador.guardar("Perfil Prueba", "Perfil de prueba", "ACTIVO"));
        List<modelo.Perfil> perfiles = controlador.buscar("Perfil Prueba");
        comprobar("Perfiles: consulta", !perfiles.isEmpty(), "");
        if (perfiles.isEmpty()) {
            return;
        }
        Integer id = perfiles.get(0).idPerfil();
        comprobarSinError("Perfiles: modificacion",
                controlador.modificar(id, "Perfil Prueba", "Perfil modificado", "ACTIVO"));
        comprobarSinError("Perfiles: asignacion de funcionalidad (relacion N:N)",
                controlador.asignarFuncionalidad(id, 1));
        comprobar("Perfiles: funcionalidades asignadas",
                controlador.funcionalidadesDelPerfil(id).contains("Gestionar Usuarios"), "");
        comprobarSinError("Perfiles: baja logica", controlador.eliminar(id));
        comprobar("Perfiles: el perfil dado de baja queda INACTIVO",
                controlador.buscar("Perfil Prueba").stream()
                        .anyMatch(p -> "INACTIVO".equals(p.estado())), "");
    }

    private static void verificarFuncionalidades() {
        FuncionalidadControlador controlador = new FuncionalidadControlador();
        comprobarSinError("Funcionalidades: alta",
                controlador.guardar("Prueba Verificacion", "Funcionalidad de prueba"));
        List<modelo.Funcionalidad> encontradas = controlador.buscar("Prueba Verificacion");
        comprobar("Funcionalidades: consulta", !encontradas.isEmpty(), "");
        if (encontradas.isEmpty()) {
            return;
        }
        Integer id = encontradas.get(0).idFuncionalidad();
        comprobarSinError("Funcionalidades: modificacion",
                controlador.modificar(id, "Prueba Verificacion", "Descripcion modificada",
                        "ACTIVO"));
        comprobarSinError("Funcionalidades: baja logica", controlador.eliminar(id));
        comprobar("Funcionalidades: dada de baja queda INACTIVO",
                controlador.listar().stream()
                        .anyMatch(f -> "Prueba Verificacion".equals(f.nombre())
                                && "INACTIVO".equals(f.estado())), "");
    }

    private static void verificarTipoDeIntervencion() {
        TipoIntervencionControlador controlador = new TipoIntervencionControlador();
        comprobarSinError("Tipo de intervencion: alta",
                controlador.guardar("Prueba Verificacion", "Tipo de prueba"));
        comprobarRechazado("Tipo de intervencion: duplicado rechazado",
                controlador.guardar("Prueba Verificacion", ""), "ya existe");
        Integer idTipoPrueba = controlador.buscar("Prueba Verificacion").stream()
                .map(modelo.TipoIntervencion::idTipoIntervencion).findFirst().orElse(null);
        comprobarSinError("Tipo de intervencion: modificacion",
                controlador.modificar(idTipoPrueba, "Prueba Verificacion",
                        "Tipo modificado"));

        modelo.TipoIntervencion preventiva = controlador.listar().stream()
                .filter(t -> "Preventiva".equals(t.nombre())).findFirst().orElse(null);
        if (preventiva != null) {
            comprobarRechazado(
                    "Tipo de intervencion: no se elimina si otras tablas lo referencian (FK)",
                    controlador.eliminar(preventiva.idTipoIntervencion()), "");
        }
    }

    private static void verificarAuditoria() {
        AuditoriaControlador controlador = new AuditoriaControlador();
        long total = controlador.totalDeRegistros();
        comprobar("Auditoria: registro automatico de operaciones (RF004)", total > 0,
                "total=" + total);
        comprobar("Auditoria: consulta general", !controlador.listar().isEmpty(), "");
        comprobar("Auditoria: filtro por operacion ALTA",
                !controlador.buscar("", "ALTA").isEmpty(), "");
        comprobar("Auditoria: inicio de sesion registrado",
                controlador.buscar("admin", "LOGIN").stream()
                        .anyMatch(f -> "LOGIN".equals(f[4])), "");
    }

    private static void verificarInformes() {
        ReporteControlador controlador = new ReporteControlador();
        List<String> nombres = controlador.nombresDeInformes();
        comprobar("Informes: consultas SQL complejas disponibles", nombres.size() >= 5,
                "cantidad=" + nombres.size());
        for (String nombre : nombres) {
            try {
                Informe informe = controlador.ejecutar(nombre);
                comprobar("Informe: " + nombre,
                        !informe.columnas().isEmpty() && informe.cantidadDeFilas() >= 0,
                        "filas=" + informe.cantidadDeFilas());
            } catch (RuntimeException e) {
                comprobar("Informe: " + nombre, false, e.getMessage());
            }
        }
        Informe porUbicacion = controlador
                .ejecutar("Equipos por ubicacion (GROUP BY + COUNT + HAVING)");
        comprobar("Informes: GROUP BY + COUNT + HAVING devuelve filas",
                porUbicacion.cantidadDeFilas() > 0, "");
    }

    private static void verificarFormularios() {
        comprobarFormulario("Equipos", vista.FormularioEquipos::new);
        comprobarFormulario("Marcas", vista.FormularioMarcas::new);
        comprobarFormulario("Modelos", vista.FormularioModelos::new);
        comprobarFormulario("Proveedores", vista.FormularioProveedores::new);
        comprobarFormulario("Paises", vista.FormularioPaises::new);
        comprobarFormulario("Tipo de equipo", vista.FormularioTipoEquipo::new);
        comprobarFormulario("Ubicaciones", vista.FormularioUbicaciones::new);
        comprobarFormulario("Instituciones", vista.FormularioInstituciones::new);
        comprobarFormulario("Usuarios", vista.FormularioUsuarios::new);
        comprobarFormulario("Perfiles", vista.FormularioPerfiles::new);
        comprobarFormulario("Funcionalidades", vista.FormularioFuncionalidades::new);
        comprobarFormulario("Intervenciones", vista.FormularioIntervenciones::new);
        comprobarFormulario("Tipo de intervencion", vista.FormularioTipoIntervencion::new);
        comprobarFormulario("Auditoria", vista.FormularioAuditoria::new);
        comprobarFormulario("Reportes", vista.FormularioReportes::new);
    }

    private static void comprobarFormulario(String nombre,
                                            java.util.function.Supplier<vista.FormularioCRUD> fabrica) {
        try {
            vista.FormularioCRUD formulario = fabrica.get();
            comprobar("Formulario: " + nombre,
                    formulario.getComponents().length > 0, "");
        } catch (Throwable t) {
            comprobar("Formulario: " + nombre, false, t.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // Limpieza de los datos creados por esta verificacion
    // -----------------------------------------------------------------

    private static void limpiarDatosDePrueba() {
        String[] sentencias = {
                "DELETE FROM intervenciones WHERE tecnico_responsable = 'Verificacion Automatica'",
                "DELETE FROM equipos WHERE codigo_interno = 'EQ-PRUEBA-0001'",
                "DELETE FROM ubicaciones WHERE nombre = 'Prueba Verificacion'",
                "DELETE FROM instituciones WHERE nombre = 'Institucion Prueba'",
                "DELETE FROM proveedores WHERE nombre = 'Proveedor Prueba'",
                "DELETE FROM modelos WHERE nombre IN ('Modelo Prueba', 'Modelo Prueba 2')",
                "DELETE FROM marcas WHERE nombre LIKE 'Prueba Verificacion%'",
                "DELETE FROM tipo_equipo WHERE nombre = 'Prueba Verificacion'",
                "DELETE FROM paises WHERE nombre LIKE 'Prueba Verificacion%' "
                        + "OR nombre LIKE 'Pais Eliminable%'",
                "DELETE FROM usuarios WHERE usuario = 'usuarionuevo'",
                "DELETE FROM perfil_funcionalidad WHERE id_perfil IN "
                        + "(SELECT id_perfil FROM perfiles WHERE nombre = 'Perfil Prueba')",
                "DELETE FROM perfiles WHERE nombre = 'Perfil Prueba'",
                "DELETE FROM funcionalidades WHERE nombre = 'Prueba Verificacion'",
                "DELETE FROM tipo_intervencion WHERE nombre = 'Prueba Verificacion'"
        };
        try (java.sql.Connection cn = conexion.ConexionBD.obtenerConexion();
             java.sql.Statement sentencia = cn.createStatement()) {
            int filasBorradas = 0;
            for (String sql : sentencias) {
                filasBorradas += sentencia.executeUpdate(sql);
            }
            comprobar("Limpieza de los datos de prueba (" + sentencias.length + " sentencias)",
                    true, "filas borradas=" + filasBorradas);
        } catch (Exception e) {
            comprobar("Limpieza de los datos de prueba", false, e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // Utilidades de comprobacion
    // -----------------------------------------------------------------

    private static void comprobar(String nombre, boolean condicion, String detalle) {
        if (condicion) {
            correctos++;
            System.out.println("[OK]    " + nombre);
        } else {
            fallidos++;
            System.out.println("[FALLA] " + nombre
                    + (detalle == null || detalle.isBlank() ? "" : "  ->  " + detalle));
        }
    }

    /** Comprueba que una operacion se realizo sin devolver error. */
    private static void comprobarSinError(String nombre, String error) {
        comprobar(nombre, error == null, error);
    }

    /** Comprueba que una operacion invalida fue rechazada con un mensaje. */
    private static void comprobarRechazado(String nombre, String error, String fragmento) {
        boolean rechazada = error != null
                && (fragmento == null || fragmento.isEmpty()
                    || error.toLowerCase().contains(fragmento.toLowerCase()));
        comprobar(nombre, rechazada,
                error == null ? "la operacion NO fue rechazada" : "mensaje: " + error);
    }

    private static void resumen() {
        System.out.println();
        System.out.println("=== RESUMEN: " + correctos + " comprobaciones correctas, "
                + fallidos + " fallidas ===");
        System.exit(fallidos == 0 ? 0 : 1);
    }
}
