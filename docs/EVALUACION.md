<!-- evaluacion:inicio -->
# Evaluación — prog2-yanez/servicio-catalogo

> Documento **generado automáticamente** por la cátedra de Programación II (UM): RepoSense + análisis de calidad de **7 dimensiones**. **No editar a mano**: se regenera en cada evaluación.
> Rama evaluada: `main`. Análisis **heurístico** (regex + métricas) + build: es una criba para orientar la revisión, **no una nota**. La **documentación** del repositorio no se evalúa.
> Generado: 2026-11-18.

## 1. Veredicto

| Aspecto | Resultado |
|---|---|
| Índice de calidad | **78/100** |
| Tipo de repositorio | `backend` |
| Build (`mvnw verify` / `gradlew check`) | OK |
| Código (LOC) | 4,813 |
| Archivos de código | 138 |
| Clases | 146 |
| Código dentro de clases (%POO) | 71% |
| Funciones fuera de clases | 0 |
| Duplicación (puerta) | 0.8% |
| Duplicación lógica | 0.9% |
| Duplicación de boilerplate | 10.4% |
| Cobertura | 99% |
| Tope duro aplicado a D1 | `POO_BAJO` → D1 ≤ 60 |

> Este informe **describe** lo que el análisis midió con los criterios ya establecidos de la materia. No es una nota.

## 2. Semáforo de aprobación

Criterios de la cátedra (`CRITERIOS-CALIDAD.md`): **Aprobar** se juega con el perfil `estandar`; **destacar**, con el `exigente` (meta de excelencia).

| Semáforo | Resultado |
|---|---|
| Aprobar (`estandar`) | **cumple** — 8/11 |
| Destacar (`exigente`) | **no cumple** — 6/11 |

**Para destacar, falta cumplir:** SOLID; POO

## 3. Autoría (RepoSense)

| Autor | Commits | Código (LOC) | Tests (LOC) | Docs (LOC) | Días activos | % del código |
|---|---:|---:|---:|---:|---:|---:|
| Guadalupe Yañez | 87 | 2,600 | 3,266 | 353 | 22 | 100% |

> _El LOC de autoría lo calcula RepoSense sobre el historial y puede diferir del LOC de código del análisis (que mira el estado de `main`)._

## 4. Dimensiones evaluadas

Cada dimensión indica **qué mide**, **por qué importa** y **tu resultado**. Cuando el puntaje queda por debajo de la referencia, se agregan preguntas para **investigar la causa** (no hay recetas).

| Dimensión | Peso | Puntaje |
|:--|--:|--:|
| **D1** Diseño OO, SOLID y patrones (20%) | 20% | 7 |
| **D2** Arquitectura hexagonal — backend (15%) | 15% | 100 |
| **D3** Frontend KMP: MVVM y buenas prácticas (10%) | 10% | N/A |
| **D4** Integración y contrato (10%) | 10% | 90 |
| **D5** Seguridad y robustez (10%) | 10% | 100 |
| **D6** Pruebas y cobertura (20%) | 20% | 100 |
| **D7** Proceso y entrega (15%) | 15% | 100 |

### D1. Diseño OO, SOLID y patrones (20%)

- **Qué mide:** Métricas orientadas a objetos (clases, métodos, campos, cohesión, acoplamiento, herencia) y proxies de SOLID, más patrones de diseño.
- **Por qué importa:** El eje de la materia es el buen uso de la POO y su arquitectura: un diseño con clases claras y poco acopladas es más fácil de entender, probar y cambiar.
- **Tu resultado:** **7/100**
- **Para investigar la causa:**
    - ¿Se pensó el modelo de clases antes de codificar o se agrupó después?
    - ¿Qué clases concentran muchas responsabilidades a la vez?
    - ¿Los colaboradores llegan como parámetros o se crean por dentro?

### D2. Arquitectura hexagonal — backend (15%)

- **Qué mide:** Separación en capas (dominio, puertos de entrada/salida, adaptadores) y dirección de las dependencias del backend.
- **Por qué importa:** Aislar el dominio de la infraestructura permite cambiar la base de datos, el framework o el transporte sin tocar las reglas de negocio.
- **Tu resultado:** **100/100**

### D4. Integración y contrato (10%)

- **Qué mide:** Cumplimiento del contrato de integración de la cátedra (formato de errores, paginación, autenticación, endpoints esperados).
- **Por qué importa:** Los servicios deben poder integrarse entre sí y con la app sin sorpresas; el contrato es lo que hacen cumplir las pruebas de integración.
- **Tu resultado:** **90/100**

### D5. Seguridad y robustez (10%)

