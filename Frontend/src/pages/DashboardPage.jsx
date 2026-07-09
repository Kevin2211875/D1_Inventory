import AppLayout from '../components/layout/AppLayout';
import { useAuth } from '../context/AuthContext';
import './DashboardPage.css';

const ROLE_LABELS = {
  ADMINISTRADOR: 'Administrador',
  GERENTE: 'Gerente',
  VENDEDOR: 'Vendedor',
};

export default function DashboardPage() {
  const { perfil } = useAuth();

  return (
    <AppLayout title="Panel principal">
      <section className="dashboard">
        <div className="dashboard__hero">
          <p className="dashboard__welcome">Bienvenido</p>
          <h2>{perfil?.nombreCompleto || perfil?.username || 'Usuario D1'}</h2>
          <p className="dashboard__email">{perfil?.email}</p>
        </div>

        <div className="dashboard__grid">
          <article className="dashboard-card">
            <h3>Roles asignados</h3>
            {perfil?.rolesInternos?.length ? (
              <ul className="role-list">
                {perfil.rolesInternos.map((role) => (
                  <li key={role} className="role-badge">
                    {ROLE_LABELS[role] || role}
                  </li>
                ))}
              </ul>
            ) : (
              <p className="dashboard-card__empty">Sin roles asignados en Keycloak.</p>
            )}
          </article>

          <article className="dashboard-card">
            <h3>Acceso unificado</h3>
            <p>
              Ingresaste con un solo login. Las opciones del menú se adaptan según tus permisos
              en el sistema.
            </p>
          </article>
        </div>
      </section>
    </AppLayout>
  );
}
