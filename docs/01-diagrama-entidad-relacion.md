# 01 - Diagrama Entidad-Relacion (MER)

**Sistema:** Gestion de Equipamiento Clinico Hospitalario

## Leyenda de relaciones

| Relacion | Cardinalidad | Comentario |
|----------|--------------|------------|
| pais -> institucion | 1:N | un pais tiene muchas instituciones |
| institucion -> ubicacion | 1:N | una institucion tiene muchas ubicaciones |
| perfil -> usuario | 1:N | un perfil agrupa muchos usuarios |
| perfil <-> funcionalidad | N:N | tabla `perfil_funcionalidad` |
| usuario -> auditoria | 1:N (nullable) | `ON DELETE SET NULL` |
| marca -> modelo | 1:N | todo modelo pertenece a una marca |
| tipo equipo / modelo / pais / proveedor / ubicacion -> equipo | N:1 | equipo con todas sus FK |
| equipo -> intervencion | 1:N | historial de intervenciones |
| tipo intervencion -> intervencion | 1:N | |

## Diagrama (Mermaid)

```mermaid
erDiagram
    PAISES ||--o{ INSTITUCIONES : "tiene"
    PAISES ||--o{ EQUIPOS : "pais de origen"
    INSTITUCIONES ||--o{ UBICACIONES : "contiene"
    PERFILES ||--o{ USUARIOS : "agrupa"
    PERFILES ||--o{ PERFIL_FUNCIONALIDAD : "incluye"
    FUNCIONALIDADES ||--o{ PERFIL_FUNCIONALIDAD : "otorga"
    USUARIOS ||--o{ AUDITORIA : "registra"
    MARCAS ||--o{ MODELOS : "fabrica"
    TIPO_EQUIPO ||--o{ EQUIPOS : "clasifica"
    MODELOS ||--o{ EQUIPOS : "identifica"
    PROVEEDORES ||--o{ EQUIPOS : "suministra"
    UBICACIONES ||--o{ EQUIPOS : "aloja"
    EQUIPOS ||--o{ INTERVENCIONES : "recibe"
    TIPO_INTERVENCION ||--o{ INTERVENCIONES : "clasifica"

    PAISES {
        int id_pais PK
        varchar nombre UK
    }
    INSTITUCIONES {
        int id_institucion PK
        varchar nombre UK
        varchar direccion
        varchar telefono
        varchar ciudad
        int id_pais FK
    }
    UBICACIONES {
        int id_ubicacion PK
        varchar nombre UK
        varchar descripcion
        int id_institucion FK
    }
    PERFILES {
        int id_perfil PK
        varchar nombre UK
        varchar descripcion
        enum estado
    }
    FUNCIONALIDADES {
        int id_funcionalidad PK
        varchar nombre UK
        varchar descripcion
        enum estado
    }
    PERFIL_FUNCIONALIDAD {
        int id_perfil PK_FK
        int id_funcionalidad PK_FK
    }
    USUARIOS {
        int id_usuario PK
        varchar nombre
        varchar apellido
        varchar cedula UK
        date fecha_nacimiento
        varchar telefono
        varchar email UK
        varchar usuario UK
        varchar contrasena
        int id_perfil FK
        enum estado
    }
    AUDITORIA {
        bigint id_auditoria PK
        int id_usuario FK
        date fecha
        time hora
        varchar operacion
        varchar tabla
        varchar detalle
    }
    MARCAS {
        int id_marca PK
        varchar nombre UK
    }
    MODELOS {
        int id_modelo PK
        varchar nombre
        int id_marca FK
    }
    TIPO_EQUIPO {
        int id_tipo_equipo PK
        varchar nombre UK
        varchar descripcion
    }
    PROVEEDORES {
        int id_proveedor PK
        varchar nombre UK
        varchar direccion
        varchar telefono
        varchar email UK
    }
    EQUIPOS {
        int id_equipo PK
        varchar nombre
        int id_tipo_equipo FK
        int id_modelo FK
        varchar numero_serie UK
        int garantia_meses
        int id_pais FK
        int id_proveedor FK
        date fecha_compra
        varchar codigo_interno UK
        int id_ubicacion FK
        enum estado
    }
    TIPO_INTERVENCION {
        int id_tipo_intervencion PK
        varchar nombre UK
        varchar descripcion
    }
    INTERVENCIONES {
        int id_intervencion PK
        int id_equipo FK
        int id_tipo_intervencion FK
        date fecha
        varchar tecnico_responsable
        varchar observaciones
    }
```

## Reglas de negocio reflejadas en el MER

1. Un equipo no puede existir sin tipo, modelo, pais, proveedor ni ubicacion
   (todas sus claves foraneas son NOT NULL).
2. Un modelo pertenece a exactamente una marca; de ahi se deriva la marca del
   equipo (3FN).
3. Una ubicacion pertenece a una institucion, y la institucion a un pais.
4. Las intervenciones son historicas: siempre referencian un equipo y un tipo
   de intervencion existentes.
5. La auditoria conserva la referencia al usuario aun si este se elimina
   (`ON DELETE SET NULL`).
