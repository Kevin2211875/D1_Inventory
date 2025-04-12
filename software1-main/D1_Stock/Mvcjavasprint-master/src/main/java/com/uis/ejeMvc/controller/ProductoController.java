package com.uis.ejeMvc.controller;

import com.uis.ejeMvc.model.Producto;
import com.uis.ejeMvc.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/productos")
    public String ListaProductos(Model model){
        List<Producto> listaProducto = productoRepository.findAll();
        model.addAttribute("listaProducto", listaProducto);
        return "productos";
    }

    @GetMapping("/nuevos_productos")
    public String mostrarFormularioDeNuevoProducto(Model model){
        model.addAttribute("producto", new Producto());
        return "producto_formulario";
    }


    @PostMapping("/guardar_productos")
    public String guardarProducto(Producto producto){

        productoRepository.save(producto);
        return "redirect:/productos";
    }

    @GetMapping("/editar_productos/{id}")
    public String EditarProductos(Model model, @PathVariable Long id){
        Producto producto = productoRepository.findById(id).get();
        model.addAttribute("producto", producto);

        return "producto_formulario";
    }

    @GetMapping("/eliminar_productos/{id}")
    public String EliminarProductos(@PathVariable Long id){
        productoRepository.deleteById(id);
        return "redirect:/productos";
    }
}
