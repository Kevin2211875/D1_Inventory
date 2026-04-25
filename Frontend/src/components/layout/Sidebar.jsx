import { NavLink } from 'react-router-dom';

export default function Sidebar({ role }) {
  const gerenteLinks = [
    { to: '/gerente', label: 'Panel Gerente' },
    { to: '/personas', label: 'Personas' },
    { to: '/productos', label: 'Productos' },
  ];

  const empleadoLinks = [
    { to: '/empleado', label: 'Panel Empleado' },
    { to: '/productos', label: 'Productos' },
  ];

  const links = role === 'gerente' ? gerenteLinks : empleadoLinks;

  return (
    <aside className="sidebar">
      <h2>App Personas</h2>
      <nav>
        {links.map((link) => (
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