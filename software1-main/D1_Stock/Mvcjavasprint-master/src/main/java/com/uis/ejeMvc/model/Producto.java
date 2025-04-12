package com.uis.ejeMvc.model;

import jakarta.persistence.*;

    @Entity
    @Table(name = "Producto", schema = "dh")
    public class Producto {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        private String nombre;
        private String descripcion;
        private String precio;
        private String cantidad;
        private String FechaLlegada;
        private String FechaExpiracion;

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

        public String getPrecio() {
            return precio;
        }

        public void setPrecio(String precio) {
            this.precio = precio;
        }

        public String getCantidad() {
            return cantidad;
        }

        public void setCantidad(String cantidad) {
            this.cantidad = cantidad;
        }

        public String getFechaLlegada() {
            return FechaLlegada;
        }

        public void setFechaLlegada(String fechaLlegada) {
            FechaLlegada = fechaLlegada;
        }

        public String getFechaExpiracion() {
            return FechaExpiracion;
        }

        public void setFechaExpiracion(String fechaExpiracion) {
            FechaExpiracion = fechaExpiracion;
        }

        public Producto(long id, String nombre, String descripcion, String precio, String cantidad, String fechaLlegada, String fechaExpiracion) {
            super();
            this.id = id;
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.precio = precio;
            this.cantidad = cantidad;
            this.FechaLlegada = fechaLlegada;
            this.FechaExpiracion = fechaExpiracion;
        }

        public Producto() {super();}

        public Producto(String nombre, String descripcion, String precio, String cantidad, String fechaLlegada, String fechaExpiracion) {
            super();
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.precio = precio;
            this.cantidad = cantidad;
            this.FechaLlegada = fechaLlegada;
            this.FechaExpiracion = fechaExpiracion;
        }
    }
