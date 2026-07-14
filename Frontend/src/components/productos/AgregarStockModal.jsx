import { useState } from 'react';
import Button from '../common/Button';
import InputField from '../common/InputField';
import { agregarStock } from '../../api/productoApi';
import { useAuth } from '../../context/AuthContext';

/**
 * Modal para registrar una entrada de stock (lote) de un producto existente.
 * `producto` debe incluir al menos { idProducto, nombre, esPerecedero, stockActual }.
 */
export default function AgregarStockModal({ producto, onClose, onSuccess }) {
  const { accessToken } = useAuth();
  const [values, setValues] = useState({
    cantidad: '',
    precioCompra: '',
    numeroLote: '',
    fechaVencimiento: '',
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

    const cantidad = Number(values.cantidad);
    const precioCompra = Number(values.precioCompra);

    if (!(cantidad > 0)) {
      setError('La cantidad debe ser mayor a cero.');
      return;
    }
    if (!(precioCompra > 0)) {
      setError('El precio de compra debe ser mayor a cero.');
      return;
    }
    if (producto.esPerecedero && !values.fechaVencimiento) {
      setError('Este producto es perecedero: indique la fecha de vencimiento del lote.');
      return;
    }

    const payload = {
      cantidad,
      precioCompra,
      numeroLote: values.numeroLote.trim() || undefined,
      fechaVencimiento: values.fechaVencimiento || undefined,
    };

    setLoading(true);
    try {
      const actualizado = await agregarStock(accessToken, producto.idProducto, payload);
      onSuccess(actualizado);
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
            <h3>Agregar stock</h3>
            <p>
              {producto.nombre}
              {producto.stockActual != null && ` · Stock actual: ${producto.stockActual}`}
            </p>
          </div>
          <button type="button" className="modal__close" onClick={onClose} aria-label="Cerrar">
            ×
          </button>
        </div>

        {error && <div className="alert alert--error">{error}</div>}

        <form className="modal__grid" onSubmit={handleSubmit}>
          <InputField
            label="Cantidad a ingresar"
            name="cantidad"
            type="number"
            min="0"
            step="0.001"
            value={values.cantidad}
            onChange={handleChange}
            required
          />
          <InputField
            label="Precio de compra (unitario)"
            name="precioCompra"
            type="number"
            min="0"
            step="0.01"
            value={values.precioCompra}
            onChange={handleChange}
            required
          />
          <InputField
            label="Número de lote (opcional)"
            name="numeroLote"
            value={values.numeroLote}
            onChange={handleChange}
            placeholder="Ej: L-2026-014"
          />
          {producto.esPerecedero && (
            <InputField
              label="Fecha de vencimiento"
              name="fechaVencimiento"
              type="date"
              value={values.fechaVencimiento}
              onChange={handleChange}
              required
            />
          )}

          <div className="form-actions">
            <Button type="submit" disabled={loading}>
              {loading ? 'Guardando…' : 'Registrar entrada'}
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
