# Servicio de catálogo

Servicio backend del sistema de turnos médicos. Mantiene una copia local del catálogo de profesionales de la cátedra (categorías, profesionales y horarios semanales) y la ofrece para búsquedas y consultas a la aplicación y al servicio de Turnos.

Este repositorio contiene la base técnica del servicio: arranque con Spring Boot 4, PostgreSQL, migraciones con Flyway, endpoint de salud y pruebas de integración con una base real.

## Qué hace el servicio

- **Guarda su propia copia del catálogo** en PostgreSQL, así las búsquedas no dependen de que la cátedra esté disponible.
- **Obtiene los datos del servicio central de la cátedra** (REST, Redis y Kafka) y los mantiene al día.
- **Responde consultas** sobre profesionales y horarios para la app Android y para el servicio de Turnos.

El resto del sistema (servicio de Turnos, app y documentación) vive en repositorios aparte.

## Tecnologías

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje |
| Spring Boot 4.1.1 | Aplicación web, JPA y Actuator |
| Maven (wrapper `mvnw`) | Compilación y dependencias |
| PostgreSQL 17 | Base de datos |
| Flyway | Migraciones del esquema |
| Testcontainers | Pruebas contra un PostgreSQL real y temporal |
| Lombok | Menos código repetitivo |

## Requisitos

- JDK 21.
- PostgreSQL disponible para ejecutar el servicio.
- Docker en marcha para las pruebas.
- No hace falta instalar Maven: `./mvnw` descarga la versión correcta. En Windows se usa `mvnw.cmd`.

## Configuración

El servicio toma los datos de conexión de estas variables de entorno:

| Variable | Para qué sirve |
|---|---|
| `SPRING_DATASOURCE_URL` | URL de conexión, por ejemplo `jdbc:postgresql://localhost:5432/catalogo` |
| `SPRING_DATASOURCE_USERNAME` | Usuario con el que se conecta el servicio |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de ese usuario |

No hay datos de conexión en `application.properties`.

## Ejecutar el servicio

Con PostgreSQL en marcha, definir las variables y ejecutar:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/catalogo
export SPRING_DATASOURCE_USERNAME=<usuario>
export SPRING_DATASOURCE_PASSWORD=<contraseña>
./mvnw spring-boot:run
```

Desde IntelliJ se pueden definir las variables `SPRING_DATASOURCE_*` en la configuración de ejecución de `ServicioCatalogoApplication`.

Si falta alguna variable o la base no responde, el servicio no arranca. El error indica qué falló sin mostrar contraseñas.

### Comprobar que funciona

```bash
curl http://localhost:8080/actuator/health
```

La respuesta esperada es `{"status":"UP"}`: el servicio está en marcha y llega a la base. Es el único endpoint de Actuator que se expone y no muestra detalles internos.

## Pruebas

```bash
./mvnw verify
```

Compila, ejecuta las pruebas y empaqueta el servicio. Las pruebas levantan un PostgreSQL temporal con Testcontainers, por lo que Docker tiene que estar en marcha. No usan la base de desarrollo.

## Base de datos y migraciones

- **Flyway es el único que crea o modifica el esquema.** Las migraciones están en `src/main/resources/db/migration/` y se nombran `V1__descripcion.sql`, `V2__descripcion.sql`, etc.
- Una migración ya aplicada no se edita: cada cambio es una migración nueva.
- Hibernate trabaja con `ddl-auto=validate`: solo comprueba que las entidades coincidan con el esquema y nunca lo modifica.

## Organización del código

Arquitectura hexagonal: las reglas del negocio no dependen de Spring, de la base ni de Kafka.

```
domain/model            entidades y reglas del negocio
application/usecases    casos de uso (una clase por acción del servicio)
application/port        interfaces que el núcleo ofrece (in) y necesita (out)
infrastructure/web      controllers y DTO
infrastructure/persistence   entidades JPA y repositorios
infrastructure/messaging     integración con Kafka
```

El dominio, las entidades JPA y los DTO son clases distintas, conectadas con mappers.

## Decisiones técnicas

| Decisión | Motivo |
|---|---|
| Java 21 | Versión LTS estable, instalada en el entorno de desarrollo |
| PostgreSQL | Base relacional completa y de uso muy extendido |
| Flyway | Migraciones en archivos SQL numerados, simples de revisar y repetir |
| Hibernate en modo `validate` | El esquema tiene un único responsable y queda versionado |
| Pruebas con Testcontainers | Se prueba contra PostgreSQL real, no contra una base simulada |
| Configuración por variables de entorno | No hay contraseñas ni direcciones en el repositorio |
| Un solo endpoint de Actuator, sin detalles | No se expone información interna del servicio |

**Alumna:** *Guadalupe Yañez - 2026*.