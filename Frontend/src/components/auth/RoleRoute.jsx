import { Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import ProtectedRoute from './ProtectedRoute';

/**
 * Ruta protegida que además exige que el usuario tenga al menos uno de los roles indicados.
 * Si está autenticado pero sin el rol, lo devuelve al panel principal.
 */
export default function RoleRoute({ roles, children }) {
  return (
    <ProtectedRoute>
      <RoleGate roles={roles}>{children}</RoleGate>
    </ProtectedRoute>
  );
}

function RoleGate({ roles, children }) {
  const { perfil } = useAuth();
  const userRoles = perfil?.rolesInternos ?? [];
  const permitido = roles.some((role) => userRoles.includes(role));

  if (!permitido) {
    return <Navigate to="/dashboard" replace />;
  }
  return children;
}
