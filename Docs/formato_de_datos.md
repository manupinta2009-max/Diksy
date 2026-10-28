# Especificación Técnica de Almacenamiento y Formato de Datos (TSV)

Documento de diseño técnico que especifica el formato de persistencia local, codificación de caracteres, esquema de datos, reglas de validación y garantías de integridad en Diksy.

---

## 1. Archivo de texto plano
 
Un archivo es una secuencia de bytes en un medio de almacenamiento. Se lo denomina *texto plano* cuando esos bytes se interpretan como caracteres legibles mediante una **codificación** estandarizada y no contienen metadatos propietarios ni estilos binarios complejos.
 
El archivo no posee estructura intrínseca en disco. No existen en él los conceptos de "campo", "registro" o "palabra": son convenciones que impone la aplicación que lo lee y lo escribe (`TsvStorage`). Por ello, es indispensable fijar de forma explícita la codificación, los delimitadores y el esquema.
 
### 1.1 Codificación
 
- Codificación obligatoria: **UTF-8** (`StandardCharsets.UTF_8`).
- Formato **sin BOM** (*Byte Order Mark*).
- UTF-8 es indispensable para representar fielmente caracteres especiales del español (á, é, í, ó, ú, ü, ñ, ¿, ¡) y posibles acentos en términos derivados de otros idiomas, evitando corrupción visual (*mojibake*).

### 1.2 Terminador de línea
 
- Terminador de registro estándar: **LF** (`\n`, U+000A).
- Al leer, el parser tolera terminaciones de estilo Windows **CRLF** (`\r\n`), limpiando el retorno de carro (`\r`) para evitar contaminación en el último campo.


## 2. Formato TSV (*Tab-Separated Values*)
 
Se seleccionó TSV en lugar de CSV debido a que las definiciones y textos en lenguaje natural contienen comas con alta frecuencia (lo que obligaría a complejas reglas de entrecomillado y escapado). La tabulación horizontal rara vez forma parte de la redacción cotidiana, lo que simplifica el saneamiento.
 
| Elemento | Regla |
|---|---|
| **Registro** | Una línea completa de texto dentro del archivo |
| **Campo** | Subcadena delimitada por tabulaciones dentro de la línea |
| **Delimitador de campo** | Tabulación horizontal (`\t`, U+0009) |
| **Delimitador de registro** | Salto de línea (`\n`) |
| **Encabezado** | Primera línea; define los nombres canónicos de las columnas |
 
La interpretación es **posicional**: el sistema asigna el significado de cada dato por el índice de su columna. Por ello, el orden de las columnas es estrictamente invariante.


## 3. Archivos de datos y organización

Los registros se almacenan por separado según el idioma en la carpeta `datos/`:
 
| Archivo | Idioma | Contenido |
|---|---|---|
| `datos/definiciones_en.tsv` | Inglés | Vocabulario y términos en inglés |
| `datos/definiciones_es.tsv` | Español | Vocabulario y términos en español |
 
La separación física por archivo evita redundancia (no es necesario almacenar una columna repetitiva de idioma en cada fila) y permite respaldar, sincronizar o compartir vocabularios de manera independiente.


## 4. Esquema de Columnas
 
Primera línea de cada archivo (encabezado canónico):
 
```text
fecha	palabra	fuente	definicion
```
 
| N.º | Columna | Tipo | Obligatorio | Origen del valor | Descripción |
|---|---|---|---|---|---|
| 1 | `fecha` | Cadena ISO `AAAA-MM-DD` | Sí | Sistema / Usuario | Fecha del registro (por defecto la fecha actual). |
| 2 | `palabra` | Cadena | Sí | Usuario | Término o vocablo que se registra. |
| 3 | `fuente` | Cadena | Sí | Usuario | Referencia de origen (libro, artículo, conferencia, etc.). |
| 4 | `definicion` | Cadena | Sí | Usuario | Explicación o significado de la palabra. |
 
