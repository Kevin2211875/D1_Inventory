import { NavLink } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const NAV_ITEMS = [
  { to: '/dashboard', label: 'Inicio', roles: ['ADMINISTRADOR', 'GERENTE', 'VENDEDOR'] },
];

export default function Sidebar() {
  const { perfil } = useAuth();
  const userRoles = perfil?.rolesInternos ?? [];

  const links = NAV_ITEMS.filter((item) =>
    item.roles.some((role) => userRoles.includes(role)),
  );

  const visibleLinks = links.length > 0 ? links : NAV_ITEMS.slice(0, 1);

  return (
    <aside className="sidebar">
      <div className="sidebar__brand">
        <div>
          <h2>Inventario D1</h2>
          <span>Gestión interna</span>
        </div>
      </div>
      <nav>
        {visibleLinks.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
          >
            {link.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
