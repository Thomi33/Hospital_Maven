# 04 - Normalizacion hasta la Tercera Forma Normal

Base: `hospital` (15 tablas, script `src/main/sql/creacion_base.sql`).

## Primera Forma Normal (1FN)

*Todos los atributos son atomicos y no existen grupos repetidos.*

| Violacion detectada en el borrador | Solucion |
|------------------------------------|----------|
| `usuarios.funcionalidades` con varios valores en una celda | tabla `perfil_funcionalidad` con clave compuesta |
| `equipos.historial_mantenimientos` con varias fechas | tabla `intervenciones` (1 equipo : N intervenciones) |
| `perfiles.permisos` como lista de texto | relacion N:N `perfil_funcionalidad` |
| Cada columna tiene un solo valor y la clave identifica unica y completamente a la fila | claves primarias `AUTO_INCREMENT` en todas las tablas |

## Segunda Forma Normal (2FN)

*Ningun atributo depende parcialmente de una clave compuesta.*

* La unica tabla con clave compuesta es `perfil_funcionalidad`
  (`id_perfil`, `id_funcionalidad`) y **no tiene atributos** que dependan de
  una sola parte de la clave: solo contiene la relacion.
* Todas las demas tablas tienen clave primaria simple (`AUTO_INCREMENT`), por
  lo que no pueden existir dependencias parciales.

## Tercera Forma Normal (3FN)

*Ningun atributo depende transitivamente de la clave primaria.*

| Dependencia transitiva detectada | Solucion |
|----------------------------------|----------|
| `equipos.id_equipo -> id_modelo -> id_marca` | se elimino `id_marca` de `equipos`; se resuelve por JOIN |
| `equipos.id_equipo -> id_pais -> nombre_pais` | `equipos` guarda solo `id_pais`; el nombre se trae por JOIN |
| `ubicaciones.id_ubicacion -> id_institucion -> ciudad` | se guarda solo `id_institucion` |
| `auditoria.id_auditoria -> id_usuario -> usuario` | se guarda el id y el nombre se resuelve por JOIN |
| `intervenciones -> equipo -> ubicacion` | se guarda solo `id_equipo` |

No quedan atributos no clave que dependan de otros atributos no clave.

## Restricciones de integridad implementadas

* **Integridad de entidad**: claves primarias `NOT NULL` + `AUTO_INCREMENT`.
* **Integridad de dominio**:
  * `CHECK (garantia_meses BETWEEN 0 AND 120)`
  * `ENUM('ACTIVO','INACTIVO')` para los estados de baja logica
  * `VARCHAR(n)` con longitudes fijas para todas las cadenas
  * fechas validas y no futuras: validadas en Java (seccion 13 del proyecto)
* **Integridad referencial** (todas las claves foraneas declaradas):
  * `equipos -> tipo_equipo, modelos, paises, proveedores, ubicaciones`
  * `modelos -> marcas`, `ubicaciones -> instituciones`, `instituciones -> paises`
  * `usuarios -> perfiles`, `auditoria -> usuarios (ON DELETE SET NULL)`
  * `intervenciones -> equipos, tipo_intervencion`
  * `perfil_funcionalidad -> perfiles, funcionalidades (ON DELETE CASCADE)`
  * por defecto `ON DELETE RESTRICT`: no se puede borrar un registro que
    otros utilizan (la aplicacion muestra un mensaje claro).
* **Unicidad**: `UNIQUE` en nombres, cedula, email, usuario, numero de serie
  y codigo interno.
