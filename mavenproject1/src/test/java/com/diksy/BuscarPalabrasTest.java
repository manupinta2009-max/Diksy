package com.diksy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuscarPalabrasTest {

    private RepoPalabras repo;
    private BuscarPalabras buscador;

    private Palabra p1;
    private Palabra p2;
    private Palabra p3;

    @BeforeEach
    void setUp() {
        repo = new RepoPalabras();
        buscador = new BuscarPalabras(repo);

        p1 = new Palabra(1, "Árbol", "planta leñosa", "Botánica General", LocalDate.of(2026, 1, 10), Idioma.ESPANOL);
        p2 = new Palabra(2, "Ñandú", "ave corredora americana", "Zoología", LocalDate.of(2026, 2, 20), Idioma.ESPANOL);
        p3 = new Palabra(3, "barco", "vehículo flotante", "Marina", LocalDate.of(2026, 3, 30), Idioma.ESPANOL);

        repo.agregar(p1);
        repo.agregar(p2);
        repo.agregar(p3);
    }

    @Test
    void testBuscarPorLetrasInsensibleMayusculas_SIT_03_2() {
        List<Palabra> res = buscador.buscarPorLetras("ár");
        assertEquals(1, res.size());
        assertEquals("Árbol", res.get(0).getPalabra());

        List<Palabra> resMayus = buscador.buscarPorLetras("BAR");
        assertEquals(1, resMayus.size());
        assertEquals("barco", resMayus.get(0).getPalabra());

        List<Palabra> vacio = buscador.buscarPorLetras("inexistente");
        assertTrue(vacio.isEmpty());

        assertTrue(buscador.buscarPorLetras(null).isEmpty());
    }

    @Test
    void testBuscarPorFecha() {
        List<Palabra> res = buscador.buscarPorFecha(LocalDate.of(2026, 2, 20));
        assertEquals(1, res.size());
        assertEquals("Ñandú", res.get(0).getPalabra());

        List<Palabra> vacio = buscador.buscarPorFecha(LocalDate.of(2020, 1, 1));
        assertTrue(vacio.isEmpty());

        assertTrue(buscador.buscarPorFecha(null).isEmpty());
    }

    @Test
    void testBuscarPorFuente() {
        List<Palabra> res = buscador.buscarPorFuente("botánica");
        assertEquals(1, res.size());
        assertEquals("Árbol", res.get(0).getPalabra());

        List<Palabra> vacio = buscador.buscarPorFuente("Astronomia");
        assertTrue(vacio.isEmpty());

        assertTrue(buscador.buscarPorFuente(null).isEmpty());
    }

    @Test
    void testBuscarGeneralMultiplesCampos_SIT_03_3() {
        // Coincidencia por definición
        List<Palabra> porDef = buscador.buscarGeneral("corredora");
        assertEquals(1, porDef.size());
        assertEquals("Ñandú", porDef.get(0).getPalabra());

        // Coincidencia por fuente
        List<Palabra> porFuente = buscador.buscarGeneral("marina");
        assertEquals(1, porFuente.size());
        assertEquals("barco", porFuente.get(0).getPalabra());

        // Coincidencia por término con espacios alrededor (SIT-03.2)
        List<Palabra> porTermino = buscador.buscarGeneral("  árbol  ");
        assertEquals(1, porTermino.size());
        assertEquals("Árbol", porTermino.get(0).getPalabra());

        // Búsqueda vacía restaura la lista completa (SIT-03.4)
        List<Palabra> completa = buscador.buscarGeneral("");
        assertEquals(3, completa.size());
    }

    @Test
    void testOrdenarPorPalabraConCollatorEspanol_SIT_06_1() {
        List<Palabra> lista = new ArrayList<>(repo.listar());

        // Orden ascendente: Árbol -> barco -> Ñandú (la Ñ va después de la N y antes de la O)
        BuscarPalabras.ordenarPorPalabra(lista, true);
        assertEquals("Árbol", lista.get(0).getPalabra());
        assertEquals("barco", lista.get(1).getPalabra());
        assertEquals("Ñandú", lista.get(2).getPalabra());

        // Orden descendente (SIT-06.2): Ñandú -> barco -> Árbol
        BuscarPalabras.ordenarPorPalabra(lista, false);
        assertEquals("Ñandú", lista.get(0).getPalabra());
        assertEquals("barco", lista.get(1).getPalabra());
        assertEquals("Árbol", lista.get(2).getPalabra());
    }

    @Test
    void testOrdenarPorFecha_CU_06() {
        List<Palabra> lista = new ArrayList<>(repo.listar());

        BuscarPalabras.ordenarPorFecha(lista, false); // más recientes primero
        assertEquals("barco", lista.get(0).getPalabra());
        assertEquals("Ñandú", lista.get(1).getPalabra());
        assertEquals("Árbol", lista.get(2).getPalabra());
    }

    @Test
    void testOrdenarPorFuente_CU_06() {
        List<Palabra> lista = new ArrayList<>(repo.listar());

        BuscarPalabras.ordenarPorFuente(lista, true);
        assertEquals("Botánica General", lista.get(0).getFuente());
        assertEquals("Marina", lista.get(1).getFuente());
        assertEquals("Zoología", lista.get(2).getFuente());
    }
}

