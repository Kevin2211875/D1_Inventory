import { useState } from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import D1Logo from '../components/brand/D1Logo';
import Button from '../components/common/Button';
import InputField from '../components/common/InputField';
import { useAuth } from '../context/AuthContext';
import useForm from '../hooks/useForm';
import './LoginPage.css';

export default function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login, loading, isAuthenticated } = useAuth();
  const { values, handleChange } = useForm({ email: '', password: '' });
  const [error, setError] = useState('');

  const redirectTo = location.state?.from || '/dashboard';

  if (isAuthenticated) {
    return <Navigate to={redirectTo} replace />;
  }

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');

    try {
      await login(values.email.trim(), values.password);
      navigate(redirectTo, { replace: true });
    } catch (err) {
      setError(err.message || 'No fue posible iniciar sesión.');
    }
  };

  return (
    <div className="login-page">
      <div className="login-page__band" aria-hidden="true" />

      <div className="login-page__container">
        <section className="login-card">
          <header className="login-card__header">
            <D1Logo size={80} />
            <div>
              <p className="login-card__eyebrow">Inventario D1</p>
              <h1>Iniciar sesión</h1>
              <p className="login-card__subtitle">
                Ingresa con tu correo y contraseña. El sistema reconocerá tu rol automáticamente.
              </p>
            </div>
          </header>

          <form className="login-form" onSubmit={handleSubmit} noValidate>
            {error && (
              <div className="login-form__error" role="alert">
                {error}
              </div>
            )}

            <InputField
              label="Correo electrónico"
              name="email"
              type="email"
              value={values.email}
              onChange={handleChange}
              placeholder="usuario@correo.com"
              autoComplete="email"
              required
            />

            <InputField
              label="Contraseña"
              name="password"
              type="password"
              value={values.password}
              onChange={handleChange}
              placeholder="••••••••"
              autoComplete="current-password"
              required
            />

            <Button type="submit" disabled={loading} className="login-form__submit">
              {loading ? 'Ingresando...' : 'Ingresar'}
            </Button>
          </form>
        </section>

        <footer className="login-page__footer">
          <span>Tiendas D1</span>
          <span className="login-page__dot" aria-hidden="true" />
          <span>Gestión de inventario</span>
        </footer>
      </div>
    </div>
  );
}