- **Qué mide:** Credenciales embebidas, consultas SQL por concatenación, y presencia de config de entorno; aislamiento de secretos.
- **Por qué importa:** Un secreto en el repositorio o una consulta construida por concatenación son riesgos concretos, no hipotéticos.
- **Tu resultado:** **100/100**

### D6. Pruebas y cobertura (20%)

- **Qué mide:** Cantidad y resultado de pruebas y cobertura de línea (JaCoCo/Kover, Surefire/Failsafe) cuando hay build.
- **Por qué importa:** Las pruebas son la red de seguridad para refactorizar e integrar; la cobertura acota las zonas sin verificar.
- **Tu resultado:** **100/100**

### D7. Proceso y entrega (15%)

- **Qué mide:** Artefactos verificables de proceso/entrega: `compose`, `.env.example`, CI (`.github/workflows/`), `CODEOWNERS` y wrapper de build (`mvnw`/`gradlew`). **No** puntúa documentación.
- **Por qué importa:** Un proyecto que compila, se levanta y se valida solo es más fácil de revisar y de integrar en equipo.
- **Tu resultado:** **100/100**

## 5. Señales detectadas

El análisis encontró las siguientes señales, con el archivo y la línea donde se originan cuando están disponibles:

**Archivos con más señales**

| Archivo | Señales |
|---|---:|
| `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/dto/SnapshotResponse.java` | 8 |
| `src/main/java/prog2/serviciocatalogo/shared/web/ManejadorErroresApi.java` | 7 |
| `src/main/java/prog2/serviciocatalogo/config/CorsConfig.java` | 3 |
| `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/CambiosCatalogoRedisAdapter.java` | 3 |
| `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/CatalogoPersistenceAdapter.java` | 3 |
| `src/main/java/prog2/serviciocatalogo/catalogo/application/usecases/ActualizarCatalogoService.java` | 3 |
| `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/CatedraRestAdapter.java` | 2 |
| `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/specification/ProfesionalSpecifications.java` | 2 |
| `src/main/java/prog2/serviciocatalogo/catalogo/application/usecases/ConsultarProfesionalService.java` | 2 |
| `src/main/java/prog2/serviciocatalogo/catalogo/domain/model/HorarioSemanal.java` | 2 |

**DIP/concretos** (26)
- clase 'ActualizarCatalogoService' muy acoplada (CBO=21) — `src/main/java/prog2/serviciocatalogo/catalogo/application/usecases/ActualizarCatalogoService.java:23`
- clase 'ConsultarProfesionalService' muy acoplada (CBO=13) — `src/main/java/prog2/serviciocatalogo/catalogo/application/usecases/ConsultarProfesionalService.java:16`
- clase 'ConsultarProfesionalService' instancia 4 concretos (CBO=13) — `src/main/java/prog2/serviciocatalogo/catalogo/application/usecases/ConsultarProfesionalService.java:16`
- clase 'InicializarCatalogoService' muy acoplada (CBO=9) — `src/main/java/prog2/serviciocatalogo/catalogo/application/usecases/InicializarCatalogoService.java:12`
- clase 'ProcesarCatalogoActualizadoService' muy acoplada (CBO=13) — `src/main/java/prog2/serviciocatalogo/catalogo/application/usecases/ProcesarCatalogoActualizadoService.java:13`
- clase 'SincronizarCatalogoService' muy acoplada (CBO=11) — `src/main/java/prog2/serviciocatalogo/catalogo/application/usecases/SincronizarCatalogoService.java:13`
- clase 'HorarioSemanal' muy acoplada (CBO=8) — `src/main/java/prog2/serviciocatalogo/catalogo/domain/model/HorarioSemanal.java:10`
- clase 'SituacionSincronizacion' muy acoplada (CBO=8) — `src/main/java/prog2/serviciocatalogo/catalogo/domain/model/SituacionSincronizacion.java:6`
- clase 'CambiosCatalogoRedisAdapter' muy acoplada (CBO=20) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/CambiosCatalogoRedisAdapter.java:26`
- clase 'CambiosCatalogoRedisAdapter' instancia 3 concretos (CBO=20) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/CambiosCatalogoRedisAdapter.java:26`
- clase 'CatedraRestAdapter' muy acoplada (CBO=15) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/CatedraRestAdapter.java:15`
- clase 'CatedraRestAdapter' instancia 4 concretos (CBO=15) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/CatedraRestAdapter.java:15`
- clase 'CatalogoActualizadoListener' muy acoplada (CBO=13) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/messaging/CatalogoActualizadoListener.java:20`
- clase 'KafkaConfig' muy acoplada (CBO=8) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/messaging/KafkaConfig.java:12`
- clase 'CatalogoPersistenceAdapter' muy acoplada (CBO=18) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/CatalogoPersistenceAdapter.java:20`
- clase 'ConsultaCategoriasPersistenceAdapter' muy acoplada (CBO=10) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/ConsultaCategoriasPersistenceAdapter.java:17`
- clase 'ConsultaProfesionalesPersistenceAdapter' muy acoplada (CBO=15) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/ConsultaProfesionalesPersistenceAdapter.java:23`
- clase 'JpaEventoProcesadoRepository' muy acoplada (CBO=11) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/repository/JpaEventoProcesadoRepository.java:9`
- clase 'ProfesionalSpecifications' muy acoplada (CBO=8) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/specification/ProfesionalSpecifications.java:9`
- clase 'ProfesionalController' muy acoplada (CBO=10) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/web/ProfesionalController.java:22`
- clase 'CorsConfig' muy acoplada (CBO=25) — `src/main/java/prog2/serviciocatalogo/config/CorsConfig.java:26`
- clase 'CorsConfig' instancia 4 concretos (CBO=25) — `src/main/java/prog2/serviciocatalogo/config/CorsConfig.java:26`
- clase 'RechazoComoProblema' muy acoplada (CBO=12) — `src/main/java/prog2/serviciocatalogo/config/CorsConfig.java:52`
- clase 'JwtConfig' muy acoplada (CBO=10) — `src/main/java/prog2/serviciocatalogo/config/JwtConfig.java:14`
- clase 'SecurityConfig' muy acoplada (CBO=20) — `src/main/java/prog2/serviciocatalogo/config/SecurityConfig.java:20`
- clase 'ManejadorErroresApi' muy acoplada (CBO=51) — `src/main/java/prog2/serviciocatalogo/shared/web/ManejadorErroresApi.java:32`

