package com.diksy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestor de persistencia en archivos TSV (Tab-Separated Values).
 * Cumple con las especificaciones de diseño y casos de uso:
 * <ul>
 *   <li>SIT-GEN-01: Creación automática de carpeta y archivos con cabecera.</li>
 *   <li>SIT-GEN-02: Tolerancia a líneas corruptas o mal formadas.</li>
 *   <li>SIT-GEN-03: Gestión de errores de acceso a disco y permisos.</li>
 *   <li>SIT-GEN-04: Codificación UTF-8 estricta.</li>
 *   <li>SIT-01.2: Saneamiento de delimitadores (\t, \r, \n) y anexado.</li>
 *   <li>SIT-04.3: Reescritura atómica segura mediante archivo temporal (.tmp).</li>
 * </ul>
 */
public class TsvStorage {

    private static final Logger LOGGER = Logger.getLogger(TsvStorage.class.getName());

    public static final String CABECERA = "fecha\tpalabra\tfuente\tdefinicion";
    public static final String EXTENSION_TEMP = ".tmp";
    public static final String MENSAJE_ERROR_DISCO = "No se pudo guardar la información por problemas de acceso al disco";

    private final Path directorioBase;

    /**
     * Constructor por defecto. Utiliza la carpeta "datos" en el directorio de trabajo actual.
     */
    public TsvStorage() {
        this(Path.of("datos"));
    }

    /**
     * Constructor que permite especificar la ruta base de los datos.
     *
     * @param rutaDirectorio Ruta del directorio de almacenamiento.
     */
    public TsvStorage(String rutaDirectorio) {
        this(Path.of(Objects.requireNonNull(rutaDirectorio, "La ruta del directorio no puede ser nula")));
    }

    /**
     * Constructor que permite especificar el Path base de los datos.
     *
     * @param directorioBase Path del directorio donde residen los archivos TSV.
     */
    public TsvStorage(Path directorioBase) {
        this.directorioBase = Objects.requireNonNull(directorioBase, "El directorio base no puede ser nulo");
    }

    /**
     * Retorna la ruta del directorio base de almacenamiento.
     */
    public Path getDirectorioBase() {
        return directorioBase;
    }

    /**
     * Obtiene el nombre estándar de archivo TSV para un idioma dado.
     * Ejemplo: "definiciones_es.tsv" o "definiciones_en.tsv".
     */
    public String obtenerNombreArchivo(Idioma idioma) {
        Objects.requireNonNull(idioma, "El idioma no puede ser nulo");
        return "definiciones_" + idioma.getCodigo() + ".tsv";
    }

    /**
     * Obtiene la ruta completa al archivo TSV correspondiente al idioma.
     */
    public Path obtenerRutaArchivo(Idioma idioma) {
        return directorioBase.resolve(obtenerNombreArchivo(idioma));
    }

