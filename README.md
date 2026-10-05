# Sistema de Gestion de Equipamiento Clinico Hospitalario

**Proyecto Integrador - Programacion Avanzada y Bases de Datos**
Escuela Tecnica Superior de Rivera - 2026

Aplicacion de escritorio en **Java 21 + Swing**, conectada a **MySQL mediante JDBC**
con arquitectura por capas (`conexion`, `modelo`, `dao`, `controlador`, `vista`,
`utilidades`, `principal`).

---

## 1. Como compilar y ejecutar

Requisitos: JDK 21, Maven y un servidor MySQL (o MariaDB) con la base `hospital`.

```bash
# 1) Crear la base y cargar datos de prueba
mysql -u root -p < src/main/sql/creacion_base.sql
mysql -u root -p < src/main/sql/datos_prueba.sql

# 2) Compilar y empaquetar (incluye las librerias en target/lib)
mvn clean package

# 3) Ejecutar la aplicacion
java -cp "target/classes:target/lib/*" principal.Principal
# (tambien: mvn exec:java)
```

**Usuarios de prueba**

| Usuario | Contraseña | Perfil |
|---------|------------|--------|
| `admin` | `admin123` | Administrador |
| `tecnico` | `tecnico123` | Tecnico Biomedico |
| `supervisor` | `supervisor123` | Supervisor |

La conexion se configura en `src/main/resources/conexion.properties`
(driver, url, usuario y contraseña).

> En maquinas sin servidor MySQL instalado (y sin permisos de root) existe el
> script `./iniciar-mysql-local.sh`, que levanta una instancia de MariaDB en el
> directorio del usuario y carga la base con `creacion_base.sql` y
> `datos_prueba.sql`.

## 2. Verificacion automatica del proyecto

```bash
# Capa a capa: JDBC, login, validaciones, CRUD, auditoria, informes y formularios
java -Djava.awt.headless=true -cp "target/classes:target/lib/*" principal.Verificacion

# Verificacion grafica: abre las ventanas y recorre todo el menu
xvfb-run -a java -cp "target/classes:target/lib/*" principal.VerificacionGrafica
```

`Verificacion` crea sus propios datos de prueba, los verifica y los vuelve a
eliminar al terminar, por lo que la base queda limpia. `VerificacionGrafica`
requiere un display (en CI se usa Xvfb).

## 3. Estructura del proyecto

```
ProyectoHospital/
├── pom.xml
├── docs/
│   ├── 01-diagrama-entidad-relacion.md
│   ├── 02-modelo-relacional.md
│   ├── 03-diccionario-de-datos.md
│   ├── 04-normalizacion-3fn.md
│   └── capturas/                 (capturas de la interfaz)
└── src/main/
    ├── java/
    │   ├── conexion/             ConexionBD (JDBC)
    │   ├── modelo/               clases de negocio (records inmutables)
    │   ├── dao/                  acceso a datos con PreparedStatement
    │   ├── controlador/          logica entre interfaz y Base de Datos
    │   ├── vista/                formularios Swing
    │   ├── utilidades/           validaciones, mensajes, sesion, hash
    │   └── principal/            Principal (main) y verificaciones
    ├── resources/conexion.properties
    └── sql/
        ├── creacion_base.sql     script de creacion (15 tablas, 3FN)
        ├── datos_prueba.sql      script con datos de prueba
        └── consultas.sql         consultas complejas (JOIN / GROUP BY)
```

> El PDF propone `src/conexion/...`; se usa el orden estandar de Maven
> (`src/main/java/...`) manteniendo exactamente los mismos paquetes.

## 4. Base de Datos

15 tablas (las 14 obligatorias + `perfil_funcionalidad`):

`usuarios`, `perfiles`, `funcionalidades`, `perfil_funcionalidad`, `auditoria`,
`intervenciones`, `equipos`, `ubicaciones`, `instituciones`, `paises`,
`tipo_equipo`, `proveedores`, `modelos`, `marcas`, `tipo_intervencion`.

Restricciones aplicadas: claves primarias, claves foraneas con integridad
referencial (`ON DELETE RESTRICT` / `SET NULL` / `CASCADE`), `UNIQUE`,
`NOT NULL`, `AUTO_INCREMENT`, `CHECK` e `ENUM` de estado.

### Decisiones de diseno

* **Marca del equipo**: no se almacena en `equipos`; se deriva del modelo
  (`modelos -> marcas`) para evitar una dependencia transitiva y cumplir la 3FN.
  La interfaz la muestra siempre mediante `JOIN`.
* **Bajas logicas**: `equipos`, `usuarios`, `perfiles` y `funcionalidades` nunca
  se borran: cambian su `estado` a `INACTIVO`.
* **Contraseñas**: se guardan como hash SHA-256 (igual que `SHA2()` de MySQL).
* **Auditoria**: cada alta, modificacion, baja, borrado e inicio/cierre de
  sesion deja un registro con usuario, fecha, hora y operacion.
# Hospital_Maven
