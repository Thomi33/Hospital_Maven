# 03 - Diccionario de Datos

Base de datos: `hospital` - 15 tablas.
Convenciones: **PK** = clave primaria, **FK** = clave foranea, **UK** = restriccion UNIQUE.

---

## 1. usuarios (RF001)

| Atributo | Tipo | Nulo | Descripcion |
|----------|------|------|-------------|
| **id_usuario** | INT UNSIGNED AI | no | Identificador del usuario |
| nombre | VARCHAR(60) | no | Nombre de la persona |
| apellido | VARCHAR(60) | no | Apellido de la persona |
| cedula | VARCHAR(20) UK | no | Cedula de identidad (formato 0.000.000-0) |
| fecha_nacimiento | DATE | no | Fecha de nacimiento (aaaa-mm-dd) |
| telefono | VARCHAR(30) | no | Telefono de contacto |
| email | VARCHAR(100) UK | no | Correo electronico valido |
| usuario | VARCHAR(40) UK | no | Nombre de usuario para ingresar |
| contrasena | VARCHAR(64) | no | Hash SHA-256 de la contrasena |
| **id_perfil** | INT UNSIGNED FK | no | Perfil al que pertenece |
| estado | ENUM | no | ACTIVO / INACTIVO (baja logica) |
| fecha_alta | DATETIME | no | Fecha de alta en el sistema |

## 2. perfiles (RF002)

| Atributo | Tipo | Nulo | Descripcion |
|----------|------|------|-------------|
| **id_perfil** | INT UNSIGNED AI | no | Identificador del perfil |
| nombre | VARCHAR(60) UK | no | Nombre del perfil (Administrador, Tecnico...) |
| descripcion | VARCHAR(150) | no | Que agrupa el perfil |
| estado | ENUM | no | ACTIVO / INACTIVO (baja logica) |

## 3. funcionalidades (RF003)

| Atributo | Tipo | Nulo | Descripcion |
|----------|------|------|-------------|
| **id_funcionalidad** | INT UNSIGNED AI | no | Identificador de la funcionalidad |
| nombre | VARCHAR(80) UK | no | Nombre de la accion del sistema |
| descripcion | VARCHAR(150) | no | Descripcion de la accion |
| estado | ENUM | no | ACTIVO / INACTIVO (baja logica) |

## 4. perfil_funcionalidad (relacion N:N)

| Atributo | Tipo | Nulo | Descripcion |
|----------|------|------|-------------|
| **id_perfil** | INT UNSIGNED FK | no | Perfil (PK compuesta) |
| **id_funcionalidad** | INT UNSIGNED FK | no | Funcionalidad (PK compuesta) |

## 5. auditoria (RF004)

| Atributo | Tipo | Nulo | Descripcion |
|----------|------|------|-------------|
| **id_auditoria** | BIGINT UNSIGNED AI | no | Identificador del registro |
| id_usuario | INT UNSIGNED FK | si | Usuario que ejecuto la operacion |
| fecha | DATE | no | Fecha del evento |
| hora | TIME | no | Hora del evento |
| operacion | VARCHAR(20) | no | LOGIN, LOGOUT, ALTA, MODIFICACION, BAJA, BORRADO |
| tabla | VARCHAR(40) | no | Tabla afectada |
| detalle | VARCHAR(255) | no | Descripcion legible de la operacion |

## 6. intervenciones (RF005)

| Atributo | Tipo | Nulo | Descripcion |
|----------|------|------|-------------|
| **id_intervencion** | INT UNSIGNED AI | no | Identificador de la intervencion |
| **id_equipo** | INT UNSIGNED FK | no | Equipo intervenido |
| **id_tipo_intervencion** | INT UNSIGNED FK | no | Preventiva, Correctiva, Instalacion... |
| fecha | DATE | no | Fecha de la intervencion (no futura) |
| tecnico_responsable | VARCHAR(120) | no | Tecnico que ejecuto el trabajo |
| observaciones | VARCHAR(500) | no | Detalle del trabajo realizado |

## 7. equipos (RF006 - modulo principal)

