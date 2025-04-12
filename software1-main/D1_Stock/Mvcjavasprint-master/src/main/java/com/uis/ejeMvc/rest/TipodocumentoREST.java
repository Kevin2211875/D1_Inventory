package com.uis.ejeMvc.rest;

import com.uis.ejeMvc.model.Tipodocumento;
import com.uis.ejeMvc.service.TipodocumentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/tipodocumento/")
public class TipodocumentoREST {

    @Autowired
    private TipodocumentoService tipodocumentoService;

    @GetMapping
    private ResponseEntity<List<Tipodocumento>> getAllTipodocumento(){
        return ResponseEntity.ok(tipodocumentoService.findAll());
    }

    @PostMapping
    private ResponseEntity<Tipodocumento> saveTipodocumento(@RequestBody Tipodocumento tipodocumento) throws URISyntaxException {

        try {
            Tipodocumento tipodocumentoGuardada = tipodocumentoService.save(tipodocumento);
            return ResponseEntity.created(new URI("/tipodocumento/" + tipodocumento.getId()))
                    .body(tipodocumentoGuardada);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
