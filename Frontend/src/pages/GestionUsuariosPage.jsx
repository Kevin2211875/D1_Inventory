import { useCallback, useEffect, useState } from 'react';
import AppLayout from '../components/layout/AppLayout';
import Button from '../components/common/Button';
import InputField from '../components/common/InputField';
import SelectField from '../components/common/SelectField';
import { useAuth } from '../context/AuthContext';
import {
  cambiarEstadoUsuario,
  cambiarRolUsuario,
  crearUsuario,
  listarUsuarios,
  resetPasswordUsuario,
} from '../api/usuariosApi';

const ROLES_ASIGNABLES = [
  { value: 'GERENTE', label: 'Gerente' },
  { value: 'VENDEDOR', label: 'Vendedor' },
];

const ROLE_LABELS = {
  ADMINISTRADOR: 'Administrador',
  GERENTE: 'Gerente',
  VENDEDOR: 'Vendedor',
};

export default function GestionUsuariosPage() {
  const { accessToken } = useAuth();
  const [usuarios, setUsuarios] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const [exito, setExito] = useState('');
  const [accion, setAccion] = useState(false);
  const [modalCrear, setModalCrear] = useState(false);
  const [resetTarget, setResetTarget] = useState(null);

  const recargar = useCallback(async () => {
    setCargando(true);
    setError('');
    try {
      setUsuarios(await listarUsuarios(accessToken));
    } catch (err) {
      setError(err.message);
    } finally {
      setCargando(false);
    }
  }, [accessToken]);

  // Carga inicial: el setState ocurre tras el await para no disparar renders en cascada dentro del efecto.
  useEffect(() => {
    let activo = true;
    (async () => {
      try {
        const data = await listarUsuarios(accessToken);
        if (activo) setUsuarios(data);
      } catch (err) {
        if (activo) setError(err.message);
      } finally {
        if (activo) setCargando(false);
      }
    })();
    return () => {
      activo = false;
    };
  }, [accessToken]);

  const ejecutar = async (accionAsync, mensajeExito) => {
    setError('');
    setExito('');
    setAccion(true);
    try {
      await accionAsync();
      setExito(mensajeExito);
      await recargar();
    } catch (err) {
      setError(err.message);
    } finally {
      setAccion(false);
    }
  };

  const toggleEstado = (usuario) =>
    ejecutar(
      () => cambiarEstadoUsuario(accessToken, usuario.id, !usuario.enabled),
      `Usuario ${usuario.username} ${usuario.enabled ? 'desactivado' : 'activado'}.`,
    );

  const cambiarRol = (usuario, rol) =>
    ejecutar(
      () => cambiarRolUsuario(accessToken, usuario.id, rol),
      `Rol de ${usuario.username} actualizado a ${ROLE_LABELS[rol] || rol}.`,
    );

  return (
    <AppLayout title="Gestión de usuarios">
      <div className="page-header">
        <div>
          <h2>Gestión de usuarios</h2>
          <p>Administra el personal de la tienda: crea usuarios, asigna roles y controla su acceso.</p>
        </div>
        <Button onClick={() => setModalCrear(true)} disabled={accion}>
          + Crear usuario
        </Button>
      </div>

      {error && <div className="alert alert--error">{error}</div>}
      {exito && <div className="alert alert--success">{exito}</div>}

      <div className="card">
        {cargando ? (
          <p className="card__subtitle">Cargando usuarios…</p>
        ) : usuarios.length === 0 ? (
          <p className="card__subtitle">No hay usuarios registrados en el realm.</p>
        ) : (
          <div className="table-wrapper">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Usuario</th>
                  <th>Correo</th>
                  <th>Roles</th>
                  <th>Estado</th>
                  <th>Rol</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {usuarios.map((usuario) => {
                  const esAdmin = usuario.roles.includes('ADMINISTRADOR');
                  const rolActual =
                    usuario.roles.find((r) => r === 'GERENTE' || r === 'VENDEDOR') || '';
                  return (
                    <tr key={usuario.id}>
                      <td>
                        <strong>{usuario.username}</strong>
                        {usuario.nombreCompleto && (
                          <div style={{ color: 'var(--d1-muted)', fontSize: '0.8rem' }}>
                            {usuario.nombreCompleto}
                          </div>
                        )}
                      </td>
                      <td>{usuario.email || '—'}</td>
                      <td>
                        {usuario.roles.length === 0 ? (
                          <span style={{ color: 'var(--d1-muted)' }}>Sin roles</span>
                        ) : (
                          usuario.roles.map((rol) => (
                            <span key={rol} className="badge badge--role">
                              {ROLE_LABELS[rol] || rol}
                            </span>
                          ))
                        )}
                      </td>
                      <td>
                        <span className={`badge ${usuario.enabled ? 'badge--on' : 'badge--off'}`}>
                          {usuario.enabled ? 'Activo' : 'Inactivo'}
                        </span>
                      </td>
                      <td>
                        {esAdmin ? (
                          <span style={{ color: 'var(--d1-muted)' }}>—</span>
                        ) : (
                          <select
                            className="form-control"
                            style={{ minHeight: 38, maxWidth: 150 }}
                            value={rolActual}
                            disabled={accion}
                            onChange={(e) => cambiarRol(usuario, e.target.value)}
                          >
                            <option value="" disabled>
                              Sin rol
                            </option>
                            {ROLES_ASIGNABLES.map((r) => (
                              <option key={r.value} value={r.value}>
                                {r.label}
                              </option>
                            ))}
                          </select>
                        )}
                      </td>
                      <td>
                        {esAdmin ? (
                          <span style={{ color: 'var(--d1-muted)' }}>Protegido</span>
                        ) : (
                          <div className="table-actions">
                            <Button
                              variant={usuario.enabled ? 'ghost' : 'primary'}
                              className="btn--sm"
                              disabled={accion}
                              onClick={() => toggleEstado(usuario)}
                            >
                              {usuario.enabled ? 'Desactivar' : 'Activar'}
                            </Button>
                            <Button
                              variant="secondary"
                              className="btn--sm"
                              disabled={accion}
                              onClick={() => setResetTarget(usuario)}
                            >
                              Contraseña
                            </Button>
                          </div>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {modalCrear && (
        <CrearUsuarioModal
          onClose={() => setModalCrear(false)}
          onSuccess={(usuario) => {
            setModalCrear(false);
            setExito(`Usuario ${usuario.username} creado.`);
            recargar();
          }}
        />
      )}

      {resetTarget && (
        <ResetPasswordModal
          usuario={resetTarget}
          onClose={() => setResetTarget(null)}
          onSuccess={() => {
            setResetTarget(null);
            setExito(`Contraseña de ${resetTarget.username} restablecida (temporal).`);
          }}
        />
      )}
    </AppLayout>
  );
}

function CrearUsuarioModal({ onClose, onSuccess }) {
  const { accessToken } = useAuth();
  const [values, setValues] = useState({
    email: '',
    username: '',
    nombre: '',
    apellido: '',
    password: '',
    rol: 'VENDEDOR',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setValues((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!values.email.trim()) {
      setError('El correo es obligatorio.');
      return;
    }
    if (values.password.length < 6) {
      setError('La contraseña temporal debe tener al menos 6 caracteres.');
      return;
    }

    const payload = {
      email: values.email.trim(),
      username: values.username.trim() || undefined,
      nombre: values.nombre.trim() || undefined,
      apellido: values.apellido.trim() || undefined,
      password: values.password,
      rol: values.rol,
    };

    setLoading(true);
    try {
      const creado = await crearUsuario(accessToken, payload);
      onSuccess(creado);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" role="dialog" aria-modal="true">
      <div className="modal">
        <div className="modal__header">
          <div>
            <h3>Crear usuario</h3>
            <p>Se crea en Keycloak con una contraseña temporal que el usuario deberá cambiar.</p>
          </div>
          <button type="button" className="modal__close" onClick={onClose} aria-label="Cerrar">
            ×
          </button>
        </div>

        {error && <div className="alert alert--error">{error}</div>}

        <form className="modal__grid" onSubmit={handleSubmit}>
          <InputField
            label="Correo *"
            name="email"
            type="email"
            value={values.email}
            onChange={handleChange}
            required
          />
          <InputField
            label="Nombre de usuario"
            name="username"
            value={values.username}
            onChange={handleChange}
            placeholder="Si se deja vacío, se usa el correo"
          />
          <InputField label="Nombres" name="nombre" value={values.nombre} onChange={handleChange} />
          <InputField label="Apellidos" name="apellido" value={values.apellido} onChange={handleChange} />
          <InputField
            label="Contraseña temporal *"
            name="password"
            type="password"
            value={values.password}
            onChange={handleChange}
            required
          />
          <SelectField
            label="Rol *"
            name="rol"
            value={values.rol}
            onChange={handleChange}
            options={ROLES_ASIGNABLES}
            required
          />

          <div className="form-actions">
            <Button type="submit" disabled={loading}>
              {loading ? 'Creando…' : 'Crear usuario'}
            </Button>
            <Button type="button" variant="secondary" onClick={onClose} disabled={loading}>
              Cancelar
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}

function ResetPasswordModal({ usuario, onClose, onSuccess }) {
  const { accessToken } = useAuth();
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    if (password.length < 6) {
      setError('La contraseña debe tener al menos 6 caracteres.');
      return;
    }
    setLoading(true);
    try {
      await resetPasswordUsuario(accessToken, usuario.id, password);
      onSuccess();
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" role="dialog" aria-modal="true">
      <div className="modal">
        <div className="modal__header">
          <div>
            <h3>Restablecer contraseña</h3>
            <p>{usuario.username} · la nueva contraseña será temporal.</p>
          </div>
          <button type="button" className="modal__close" onClick={onClose} aria-label="Cerrar">
            ×
          </button>
        </div>

        {error && <div className="alert alert--error">{error}</div>}

        <form className="modal__grid" onSubmit={handleSubmit}>
          <InputField
            label="Nueva contraseña temporal *"
            name="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <div className="form-actions">
            <Button type="submit" disabled={loading}>
              {loading ? 'Guardando…' : 'Restablecer'}
            </Button>
            <Button type="button" variant="secondary" onClick={onClose} disabled={loading}>
              Cancelar
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}
