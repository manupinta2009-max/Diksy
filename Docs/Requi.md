# Requisitos funcionales:

* **RF-01 — Registrar palabras:** El sistema debe permitir al usuario registrar una palabra junto con su definición, fuente e idioma.

* **RF-02 — Asignar fecha automáticamente:** El sistema debe asignar automáticamente la fecha en la que se registra cada palabra, utilizando la fecha del sistema. El usuario debe poder ingresar otra manualmente.

* **RF-03 — Separar palabras por idioma:** El sistema debe almacenar las palabras de cada idioma de forma independiente. Inicialmente se contemplan los idiomas español e inglés, pero el diseño debe permitir agregar otros idiomas posteriormente.

* **RF-04 — Guardar los datos localmente:** El sistema debe almacenar las palabras y sus datos asociados en archivos locales del dispositivo donde se ejecuta la aplicación.

* **RF-05 — Consultar palabras:** El sistema debe permitir al usuario visualizar las palabras registradas junto con sus datos asociados.

* **RF-06 — Buscar palabras:** El sistema debe permitir buscar palabras registradas por el término de la palabra, por contenido de la definición, por fecha y por fuente.

* **RF-07 — Editar palabras:** El sistema debe permitir modificar los datos asociados a una palabra previamente registrada.

* **RF-08 — Eliminar palabras:** El sistema debe permitir eliminar una palabra previamente registrada.

* **RF-09 — Mantener el formato de los datos:** El sistema debe validar los datos antes de almacenarlos para evitar registros incompletos o incompatibles con el formato de almacenamiento.

* **RF-10 — Organizar las palabras:** El sistema debe permitir visualizar las palabras ordenadas según criterios definidos y facilemtente cambiables por el usuario, como fecha, palabra o fuente.

---

# Requisitos no funcionales

* **RNF-01 — Rendimiento:** Las operaciones habituales de registro, consulta, edición, eliminación y búsqueda deben ejecutarse en un tiempo adecuado para el volumen de datos esperado para un uso personal.

* **RNF-02 — Usabilidad:** La aplicación debe proporcionar una interfaz sencilla que permita registrar y consultar palabras sin requerir conocimientos técnicos sobre archivos o su formato.

* **RNF-03 — Privacidad:** Los datos registrados deben permanecer almacenados localmente en el dispositivo del usuario. La aplicación no debe requerir conexión a internet para funcionar.

* **RNF-04 — Disponibilidad:** La aplicación debe poder utilizarse sin conexión a internet y debe permitir acceder a los datos previamente guardados mientras los archivos locales estén disponibles.

* **RNF-05 — Integridad de datos:** El sistema debe validar los datos antes de guardarlos y mantener el formato establecido para los archivos de almacenamiento, evitando registros incompletos o incompatibles.

* **RNF-06 — Compatibilidad:** La aplicación debe poder ejecutarse en un entorno que disponga de una versión compatible de Java.

* **RNF-07 — Persistencia:** Los datos registrados deben permanecer disponibles después de cerrar y volver a abrir la aplicación.
