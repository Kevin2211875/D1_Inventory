import { useNavigate } from 'react-router-dom';
import Button from '../common/Button';
import { useAuth } from '../../context/AuthContext';

export default function Topbar({ title }) {
  const navigate = useNavigate();
  const { logout, perfil } = useAuth();

  const handleLogout = () => {
    logout();
    navigate('/login', { replace: true });
  };

  return (
    <header className="topbar">
      <h1>{title}</h1>
      <div className="topbar__user">
        <div className="topbar__user-info">
          <strong>{perfil?.nombreCompleto || perfil?.username}</strong>
          <span>{perfil?.email}</span>
        </div>
        <Button variant="secondary" onClick={handleLogout}>
          Cerrar sesión
        </Button>
      </div>
    </header>
  );
}
