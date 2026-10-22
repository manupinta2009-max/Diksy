package com.mycompany.mavenproject1;

import java.time.LocalDate;

public class Palabra {

    private int id_palabra;
    private String palabra;
    private String definicion;
    private LocalDate fecha;
    private Idioma idioma;
    private String fuente;

    public Palabra(int id_palabra, String palabra, String definicion,
                   LocalDate fecha, Idioma idioma) {

        this.id_palabra = id_palabra;
        this.palabra = palabra;
        this.definicion = definicion;
        this.fecha = fecha;
        this.idioma = idioma;
    }


    //Getters y Setters
    public int getId_palabra() {
        return id_palabra;
    }
    public void setId_palabra(int id_palabra) {
        this.id_palabra = id_palabra;
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
    public String getFuente() {
        return fuente;
    }
    public void setFuente(String fuente) {
        this.fuente = fuente;
    }
    
}
