package com.diksy;

import java.util.ArrayList;
import java.util.List;

public class RepoPalabras {

    private final List<Palabra> palabras;

    public RepoPalabras() {
        this.palabras = new ArrayList<>();
    }

    // Agrega una palabra al repositorio en memoria
    public void agregar(Palabra palabra) {
        if (palabra != null) {
            palabras.add(palabra);
        }
    }

    // Elimina una palabra del repositorio
    public boolean eliminar(Palabra palabra) {
        return palabras.remove(palabra);
    }

    // Elimina una palabra por su ID
    public boolean eliminarPorId(int id) {
        Palabra encontrada = buscarPorId(id);
        if (encontrada != null) {
            return palabras.remove(encontrada);
        }
        return false;
    }

    // Retorna una copia de la lista de palabras
    public List<Palabra> listar() {
        return new ArrayList<>(palabras);
    }

    // Busca una palabra por su ID
    public Palabra buscarPorId(int id) {
        for (Palabra p : palabras) {
            if (p.getIdPalabra() == id) {
                return p;
            }
        }
        return null;
    }

    // Vacía el repositorio
    public void limpiar() {
        palabras.clear();
    }

    // Cantidad total de palabras cargadas
    public int cantidad() {
        return palabras.size();
    }
}
