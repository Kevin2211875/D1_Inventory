import { apiFetch } from './http';

export function listarUsuarios(token) {
  return apiFetch('/api/admin/usuarios', { token });
}

export function crearUsuario(token, payload) {
  return apiFetch('/api/admin/usuarios', { token, method: 'POST', body: payload });
}

export function cambiarEstadoUsuario(token, userId, enabled) {
  return apiFetch(`/api/admin/usuarios/${userId}/estado`, { token, method: 'PUT', body: { enabled } });
}

export function cambiarRolUsuario(token, userId, rol) {
  return apiFetch(`/api/admin/usuarios/${userId}/rol`, { token, method: 'PUT', body: { rol } });
}

export function resetPasswordUsuario(token, userId, password) {
  return apiFetch(`/api/admin/usuarios/${userId}/password`, {
    token,
    method: 'PUT',
    body: { password, temporal: true },
  });
}
