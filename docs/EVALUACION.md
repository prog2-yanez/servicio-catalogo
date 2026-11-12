<!-- evaluacion:inicio -->
# Evaluación — prog2-yanez/servicio-catalogo

> Documento **generado automáticamente** por la cátedra de Programación II (UM): RepoSense + análisis de calidad de **7 dimensiones**. **No editar a mano**: se regenera en cada evaluación.
> Rama evaluada: `main`. Análisis **heurístico** (regex + métricas) + build: es una criba para orientar la revisión, **no una nota**. La **documentación** del repositorio no se evalúa.
> Generado: 2026-11-12.

## 1. Veredicto

| Aspecto | Resultado |
|---|---|
| Índice de calidad | **77/100** |
| Tipo de repositorio | `backend` |
| Build (`mvnw verify` / `gradlew check`) | OK |
| Código (LOC) | 4,813 |
| Archivos de código | 138 |
| Clases | 146 |
| Código dentro de clases (%POO) | 73% |
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
| Aprobar (`estandar`) | **no cumple** — 7/11 |
| Destacar (`exigente`) | **no cumple** — 6/11 |

**Para aprobar, falta cumplir:** SOLID

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
| **D1** Diseño OO, SOLID y patrones (20%) | 20% | 3 |
| **D2** Arquitectura hexagonal — backend (15%) | 15% | 100 |
| **D3** Frontend KMP: MVVM y buenas prácticas (10%) | 10% | N/A |
| **D4** Integración y contrato (10%) | 10% | 90 |
| **D5** Seguridad y robustez (10%) | 10% | 100 |
| **D6** Pruebas y cobertura (20%) | 20% | 100 |
| **D7** Proceso y entrega (15%) | 15% | 100 |

### D1. Diseño OO, SOLID y patrones (20%)

- **Qué mide:** Métricas orientadas a objetos (clases, métodos, campos, cohesión, acoplamiento, herencia) y proxies de SOLID, más patrones de diseño.
- **Por qué importa:** El eje de la materia es el buen uso de la POO y su arquitectura: un diseño con clases claras y poco acopladas es más fácil de entender, probar y cambiar.
- **Tu resultado:** **3/100**
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

El análisis encontró las siguientes señales (archivo, sin número de línea a propósito):

