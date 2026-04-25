import { useNavigate } from 'react-router-dom';
import Button from '../common/Button';

export default function Topbar({ title, onLogout }) {
  const navigate = useNavigate();

  const handleLogout = () => {
    onLogout?.();
    navigate('/');
  };

  return (
    <header className="topbar">
      <h1>{title}</h1>
      <Button variant="secondary" onClick={handleLogout}>
        Cerrar sesión
      </Button>
    </header>
  );
}