### Notas sobre el campo `fecha`
- Formato según norma internacional **ISO-8601** (`AAAA-MM-DD`, ej. `2026-10-28`).
- Es unívoco y no ambiguo frente a convenciones regionales.
- Su orden lexicográfico coincide con el cronológico, permitiendo comparaciones directas.
- El sistema propone automáticamente la fecha local (`LocalDate.now()`), pero permite que el usuario la modifique manualmente para registrar palabras aprendidas en días previos.

### Identificador de sesión (`idPalabra`)
El modelo de dominio `Palabra` contiene el atributo `idPalabra` (numérico entero). Este identificador es asignado de forma dinámica y autoincremental en memoria al cargar las palabras desde el archivo TSV. **No se almacena en el archivo en disco** para mantener el esquema liviano y permitir edición manual limpia con editores de texto externos.


## 5. Reglas de Validación y Saneamiento

Para preservar la integridad del formato ante cualquier entrada del usuario:

1. **Campos obligatorios:** Los campos `palabra`, `fuente` y `definicion` no pueden estar vacíos ni formados exclusivamente por espacios en blanco.
2. **Saneamiento de delimitadores:** Toda tabulación (`\t`) y salto de línea (`\n`, `\r`) dentro del texto ingresado por el usuario se convierte automáticamente en un espacio simple mediante el método `TsvStorage.sanear()`. Esto previene desfasajes de columnas o registros rotos.
3. **Recorte de espacios (*trim*):** Se eliminan espacios redundantes al inicio y final de cada campo antes de persistir.
4. **Validación de formato de fecha:** Si la fecha fue editada por el usuario, el sistema valida que cumpla con el patrón `AAAA-MM-DD` mediante `LocalDate.parse()`. Si es inválida, se detiene el guardado y se notifica el error.
5. **Aridad fija de 4 columnas:** Cada registro válido debe contener exactamente 3 tabulaciones separadoras.


## 6. Invariantes del archivo

- La primera línea es siempre el encabezado unificado: `fecha\tpalabra\tfuente\tdefinicion`.
- Toda línea de datos contiene exactamente 4 columnas válidas.
- No se permiten líneas en blanco intermedias.
- **Se admiten repeticiones de la misma palabra:** Una misma palabra puede registrarse varias veces si proviene de distintas fuentes o aporta acepciones y contextos diferentes.
- Las nuevas altas se agregan al final del archivo (*append*), manteniendo un orden cronológico natural por defecto.


## 7. Operaciones y Complejidad Algorítmica

| Operación | Implementación | Complejidad Temporal |
|---|---|---|
| **Alta (registro nuevo)** | Anexar una línea al final del archivo (*append mode*) | $O(1)$ respecto al tamaño del archivo |
| **Consulta / Carga inicial** | Lectura secuencial línea por línea cargando a memoria | $O(n)$ |
| **Búsqueda / Filtrado** | Búsqueda en memoria por coincidencia parcial insensible a mayúsculas | $O(n)$ |
| **Ordenamiento** | Algoritmo TimSort (`List.sort`) usando `java.text.Collator` | $O(n \log n)$ |
| **Edición o Eliminación** | Reescribir la lista completa en memoria sobre el archivo en disco | $O(n)$ |

### Mecanismo de persistencia atómica y segura
Al modificar o eliminar registros, `TsvStorage` reescribe la información utilizando un archivo temporal con extensión `.tmp` en el mismo directorio. Una vez completada y verificada la escritura sin errores, el archivo temporal reemplaza al original mediante `Files.move` con reemplazo atómico. Esto protege los datos contra cortes de energía o caídas de la aplicación durante la escritura.


## 8. Limitaciones y Evolución Futura

- **Monoproceso:** El diseño asume un único usuario en un equipo de escritorio local, sin soporte para escritura concurrente entre múltiples procesos simultáneos.
- **Búsqueda lineal en memoria:** Óptimo y casi instantáneo para colecciones personales de miles de palabras. Si el volumen superara cientos de miles de entradas, se evaluaría un motor embebido como SQLite.
- **Normalización de mayúsculas:** Las búsquedas son insensibles a mayúsculas, minúsculas y tildes gracias a `Collator`, pero se preserva la capitalización original elegida por el usuario al guardar.
