package com.uis.ejeMvc.service;

import com.uis.ejeMvc.dto.venta.RegistrarVentaRequestDTO;
import com.uis.ejeMvc.dto.venta.VentaResponseDTO;

public interface VentaService {
    VentaResponseDTO registrarVenta(RegistrarVentaRequestDTO request);
}
