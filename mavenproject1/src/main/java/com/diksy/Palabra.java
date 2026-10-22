package com.diksy;

import java.time.LocalDate;
import java.util.Objects;

public class Palabra {

    private int idPalabra;
    private String palabra;
    private String definicion;
    private String fuente;
    private LocalDate fecha;
    private Idioma idioma;

    // Constructor completo (con ID y fuente)
    public Palabra(int idPalabra, String palabra, String definicion, String fuente,
                   LocalDate fecha, Idioma idioma) {
        this.idPalabra = idPalabra;
        this.palabra = palabra;
        this.definicion = definicion;
        this.fuente = fuente != null ? fuente : "";
        this.fecha = fecha != null ? fecha : LocalDate.now();
        this.idioma = idioma;
    }

    // Constructor sin ID (útil para cuando el usuario crea una nueva palabra antes de guardarla)
    public Palabra(String palabra, String definicion, String fuente,
                   LocalDate fecha, Idioma idioma) {
        this(0, palabra, definicion, fuente, fecha, idioma);
    }

    // Constructor de compatibilidad (por si no se pasa fuente)
    public Palabra(int idPalabra, String palabra, String definicion,
                   LocalDate fecha, Idioma idioma) {
        this(idPalabra, palabra, definicion, "", fecha, idioma);
    }

    // Getters y Setters
    public int getIdPalabra() {
        return idPalabra;
    }

    public void setIdPalabra(int idPalabra) {
        this.idPalabra = idPalabra;
    }

    // Compatibilidad con nombre anterior
    public int getId_palabra() {
        return idPalabra;
    }

    public void setId_palabra(int id_palabra) {
        this.idPalabra = id_palabra;
    }

    public String getPalabra() {
        return palabra;
    }

    public void setPalabra(String palabra) {
        this.palabra = palabra;
    }

    public String getDefinicion() {
        return definicion;
    }

    public void setDefinicion(String definicion) {
        this.definicion = definicion;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Idioma getIdioma() {
        return idioma;
    }

    public void setIdioma(Idioma idioma) {
        this.idioma = idioma;
    }

    @Override
    public String toString() {
        return palabra + " (" + fecha + ") [" + fuente + "]: " + definicion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Palabra other = (Palabra) o;
        return idPalabra == other.idPalabra &&
               Objects.equals(palabra, other.palabra) &&
               Objects.equals(fuente, other.fuente) &&
               Objects.equals(fecha, other.fecha) &&
               idioma == other.idioma;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPalabra, palabra, fuente, fecha, idioma);
    }
}
