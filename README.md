# Diksy

**Hecho por Manuel Pintaluba**

### Descripción del problema y solución:
Esta aplicación busca resolver una dificultad personal: me cuesta escribir a mano con cualquiera de las dos manos, por lo que me resulta mucho más fácil hacerlo en una computadora.

Mi objetivo es registrar definiciones en inglés y español provenientes de diversas fuentes que consulto, sin que la información quede desorganizada en un archivo cualquiera de mi equipo. Por ello, requiero un orden estricto. La aplicación debe organizar las definiciones de modo que, cada vez que se ingrese una palabra, se agregue automáticamente la fecha, la fuente y la propia definición. Dado que manejo dos idiomas, las entradas deben guardarse por separado según la lengua correspondiente.

Por simplicidad, considero esencial que no requiera conexión a internet ni el uso de una base de datos externa. Guardar la información de forma local resulta una alternativa mucho más sencilla e ideal para este propósito.


## Alcance

Mi proyecto contempla: 
- Guardado de palabras y sus definiciones.
- Edición de datos guardados.
- Leer datos guardados.
- Organizar palabras.
- Buscar palabras.


## Alternativas consideradas
Consideré seriamente hacer un simple gestor con archivos TXT, algo similar a Obsidian aunque más simple. Pero siento que se pierde parte de la organización de esta manera. A su vez, lo veo menos escalable y amigable con el usuario general.


## Guardado de archivos
 
Los datos se almacenan en archivos **TSV** (*Tab-Separated Values*): texto plano codificado en UTF-8 en el que cada línea es un registro y cada campo se delimita con el carácter de tabulación (U+0009).
 
Se eligió TSV en lugar de CSV porque la coma es un carácter frecuente dentro de una definición en lenguaje natural y obligaría a implementar escapado mediante comillas. La tabulación casi no aparece en texto escrito de forma ordinaria, por lo que puede reservarse como delimitador y sanearse de la entrada del usuario.
 
| Archivo | Contenido |
|---|---|
| `datos/definiciones_en.tsv` | Definiciones en inglés |
| `datos/definiciones_es.tsv` | Definiciones en español |
 
Ambos archivos comparten el mismo esquema de columnas:
 
```
fecha	palabra	fuente	definicion
```
 
La especificación completa del formato (codificación, delimitadores, validaciones, invariantes y complejidad de las operaciones) está en [`Docs/formato_de_datos.md`](Docs/formato_de_datos.md).


## Estructura del proyecto

```text
Diksy/
├── Docs/                     # Documentación de arquitectura, diseño y formatos
│   ├── formato_de_datos.md   # Especificación técnica del almacenamiento TSV
│   ├── UML.md                # Diagramas de clases y casos de uso
│   └── Stacks.md             # Tecnologías y herramientas utilizadas
├── datos/                    # Carpeta local para los archivos TSV (definiciones)
├── mavenproject1/            # Código fuente del proyecto en Java (Maven)
│   ├── pom.xml               # Configuración de dependencias y compilación
│   └── src/
│       ├── main/java/com/diksy/  # Lógica principal de la aplicación
│       │   ├── Idioma.java       # Enumeración con los idiomas soportados
│       │   ├── Palabra.java      # Modelo de datos de una definición
│       │   ├── RepoPalabras.java # Repositorio de gestión de palabras en memoria
│       │   ├── BuscarPalabras.java # Módulo de búsqueda y filtrado
│       │   ├── TsvStorage.java   # Gestor de persistencia TSV atómica y UTF-8
│       │   └── Main.java         # Punto de entrada / ejecución
│       └── test/java/com/diksy/  # Pruebas automatizadas (JUnit 5)
│           ├── PalabraTest.java        # Pruebas del modelo de datos
│           ├── RepoPalabrasTest.java   # Pruebas del repositorio en memoria
│           ├── BuscarPalabrasTest.java # Pruebas de búsqueda y ordenamiento (Collator)
│           └── TsvStorageTest.java     # Pruebas del almacenamiento TSV y atomicidad
├── .gitignore                # Archivos y carpetas excluidas del control de versiones
└── README.md                 # Portada y guía general del proyecto
```


## Requisitos del sistema

- **Java Development Kit (JDK):** Versión 17 LTS o superior.
- **Apache Maven:** Versión 3.8 o superior (o utilizar el soporte integrado de su IDE: VS Code, NetBeans, IntelliJ).


## Compilación y ejecución

Desde la carpeta del proyecto `mavenproject1/`:

1. **Compilar el proyecto:**
   ```bash
   mvn clean compile
   ```

2. **Ejecutar la aplicación:**
   ```bash
   mvn exec:java
   ```

3. **Ejecutar las pruebas automatizadas:**
   ```bash
   mvn test
   ```


## Estado del proyecto y Roadmap

- [x] Definición del problema, alcance y arquitectura.
- [x] Especificación técnica de almacenamiento en TSV UTF-8.
- [x] Modelos de datos del dominio (`Palabra`, `Idioma`).
- [x] Lógica de repositorio en memoria y motor de búsqueda básico.
- [x] Estandarización de paquetes (`com.diksy`) y `.gitignore`.
- [x] Implementación del almacenamiento persistente en archivos TSV (`datos/`).
- [x] Pruebas unitarias completas con JUnit 5.
- [ ] Interfaz gráfica de escritorio con Java Swing.
- [ ] Empaquetado final en archivo ejecutable `.jar`.
