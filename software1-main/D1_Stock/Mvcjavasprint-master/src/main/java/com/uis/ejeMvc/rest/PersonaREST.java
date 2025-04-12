package com.uis.ejeMvc.rest;

import com.uis.ejeMvc.model.Persona;
import com.uis.ejeMvc.service.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/persona/")
public class PersonaREST {

    @Autowired
    private PersonaService personaService;


    @GetMapping
    private ResponseEntity<List<Persona>> getAllPersona() {
        return ResponseEntity.ok(personaService.findAll());
    }

    @PostMapping
    private ResponseEntity<Persona> savePersona(@RequestBody Persona persona) throws URISyntaxException {

        try {
            Persona personaGuardada = personaService.savePersona(persona);
            return ResponseEntity.created(new URI("/persona/" + persona.getId()))
                    .body(personaGuardada);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


}