    /**
     * SIT-GEN-01: Creación automática.
     * Si la carpeta datos/ o los archivos .tsv no existen, los crea con su cabecera.
     * Si el archivo ya existe pero está vacío (0 bytes), también escribe su cabecera.
     *
     * @throws IOException Si ocurre un error de E/S al crear carpetas o archivos.
     */
    public void asegurarArchivos() throws IOException {
        try {
            if (!Files.exists(directorioBase)) {
                Files.createDirectories(directorioBase);
            }

            for (Idioma idioma : Idioma.values()) {
                Path archivo = obtenerRutaArchivo(idioma);
                if (!Files.exists(archivo)) {
                    Files.writeString(archivo, CABECERA + "\n", StandardCharsets.UTF_8,
                            StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                } else if (Files.size(archivo) == 0) {
                    Files.writeString(archivo, CABECERA + "\n", StandardCharsets.UTF_8,
                            StandardOpenOption.WRITE);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, MENSAJE_ERROR_DISCO + ": " + e.getMessage(), e);
            throw new IOException(MENSAJE_ERROR_DISCO + ": " + e.getMessage(), e);
        }
    }

    /**
     * Alias de conveniencia para asegurar la existencia de los archivos.
     */
    public void inicializar() throws IOException {
        asegurarArchivos();
    }

    /**
     * SIT-01.2: Sanea un texto de entrada reemplazando cualquier tabulador (\t)
     * o salto de línea (\r, \n) por un espacio simple, y aplicando trim().
     *
     * @param texto Cadena de texto a sanear.
     * @return Cadena saneada sin delimitadores que rompan el formato TSV.
     */
    public static String sanear(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace('\t', ' ')
                    .replace('\r', ' ')
                    .replace('\n', ' ')
                    .trim();
    }

    /**
     * Formatea un objeto Palabra en una línea TSV válida con sus campos saneados.
     * Estructura: fecha\tpalabra\tfuente\tdefinicion
     *
     * @param palabra Objeto palabra a formatear.
     * @return Línea TSV (sin salto de línea final).
     */
    public String formatearRegistro(Palabra palabra) {
        Objects.requireNonNull(palabra, "La palabra a formatear no puede ser nula");
        String fechaStr = palabra.getFecha() != null ? palabra.getFecha().toString() : LocalDate.now().toString();
        String palabraStr = sanear(palabra.getPalabra());
        String fuenteStr = sanear(palabra.getFuente());
        String definicionStr = sanear(palabra.getDefinicion());
        return fechaStr + "\t" + palabraStr + "\t" + fuenteStr + "\t" + definicionStr;
    }

    /**
     * SIT-01.2: Escritura y anexado de una nueva palabra.
     * Sanea los campos y anexa la nueva línea al final del archivo correspondiente al idioma.
     *
     * @param palabra Palabra a agregar y persistir.
     * @throws IOException Si ocurre un error al acceder al disco (SIT-GEN-03).
     */
    public void agregarPalabra(Palabra palabra) throws IOException {
        Objects.requireNonNull(palabra, "La palabra no puede ser nula");
        if (palabra.getIdioma() == null) {
            throw new IllegalArgumentException("La palabra debe tener un idioma asignado para poder persistirse.");
        }

        try {
            asegurarArchivos();
            Path rutaArchivo = obtenerRutaArchivo(palabra.getIdioma());
            String linea = formatearRegistro(palabra) + "\n";
            Files.writeString(rutaArchivo, linea, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, MENSAJE_ERROR_DISCO + ": " + e.getMessage(), e);
            throw new IOException(MENSAJE_ERROR_DISCO + ": " + e.getMessage(), e);
        }
    }

    /**
     * Lectura de palabras (SIT-GEN-04, SIT-GEN-02):
     * Abre el archivo .tsv, lee línea por línea en UTF-8, separa por tabuladores (\t)
     * y convierte cada fila válida en un objeto Palabra.
     * Si encuentra una línea rota o mal formada (SIT-GEN-02), registra advertencia,
     * la ignora y continúa con las demás.
     *
     * @param idioma Idioma del archivo a leer.
     * @return Lista de palabras cargadas desde el archivo.
     * @throws IOException Si ocurre un error de lectura o acceso al disco.
     */
    public List<Palabra> leerPalabras(Idioma idioma) throws IOException {
        Objects.requireNonNull(idioma, "El idioma no puede ser nulo");
        asegurarArchivos();

        Path rutaArchivo = obtenerRutaArchivo(idioma);
        List<Palabra> palabras = new ArrayList<>();
        int idGenerado = 1;

        try (BufferedReader reader = Files.newBufferedReader(rutaArchivo, StandardCharsets.UTF_8)) {
            String linea;
            int numeroLinea = 0;

            while ((linea = reader.readLine()) != null) {
                numeroLinea++;

                // Omitir líneas vacías (por ejemplo saltos finales)
                if (linea.trim().isEmpty()) {
                    continue;
                }

                // Omitir cabecera si es la primera línea o coincide con el formato de cabecera
                if (numeroLinea == 1 && linea.toLowerCase().startsWith("fecha\tpalabra")) {
                    continue;
                }

                // Separar por tabuladores sin descartar columnas vacías al final (-1)
                String[] columnas = linea.split("\t", -1);

                // SIT-GEN-02: Validar aridad de 4 columnas exactas
                if (columnas.length != 4) {
                    LOGGER.warning(String.format(
                            "[SIT-GEN-02] Línea %d corrupta u omitida en %s: se esperaban 4 columnas y se encontraron %d. Contenido: '%s'",
                            numeroLinea, rutaArchivo.getFileName(), columnas.length, linea));
                    continue;
                }

                String fechaStr = columnas[0].trim();
                String palabraStr = columnas[1].trim();
                String fuenteStr = columnas[2].trim();
                String definicionStr = columnas[3].trim();

                // SIT-GEN-02: Validar formato de fecha ISO 8601 (AAAA-MM-DD)
                LocalDate fecha;
                try {
                    fecha = LocalDate.parse(fechaStr);
                } catch (DateTimeParseException e) {
                    LOGGER.warning(String.format(
                            "[SIT-GEN-02] Fecha inválida en línea %d de %s: '%s'. Registro omitido.",
                            numeroLinea, rutaArchivo.getFileName(), fechaStr));
                    continue;
                }

                // SIT-01.1 / SIT-GEN-02: Palabra y definición no deben estar completamente vacías
                if (palabraStr.isEmpty() || definicionStr.isEmpty()) {
                    LOGGER.warning(String.format(
                            "[SIT-GEN-02] Campo obligatorio vacío en línea %d de %s. Registro omitido.",
                            numeroLinea, rutaArchivo.getFileName()));
                    continue;
                }

                // Construcción de la entidad en memoria con ID de sesión
                Palabra entidad = new Palabra(idGenerado++, palabraStr, definicionStr, fuenteStr, fecha, idioma);
                palabras.add(entidad);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, MENSAJE_ERROR_DISCO + ": " + e.getMessage(), e);
            throw new IOException(MENSAJE_ERROR_DISCO + ": " + e.getMessage(), e);
        }

        return palabras;
    }

    /**
     * Carga las palabras del archivo TSV directamente en un repositorio RepoPalabras.
     *
     * @param repo Repositorio de palabras donde cargar los registros.
     * @param idioma Idioma que se desea cargar.
     * @throws IOException Si ocurre un error al leer el archivo.
     */
    public void cargarEnRepositorio(RepoPalabras repo, Idioma idioma) throws IOException {
        Objects.requireNonNull(repo, "El repositorio no puede ser nulo");
        List<Palabra> palabras = leerPalabras(idioma);
        for (Palabra p : palabras) {
            repo.agregar(p);
        }
    }

    /**
     * SIT-04.3: Reescritura segura para edición/eliminación.
     * Escribe primero en un archivo temporal (.tmp) en el mismo directorio
     * y reemplaza atómicamente el archivo original para prevenir pérdida o corrupción
     * de datos ante un corte de energía o interrupción imprevista.
     *
     * @param idioma Idioma de las definiciones a reescribir.
     * @param palabras Lista completa de palabras a guardar.
     * @throws IOException Si ocurre un error durante la escritura o reemplazo.
     */
    public void reescribirArchivo(Idioma idioma, List<Palabra> palabras) throws IOException {
        Objects.requireNonNull(idioma, "El idioma no puede ser nulo");
        Objects.requireNonNull(palabras, "La lista de palabras no puede ser nula");

        asegurarArchivos();
        Path archivoDestino = obtenerRutaArchivo(idioma);

        // Se crea el archivo temporal en el mismo directorio para garantizar
        // que pertenezca al mismo filesystem y permitir el movimiento atómico (ATOMIC_MOVE).
        Path archivoTemp = directorioBase.resolve(obtenerNombreArchivo(idioma) + EXTENSION_TEMP);

        try {
            try (BufferedWriter writer = Files.newBufferedWriter(archivoTemp, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {

                // Escribir cabecera
                writer.write(CABECERA);
                writer.newLine();

                // Escribir cada registro saneado
                for (Palabra p : palabras) {
                    if (p != null) {
                        writer.write(formatearRegistro(p));
                        writer.newLine();
                    }
                }
            }

            // Reemplazo atómico seguro sobre el archivo destino
            try {
                Files.move(archivoTemp, archivoDestino,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                // Fallback si el sistema de archivos no soporta movimiento atómico
                Files.move(archivoTemp, archivoDestino, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            // Limpieza del archivo temporal si falló la operación
            try {
                Files.deleteIfExists(archivoTemp);
            } catch (IOException ignored) {
                // Ignorar error al limpiar temporal
            }
            LOGGER.log(Level.SEVERE, MENSAJE_ERROR_DISCO + ": " + e.getMessage(), e);
            throw new IOException(MENSAJE_ERROR_DISCO + ": " + e.getMessage(), e);
        }
    }

    /**
     * Alias de reescribirArchivo para persistir una colección completa tras edición o borrado.
     *
     * @param idioma Idioma correspondiente.
     * @param palabras Lista de palabras a persistir.
     * @throws IOException Si ocurre un error de disco.
     */
    public void guardarTodo(Idioma idioma, List<Palabra> palabras) throws IOException {
        reescribirArchivo(idioma, palabras);
    }
}

