package com.diksy;

import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== Diksy: Sistema de Registro de Palabras ===");

        // Prueba básica de funcionamiento en consola
        RepoPalabras repo = new RepoPalabras();

        Palabra p1 = new Palabra(1, "ubiquitous", "present or found everywhere",
                "Libro: Sapiens", LocalDate.now(), Idioma.INGLES);
        Palabra p2 = new Palabra(2, "resiliencia", "capacidad de sobreponerse a momentos críticos",
                "Artículo Psicología", LocalDate.now(), Idioma.ESPANOL);

        repo.agregar(p1);
        repo.agregar(p2);

        System.out.println("Palabras registradas: " + repo.cantidad());

        BuscarPalabras buscador = new BuscarPalabras(repo);
        List<Palabra> encontrados = buscador.buscarPorLetras("ubi");

        System.out.println("\nBúsqueda con 'ubi':");
        for (Palabra p : encontrados) {
            System.out.println(" - " + p);
        }
    }
}