**DIP/concretos** (69)
- clase 'CorsConfig' muy acoplada (CBO=25) — `src/main/java/prog2/serviciocatalogo/config/CorsConfig.java`
- clase 'CorsConfig' instancia 4 concretos (CBO=25) — `src/main/java/prog2/serviciocatalogo/config/CorsConfig.java`
- clase 'RechazoComoProblema' muy acoplada (CBO=12) — `src/main/java/prog2/serviciocatalogo/config/CorsConfig.java`
- clase 'JwtConfig' muy acoplada (CBO=10) — `src/main/java/prog2/serviciocatalogo/config/JwtConfig.java`
- clase 'SecurityConfig' muy acoplada (CBO=20) — `src/main/java/prog2/serviciocatalogo/config/SecurityConfig.java`
- clase 'ManejadorErroresApi' muy acoplada (CBO=51) — `src/main/java/prog2/serviciocatalogo/shared/web/ManejadorErroresApi.java`
- clase 'CambiosCatalogoRedisAdapter' muy acoplada (CBO=20) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/CambiosCatalogoRedisAdapter.java`
- clase 'CambiosCatalogoRedisAdapter' instancia 3 concretos (CBO=20) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/CambiosCatalogoRedisAdapter.java`
- clase 'CatedraRestAdapter' muy acoplada (CBO=15) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/CatedraRestAdapter.java`
- clase 'CatedraRestAdapter' instancia 4 concretos (CBO=15) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/CatedraRestAdapter.java`
- clase 'ConsultaCategoriasPersistenceAdapter' muy acoplada (CBO=10) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/ConsultaCategoriasPersistenceAdapter.java`
- clase 'CatalogoPersistenceAdapter' muy acoplada (CBO=18) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/CatalogoPersistenceAdapter.java`
- clase 'ConsultaProfesionalesPersistenceAdapter' muy acoplada (CBO=15) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/ConsultaProfesionalesPersistenceAdapter.java`
- clase 'JpaEventoProcesadoRepository' muy acoplada (CBO=11) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/repository/JpaEventoProcesadoRepository.java`
- clase 'ProfesionalSpecifications' muy acoplada (CBO=8) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/specification/ProfesionalSpecifications.java`
- clase 'CatalogoActualizadoListener' muy acoplada (CBO=13) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/messaging/CatalogoActualizadoListener.java`
- clase 'KafkaConfig' muy acoplada (CBO=8) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/messaging/KafkaConfig.java`
- clase 'ProfesionalController' muy acoplada (CBO=10) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/web/ProfesionalController.java`
- clase 'InicializarCatalogoService' muy acoplada (CBO=9) — `src/main/java/prog2/serviciocatalogo/catalogo/application/usecases/InicializarCatalogoService.java`
- clase 'ActualizarCatalogoService' muy acoplada (CBO=21) — `src/main/java/prog2/serviciocatalogo/catalogo/application/usecases/ActualizarCatalogoService.java`
- clase 'SincronizarCatalogoService' muy acoplada (CBO=11) — `src/main/java/prog2/serviciocatalogo/catalogo/application/usecases/SincronizarCatalogoService.java`
- clase 'ConsultarProfesionalService' muy acoplada (CBO=13) — `src/main/java/prog2/serviciocatalogo/catalogo/application/usecases/ConsultarProfesionalService.java`
- clase 'ConsultarProfesionalService' instancia 4 concretos (CBO=13) — `src/main/java/prog2/serviciocatalogo/catalogo/application/usecases/ConsultarProfesionalService.java`
- clase 'ProcesarCatalogoActualizadoService' muy acoplada (CBO=13) — `src/main/java/prog2/serviciocatalogo/catalogo/application/usecases/ProcesarCatalogoActualizadoService.java`
- clase 'SituacionSincronizacion' muy acoplada (CBO=8) — `src/main/java/prog2/serviciocatalogo/catalogo/domain/model/SituacionSincronizacion.java`
- clase 'CorsIntegracionTests' muy acoplada (CBO=32) — `src/test/java/prog2/serviciocatalogo/CorsIntegracionTests.java`
- clase 'SeguridadIntegracionTests' muy acoplada (CBO=31) — `src/test/java/prog2/serviciocatalogo/SeguridadIntegracionTests.java`
- clase 'IdentidadesSeparadasTests' muy acoplada (CBO=36) — `src/test/java/prog2/serviciocatalogo/IdentidadesSeparadasTests.java`
- clase 'ManejadorErroresApiTests' muy acoplada (CBO=31) — `src/test/java/prog2/serviciocatalogo/shared/web/ManejadorErroresApiTests.java`
- clase 'ManejadorErroresApiTests' instancia 4 concretos (CBO=31) — `src/test/java/prog2/serviciocatalogo/shared/web/ManejadorErroresApiTests.java`
- clase 'CatedraRestAdapterTests' muy acoplada (CBO=29) — `src/test/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/CatedraRestAdapterTests.java`
- clase 'CatedraRestAdapterTests' instancia 4 concretos (CBO=29) — `src/test/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/CatedraRestAdapterTests.java`
- clase 'CambiosCatalogoRedisTests' muy acoplada (CBO=23) — `src/test/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/CambiosCatalogoRedisTests.java`
- clase 'CambiosCatalogoRedisTests' instancia 9 concretos (CBO=23) — `src/test/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/CambiosCatalogoRedisTests.java`
- clase 'PersistenciaCatalogoTests' muy acoplada (CBO=23) — `src/test/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/PersistenciaCatalogoTests.java`
- clase 'PersistenciaCatalogoTests' instancia 6 concretos (CBO=23) — `src/test/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/PersistenciaCatalogoTests.java`
- clase 'ConsultaCatalogoPersistenciaTests' muy acoplada (CBO=32) — `src/test/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/ConsultaCatalogoPersistenciaTests.java`
- clase 'ConsultaCatalogoPersistenciaTests' instancia 8 concretos (CBO=32) — `src/test/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/ConsultaCatalogoPersistenciaTests.java`
- clase 'CatalogoActualizadoKafkaTests' muy acoplada (CBO=14) — `src/test/java/prog2/serviciocatalogo/catalogo/infrastructure/messaging/CatalogoActualizadoKafkaTests.java`
- clase 'ProfesionalControllerTests' muy acoplada (CBO=23) — `src/test/java/prog2/serviciocatalogo/catalogo/infrastructure/web/ProfesionalControllerTests.java`
- clase 'ProfesionalControllerTests' instancia 12 concretos (CBO=23) — `src/test/java/prog2/serviciocatalogo/catalogo/infrastructure/web/ProfesionalControllerTests.java`
- clase 'CategoriaControllerTests' muy acoplada (CBO=13) — `src/test/java/prog2/serviciocatalogo/catalogo/infrastructure/web/CategoriaControllerTests.java`
- clase 'CategoriaControllerTests' instancia 3 concretos (CBO=13) — `src/test/java/prog2/serviciocatalogo/catalogo/infrastructure/web/CategoriaControllerTests.java`
- clase 'SincronizacionControllerTests' muy acoplada (CBO=13) — `src/test/java/prog2/serviciocatalogo/catalogo/infrastructure/web/SincronizacionControllerTests.java`
- clase 'ConsultarCategoriasServiceTests' muy acoplada (CBO=9) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/ConsultarCategoriasServiceTests.java`
- clase 'ConsultarCategoriasServiceTests' instancia 3 concretos (CBO=9) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/ConsultarCategoriasServiceTests.java`
- clase 'ReconstruirCatalogoServiceTests' muy acoplada (CBO=10) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/ReconstruirCatalogoServiceTests.java`
- clase 'ReconstruirCatalogoServiceTests' instancia 4 concretos (CBO=10) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/ReconstruirCatalogoServiceTests.java`
- clase 'AplicarVersionTransaccionTests' muy acoplada (CBO=25) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/AplicarVersionTransaccionTests.java`
- clase 'AplicarVersionTransaccionTests' instancia 7 concretos (CBO=25) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/AplicarVersionTransaccionTests.java`
- clase 'ConsultarProfesionalServiceTests' muy acoplada (CBO=16) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/ConsultarProfesionalServiceTests.java`
- clase 'ConsultarProfesionalServiceTests' instancia 5 concretos (CBO=16) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/ConsultarProfesionalServiceTests.java`
- clase 'InicializarCatalogoServiceTests' muy acoplada (CBO=8) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/InicializarCatalogoServiceTests.java`
- clase 'InicializarCatalogoServiceTests' instancia 4 concretos (CBO=8) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/InicializarCatalogoServiceTests.java`
- clase 'ProcesarCatalogoActualizadoServiceTests' muy acoplada (CBO=10) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/ProcesarCatalogoActualizadoServiceTests.java`
- clase 'ProcesarCatalogoActualizadoServiceTests' instancia 3 concretos (CBO=10) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/ProcesarCatalogoActualizadoServiceTests.java`
- clase 'SincronizarCatalogoIntegracionTests' muy acoplada (CBO=38) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/SincronizarCatalogoIntegracionTests.java`
- clase 'SincronizarCatalogoIntegracionTests' instancia 15 concretos (CBO=38) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/SincronizarCatalogoIntegracionTests.java`
- clase 'AplicarSnapshotTransaccionTests' muy acoplada (CBO=23) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/AplicarSnapshotTransaccionTests.java`
- clase 'AplicarSnapshotTransaccionTests' instancia 6 concretos (CBO=23) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/AplicarSnapshotTransaccionTests.java`
- clase 'ActualizarCatalogoServiceTests' muy acoplada (CBO=30) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/ActualizarCatalogoServiceTests.java`
- clase 'ActualizarCatalogoServiceTests' instancia 15 concretos (CBO=30) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/ActualizarCatalogoServiceTests.java`
- clase 'InicializarCatalogoIntegracionTests' muy acoplada (CBO=18) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/InicializarCatalogoIntegracionTests.java`
- clase 'InicializarCatalogoIntegracionTests' instancia 5 concretos (CBO=18) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/InicializarCatalogoIntegracionTests.java`
- clase 'SincronizarCatalogoServiceTests' muy acoplada (CBO=12) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/SincronizarCatalogoServiceTests.java`
- clase 'SincronizarCatalogoServiceTests' instancia 3 concretos (CBO=12) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/SincronizarCatalogoServiceTests.java`
- clase 'SituacionSincronizacionTests' muy acoplada (CBO=8) — `src/test/java/prog2/serviciocatalogo/catalogo/domain/model/SituacionSincronizacionTests.java`
- clase 'AgendaProfesionalTests' muy acoplada (CBO=11) — `src/test/java/prog2/serviciocatalogo/catalogo/domain/model/AgendaProfesionalTests.java`
- clase 'AgendaProfesionalTests' instancia 3 concretos (CBO=11) — `src/test/java/prog2/serviciocatalogo/catalogo/domain/model/AgendaProfesionalTests.java`