**SRP/responsabilidad** (25)
- clase 'ActualizarCatalogoService' con baja cohesión (40%) — `src/main/java/prog2/serviciocatalogo/catalogo/application/usecases/ActualizarCatalogoService.java:23`
- método 'ActualizarCatalogoService.aplicarVersionesNuevas' con complejidad 6 — `src/main/java/prog2/serviciocatalogo/catalogo/application/usecases/ActualizarCatalogoService.java:55`
- método 'CategoriaProfesional.CategoriaProfesional' con 6 parámetros — `src/main/java/prog2/serviciocatalogo/catalogo/domain/model/CategoriaProfesional.java:6`
- método 'HorarioSemanal.HorarioSemanal' con 9 parámetros — `src/main/java/prog2/serviciocatalogo/catalogo/domain/model/HorarioSemanal.java:10`
- método 'Profesional.Profesional' con 7 parámetros — `src/main/java/prog2/serviciocatalogo/catalogo/domain/model/Profesional.java:6`
- método 'SnapshotCatalogo.SnapshotCatalogo' con 5 parámetros — `src/main/java/prog2/serviciocatalogo/catalogo/domain/model/SnapshotCatalogo.java:6`
- clase 'CambiosCatalogoRedisAdapter' con baja cohesión (19%) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/CambiosCatalogoRedisAdapter.java:26`
- método 'SnapshotResponse.SnapshotResponse' de 43 líneas — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/dto/SnapshotResponse.java:14`
- método 'SnapshotResponse.SnapshotResponse' con 5 parámetros — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/dto/SnapshotResponse.java:14`
- método 'SnapshotResponse.CategoryResponse' con 6 parámetros — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/dto/SnapshotResponse.java:35`
- método 'CategoryResponse.CategoryResponse' con 6 parámetros — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/dto/SnapshotResponse.java:35`
- método 'SnapshotResponse.ProfessionalResponse' con 7 parámetros — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/dto/SnapshotResponse.java:45`
- método 'ProfessionalResponse.ProfessionalResponse' con 7 parámetros — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/dto/SnapshotResponse.java:45`
- método 'SnapshotResponse.WeeklyScheduleResponse' con 9 parámetros — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/dto/SnapshotResponse.java:55`
- método 'WeeklyScheduleResponse.WeeklyScheduleResponse' con 9 parámetros — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/dto/SnapshotResponse.java:55`
- clase 'CatalogoPersistenceAdapter' grande (LOC=55, métodos=5, campos=8) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/CatalogoPersistenceAdapter.java:20`
- método 'CatalogoPersistenceAdapter.CatalogoPersistenceAdapter' con 8 parámetros — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/CatalogoPersistenceAdapter.java:31`
- método 'ProfesionalSpecifications.segun' con complejidad 7 — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/specification/ProfesionalSpecifications.java:16`
- método 'ProfesionalConAgendaResponse.ProfesionalConAgendaResponse' con 7 parámetros — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/web/dto/ProfesionalConAgendaResponse.java:7`
- método 'ProfesionalResumenResponse.ProfesionalResumenResponse' con 5 parámetros — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/web/dto/ProfesionalResumenResponse.java:5`
- clase 'ManejadorErroresApi' grande (LOC=133, métodos=17, campos=2) — `src/main/java/prog2/serviciocatalogo/shared/web/ManejadorErroresApi.java:32`
- método 'ManejadorErroresApi.handleExceptionInternal' con 5 parámetros — `src/main/java/prog2/serviciocatalogo/shared/web/ManejadorErroresApi.java:124`
- método 'ManejadorErroresApi.conDetalle' con 5 parámetros — `src/main/java/prog2/serviciocatalogo/shared/web/ManejadorErroresApi.java:140`
- método 'ManejadorErroresApi.tituloSegunEstado' con complejidad 7 — `src/main/java/prog2/serviciocatalogo/shared/web/ManejadorErroresApi.java:164`
- método 'ManejadorErroresApi.problema' con 5 parámetros — `src/main/java/prog2/serviciocatalogo/shared/web/ManejadorErroresApi.java:175`

