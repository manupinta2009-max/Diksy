package com.diksy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepoPalabrasTest {

    private RepoPalabras repo;
    private Palabra p1;
    private Palabra p2;

    @BeforeEach
    void setUp() {
        repo = new RepoPalabras();
        p1 = new Palabra(1, "algoritmo", "conjunto de instrucciones", "Computación", LocalDate.now(), Idioma.ESPANOL);
        p2 = new Palabra(2, "estructura", "disposición de las partes", "Arquitectura", LocalDate.now(), Idioma.ESPANOL);
    }

    @Test
    void testAgregarYCantidad() {
        assertEquals(0, repo.cantidad());
        repo.agregar(p1);
        repo.agregar(p2);
        assertEquals(2, repo.cantidad());

        // Si se intenta agregar null, no debe modificar el tamaño
        repo.agregar(null);
        assertEquals(2, repo.cantidad());
    }

    @Test
    void testListarRetornaCopiaDefensiva() {
        repo.agregar(p1);
        List<Palabra> lista = repo.listar();
        assertEquals(1, lista.size());

        // Modificar la lista retornada no debe alterar el repositorio
        lista.clear();
        assertEquals(1, repo.cantidad(), "El repositorio original debe mantener sus elementos");
    }

    @Test
    void testBuscarPorId() {
        repo.agregar(p1);
        repo.agregar(p2);

        Palabra encontrada = repo.buscarPorId(2);
        assertNotNull(encontrada);
        assertEquals("estructura", encontrada.getPalabra());

        Palabra noEncontrada = repo.buscarPorId(999);
        assertNull(noEncontrada);
    }

    @Test
    void testEliminarPorObjeto() {
        repo.agregar(p1);
        repo.agregar(p2);

        assertTrue(repo.eliminar(p1));
        assertEquals(1, repo.cantidad());
        assertNull(repo.buscarPorId(1));

        // Eliminar elemento inexistente
        assertFalse(repo.eliminar(p1));
    }

    @Test
    void testEliminarPorId() {
        repo.agregar(p1);
        repo.agregar(p2);

        assertTrue(repo.eliminarPorId(1));
        assertEquals(1, repo.cantidad());
        assertFalse(repo.eliminarPorId(1), "No se puede eliminar de nuevo el mismo id");
        assertFalse(repo.eliminarPorId(500), "Eliminar id inexistente retorna false");
    }

    @Test
    void testLimpiar() {
        repo.agregar(p1);
        repo.agregar(p2);
        assertEquals(2, repo.cantidad());

        repo.limpiar();
        assertEquals(0, repo.cantidad());
        assertTrue(repo.listar().isEmpty());
    }
}

