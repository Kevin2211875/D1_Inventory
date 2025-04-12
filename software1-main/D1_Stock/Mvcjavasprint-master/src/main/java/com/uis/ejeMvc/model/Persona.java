package com.uis.ejeMvc.model;

import jakarta.persistence.*;

@Entity
@Table(name = "persona", schema = "dh")
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 40)
    private String name;
    @Column(nullable = false, length = 60)
    private String apellidos;
    @Column(nullable = false, length = 15)
    private String documento;
    @Column(nullable = false, length = 20)
    private String fechanacimento;
    @Column(nullable = false, length = 25)
    private String email;
    @Column(nullable = false, length = 12)
    private String telefono;
    @Column(nullable = false, length = 15)
    private String usuario;
    @Column(nullable = false, length = 15)
    private String contrasena;
    @Column(nullable = false, length = 15)
    private String rol;

    @ManyToOne
    @JoinColumn(name = "tipodocumento_id")
    private Tipodocumento tipodocumento;

    public Persona() {super();}

    public Persona(long id, String name, String apellidos, String documento, String fechanacimento, String email, String telefono, String usuario, String contrasena, String rol, Tipodocumento tipodocumento) {
        super();
        this.id = id;
        this.name = name;
        this.apellidos = apellidos;
        this.documento = documento;
        this.fechanacimento = fechanacimento;
        this.email = email;
        this.telefono = telefono;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.rol = rol;
        this.tipodocumento = tipodocumento;
    }

    public Persona(String name, String apellidos, String documento, String fechanacimento, String email, String telefono, String usuario, String contrasena, String rol, Tipodocumento tipodocumento) {
        super();
        this.name = name;
        this.apellidos = apellidos;
        this.documento = documento;
        this.fechanacimento = fechanacimento;
        this.email = email;
        this.telefono = telefono;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.rol = rol;
        this.tipodocumento = tipodocumento;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getFechanacimento() {
        return fechanacimento;
    }

    public void setFechanacimento(String fechanacimento) {
        this.fechanacimento = fechanacimento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Tipodocumento getTipodocumento() {
        return tipodocumento;
    }

    public void setTipodocumento(Tipodocumento tipodocumento) {
        this.tipodocumento = tipodocumento;
    }
}
