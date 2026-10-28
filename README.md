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
- Consulta y visualización interactiva.
- Organización y ordenamiento alfabético, cronológico y por fuente.
- Búsqueda en tiempo real por término, definición o fuente.
- Interfaz gráfica de escritorio moderna con soporte de tema oscuro.
- Empaquetado autónomo en `.jar` ejecutable y lanzador para sistemas operativos de escritorio.


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
│   ├── Requi.md              # Requisitos funcionales y no funcionales
│   ├── Stacks.md             # Tecnologías, librerías y herramientas utilizadas
│   └── UML.md                # Diagramas de clases, arquitectura y casos de uso
├── datos/                    # Carpeta local para los archivos TSV (definiciones)
│   ├── definiciones_en.tsv   # Almacén de vocabulario en inglés
│   └── definiciones_es.tsv   # Almacén de vocabulario en español
├── Diksy.desktop             # Acceso directo para el menú de aplicaciones en Linux
├── diksy.sh                  # Script bash para lanzamiento directo del JAR
├── mavenproject1/            # Código fuente del proyecto en Java (Maven)
│   ├── pom.xml               # Configuración de dependencias, FlatLaf y plugins
│   └── src/
│       ├── main/java/com/diksy/
│       │   ├── Idioma.java       # Enumeración con los idiomas soportados
│       │   ├── Palabra.java      # Modelo de datos del registro
│       │   ├── RepoPalabras.java # Repositorio de gestión de palabras en memoria
│       │   ├── BuscarPalabras.java # Módulo de búsqueda, filtros y ordenamiento
│       │   ├── TsvStorage.java   # Gestor de persistencia TSV atómica y UTF-8
│       │   ├── Main.java         # Prueba de ejecución básica por consola
│       │   └── gui/
│       │       └── DiksyVentanaPrincipal.java # Interfaz gráfica Swing (FlatLaf)
│       └── test/java/com/diksy/  # Pruebas automatizadas (JUnit 5)
│           ├── PalabraTest.java        # Pruebas del modelo de datos
│           ├── RepoPalabrasTest.java   # Pruebas del repositorio en memoria
│           ├── BuscarPalabrasTest.java # Pruebas de búsqueda y ordenamiento (Collator)
│           ├── TsvStorageTest.java     # Pruebas del almacenamiento TSV y atomicidad
│           └── gui/
│               └── DiksyVentanaPrincipalTest.java # Pruebas de la interfaz gráfica
├── .gitignore                # Archivos y carpetas excluidas del control de versiones
└── README.md                 # Portada y guía general del proyecto
```


## Requisitos del sistema

- **Java Development Kit (JDK):** Versión 17 LTS o superior.
- **Apache Maven:** Versión 3.8 o superior (para compilar desde el código fuente o mediante IDE como VS Code, NetBeans o IntelliJ).


## Compilación y desarrollo

Desde la carpeta del proyecto `mavenproject1/`:

1. **Compilar el proyecto:**
   ```bash
   mvn clean compile
   ```

2. **Ejecutar en modo desarrollo (inicia la interfaz gráfica):**
   ```bash
   mvn exec:java
   ```

3. **Ejecutar las pruebas automatizadas:**
   ```bash
   mvn test
   ```


## Empaquetado y Distribución

1. **Generar el ejecutable autónomo (`diksy.jar`):**
   ```bash
   cd mavenproject1
   mvn clean package
   ```
   El plugin `maven-assembly-plugin` compilará las dependencias necesarias (incluyendo el tema FlatLaf) y generará el archivo `diksy.jar` listo para ejecutarse en la raíz del proyecto.

2. **Lanzar la aplicación directamente:**
   Desde la raíz del proyecto:
   ```bash
   ./diksy.sh
   ```
   O con el comando directo de Java:
   ```bash
   java -jar diksy.jar
   ```

3. **Instalación como aplicación de escritorio (Linux):**
   Para integrar Diksy al lanzador de aplicaciones de tu distribución (GNOME, KDE, XFCE, etc.):
   - Abre `Diksy.desktop` y verifica que las rutas `Exec` y `Path` coincidan con la ubicación del proyecto en tu equipo.
   - Otorga permisos de ejecución al script y al archivo de escritorio:
     ```bash
     chmod +x diksy.sh Diksy.desktop
     ```
   - Cópialo a tu directorio de aplicaciones de usuario:
     ```bash
     cp Diksy.desktop ~/.local/share/applications/
     ```


## Interfaz Gráfica

La interfaz gráfica fue construida con **Java Swing** y estilizada mediante **FlatLaf Dark**, ofreciendo:
* **Selector lateral de idiomas:** Cambio instantáneo entre Español e Inglés con recarga dinámica de registros.
* **Filtro de búsqueda en tiempo real:** Búsqueda predictiva e insensible a mayúsculas que filtra por término, definición o fuente a medida que se escribe.
* **Tabla interactiva:** Permite visualizar los registros existentes y seleccionar cualquiera para cargarlo en el formulario de edición.
* **Formulario con validación integrada:** Inserción y actualización atómica en el archivo TSV correspondiente, con propuesta automática de fecha ISO-8601 (`AAAA-MM-DD`).
* **Protección contra pérdida de datos:** Confirmación mediante diálogo modal antes de eliminar palabras y escritura segura mediante archivos temporales.


## Estado del proyecto y Roadmap

- [x] Definición del problema, alcance y arquitectura.
- [x] Especificación técnica de almacenamiento en TSV UTF-8.
- [x] Modelos de datos del dominio (`Palabra`, `Idioma`).
- [x] Lógica de repositorio en memoria y motor de búsqueda con soporte lingüístico (`Collator`).
- [x] Estandarización de paquetes (`com.diksy`) y control de versiones `.gitignore`.
- [x] Persistencia atómica y resiliente en archivos TSV (`datos/`).
- [x] Pruebas unitarias completas con JUnit 5 (dominio, repositorio, persistencia y GUI).
- [x] Interfaz gráfica de escritorio completa (Java Swing + FlatLaf Dark Theme).
- [x] Empaquetado en fat JAR ejecutable (`diksy.jar`) y lanzador de escritorio para Linux.
- [ ] Exportación de datos a formatos adicionales (JSON, CSV, Markdown).
- [ ] Estadísticas y métricas de palabras aprendidas por semana/mes.
