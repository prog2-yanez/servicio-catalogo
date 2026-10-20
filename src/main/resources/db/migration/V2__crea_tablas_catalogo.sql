-- Copia local del catálogo de la cátedra.
-- Los IDs son los de la cátedra, por eso no se autogeneran.
-- Las bajas son lógicas: se marca habilitado = false y la fila se conserva.

CREATE TABLE categoria_profesional (
    id             BIGINT       PRIMARY KEY,
    nombre         VARCHAR(255) NOT NULL,
    descripcion    TEXT,
    habilitado     BOOLEAN      NOT NULL,
    creado_en      TIMESTAMPTZ  NOT NULL,
    actualizado_en TIMESTAMPTZ  NOT NULL
);

CREATE TABLE profesional (
    id             BIGINT       PRIMARY KEY,
    categoria_id   BIGINT       NOT NULL REFERENCES categoria_profesional (id),
    nombre         VARCHAR(255) NOT NULL,
    apellido       VARCHAR(255) NOT NULL,
    habilitado     BOOLEAN      NOT NULL,
    creado_en      TIMESTAMPTZ  NOT NULL,
    actualizado_en TIMESTAMPTZ  NOT NULL
);

CREATE TABLE horario_semanal (
    id                     BIGINT      PRIMARY KEY,
    profesional_id         BIGINT      NOT NULL REFERENCES profesional (id),
    dia_semana             VARCHAR(9)  NOT NULL
        CHECK (dia_semana IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY')),
    hora_inicio            TIME        NOT NULL,
    hora_fin               TIME        NOT NULL,
    duracion_turno_minutos INTEGER     NOT NULL,
    habilitado             BOOLEAN     NOT NULL,
    creado_en              TIMESTAMPTZ NOT NULL,
    actualizado_en         TIMESTAMPTZ NOT NULL
);

-- Versión del catálogo aplicada localmente. Tiene como máximo una fila;
-- si está vacía, todavía no se cargó ningún snapshot.
CREATE TABLE version_catalogo (
    id             SMALLINT    PRIMARY KEY CHECK (id = 1),
    version        BIGINT      NOT NULL,
    actualizado_en TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_profesional_categoria ON profesional (categoria_id);
CREATE INDEX idx_horario_semanal_profesional ON horario_semanal (profesional_id);
