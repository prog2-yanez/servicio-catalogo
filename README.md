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
| Docker Compose | Base de datos local |
| Testcontainers | Pruebas contra un PostgreSQL real y temporal |
| Lombok | Menos código repetitivo |

## Requisitos

- JDK 21.
- Docker con Docker Compose (para la base local y para las pruebas).
- No hace falta instalar Maven: `./mvnw` descarga la versión correcta. En Windows se usa `mvnw.cmd`.
- Node.js 18 o superior, solo para correr la colección de Postman con Newman.

## Configuración

Copiar el archivo de ejemplo y completar las contraseñas:

```bash
cp .env.example .env
```

| Variable | Para qué sirve |
|---|---|
| `POSTGRES_DB` | Nombre de la base del catálogo |
| `POSTGRES_USER` | Usuario de esa base |
| `POSTGRES_PASSWORD` | Contraseña de ese usuario |
| `DB_PORT` | Puerto local donde se publica PostgreSQL (por defecto `5433`) |
| `SPRING_DATASOURCE_URL` | URL de conexión del servicio, por ejemplo `jdbc:postgresql://localhost:5433/catalogo` |
| `SPRING_DATASOURCE_USERNAME` | Usuario con el que se conecta el servicio |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña con la que se conecta el servicio |

- El puerto de `SPRING_DATASOURCE_URL` tiene que coincidir con `DB_PORT`.
- El puerto por defecto es `5433` para no chocar con una instalación local de PostgreSQL, que usa `5432`.
- `.env` no se versiona. Docker Compose lo lee solo; Spring Boot no, por eso se carga a mano al ejecutar el servicio (ver más abajo).

## Integración con la cátedra

El servicio se conecta al servicio central de la cátedra por REST, Redis y Kafka. Para eso usa una cuenta técnica del proyecto, que se crea una sola vez, a mano.

### Alta de la cuenta técnica (paso manual único)

1. Con Postman, enviar `POST /api/student/register` a la URL de la API que entrega la cátedra, con un JSON que tenga `login`, `password` y `groupId` (y opcionalmente nombre, apellido y email).
2. La respuesta trae `id_token` (el JWT técnico, válido por un año) y el objeto `integration` con los datos de Redis y Kafka.
3. Copiar esos valores al `.env`:

| Variable | Campo de la respuesta |
|---|---|
| `CATEDRA_API_URL` | URL base de la API (la entrega la cátedra) |
| `CATEDRA_JWT` | `id_token` |
| `CATEDRA_REDIS_HOST` | `integration.redisHost` |
| `CATEDRA_REDIS_PORT` | `integration.redisPort` |
| `CATEDRA_REDIS_USERNAME` | `integration.redisUsername` |
| `CATEDRA_REDIS_PASSWORD` | `integration.redisPassword` |
| `CATEDRA_KAFKA_BOOTSTRAP_SERVERS` | `integration.kafkaBootstrapServers` |
| `CATEDRA_KAFKA_CONSUMER_GROUP` | `integration.kafkaConsumerGroupId` |
| `CATEDRA_KAFKA_TOPIC_CATALOGO` | `integration.kafkaCatalogTopic` |

- Si se pierde el JWT, `POST /api/authenticate` con el mismo usuario y contraseña devuelve uno nuevo y la configuración vigente.
- Estos valores no se suben al repositorio ni aparecen en logs, capturas o documentación.
- Si alguno se expone, hay que crear otra cuenta técnica y avisar a la cátedra.
- El servicio de turnos usa la misma cuenta técnica.

## Base de datos con Docker Compose

```bash
docker compose up -d
docker compose ps
```

Levanta PostgreSQL con los datos de `.env`. Los datos se guardan en un volumen, así que se conservan al detener o recrear el contenedor.

```bash
docker compose down        # detiene el contenedor y conserva los datos
docker compose down -v     # además borra los datos
```

Cambiar las contraseñas en `.env` no modifica las de una base ya creada: hay que borrar el volumen o cambiarlas dentro de PostgreSQL.

## Ejecutar el servicio

Con la base levantada, cargar las variables y ejecutar:

