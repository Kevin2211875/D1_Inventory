package com.uis.ejeMvc.rest;

import com.uis.ejeMvc.model.Producto;
import com.uis.ejeMvc.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/producto/")
public class ProductoREST {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    private ResponseEntity<List<Producto>> getAllTipodocumento(){
        return ResponseEntity.ok(productoService.findAll());
    }

    @PostMapping
    private ResponseEntity<Producto> saveTipodocumento(@RequestBody Producto producto) throws URISyntaxException {

        try {
            Producto tipodocumentoGuardada = productoService.save(producto);
            return ResponseEntity.created(new URI("/tipodocumento/" + producto.getId()))
                    .body(tipodocumentoGuardada);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}