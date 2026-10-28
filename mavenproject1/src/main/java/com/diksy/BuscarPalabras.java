package com.diksy;

import java.text.Collator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BuscarPalabras {

    private final RepoPalabras repo;

    public BuscarPalabras(RepoPalabras repo) {
        this.repo = repo;
    }

    // Busca palabras cuyo término contenga el texto indicado (sin importar mayúsculas)
    public List<Palabra> buscarPorLetras(String letras) {
        List<Palabra> resultados = new ArrayList<>();
        if (letras == null || repo == null) {
            return resultados;
        }

        String busqueda = letras.trim().toLowerCase();
        for (Palabra p : repo.listar()) {
            if (p.getPalabra() != null && p.getPalabra().toLowerCase().contains(busqueda)) {
                resultados.add(p);
            }
        }
        return resultados;
    }

    // Busca palabras registradas en una fecha determinada
    public List<Palabra> buscarPorFecha(LocalDate fecha) {
        List<Palabra> resultados = new ArrayList<>();
        if (fecha == null || repo == null) {
            return resultados;
        }

        for (Palabra p : repo.listar()) {
            if (fecha.equals(p.getFecha())) {
                resultados.add(p);
            }
        }
        return resultados;
    }

    // Busca palabras por su fuente (libro, web, etc.)
    public List<Palabra> buscarPorFuente(String fuente) {
        List<Palabra> resultados = new ArrayList<>();
        if (fuente == null || repo == null) {
            return resultados;
        }

        String busqueda = fuente.trim().toLowerCase();
        for (Palabra p : repo.listar()) {
            if (p.getFuente() != null && p.getFuente().toLowerCase().contains(busqueda)) {
                resultados.add(p);
            }
        }
        return resultados;
    }

    // SIT-03.3: Búsqueda por múltiples campos (palabra, definición o fuente)
    // SIT-03.2: Insensibilidad a mayúsculas y espacios (trim + toLowerCase)
    // SIT-03.4: Si el criterio está vacío, retorna la lista completa
    public List<Palabra> buscarGeneral(String criterio) {
        if (repo == null) {
            return new ArrayList<>();
        }
        return buscar(repo.listar(), criterio);
    }

    // Búsqueda en una lista por coincidencia en palabra, definición o fuente
    public static List<Palabra> buscar(List<Palabra> lista, String criterio) {
        List<Palabra> resultados = new ArrayList<>();
        if (lista == null) {
            return resultados;
        }
        if (criterio == null || criterio.trim().isEmpty()) {
            return new ArrayList<>(lista);
        }

        String busqueda = criterio.trim().toLowerCase();
        for (Palabra p : lista) {
            boolean coincidePalabra = p.getPalabra() != null && p.getPalabra().toLowerCase().contains(busqueda);
            boolean coincideDefinicion = p.getDefinicion() != null && p.getDefinicion().toLowerCase().contains(busqueda);
            boolean coincideFuente = p.getFuente() != null && p.getFuente().toLowerCase().contains(busqueda);

            if (coincidePalabra || coincideDefinicion || coincideFuente) {
                resultados.add(p);
            }
        }
        return resultados;
    }

    // SIT-06.1 y SIT-06.2: Ordenamiento alfabético con Collator en español
    public static void ordenarPorPalabra(List<Palabra> lista, boolean ascendente) {
        if (lista == null) return;
        Collator collator = Collator.getInstance(Locale.forLanguageTag("es-ES"));
        lista.sort((p1, p2) -> {
            String s1 = p1.getPalabra() != null ? p1.getPalabra() : "";
            String s2 = p2.getPalabra() != null ? p2.getPalabra() : "";
            int cmp = collator.compare(s1, s2);
            return ascendente ? cmp : -cmp;
        });
    }

    // CU-06: Ordenamiento por fecha
    public static void ordenarPorFecha(List<Palabra> lista, boolean ascendente) {
        if (lista == null) return;
        lista.sort((p1, p2) -> {
            LocalDate f1 = p1.getFecha() != null ? p1.getFecha() : LocalDate.MIN;
            LocalDate f2 = p2.getFecha() != null ? p2.getFecha() : LocalDate.MIN;
            return ascendente ? f1.compareTo(f2) : f2.compareTo(f1);
        });
    }

    // CU-06: Ordenamiento por fuente
    public static void ordenarPorFuente(List<Palabra> lista, boolean ascendente) {
        if (lista == null) return;
        Collator collator = Collator.getInstance(Locale.forLanguageTag("es-ES"));
        lista.sort((p1, p2) -> {
            String s1 = p1.getFuente() != null ? p1.getFuente() : "";
            String s2 = p2.getFuente() != null ? p2.getFuente() : "";
            int cmp = collator.compare(s1, s2);
            return ascendente ? cmp : -cmp;
        });
    }
}