**OCP/type-switch** (1)
- clase 'ManejadorErroresApi' con 3 type-checks — `src/main/java/prog2/serviciocatalogo/shared/web/ManejadorErroresApi.java`

**SRP/responsabilidad** (30)
- método 'ManejadorErroresApi.tituloSegunEstado' con complejidad 7 — `src/main/java/prog2/serviciocatalogo/shared/web/ManejadorErroresApi.java`
- clase 'CambiosCatalogoRedisAdapter' con baja cohesión (19%) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/CambiosCatalogoRedisAdapter.java`
- clase 'CatalogoPersistenceAdapter' grande (LOC=55, métodos=4, campos=8) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/CatalogoPersistenceAdapter.java`
- clase 'CatalogoPersistenceAdapter' con baja cohesión (33%) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/CatalogoPersistenceAdapter.java`
- método 'ProfesionalSpecifications.segun' con complejidad 7 — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/specification/ProfesionalSpecifications.java`
- clase 'HorarioSemanalEntity' grande (LOC=21, métodos=0, campos=9) — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/entity/HorarioSemanalEntity.java`
- clase 'HorarioSemanalEntity' sin métodos (9 campos): solo datos — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/entity/HorarioSemanalEntity.java`
- clase 'ProfesionalEntity' sin métodos (7 campos): solo datos — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/entity/ProfesionalEntity.java`
- clase 'EventoProcesadoEntity' sin métodos (3 campos): solo datos — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/entity/EventoProcesadoEntity.java`
- clase 'VersionCatalogoEntity' sin métodos (4 campos): solo datos — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/entity/VersionCatalogoEntity.java`
- clase 'CategoriaProfesionalEntity' sin métodos (6 campos): solo datos — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/entity/CategoriaProfesionalEntity.java`
- método 'ProfesionalResumenResponse.ProfesionalResumenResponse' con 5 parámetros — `src/main/java/prog2/serviciocatalogo/catalogo/infrastructure/web/dto/ProfesionalResumenResponse.java`
- clase 'ActualizarCatalogoService' con baja cohesión (17%) — `src/main/java/prog2/serviciocatalogo/catalogo/application/usecases/ActualizarCatalogoService.java`
- método 'ActualizarCatalogoService.aplicarVersionesNuevas' con complejidad 6 — `src/main/java/prog2/serviciocatalogo/catalogo/application/usecases/ActualizarCatalogoService.java`
- clase 'SeguridadIntegracionTests' grande (LOC=88, métodos=14, campos=3) — `src/test/java/prog2/serviciocatalogo/SeguridadIntegracionTests.java`
- clase 'SeguridadIntegracionTests' con baja cohesión (22%) — `src/test/java/prog2/serviciocatalogo/SeguridadIntegracionTests.java`
- clase 'IdentidadesSeparadasTests' grande (LOC=87, métodos=11, campos=5) — `src/test/java/prog2/serviciocatalogo/IdentidadesSeparadasTests.java`
- clase 'IdentidadesSeparadasTests' con baja cohesión (27%) — `src/test/java/prog2/serviciocatalogo/IdentidadesSeparadasTests.java`
- clase 'ManejadorErroresApiTests' grande (LOC=114, métodos=17, campos=1) — `src/test/java/prog2/serviciocatalogo/shared/web/ManejadorErroresApiTests.java`
- clase 'CambiosCatalogoRedisTests' grande (LOC=86, métodos=15, campos=3) — `src/test/java/prog2/serviciocatalogo/catalogo/infrastructure/catedra/CambiosCatalogoRedisTests.java`
- clase 'PersistenciaCatalogoTests' grande (LOC=60, métodos=7, campos=10) — `src/test/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/PersistenciaCatalogoTests.java`
- clase 'ConsultaCatalogoPersistenciaTests' grande (LOC=135, métodos=21, campos=11) — `src/test/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/ConsultaCatalogoPersistenciaTests.java`
- clase 'ConsultaCatalogoPersistenciaTests' con baja cohesión (10%) — `src/test/java/prog2/serviciocatalogo/catalogo/infrastructure/persistence/ConsultaCatalogoPersistenciaTests.java`
- clase 'CatalogoActualizadoKafkaTests' con baja cohesión (20%) — `src/test/java/prog2/serviciocatalogo/catalogo/infrastructure/messaging/CatalogoActualizadoKafkaTests.java`
- clase 'ProfesionalControllerTests' grande (LOC=127, métodos=16, campos=4) — `src/test/java/prog2/serviciocatalogo/catalogo/infrastructure/web/ProfesionalControllerTests.java`
- clase 'SincronizacionControllerTests' con baja cohesión (30%) — `src/test/java/prog2/serviciocatalogo/catalogo/infrastructure/web/SincronizacionControllerTests.java`
- clase 'AplicarVersionTransaccionTests' con baja cohesión (46%) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/AplicarVersionTransaccionTests.java`
- clase 'SincronizarCatalogoIntegracionTests' grande (LOC=115, métodos=12, campos=13) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/SincronizarCatalogoIntegracionTests.java`
- clase 'ActualizarCatalogoServiceTests' grande (LOC=95, métodos=10, campos=6) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/ActualizarCatalogoServiceTests.java`
- clase 'InicializarCatalogoIntegracionTests' grande (LOC=46, métodos=3, campos=10) — `src/test/java/prog2/serviciocatalogo/catalogo/application/usecases/InicializarCatalogoIntegracionTests.java`

**?** (4)
- clase 'ProfesionalNoEncontradoException' con herencia profunda (DIT=2) — `src/main/java/prog2/serviciocatalogo/catalogo/domain/exception/ProfesionalNoEncontradoException.java`
- clase 'CatedraNoDisponibleException' con herencia profunda (DIT=2) — `src/main/java/prog2/serviciocatalogo/catalogo/domain/exception/CatedraNoDisponibleException.java`
- clase 'CatedraAccesoDenegadoException' con herencia profunda (DIT=2) — `src/main/java/prog2/serviciocatalogo/catalogo/domain/exception/CatedraAccesoDenegadoException.java`
- clase 'CategoriaNoEncontradaException' con herencia profunda (DIT=2) — `src/main/java/prog2/serviciocatalogo/catalogo/domain/exception/CategoriaNoEncontradaException.java`

## 6. Evolución por commit

Métricas calculadas sobre el estado de `main` en **cada commit** (8 commits con trabajo), del primero al último. La calidad usa el perfil `exigente`; el aporte de código lo mide RepoSense. El índice puede **bajar** al crecer el proyecto: se acumulan señales.

```mermaid
xychart-beta
    title "Índice de calidad a lo largo de los commits"
    x-axis ["83d3d53", "99beebb", "e503b78", "26112b9", "1a1ee17", "bc1a212", "995a365", "67fa824"]
    y-axis "Índice" 0 --> 100
    line [0, 15, 37, 42, 50, 56, 67, 66]
