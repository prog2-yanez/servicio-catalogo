-- Eventos de Kafka que ya se procesaron. Kafka puede entregar el mismo evento más de una vez;
-- si su eventId está acá, se descarta sin efectos.
CREATE TABLE evento_procesado (
    event_id     VARCHAR(100) PRIMARY KEY,
    tipo         VARCHAR(100) NOT NULL,
    procesado_en TIMESTAMPTZ  NOT NULL
);