**OCP/type-switch** (1)
- clase 'ManejadorErroresApi' con 3 type-checks — `src/main/java/prog2/serviciocatalogo/shared/web/ManejadorErroresApi.java:32`

**?** (4)
- clase 'CatedraAccesoDenegadoException' con herencia profunda (DIT=2) — `src/main/java/prog2/serviciocatalogo/catalogo/domain/exception/CatedraAccesoDenegadoException.java:3`
- clase 'CatedraNoDisponibleException' con herencia profunda (DIT=2) — `src/main/java/prog2/serviciocatalogo/catalogo/domain/exception/CatedraNoDisponibleException.java:3`
- clase 'CategoriaNoEncontradaException' con herencia profunda (DIT=2) — `src/main/java/prog2/serviciocatalogo/catalogo/domain/exception/CategoriaNoEncontradaException.java:3`
- clase 'ProfesionalNoEncontradoException' con herencia profunda (DIT=2) — `src/main/java/prog2/serviciocatalogo/catalogo/domain/exception/ProfesionalNoEncontradoException.java:3`

## 6. Evolución por commit

Métricas calculadas sobre el estado de `main` en **cada commit** (8 commits con trabajo), del primero al último. La calidad usa el perfil `exigente`; el aporte de código lo mide RepoSense. El índice puede **bajar** al crecer el proyecto: se acumulan señales.

```mermaid
xychart-beta
    title "Índice de calidad a lo largo de los commits"
    x-axis ["83d3d53", "99beebb", "e503b78", "26112b9", "1a1ee17", "bc1a212", "995a365", "67fa824"]
    y-axis "Índice" 0 --> 100
    line [0, 15, 35, 40, 48, 57, 68, 67]
```

| # | Fecha | Commit | Autor | LOC | Índice | %POO | Duplic. | Sueltas |
|---|---|---|---|---:|---:|---:|---:|---:|
| 1 | 2026-09-10 | `83d3d53` | Ing. Daniel Quinteros | 0 | 0 | 0% | 0.0% | 0 |
| 2 | 2026-09-10 | `99beebb` | Ing. Daniel Quinteros | 0 | 15 | 0% | 0.0% | 0 |
| 3 | 2026-10-09 | `e503b78` | Guadalupe Yañez | 41 | 35 | 56% | 0.0% | 0 |
| 4 | 2026-10-12 | `26112b9` | Guadalupe Yañez | 41 | 40 | 56% | 0.0% | 0 |
| 5 | 2026-10-15 | `1a1ee17` | Guadalupe Yañez | 89 | 48 | 56% | 0.0% | 0 |
| 6 | 2026-10-19 | `bc1a212` | Guadalupe Yañez | 1,185 | 57 | 72% | 0.0% | 0 |
| 7 | 2026-10-27 | `995a365` | Guadalupe Yañez | 2,672 | 68 | 71% | 0.0% | 0 |
| 8 | 2026-11-07 | `67fa824` | Guadalupe Yañez | 4,813 | 67 | 71% | 0.8% | 0 |

---
_Informe generado por la cátedra. Las dimensiones y umbrales corresponden a `calidad.py` (perfil `exigente`); el semáforo, a `CRITERIOS-CALIDAD.md`. La documentación del repositorio no se evalúa. Ante dudas, revisá el archivo señalado._
<!-- evaluacion:fin -->
