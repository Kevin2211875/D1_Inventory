package com.uis.ejeMvc.security.roles;

import com.uis.ejeMvc.enums.rolesSecurity.RolUsuario;

/**
 * Expresiones {@code @PreAuthorize} centralizadas, alineadas con {@link RolUsuario}.
 *
 * <p>Las agrupaciones con {@code hasAnyRole} exigen <strong>al menos uno</strong> de los roles; las que usan
 * {@code hasRole(...) and hasRole(...)} exigen <strong>todos</strong>. Cada constante es un {@code String}
 * compilable, por lo que puede usarse directamente en {@code @PreAuthorize(SecureRoles.SOLO_GERENTE)} o
 * concatenarse con {@link #ROLE_AND} / {@link #ROLE_OR}.</p>
 *
 * <p>Roles de la tienda D1: <strong>ADMINISTRADOR</strong> (gestión integral de la plataforma),
 * <strong>GERENTE</strong> (gestión de la tienda) y <strong>VENDEDOR</strong> (operación de ventas).</p>
 */
public final class SecureRoles {

    private static final String E = "T(com.uis.ejeMvc.enums.rolesSecurity.RolUsuario)";

    /** Conector SpEL {@code and} entre varias expresiones {@code hasRole(...)}. */
    public static final String ROLE_AND = " and ";

    /** Conector SpEL {@code or}. */
    public static final String ROLE_OR = " or ";

    /** Prefijo SpEL {@code hasRole(}. */
    public static final String HAS_ROLE_OPEN = "hasRole(";

    /** Prefijo SpEL {@code hasAnyRole(}. */
    public static final String HAS_ANY_ROLE_OPEN = "hasAnyRole(";

    /** Cierre {@code )} de {@link #HAS_ROLE_OPEN} / {@link #HAS_ANY_ROLE_OPEN}. */
    public static final String ROLE_EXPR_CLOSE = ")";

    // --- Fragmentos SpEL por rol (nombre interno) ---

    public static final String GERENTE = E + ".GERENTE.getNombreInterno()";
    public static final String VENDEDOR = E + ".VENDEDOR.getNombreInterno()";
    public static final String ADMINISTRADOR = E + ".ADMINISTRADOR.getNombreInterno()";

    // --- Un solo rol ---

    public static final String SOLO_GERENTE = HAS_ROLE_OPEN + GERENTE + ROLE_EXPR_CLOSE;
    public static final String SOLO_VENDEDOR = HAS_ROLE_OPEN + VENDEDOR + ROLE_EXPR_CLOSE;
    public static final String SOLO_ADMINISTRADOR = HAS_ROLE_OPEN + ADMINISTRADOR + ROLE_EXPR_CLOSE;

    // --- Agrupaciones OR (hasAnyRole): al menos uno ---

    /** Gerente o administrador (administración de la tienda). */
    public static final String GERENTE_O_ADMIN =
            HAS_ANY_ROLE_OPEN + GERENTE + ", " + ADMINISTRADOR + ROLE_EXPR_CLOSE;

    /** Vendedor o gerente (operación + supervisión de ventas). */
    public static final String VENDEDOR_O_GERENTE =
            HAS_ANY_ROLE_OPEN + VENDEDOR + ", " + GERENTE + ROLE_EXPR_CLOSE;

    /** Cualquier miembro del personal de la tienda (los tres roles). */
    public static final String STAFF_TIENDA =
            HAS_ANY_ROLE_OPEN + VENDEDOR + ", " + GERENTE + ", " + ADMINISTRADOR + ROLE_EXPR_CLOSE;

    // --- Agrupaciones AND (hasRole y hasRole): deben estar todos en el token ---

    /** Usuario que es gerente y vendedor a la vez. */
    public static final String GERENTE_Y_VENDEDOR =
            HAS_ROLE_OPEN + GERENTE + ROLE_EXPR_CLOSE + ROLE_AND + HAS_ROLE_OPEN + VENDEDOR + ROLE_EXPR_CLOSE;

    private SecureRoles() {}
}
