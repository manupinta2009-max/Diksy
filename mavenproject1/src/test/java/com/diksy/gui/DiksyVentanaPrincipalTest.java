package com.diksy.gui;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

class DiksyVentanaPrincipalTest {

    @Test
    void testParsearFechaIso() {
        LocalDate fecha = DiksyVentanaPrincipal.parsearFecha("2026-10-28");
        assertEquals(LocalDate.of(2026, 10, 28), fecha);
    }

    @Test
    void testParsearFechaFormatoLatinoBarra() {
        LocalDate fecha = DiksyVentanaPrincipal.parsearFecha("28/10/2026");
        assertEquals(LocalDate.of(2026, 10, 28), fecha);

        LocalDate fechaCorta = DiksyVentanaPrincipal.parsearFecha("5/3/2026");
        assertEquals(LocalDate.of(2026, 3, 5), fechaCorta);
    }

    @Test
    void testParsearFechaFormatoLatinoGuion() {
        LocalDate fecha = DiksyVentanaPrincipal.parsearFecha("28-10-2026");
        assertEquals(LocalDate.of(2026, 10, 28), fecha);
    }

    @Test
    void testParsearFechaVaciaONulaRetornaHoy() {
        LocalDate hoy = LocalDate.now();
        assertEquals(hoy, DiksyVentanaPrincipal.parsearFecha(null));
        assertEquals(hoy, DiksyVentanaPrincipal.parsearFecha(""));
        assertEquals(hoy, DiksyVentanaPrincipal.parsearFecha("   "));
    }

    @Test
    void testParsearFechaInvalidaLanzaExcepcion() {
        assertThrows(DateTimeParseException.class, () -> DiksyVentanaPrincipal.parsearFecha("fecha-incorrecta"));
        assertThrows(DateTimeParseException.class, () -> DiksyVentanaPrincipal.parsearFecha("32/01/2026"));
        assertThrows(DateTimeParseException.class, () -> DiksyVentanaPrincipal.parsearFecha("2026-13-01"));
    }
}

