package com.uis.ejeMvc.controller;

import com.uis.ejeMvc.model.Persona;
import com.uis.ejeMvc.model.Tipodocumento;
import com.uis.ejeMvc.repository.PersonaRepositoryInter;
import com.uis.ejeMvc.repository.TipodocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class PersonaController {
    @Autowired
    private PersonaRepositoryInter personaRepositoryInter;

    @Autowired
    private TipodocumentoRepository tipodocumentoRepository;

    @GetMapping("/personas")
    public String ListaPersonas(Model model){
        List<Persona> listaPersona = personaRepositoryInter.findAll();
        model.addAttribute("listaPersona", listaPersona);
        return "personas";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioDeNuevaPersona(Model model){
        List<Tipodocumento> listaTipodocumento = tipodocumentoRepository.findAll();

        model.addAttribute("persona", new Persona());
        model.addAttribute("listaTipodocumento", listaTipodocumento);
        return "persona_formulario";
    }

    @PostMapping("/guardar")
    public String guardarPersona(Persona persona){
        persona.setRol("Empleado");
        personaRepositoryInter.save(persona);
        return "redirect:/personas";
    }

    @GetMapping("/inicio")
    public String regreso() {
        return "index";
    }

    @GetMapping("/personas/eliminar/{id}")
    public String deletePersona(@PathVariable ("id") Long id, Model model){
        personaRepositoryInter.deleteById(id);
        return "redirect:/personas";
    }

    @GetMapping("/personas/editar/{id}")
    public String EditarPersona(@PathVariable("id") Long id, Model modelo){
        Persona persona = personaRepositoryInter.findById(id).get();
        modelo.addAttribute("persona", persona);

        List<Tipodocumento> listaTipodocumento = tipodocumentoRepository.findAll();
        modelo.addAttribute("listaTipodocumento", listaTipodocumento);

        return "persona_formulario";
    }

    @PostMapping("/login_gerente")
    public String loginGerente(@RequestParam String usuario, @RequestParam String contrasena, RedirectAttributes redirectAttributes) {
        Persona persona = personaRepositoryInter.findByUsuarioAndContrasena(usuario, contrasena);

        if (persona == null) {
            redirectAttributes.addFlashAttribute("error", "Usuario o contraseña incorrectos");
            return "redirect:/sesion_gerente";
        } else if ("Empleado".equals(persona.getRol())) {
            redirectAttributes.addFlashAttribute("error", "Acceso no permitido para el rol de Empleado");
            return "redirect:/sesion_gerente";
        } else {
            return "gerente";
        }
    }

    @GetMapping("/sesion_gerente")
    public String sesionGerente() {
        return "inicio_sesion_gerente";
    }

    @PostMapping("/login_empleado")
    public String loginEmpleado(@RequestParam String usuario, @RequestParam String contrasena, RedirectAttributes redirectAttributes) {
        Persona persona = personaRepositoryInter.findByUsuarioAndContrasena(usuario, contrasena);

        if (persona == null) {

            redirectAttributes.addFlashAttribute("error", "Usuario o contraseña incorrectos");
            return "redirect:/sesion_empleado";
        } else if ("Gerente".equals(persona.getRol())) {

            redirectAttributes.addFlashAttribute("error", "Acceso no permitido para el rol de Gerente");
            return "redirect:/sesion_empleado";
        } else {
            return "empleado";
        }
    }

    @GetMapping("/sesion_empleado")
    public String sesionEmpleado() {
        return "inicio_sesion_empleado";
    }

}


/*Querys para la BD
* insert into persona(apellidos, contrasena, documento, email, fechanacimento, name, telefono, usuario, tipodocumento_id, rol)
VALUES ('Contreras', '45678', '10258475', 'contre@gmial.com','1999-02-02',
        'Carlos', '3155558', 'contre', 1, 'Empleado' );

INSERT INTO persona (apellidos, contrasena, documento, email, fechanacimento, name, telefono, usuario, tipodocumento_id, rol)
VALUES
    ('Ramírez', 'abc123', '20314567', 'ramirez@mail.com', '1988-08-15', 'Lucía', '3123456789', 'luciar', 2, 'Gerente'),
    ('Gómez', 'pass2023', '30567891', 'gomezj@correo.com', '1995-11-30', 'Julián', '3179876543', 'juliang', 3, 'Empleado');

insert into tipodocumento(id, nombre, descripcion)
values (1, 'C.C', 'documento para mayores de edad'),
       (2, 'T.I', 'documento para menores de edad'),
       (3, 'C.E', 'dcoumento para extranjeros');

INSERT INTO producto (nombre, descripcion, precio, cantidad, fecha_llegada, fecha_expiracion)
VALUES
    ('Manzanas', 'Fruta fresca y jugosa', '2.50', '100', '2024-12-01', '2024-12-10'),
    ('Leche', 'Leche entera pasteurizada', '1.20', '50', '2024-12-01', '2024-12-15'),
    ('Pan Integral', 'Pan saludable sin conservantes', '1.00', '30', '2024-12-01', '2024-12-05'),
    ('Arroz', 'Arroz blanco de grano largo', '0.90', '200', '2024-12-01', '2025-01-01');

* */