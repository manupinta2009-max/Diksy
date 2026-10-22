package com.mycompany.mavenproject1;

import java.util.ArrayList; // Permite utilizar ArrayList.
import java.util.List;      // Permite utilizar el tipo List.

public class RepoPalabras {
    private List<Palabra> palabras;
    public RepoPalabras() {
        palabras = new ArrayList<>();
    }
    
    
    // Método para agregar una palabra al repositorio.
    public void agregar(Palabra palabra) {
        palabras.add(palabra);
    }

    // Método para eliminar una palabra del repositorio.
    public void eliminar(Palabra palabra) {
        palabras.remove(palabra);
    }

    // Método para obtener todas las palabras.
    public List<Palabra> listar() {
        return palabras;
    }

public List<Palabra> buscarPorLetras(String letras) {
    List<Palabra> resultados = new ArrayList<>();
    for (Palabra palabra : palabras) {
        // Comprobamos si el texto contiene las letras buscadas.
        if (palabra.getPalabra().toLowerCase()
                .contains(letras.toLowerCase())) {
            // Si coincide, añadimos la palabra a los resultados.
            resultados.add(palabra);
        }
    }

    return resultados;
}
    
    // Método para buscar una palabra utilizando su ID.
    public Palabra buscarPorId(int id) {
        for (Palabra palabra : palabras) {
            if (palabra.getId_palabra() == id) {

                return palabra;
            }
        }
        return null;
    }
}