```bash
set -a
. ./.env
set +a
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

Compila, ejecuta las pruebas y empaqueta el servicio. Las pruebas levantan un PostgreSQL temporal con Testcontainers, por lo que Docker tiene que estar en marcha. No usan `.env` ni la base de desarrollo.

### Pruebas de la API con Postman y Newman

La carpeta `postman/` tiene una colección que prueba la API de punta a punta con el servicio levantado, igual que la usan Turnos y la app.

| Archivo | Qué tiene |
|---|---|
| `catalogo.postman_collection.json` | Un pedido por caso: categorías, profesionales (con sus filtros y su agenda), estado de la sincronización y health. Cada endpoint tiene al menos un caso exitoso y uno de error. |
| `local.postman_environment.json` | `baseUrl` (por defecto `http://localhost:8080`) y `token`, vacío. |

Con el servicio en marcha y el catálogo cargado:

```bash
npx newman run postman/catalogo.postman_collection.json -e postman/local.postman_environment.json
```

`npx` descarga Newman la primera vez; no hace falta instalarlo. También se puede importar la colección y el environment en Postman y ejecutarla desde ahí.

- Cada pedido verifica el status, el `Content-Type` y los campos principales; en los errores, que el cuerpo sea un ProblemDetail con su `type`.
- No hay ids escritos en la colección: el primer listado guarda una categoría y un profesional que existen y los pedidos siguientes los usan. Si el catálogo está vacío, los pedidos que necesitan un id se saltean con un aviso.
- El token se manda solo si el environment lo trae. Si hace falta uno, se completa en una copia local del environment, sin versionarla.

## Base de datos y migraciones

- **Flyway es el único que crea o modifica el esquema.** Las migraciones están en `src/main/resources/db/migration/` y se nombran `V1__descripcion.sql`, `V2__descripcion.sql`, etc.
- Una migración ya aplicada no se edita: cada cambio es una migración nueva.
- Hibernate trabaja con `ddl-auto=validate`: solo comprueba que las entidades coincidan con el esquema y nunca lo modifica.

## Organización del código

Arquitectura hexagonal: las reglas del negocio no dependen de Spring, de la base ni de Kafka. El código del catálogo está en `prog2.serviciocatalogo.catalogo`:

```
catalogo/
├── domain/
│   ├── model             categorías, profesionales, horarios semanales y versión del catálogo
│   └── ports             interfaces que el núcleo ofrece (in) y necesita (out)
├── application/
│   └── usecases          casos de uso, una clase por acción del servicio
└── infrastructure/
    ├── persistence       entidades JPA, repositorios Spring Data y mappers
    ├── web               controllers y DTO
    └── messaging         integración con Kafka
```

El dominio, las entidades JPA y los DTO son clases distintas, conectadas con mappers. El dominio usa `record` de Java y no importa nada de Spring ni de JPA.

### Base de datos

| Tabla | Contenido |
|---|---|
| `categoria_profesional` | Categorías de profesionales |
| `profesional` | Profesionales, con su categoría |
| `horario_semanal` | Franjas de atención semanales de cada profesional |
| `version_catalogo` | Versión del catálogo aplicada localmente (una sola fila) |

Los IDs son los de la cátedra y las bajas son lógicas (`habilitado = false`).

## Decisiones técnicas

| Decisión | Motivo |
|---|---|
| Java 21 | Versión LTS estable, instalada en el entorno de desarrollo |
| PostgreSQL | Base relacional completa y de uso muy extendido |
| Flyway | Migraciones en archivos SQL numerados, simples de revisar y repetir |
| Hibernate en modo `validate` | El esquema tiene un único responsable y queda versionado |
| Puerto local 5433 | Evita el choque con un PostgreSQL local en el 5432 |
| Pruebas con Testcontainers | Se prueba contra PostgreSQL real, no contra una base simulada |
| Configuración por variables de entorno | No hay contraseñas ni direcciones en el repositorio |
| Un solo endpoint de Actuator, sin detalles | No se expone información interna del servicio |

**Alumna:** *Guadalupe Yañez - 2026*.