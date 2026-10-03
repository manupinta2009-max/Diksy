# Diksy

**Hecho por Manuel Pintaluba**

### Descripción del problema y solución:
Esta aplicación busca resolver una dificultad personal: me cuesta escribir a mano con cualquiera de las dos manos, por lo que me resulta mucho más fácil hacerlo en una computadora.

Mi objetivo es registrar definiciones en inglés y español provenientes de diversas fuentes que consulto, sin que la información quede desorganizada en un archivo cualquiera de mi equipo. Por ello, requiero un orden estricto. La aplicación debe organizar las definiciones de modo que, cada vez que se ingrese una palabra, se agregue automáticamente la fecha, la fuente y la propia definición. Dado que manejo dos idiomas, las entradas deben guardarse por separado según la lengua correspondiente.

Por simplicidad, considero esencial que no requiera conexión a internet ni el uso de una base de datos. Guardar la información de forma local resulta una alternativa mucho más sencilla e ideal para este propósito.


## Alcance

Mi proyecto contempla: 
- Guardado de palabras y sus definiciones.
- Edición de datos guardados.
- Leer datos guardados.
- Organizar palabras.
- Buscar palabras.


## Alternativas consideradas
Considere seriamente hacer un simple file manager con archivos TXT, algo similar a Obsidian aunque más simple. Pero siento que se pierde parte de la organización de esta manera. A su vez, lo veo menos escalabale y amigable con el usuario general.


## Guardado de archivos
 
Los datos se almacenan en archivos **TSV** (*Tab-Separated Values*): texto plano codificado en UTF-8 en el que cada línea es un registro y cada campo se delimita con el carácter de tabulación (U+0009).
 
Se eligió TSV en lugar de CSV porque la coma es un carácter frecuente dentro de una definición en lenguaje natural y obligaría a implementar escapado mediante comillas. La tabulación casi no aparece en texto escrito de forma ordinaria, por lo que puede reservarse como delimitador y eliminarse de la entrada del usuario.
 
| Archivo | Contenido |
|---|---|
| `datos/definiciones_en.tsv` | Definiciones en inglés |
| `datos/definiciones_es.tsv` | Definiciones en español |
 
Ambos archivos comparten el mismo esquema de columnas:
 
```
fecha	palabra	fuente	definicion
```
 
La especificación completa del formato (codificación, delimitadores, validaciones, invariantes y complejidad de las operaciones) está en [`docs/formato-de-datos.md`](Docs/formato_de_datos.md).
