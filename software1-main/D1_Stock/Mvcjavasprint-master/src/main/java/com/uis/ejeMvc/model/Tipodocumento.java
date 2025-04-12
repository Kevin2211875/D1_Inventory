package com.uis.ejeMvc.model;

import jakarta.persistence.*;


@Entity
@Table(name = "tipodocumento", schema = "dh")
public class Tipodocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nombre;
    private String descripcion;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Tipodocumento(){
        super();
    }

    public Tipodocumento(String nombre, String descripcion) {
        super();
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Tipodocumento(long id, String nombre, String descripcion) {

        super();
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}