```

| # | Fecha | Commit | Autor | LOC | Índice | %POO | Duplic. | Sueltas |
|---|---|---|---|---:|---:|---:|---:|---:|
| 1 | 2026-09-10 | `83d3d53` | Ing. Daniel Quinteros | 0 | 0 | 0% | 0.0% | 0 |
| 2 | 2026-09-10 | `99beebb` | Ing. Daniel Quinteros | 0 | 15 | 0% | 0.0% | 0 |
| 3 | 2026-10-09 | `e503b78` | Guadalupe Yañez | 41 | 37 | 54% | 0.0% | 0 |
| 4 | 2026-10-12 | `26112b9` | Guadalupe Yañez | 41 | 42 | 54% | 0.0% | 0 |
| 5 | 2026-10-15 | `1a1ee17` | Guadalupe Yañez | 89 | 50 | 53% | 0.0% | 0 |
| 6 | 2026-10-19 | `bc1a212` | Guadalupe Yañez | 1,185 | 56 | 70% | 0.0% | 0 |
| 7 | 2026-10-27 | `995a365` | Guadalupe Yañez | 2,672 | 67 | 72% | 0.0% | 0 |
| 8 | 2026-11-07 | `67fa824` | Guadalupe Yañez | 4,813 | 66 | 73% | 0.8% | 0 |

---
_Informe generado por la cátedra. Las dimensiones y umbrales corresponden a `calidad.py` (perfil `exigente`); el semáforo, a `CRITERIOS-CALIDAD.md`. La documentación del repositorio no se evalúa. Ante dudas, revisá el archivo señalado._
<!-- evaluacion:fin -->
