package com.uis.ejeMvc.enums.rolesSecurity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Reglas del rol activo: qué roles activos ofrece cada combinación de roles del usuario y cuáles pueden
 * registrar ventas. Es la lógica que protege el punto de venta, por eso se fija con pruebas.
 */
class RolActivoTest {

    private static final String GERENTE = RolUsuario.GERENTE.getNombreInterno();
    private static final String VENDEDOR = RolUsuario.VENDEDOR.getNombreInterno();
    private static final String ADMINISTRADOR = RolUsuario.ADMINISTRADOR.getNombreInterno();

    @Nested
    @DisplayName("disponiblesPara")
    class DisponiblesPara {

        @Test
        void sinRolesNoOfreceNada() {
            assertThat(RolActivo.disponiblesPara(null)).isEmpty();
            assertThat(RolActivo.disponiblesPara(List.of())).isEmpty();
        }

        @Test
        void vendedorSoloOfreceVendedor() {
            assertThat(RolActivo.disponiblesPara(List.of(VENDEDOR)))
                    .containsExactly(RolActivo.VENDEDOR);
        }

        @Test
        void gerenteSoloOfreceGerente() {
            assertThat(RolActivo.disponiblesPara(List.of(GERENTE)))
                    .containsExactly(RolActivo.GERENTE);
        }

        @Test
        void gerenteYVendedorSeCombinanEnUnSoloRolActivo() {
            assertThat(RolActivo.disponiblesPara(List.of(GERENTE, VENDEDOR)))
                    .containsExactly(RolActivo.GERENTE_VENDEDOR);
        }

        @Test
        void administradorSeOfreceSiempreDeFormaIndependiente() {
            assertThat(RolActivo.disponiblesPara(List.of(VENDEDOR, ADMINISTRADOR)))
                    .containsExactly(RolActivo.VENDEDOR, RolActivo.ADMINISTRADOR);
            assertThat(RolActivo.disponiblesPara(List.of(GERENTE, VENDEDOR, ADMINISTRADOR)))
                    .containsExactly(RolActivo.GERENTE_VENDEDOR, RolActivo.ADMINISTRADOR);
            assertThat(RolActivo.disponiblesPara(List.of(ADMINISTRADOR)))
                    .containsExactly(RolActivo.ADMINISTRADOR);
        }
    }

    @Nested
    @DisplayName("permiteRegistrarVentas")
    class PermiteRegistrarVentas {

        @Test
        void personalDeCajaPuedeVender() {
            assertThat(RolActivo.VENDEDOR.permiteRegistrarVentas()).isTrue();
            assertThat(RolActivo.GERENTE.permiteRegistrarVentas()).isTrue();
            assertThat(RolActivo.GERENTE_VENDEDOR.permiteRegistrarVentas()).isTrue();
        }

        @Test
        void administradorNoPuedeVender() {
            assertThat(RolActivo.ADMINISTRADOR.permiteRegistrarVentas()).isFalse();
        }
    }

    @Nested
    @DisplayName("fromHeader")
    class FromHeader {

        @Test
        void reconoceValoresDeCabeceraYNombresDeEnum() {
            assertThat(RolActivo.fromHeader("VENDEDOR")).isEqualTo(RolActivo.VENDEDOR);
            assertThat(RolActivo.fromHeader("GERENTE + VENDEDOR")).isEqualTo(RolActivo.GERENTE_VENDEDOR);
            assertThat(RolActivo.fromHeader("gerente_vendedor")).isEqualTo(RolActivo.GERENTE_VENDEDOR);
            assertThat(RolActivo.fromHeader("  gerente   +   vendedor ")).isEqualTo(RolActivo.GERENTE_VENDEDOR);
            assertThat(RolActivo.fromHeader("GERENTEVENDEDOR")).isEqualTo(RolActivo.GERENTE_VENDEDOR);
        }

        @Test
        void devuelveNullSiEsNuloOInvalido() {
            assertThat(RolActivo.fromHeader(null)).isNull();
            assertThat(RolActivo.fromHeader("SUPERVISOR")).isNull();
        }
    }
}