| Atributo | Tipo | Nulo | Descripcion |
|----------|------|------|-------------|
| **id_equipo** | INT UNSIGNED AI | no | Identificador del equipo |
| nombre | VARCHAR(120) | no | Nombre o descripcion del equipo |
| **id_tipo_equipo** | INT UNSIGNED FK | no | Categoria (Monitor, Respirador...) |
| **id_modelo** | INT UNSIGNED FK | no | Modelo (de ahi se deriva la marca) |
| numero_serie | VARCHAR(60) UK | no | Numero de serie del fabricante |
| garantia_meses | INT UNSIGNED | no | Garantia en meses (CHECK 0..120) |
| **id_pais** | INT UNSIGNED FK | no | Pais de origen |
| **id_proveedor** | INT UNSIGNED FK | no | Proveedor |
| fecha_compra | DATE | no | Fecha de compra (no futura) |
| codigo_interno | VARCHAR(40) UK | no | Codigo de inventario del hospital |
| **id_ubicacion** | INT UNSIGNED FK | no | Ubicacion fisica actual |
| estado | ENUM | no | ACTIVO / INACTIVO (baja logica) |
| fecha_registro | DATETIME | no | Alta del registro |

## 8. ubicaciones (RF007)

| Atributo | Tipo | Nulo | Descripcion |
|----------|------|------|-------------|
| **id_ubicacion** | INT UNSIGNED AI | no | Identificador de la ubicacion |
| nombre | VARCHAR(80) UK | no | CTI, Emergencia, Policlinica... |
| descripcion | VARCHAR(150) | si | Detalle de la ubicacion |
| **id_institucion** | INT UNSIGNED FK | no | Institucion a la que pertenece |

## 9. instituciones (RF008)

| Atributo | Tipo | Nulo | Descripcion |
|----------|------|------|-------------|
| **id_institucion** | INT UNSIGNED AI | no | Identificador de la institucion |
| nombre | VARCHAR(120) UK | no | Nombre del centro de salud |
| direccion | VARCHAR(150) | no | Direccion |
| telefono | VARCHAR(30) | no | Telefono |
| ciudad | VARCHAR(80) | no | Ciudad |
| **id_pais** | INT UNSIGNED FK | no | Pais |

## 10. paises (RF009)

| Atributo | Tipo | Nulo | Descripcion |
|----------|------|------|-------------|
| **id_pais** | INT UNSIGNED AI | no | Identificador del pais |
| nombre | VARCHAR(80) UK | no | Nombre del pais |

## 11. tipo_equipo (RF010)

| Atributo | Tipo | Nulo | Descripcion |
|----------|------|------|-------------|
| **id_tipo_equipo** | INT UNSIGNED AI | no | Identificador del tipo |
| nombre | VARCHAR(80) UK | no | Monitor, Respirador, Tomografo... |
| descripcion | VARCHAR(150) | si | Descripcion del tipo |

## 12. proveedores (RF011)

| Atributo | Tipo | Nulo | Descripcion |
|----------|------|------|-------------|
| **id_proveedor** | INT UNSIGNED AI | no | Identificador del proveedor |
| nombre | VARCHAR(120) UK | no | Razon social |
| direccion | VARCHAR(150) | no | Direccion |
| telefono | VARCHAR(30) | no | Telefono |
| email | VARCHAR(100) UK | no | Correo electronico valido |

## 13. modelos (RF012)

| Atributo | Tipo | Nulo | Descripcion |
|----------|------|------|-------------|
| **id_modelo** | INT UNSIGNED AI | no | Identificador del modelo |
| nombre | VARCHAR(80) | no | Nombre del modelo |
| **id_marca** | INT UNSIGNED FK | no | Marca a la que pertenece |

Restriccion `UNIQUE (id_marca, nombre)`: el nombre se repite solo entre modelos
de la misma marca.

## 14. marcas (RF013)

| Atributo | Tipo | Nulo | Descripcion |
|----------|------|------|-------------|
| **id_marca** | INT UNSIGNED AI | no | Identificador de la marca |
| nombre | VARCHAR(80) UK | no | Philips, Siemens, GE, Samsung, Mindray... |

## 15. tipo_intervencion (RF014)

| Atributo | Tipo | Nulo | Descripcion |
|----------|------|------|-------------|
| **id_tipo_intervencion** | INT UNSIGNED AI | no | Identificador del tipo |
| nombre | VARCHAR(60) UK | no | Preventiva, Correctiva, Instalacion, Mantenimiento, Calibracion |
| descripcion | VARCHAR(150) | si | Descripcion del tipo |
