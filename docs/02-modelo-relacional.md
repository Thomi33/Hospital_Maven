# 02 - Modelo Relacional

Notacion: **negrita** = clave primaria, *cursiva* = clave foranea.

```
PAISES (<u>id_pais</u>, nombre)

INSTITUCIONES (<u>id_institucion</u>, nombre, direccion, telefono, ciudad, *id_pais*)

UBICACIONES (<u>id_ubicacion</u>, nombre, descripcion, *id_institucion*)

PERFILES (<u>id_perfil</u>, nombre, descripcion, estado)

FUNCIONALIDADES (<u>id_funcionalidad</u>, nombre, descripcion, estado)

PERFIL_FUNCIONALIDAD (<u>*id_perfil*, *id_funcionalidad*</u>)

USUARIOS (<u>id_usuario</u>, nombre, apellido, cedula, fecha_nacimiento, telefono,
          email, usuario, contrasena, *id_perfil*, estado, fecha_alta)

AUDITORIA (<u>id_auditoria</u>, *id_usuario*, fecha, hora, operacion, tabla, detalle)

MARCAS (<u>id_marca</u>, nombre)

MODELOS (<u>id_modelo</u>, nombre, *id_marca*)

TIPO_EQUIPO (<u>id_tipo_equipo</u>, nombre, descripcion)

PROVEEDORES (<u>id_proveedor</u>, nombre, direccion, telefono, email)

EQUIPOS (<u>id_equipo</u>, nombre, *id_tipo_equipo*, *id_modelo*, numero_serie,
         garantia_meses, *id_pais*, *id_proveedor*, fecha_compra, codigo_interno,
         *id_ubicacion*, estado, fecha_registro)

TIPO_INTERVENCION (<u>id_tipo_intervencion</u>, nombre, descripcion)

INTERVENCIONES (<u>id_intervencion</u>, *id_equipo*, *id_tipo_intervencion*, fecha,
                tecnico_responsable, observaciones)
```

## Dependencias funcionales

| Entidad | Dependencias funcionales |
|---------|--------------------------|
| paises | id_pais -> nombre |
| instituciones | id_institucion -> nombre, direccion, telefono, ciudad, id_pais |
| ubicaciones | id_ubicacion -> nombre, descripcion, id_institucion |
| perfiles | id_perfil -> nombre, descripcion, estado |
| funcionalidades | id_funcionalidad -> nombre, descripcion, estado |
| usuarios | id_usuario -> nombre, apellido, cedula, fecha_nacimiento, telefono, email, usuario, contrasena, id_perfil, estado, fecha_alta |
| auditoria | id_auditoria -> id_usuario, fecha, hora, operacion, tabla, detalle |
| marcas | id_marca -> nombre |
| modelos | id_modelo -> nombre, id_marca |
| tipo_equipo | id_tipo_equipo -> nombre, descripcion |
| proveedores | id_proveedor -> nombre, direccion, telefono, email |
| equipos | id_equipo -> nombre, id_tipo_equipo, id_modelo, numero_serie, garantia_meses, id_pais, id_proveedor, fecha_compra, codigo_interno, id_ubicacion, estado, fecha_registro |
| tipo_intervencion | id_tipo_intervencion -> nombre, descripcion |
| intervenciones | id_intervencion -> id_equipo, id_tipo_intervencion, fecha, tecnico_responsable, observaciones |

## Claves candidatas adicionales (UNIQUE)

| Tabla | Clave candidata |
|-------|-----------------|
| paises | nombre |
| instituciones | nombre |
| ubicaciones | nombre |
| perfiles | nombre |
| funcionalidades | nombre |
| usuarios | usuario, cedula, email |
| marcas | nombre |
| modelos | (id_marca, nombre) |
| tipo_equipo | nombre |
| proveedores | nombre, email |
| equipos | numero_serie, codigo_interno |
| tipo_intervencion | nombre |

## Justificacion de la eliminacion de la marca en EQUIPOS

En el primer borrador, `equipos` contenia `id_marca` junto a `id_modelo`.
Como `id_modelo -> id_marca`, se creaba la dependencia transitiva
`id_equipo -> id_modelo -> id_marca` (violacion de la 3FN). Se elimino el
atributo y la marca se obtiene con:

```sql
SELECT ... , ma.nombre AS marca
FROM equipos e
JOIN modelos mo ON mo.id_modelo = e.id_modelo
JOIN marcas  ma ON ma.id_marca  = mo.id_marca;
```

Lo mismo ocurrio con `auditoria.usuario`: se guardaba el nombre del usuario y
hoy se resuelve por `JOIN` con `usuarios`.
