package com.mycompany.mavenproject1;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BuscarPalabras {

    private RepoPalabras repo;

    public BuscarPalabras(RepoPalabras repo) {
        this.repo = repo;
    }

    // Busca palabras cuyo nombre contenga las letras indicadas.
    public List<Palabra> buscarPorLetras(String letras) {

        List<Palabra> resultados = new ArrayList<>(); // Lista de coincidencias.

        // Recorremos las palabras que tiene el repositorio.
        for (Palabra palabra : repo.listar()) {

            // Comparamos ignorando mayúsculas y minúsculas.
            if (palabra.getPalabra().toLowerCase()
                    .contains(letras.toLowerCase())) {

                resultados.add(palabra); // Guardamos la coincidencia.
            }
        }

        return resultados; // Devolvemos los resultados encontrados.
    }

    // Busca palabras registradas en una fecha determinada.
    public List<Palabra> buscarPorFecha(LocalDate fecha) {

        List<Palabra> resultados = new ArrayList<>(); // Lista de coincidencias.

        for (Palabra palabra : repo.listar()) { // Recorremos los registros.

            if (palabra.getFecha().equals(fecha)) { // Comparamos las fechas.
                resultados.add(palabra); // Agregamos la coincidencia.
            }
        }

        return resultados; // Devolvemos la lista.
    }

    // Busca palabras por el nombre de su fuente.
    public List<Palabra> buscarPorFuente(String fuente) {

        List<Palabra> resultados = new ArrayList<>(); // Lista de coincidencias.

        for (Palabra palabra : repo.listar()) { // Recorremos los registros.

            if (palabra.getFuente().toLowerCase()
                    .contains(fuente.toLowerCase())) {

                resultados.add(palabra); // Agregamos la coincidencia.
            }
        }

        return resultados; // Devolvemos la lista.
    }
}