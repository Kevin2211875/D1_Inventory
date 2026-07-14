import { apiFetch } from './http';

/** Unidades de medida admitidas por el backend (enum UnidadMedida). */
export const UNIDADES_MEDIDA = [
  { value: 'unidad', label: 'Unidad' },
  { value: 'kg', label: 'Kilogramo (kg)' },
  { value: 'g', label: 'Gramo (g)' },
  { value: 'litro', label: 'Litro' },
  { value: 'ml', label: 'Mililitro (ml)' },
  { value: 'paquete', label: 'Paquete' },
  { value: 'caja', label: 'Caja' },
  { value: 'bolsa', label: 'Bolsa' },
  { value: 'metro', label: 'Metro' },
];

export function listarCategorias(token) {
  return apiFetch('/api/categorias', { token });
}

export function registrarProducto(token, payload) {
  return apiFetch('/api/productos', { token, method: 'POST', body: payload });
}

export function agregarStock(token, idProducto, payload) {
  return apiFetch(`/api/productos/${idProducto}/stock`, { token, method: 'POST', body: payload });
}
