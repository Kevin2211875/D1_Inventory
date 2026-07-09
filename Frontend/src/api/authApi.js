const API_BASE = import.meta.env.VITE_API_URL ?? '';

export async function loginRequest(email, password) {
  const response = await fetch(`${API_BASE}/api/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password }),
  });

  const data = await response.json().catch(() => null);

  if (!response.ok) {
    const message = data?.message || data?.error || 'Correo o contraseña incorrectos.';
    throw new Error(message);
  }

  return data;
}

export async function fetchProfile(accessToken) {
  const response = await fetch(`${API_BASE}/api/auth/me`, {
    headers: { Authorization: `Bearer ${accessToken}` },
  });

  const data = await response.json().catch(() => null);

  if (!response.ok) {
    throw new Error(data?.message || 'No se pudo obtener el perfil.');
  }

  return data;
}
