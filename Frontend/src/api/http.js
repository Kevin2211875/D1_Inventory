const API_BASE = import.meta.env.VITE_API_URL ?? '';

/**
 * Cliente HTTP para la API D1. Adjunta el Bearer token, serializa el body a JSON y normaliza los errores del
 * backend (`ApiError`) en un `Error` con `status` y `data` para que las pantallas puedan reaccionar a casos
 * concretos (p. ej. `data.codigo === 'PRODUCTO_DUPLICADO'`).
 */
export async function apiFetch(path, { token, method = 'GET', body, headers = {} } = {}) {
  const options = { method, headers: { ...headers } };

  if (token) {
    options.headers.Authorization = `Bearer ${token}`;
  }
  if (body !== undefined) {
    options.headers['Content-Type'] = 'application/json';
    options.body = JSON.stringify(body);
  }

  const response = await fetch(`${API_BASE}${path}`, options);
  const data = response.status === 204 ? null : await response.json().catch(() => null);

  if (!response.ok) {
    const error = new Error(data?.message || data?.error || 'Ocurrió un error al procesar la solicitud.');
    error.status = response.status;
    error.data = data;
    throw error;
  }

  return data;
}
