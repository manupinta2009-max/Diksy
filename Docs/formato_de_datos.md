# Elección de formato para el guardado de datos.
---
#### **ESTE TEXTO NO LO ESCRIBÍ YO, LO HICE CON CLAUDE SONNET 5.5, ENTIENDO MUY POR ARRIBA LO QUE ES ESTE TIPO DE ARCHIVOS Y SU CODIFICACIÓN**
---

## 1. Archivo de texto plano
 
Un archivo es una secuencia de bytes en un medio de almacenamiento. Se lo denomina *texto plano* cuando esos bytes se interpretan como caracteres mediante una **codificación** y no contienen metadatos de formato (tipografía, estilos, estructura binaria).
 
El archivo no posee estructura intrínseca. No existen en él los conceptos de "campo", "registro" o "palabra": son convenciones que impone el programa que lo lee y lo escribe. Por eso es indispensable fijar de forma explícita la codificación, los delimitadores y el esquema.
 
### 1.1 Codificación
 
- Codificación obligatoria: **UTF-8**.
- Se recomienda **sin BOM** (*byte order mark*, la secuencia `EF BB BF` al inicio del archivo). Algunas aplicaciones, como Microsoft Excel, requieren el BOM para detectar UTF-8; si se abre allí el archivo y los caracteres con tilde o la ñ se muestran incorrectamente, es un problema de detección de codificación de la aplicación y no del archivo.
- UTF-8 es necesario porque las palabras en español incluyen caracteres fuera de ASCII (á, é, í, ó, ú, ü, ñ, ¿, ¡), que se representan con secuencias de más de un byte. Leer con una codificación distinta a la de escritura produce *mojibake* (por ejemplo, `Ã±` en lugar de `ñ`).
### 1.2 Terminador de línea
 
- Terminador de registro escrito por el programa: **LF** (`\n`, U+000A).
- El lector debe tolerar **CRLF** (`\r\n`), que es el terminador habitual en Windows, y descartar el `\r` residual para que no pase a formar parte del último campo.
## 2. Formato TSV
 
*Tab-Separated Values* es un formato tabular de texto plano con estas reglas:
 
| Elemento | Regla |
|---|---|
| Registro | Una línea del archivo |
| Campo | Subcadena delimitada por tabulaciones dentro de una línea |
| Delimitador de campo | Tabulación horizontal, U+0009 (`\t`) |
| Delimitador de registro | Salto de línea (`\n`) |
| Encabezado | Primera línea; nombra las columnas y no es un registro de datos |
 
A diferencia de CSV, **TSV no define mecanismo de escapado**: el delimitador no puede aparecer dentro de un campo. Esto elimina la ambigüedad de las comas dentro de las definiciones, pero obliga a sanear la entrada (ver sección 5).
 
La interpretación de cada campo es **posicional**: el programa asigna el significado de un valor por el índice de su columna, no por una etiqueta. Un registro con los campos en distinto orden sigue siendo sintácticamente válido, pero semánticamente corrupto, y ninguna herramienta lo detectará. Por esa razón el orden de las columnas es invariante.
 
## 3. Archivos de datos
 
Un archivo por idioma, dentro de la carpeta `datos/`:
 
| Archivo | Idioma |
|---|---|
| `datos/definiciones_en.tsv` | Inglés |
| `datos/definiciones_es.tsv` | Español |
 
La separación por archivo, en lugar de una columna "idioma", evita repetir ese valor en cada registro y permite leer, respaldar o editar cada idioma de forma independiente. Ambos archivos comparten el mismo esquema.
 
## 4. Esquema
 
Primera línea de cada archivo (encabezado):
 
```
fecha	palabra	fuente	definicion
```
 
| N.º | Columna | Tipo | Obligatorio | Origen del valor | Descripción |
|---|---|---|---|---|---|
| 1 | `fecha` | Cadena `AAAA-MM-DD` | Sí | Automático | Fecha de registro |
| 2 | `palabra` | Cadena | Sí | Usuario | Término que se define |
| 3 | `fuente` | Cadena | Sí | Usuario | Origen de la definición (libro, artículo, sitio, etc.) |
| 4 | `definicion` | Cadena | Sí | Usuario | Texto de la definición |
 
Notas sobre el campo `fecha`:
 
