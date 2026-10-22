package com.diksy;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PalabraTest {

    @Test
    void testConstructorCompleto() {
        LocalDate fecha = LocalDate.of(2026, 10, 15);
        Palabra p = new Palabra(1, "ubiquitous", "present everywhere", "Sapiens", fecha, Idioma.INGLES);

        assertEquals(1, p.getIdPalabra());
        assertEquals("ubiquitous", p.getPalabra());
        assertEquals("present everywhere", p.getDefinicion());
        assertEquals("Sapiens", p.getFuente());
        assertEquals(fecha, p.getFecha());
        assertEquals(Idioma.INGLES, p.getIdioma());
    }

    @Test
    void testConstructorSinIdValoresPorDefecto() {
        Palabra p = new Palabra("resiliencia", "capacidad de adaptación", null, null, Idioma.ESPANOL);

        assertEquals(0, p.getIdPalabra());
        assertEquals("resiliencia", p.getPalabra());
        assertEquals("capacidad de adaptación", p.getDefinicion());
        assertEquals("", p.getFuente(), "Si fuente es null debe inicializarse en cadena vacía");
        assertNotNull(p.getFecha(), "Si fecha es null debe asignarse la fecha actual");
        assertEquals(Idioma.ESPANOL, p.getIdioma());
    }

    @Test
    void testGettersYSetters() {
        Palabra p = new Palabra(10, "test", "def", "src", LocalDate.now(), Idioma.ESPANOL);

        p.setIdPalabra(20);
        p.setPalabra("modificado");
        p.setDefinicion("def modificada");
        p.setFuente("nueva fuente");
        LocalDate nuevaFecha = LocalDate.of(2025, 1, 1);
        p.setFecha(nuevaFecha);
        p.setIdioma(Idioma.INGLES);

        assertEquals(20, p.getIdPalabra());
        assertEquals(20, p.getId_palabra()); // Método de compatibilidad
        assertEquals("modificado", p.getPalabra());
        assertEquals("def modificada", p.getDefinicion());
        assertEquals("nueva fuente", p.getFuente());
        assertEquals(nuevaFecha, p.getFecha());
        assertEquals(Idioma.INGLES, p.getIdioma());
    }

    @Test
    void testEqualsYHashCode() {
        LocalDate fecha = LocalDate.of(2026, 10, 20);
        Palabra p1 = new Palabra(1, "casa", "hogar", "diccionario", fecha, Idioma.ESPANOL);
        Palabra p2 = new Palabra(1, "casa", "otra definicion", "diccionario", fecha, Idioma.ESPANOL);
        Palabra p3 = new Palabra(2, "casa", "hogar", "diccionario", fecha, Idioma.ESPANOL);

        assertEquals(p1, p2, "Palabras con mismo ID, término, fuente, fecha e idioma son equivalentes");
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1, p3, "Palabras con distinto ID no deben ser equivalentes");
        assertNotEquals(p1, null);
    }

    @Test
    void testToStringFormato() {
        Palabra p = new Palabra(1, "hola", "saludo", "RAE", LocalDate.of(2026, 1, 1), Idioma.ESPANOL);
        String texto = p.toString();
        assertTrue(texto.contains("hola"));
        assertTrue(texto.contains("2026-01-01"));
        assertTrue(texto.contains("RAE"));
        assertTrue(texto.contains("saludo"));
    }
}

