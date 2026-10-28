# Stack Tecnológico y Arquitectura de Componentes

Detalle de las tecnologías, librerías, plugins de compilación y herramientas utilizadas en el diseño y desarrollo de **Diksy**.

---

### 1. Lenguaje de Programación

| Lenguaje | Versión | Propósito |
| :--- | :--- | :--- |
| **Java** | **17 LTS (Java SE)** | Base de la lógica de negocio, manipulación de colecciones y componentes de la aplicación. Se emplea una versión con soporte a largo plazo (*Long-Term Support*) para garantizar estabilidad, rendimiento y compatibilidad con APIs modernas. |

---

### 2. Interfaz Gráfica de Usuario (Desktop UI)

| Tecnología / Librería | Versión / Origen | Propósito |
| :--- | :--- | :--- |
| **Java Swing** | Estándar JDK 17 (`javax.swing.*`) | Construcción de componentes de ventana, tablas interactivas (`JTable`), campos de texto y formularios de carga rápida sin requerir dependencias de runtime pesadas. |
| **FlatLaf (FlatDarkLaf)** | `3.5.4` (`com.formdev`) | Look and Feel moderno y limpio con tema oscuro nativo, optimización para pantallas de alta densidad (HiDPI) y bordes redondeados adaptables. |
| **FlatLaf Extras** | `3.5.4` (`com.formdev`) | Utilidades avanzadas de estilizado visual, renderers e iconos para Swing. |

---

### 3. Librerías y APIs del JDK

| Módulo / API | Origen | Propósito |
| :--- | :--- | :--- |
| **java.nio.file** | Estándar JDK 17 | Manejo robusto de persistencia en disco: verificación de directorios (`Files.exists`), escritura atómica (`Files.move`), buffers de lectura y reemplazo seguro de archivos. |
| **java.time** | Estándar JDK 17 | Gestión estandarizada de fechas bajo la norma internacional ISO-8601 mediante `LocalDate` y `DateTimeFormatter`. |
| **java.text.Collator** | Estándar JDK 17 | Comparaciones y ordenamiento lingüístico sensible a la localización en español (`es-ES`), asegurando que palabras con tildes (ej. *Árbol*) y la letra *Ñ* se ordenen según las reglas del idioma. |
| **JUnit 5 (Jupiter)** | Maven (`5.10.2`) | Framework de pruebas automatizadas unitarias y de integración para validar la lógica del repositorio, el motor de búsqueda y la persistencia en archivos TSV. |

---

### 4. Gestor de Dependencias y Plugins de Construcción

| Herramienta / Plugin | Versión | Propósito |
| :--- | :--- | :--- |
| **Apache Maven** | **3.8+** | Gestión del ciclo de vida del proyecto, resolución de dependencias externas y automatización de la compilación. |
| **maven-compiler-plugin** | Configurado para Java 17 | Compilación estricta asegurando compatibilidad del bytecode con Java 17 LTS (`release 17`). |
| **maven-surefire-plugin** | `3.2.5` | Ejecución automatizada de la suite de pruebas unitarias durante el ciclo `mvn test`. |
| **maven-assembly-plugin** | `3.6.0` | Empaquetado de la aplicación en un fat JAR autónomo (`diksy.jar`), vinculando dependencias y configurando la clase principal `com.diksy.gui.DiksyVentanaPrincipal` en el manifiesto. |
| **exec-maven-plugin** | `3.1.1` | Facilita la ejecución directa de la interfaz gráfica durante el desarrollo con el comando `mvn exec:java`. |

---

### 5. Control de Versiones e Integración de Escritorio

| Herramienta | Utilización |
| :--- | :--- |
| **Git & GitHub** | Control de versiones distribuido, seguimiento de cambios y publicación de código fuente mediante repositorio remoto. |
| **Bash (`diksy.sh`)** | Script de inicio para sistemas Unix/Linux que verifica la disponibilidad de Java en el sistema y ejecuta el binario `.jar`. |
| **XDG Desktop Entry (`Diksy.desktop`)** | Integración nativa con entornos de escritorio en Linux (GNOME, KDE, XFCE) para registrar la aplicación en el menú principal. |

---

### 6. Metodología de Desarrollo y Herramientas de Asistencia con IA

Durante el desarrollo del proyecto se utilizaron herramientas de inteligencia artificial generativa con fines didácticos, de aprendizaje asistido y revisión de código:

| Herramienta | Área de apoyo |
| :--- | :--- |
| **Antigravity** | Pair programming interactivo, refactorización de código, estructuración de pruebas unitarias y sincronización de documentación. |
| **Claude** | Asistencia en la lógica algorítmica, redacción técnica y aprendizaje sobre APIs estándar del JDK. |
| **ChatGPT** | Corrección de sintaxis de diagramas Mermaid y estilo en documentos Markdown. |
| **Gemini** | Búsqueda de documentación de librerías y consultas puntuales de sintaxis. |
