# Requisitos del Sistema: Funcionales y No Funcionales

Especificación de los requerimientos de la aplicación **Diksy** y su correspondencia con la arquitectura del sistema.

---

## 1. Requisitos Funcionales (RF)

* **RF-01 — Registrar palabras:** El sistema debe permitir al usuario registrar una palabra junto con su definición, fuente e idioma de destino.
* **RF-02 — Asignar fecha automáticamente:** El sistema debe proponer automáticamente la fecha actual del sistema bajo la norma ISO-8601 (`AAAA-MM-DD`). El usuario debe poder modificar manualmente esta fecha antes de guardar la entrada si desea registrar un término de días anteriores.
* **RF-03 — Separar palabras por idioma:** El sistema debe almacenar las palabras de cada idioma de forma física independiente (`definiciones_es.tsv` y `definiciones_en.tsv`), permitiendo extender el soporte a nuevos idiomas a futuro sin alterar los esquemas existentes.
* **RF-04 — Guardar los datos localmente:** El sistema debe almacenar los registros en archivos locales de texto plano (TSV) en el dispositivo del usuario, sin requerir bases de datos remotas ni servidores externos.
* **RF-05 — Consultar palabras:** El sistema debe permitir visualizar las palabras guardadas en una tabla interactiva, mostrando las columnas de Fecha, Palabra, Fuente y Definición.
* **RF-06 — Buscar palabras en tiempo real:** El sistema debe filtrar instantáneamente la lista de palabras mostradas a medida que el usuario escribe un criterio de búsqueda, buscando coincidencias en la palabra, la definición o la fuente de manera insensible a mayúsculas y espacios.
* **RF-07 — Editar palabras:** El sistema debe permitir seleccionar un registro existente, cargar sus datos en el formulario y modificar su término, fuente o definición, actualizando el almacenamiento persistente en disco.
* **RF-08 — Eliminar palabras:** El sistema debe permitir eliminar un registro seleccionado de forma permanente.
* **RF-09 — Validación y saneamiento de datos:** El sistema debe validar que los campos obligatorios contengan texto y sanear automáticamente cualquier tabulación (`\t`) o salto de línea (`\n`, `\r`) para evitar corromper la estructura de columnas del archivo TSV.
* **RF-10 — Organizar palabras:** El sistema debe permitir visualizar las palabras ordenadas según criterios definidos y fácilmente cambiables por el usuario (orden alfabético por palabra, por fecha o por fuente), respetando las normas ortográficas del español (tildes y letra Ñ) mediante `Collator`.
* **RF-11 — Interfaz Gráfica de Usuario (GUI):** El sistema debe proveer una interfaz de escritorio accesible y visual que integre en una sola pantalla la barra de cambio de idioma, el cuadro de búsqueda, la tabla de resultados y el formulario de alta/edición.
* **RF-12 — Confirmación de acciones destructivas:** El sistema debe solicitar confirmación explícita mediante un cuadro de diálogo antes de ejecutar la eliminación definitiva de cualquier palabra.

---

## 2. Requisitos No Funcionales (RNF)

* **RNF-01 — Rendimiento:** Las operaciones de lectura, filtrado, ordenamiento y persistencia deben responder de forma imperceptible (< 50 ms) para colecciones personales de miles de registros.
* **RNF-02 — Usabilidad y Accesibilidad:** La interfaz debe ser intuitiva, con atajos rápidos y controles simples, diseñada pensando en personas a las que les resulta incómodo o fatigoso escribir a mano.
* **RNF-03 — Privacidad y Soberanía de Datos:** Todos los datos registrados deben permanecer exclusivamente en el almacenamiento local del equipo del usuario, sin realizar ningún tipo de telemetría ni conexión de red.
* **RNF-04 — Disponibilidad sin conexión (Offline-first):** La aplicación debe ser 100% funcional sin conexión a internet.
* **RNF-05 — Integridad y Escritura Atómica:** La persistencia ante modificaciones o bajas debe realizarse mediante reemplazo atómico de archivos temporales (`.tmp`), evitando la pérdida o corrupción del archivo ante cierres imprevistos o cortes de energía.
* **RNF-06 — Compatibilidad:** La aplicación debe funcionar en cualquier sistema operativo (Linux, macOS, Windows) que cuente con Java Runtime Environment (JRE) versión 17 LTS o superior.
* **RNF-07 — Persistencia en formato estándar:** Los datos deben conservarse en texto plano TSV codificado en UTF-8 sin dependencias propietarias, permitiendo al usuario abrirlos o respaldarlos con cualquier editor de texto o software de hojas de cálculo.
* **RNF-08 — Ergonomía Visual (Dark Theme):** La interfaz gráfica debe incorporar un tema oscuro moderno (FlatDarkLaf) que reduzca la fatiga visual durante sesiones prolongadas de lectura y estudio.

---

## 3. Matriz de Trazabilidad (Requisitos vs. Implementación)

| Requisito | Caso de Uso Asociado | Componentes / Clases Responsables |
| :--- | :--- | :--- |
| **RF-01, RF-02** | `CU-01` (Registrar palabra) | `Palabra`, `DiksyVentanaPrincipal`, `TsvStorage` |
| **RF-03, RF-04** | `SIT-GEN-01`, `CU-01` | `Idioma`, `TsvStorage` |
| **RF-05** | `CU-02` (Consultar palabras) | `DiksyVentanaPrincipal`, `RepoPalabras` |
| **RF-06** | `CU-03` (Buscar palabras) | `BuscarPalabras`, `DiksyVentanaPrincipal` |
| **RF-07** | `CU-04` (Editar palabra) | `DiksyVentanaPrincipal`, `RepoPalabras`, `TsvStorage` |
| **RF-08, RF-12** | `CU-05` (Eliminar palabra) | `DiksyVentanaPrincipal`, `TsvStorage` |
| **RF-09** | `SIT-01.1`, `SIT-01.2` | `TsvStorage.sanear()`, `DiksyVentanaPrincipal` |
| **RF-10** | `CU-06` (Ordenar palabras) | `BuscarPalabras` (`Collator`), `RepoPalabras` |
| **RF-11** | Transversal | `com.diksy.gui.DiksyVentanaPrincipal` |
| **RNF-05** | `SIT-04.3`, `SIT-GEN-03` | `TsvStorage.reescribirArchivo()` (Escritura atómica) |
| **RNF-08** | Transversal | `com.formdev.flatlaf.FlatDarkLaf` |