- Formato **ISO 8601** de fecha calendario (`AAAA-MM-DD`), por ejemplo `2026-10-03`.
- Es inequívoco, a diferencia de `03/10/2026`, que puede leerse como 3 de octubre o 10 de marzo según la convención regional.
- Su **orden lexicográfico coincide con el cronológico**: ordenar las cadenas como texto equivale a ordenar las fechas en el tiempo. Esto permite ordenar sin convertir a un tipo de fecha.
- La asigna el programa a partir del reloj del sistema; se acepta modificar la fecha por el usuario antes de cargar datos.
Ejemplo de contenido (los separadores entre columnas son tabulaciones):
 
```
fecha	palabra	fuente	definicion
2026-10-03	ubiquitous	Libro: Sapiens	present or found everywhere
2026-10-03	ubiquitous	Artículo BBC	seeming to be in all places at the same time
```
 
## 5. Reglas de validación
 
El archivo es texto libre y cualquier editor puede romper su estructura. El orden estricto lo garantiza el programa, **único escritor autorizado**, mediante validación y saneamiento antes de cada escritura.
 
1. **Campos obligatorios no vacíos.** `palabra`, `fuente` y `definicion` deben contener al menos un carácter que no sea espacio en blanco. Si no, se rechaza el registro.
2. **Saneamiento de delimitadores.** Toda tabulación (`\t`) y todo salto de línea (`\n`, `\r`) presente en la entrada del usuario se reemplaza por un espacio. La tabulación generaría una columna adicional y el salto de línea generaría una fila adicional, rompiendo el esquema.
3. **Recorte de espacios.** Se eliminan los espacios al inicio y al final de cada campo. Sin esto, `casa` y `casa ` serían cadenas distintas.
4. **Fecha generada por el sistema.** Nunca proviene de la entrada.
5. **Aridad fija.** Cada registro escrito contiene exactamente cuatro campos, es decir, tres tabulaciones.
## 6. Invariantes del archivo
 
- La primera línea es siempre el encabezado exacto de la sección 4.
- Toda línea de datos tiene exactamente cuatro campos.
- No hay líneas vacías intermedias. Al leer, se ignora una línea vacía final causada por el último terminador de línea.
- **Se admiten repeticiones de `palabra`.** Dos registros con la misma palabra y distinta fuente son información distinta, no duplicados a eliminar.
- Los registros se anexan en orden de inserción, por lo que el archivo queda ordenado cronológicamente de forma natural.
## 7. Operaciones y complejidad
 
| Operación | Procedimiento | Costo |
|---|---|---|
| Alta | Anexar una línea al final del archivo (*append*) | O(1) respecto del tamaño del archivo |
| Consulta | Leer secuencialmente y filtrar | O(n) |
| Modificación o baja | Cargar todos los registros en memoria, alterar la colección y reescribir el archivo completo | O(n) |
 
Un archivo de texto es una secuencia contigua de bytes sin índice ni huecos reutilizables. Modificar un registro de longitud distinta desplaza todos los bytes posteriores, por lo que no es posible editar "en el lugar". Para el volumen de un uso personal (miles de registros) el costo es despreciable.
 
**Escritura segura.** Para evitar corrupción si el programa se interrumpe durante la reescritura completa, se recomienda escribir primero en un archivo temporal y luego reemplazar el original (renombrado, que en la mayoría de los sistemas de archivos es una operación atómica).
 
## 8. Limitaciones conocidas
 
- **Sin unicidad forzada.** El formato no impide registros repetidos; cualquier control adicional recae en el código.
- **Sin integridad referencial.** No hay vínculo entre una palabra en inglés y su equivalente en español, ya que cada archivo es independiente.
- **Sin transacciones ni concurrencia.** Dos procesos escribiendo a la vez sobre el mismo archivo pueden corromperlo. El diseño asume un único proceso escritor.
- **Búsqueda lineal.** No existen índices; el costo de consulta crece con el tamaño del archivo.
Si alguna de estas limitaciones deja de ser aceptable, la alternativa natural es un motor embebido de un solo archivo, como SQLite.
 
## 9. Respaldo
 
El almacenamiento es local y de archivo único por idioma. La pérdida o corrupción del archivo implica la pérdida de sus datos. Se recomienda copiar periódicamente la carpeta `datos/` a un medio externo.
 
## 10. Decisiones abiertas
 
- **Vinculación entre idiomas.** Si en el futuro se desea asociar una palabra en inglés con su traducción en español, el esquema requerirá una columna adicional (por ejemplo, un identificador común). Es preferible resolverlo antes de implementar para no migrar archivos ya cargados.
- **Normalización de mayúsculas.** Falta definir si `Casa` y `casa` se tratan como la misma palabra en comparaciones y búsquedas.
 
