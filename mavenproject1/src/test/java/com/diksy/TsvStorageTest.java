package com.diksy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TsvStorageTest {

    @TempDir
    Path tempDir;

    private TsvStorage storage;

    @BeforeEach
    void setUp() {
        storage = new TsvStorage(tempDir);
    }

    @Test
    void testCreacionAutomaticaArchivosYCabecera_SIT_GEN_01() throws IOException {
        storage.asegurarArchivos();

        Path archivoEs = storage.obtenerRutaArchivo(Idioma.ESPANOL);
        Path archivoEn = storage.obtenerRutaArchivo(Idioma.INGLES);

        assertTrue(Files.exists(archivoEs), "El archivo de español debe crearse automáticamente");
        assertTrue(Files.exists(archivoEn), "El archivo de inglés debe crearse automáticamente");

        List<String> lineasEs = Files.readAllLines(archivoEs, StandardCharsets.UTF_8);
        List<String> lineasEn = Files.readAllLines(archivoEn, StandardCharsets.UTF_8);

        assertEquals(1, lineasEs.size(), "El archivo nuevo de español debe contener únicamente la cabecera");
        assertEquals(TsvStorage.CABECERA, lineasEs.get(0));

        assertEquals(1, lineasEn.size(), "El archivo nuevo de inglés debe contener únicamente la cabecera");
        assertEquals(TsvStorage.CABECERA, lineasEn.get(0));
    }

    @Test
    void testSaneamientoDeCaracteresDelimitadores_SIT_01_2() {
        String textoConTab = "palabra\tcon\ttab";
        String textoConSaltos = "linea 1\nlinea 2\r\nlinea 3";
        String textoConEspacios = "   palabra con espacios al inicio y final   ";

        assertEquals("palabra con tab", TsvStorage.sanear(textoConTab));
        assertEquals("linea 1 linea 2  linea 3", TsvStorage.sanear(textoConSaltos));
        assertEquals("palabra con espacios al inicio y final", TsvStorage.sanear(textoConEspacios));
        assertEquals("", TsvStorage.sanear(null));
    }

    @Test
    void testAgregarPalabraYCodificacionUtf8_SIT_01_2_Y_SIT_GEN_04() throws IOException {
        Palabra p = new Palabra(0, "ñandú\tcon salto\n", "ave corredora americana con tildes á, é, í, ó, ú",
                "RAE\tOnline", LocalDate.of(2026, 10, 3), Idioma.ESPANOL);

        storage.agregarPalabra(p);

        List<Palabra> leidas = storage.leerPalabras(Idioma.ESPANOL);
        assertEquals(1, leidas.size());

        Palabra recuperada = leidas.get(0);
        // Saneamiento verificado: no debe contener \t ni \n
        assertEquals("ñandú con salto", recuperada.getPalabra());
        assertEquals("ave corredora americana con tildes á, é, í, ó, ú", recuperada.getDefinicion());
        assertEquals("RAE Online", recuperada.getFuente());
        assertEquals(LocalDate.of(2026, 10, 3), recuperada.getFecha());
        assertEquals(Idioma.ESPANOL, recuperada.getIdioma());
    }

    @Test
    void testLecturaToleraLineasCorruptas_SIT_GEN_02() throws IOException {
        storage.asegurarArchivos();
        Path archivoEs = storage.obtenerRutaArchivo(Idioma.ESPANOL);

        // Simulamos un archivo con cabecera, una línea válida, líneas rotas (pocas/muchas columnas o fecha inválida)
        // y otra línea válida
        String contenido = TsvStorage.CABECERA + "\n"
                + "2026-10-01\tcasa\tdiccionario\tlugar para vivir\n"
                + "linea rota sin columnas suficientes\n"
                + "2026-10-02\tperro\tfuente\tdef1\textra_columna\n"
                + "fecha_invalida\tgato\tfuente\tanimal felino\n"
                + "\t\t\t\n" // campos vacíos
                + "2026-10-03\tárbol\tbotánica\tplanta perenne con tronco\n";

        Files.writeString(archivoEs, contenido, StandardCharsets.UTF_8);

        List<Palabra> leidas = storage.leerPalabras(Idioma.ESPANOL);

        // Debe ignorar las líneas corruptas y conservar únicamente las 2 válidas
        assertEquals(2, leidas.size());
        assertEquals("casa", leidas.get(0).getPalabra());
        assertEquals("árbol", leidas.get(1).getPalabra());
    }

    @Test
    void testReescrituraSeguraParaEdicionYEliminacion_SIT_04_3() throws IOException {
        Palabra p1 = new Palabra(1, "primera", "def1", "fuente1", LocalDate.now(), Idioma.INGLES);
        Palabra p2 = new Palabra(2, "segunda", "def2", "fuente2", LocalDate.now(), Idioma.INGLES);

        storage.agregarPalabra(p1);
        storage.agregarPalabra(p2);

        List<Palabra> actuales = storage.leerPalabras(Idioma.INGLES);
        assertEquals(2, actuales.size());

        // Simulamos edición de la primera y eliminación de la segunda
        actuales.get(0).setDefinicion("definicion modificada");
        List<Palabra> actualizadas = new ArrayList<>();
        actualizadas.add(actuales.get(0)); // Solo dejamos la primera modificada

        storage.reescribirArchivo(Idioma.INGLES, actualizadas);

        // No debe quedar archivo temporal huérfano
        Path archivoTemp = storage.getDirectorioBase().resolve("definiciones_en.tsv.tmp");
        assertFalse(Files.exists(archivoTemp), "El archivo temporal .tmp debe haber sido renombrado al original");

        // Verificamos el contenido reescrito
        List<Palabra> trasReescritura = storage.leerPalabras(Idioma.INGLES);
        assertEquals(1, trasReescritura.size());
        assertEquals("primera", trasReescritura.get(0).getPalabra());
        assertEquals("definicion modificada", trasReescritura.get(0).getDefinicion());
    }

    @Test
    void testCargarEnRepositorio() throws IOException {
        RepoPalabras repo = new RepoPalabras();
        storage.agregarPalabra(new Palabra(0, "book", "a written or printed work", "Oxford", LocalDate.now(), Idioma.INGLES));
        storage.agregarPalabra(new Palabra(0, "computer", "an electronic device", "Cambridge", LocalDate.now(), Idioma.INGLES));

        storage.cargarEnRepositorio(repo, Idioma.INGLES);

        assertEquals(2, repo.cantidad());
        assertNotNull(repo.buscarPorId(1));
        assertNotNull(repo.buscarPorId(2));
    }
}

