import { useEffect, useState } from 'react';
import AppLayout from '../components/layout/AppLayout';
import Button from '../components/common/Button';
import InputField from '../components/common/InputField';
import SelectField from '../components/common/SelectField';
import AgregarStockModal from '../components/productos/AgregarStockModal';
import { useAuth } from '../context/AuthContext';
import { listarCategorias, registrarProducto, UNIDADES_MEDIDA } from '../api/productoApi';

const ESTADO_INICIAL = {
  codigoBarras: '',
  nombre: '',
  idCategoria: '',
  marca: '',
  unidadMedida: 'unidad',
  precioVenta: '',
  porcentajeIva: '',
  cantidadMinima: '',
  ubicacion: '',
  esPerecedero: false,
  conStock: false,
  cantidad: '',
  precioCompra: '',
  numeroLote: '',
  fechaVencimiento: '',
};

export default function RegistrarProductoPage() {
  const { accessToken } = useAuth();
  const [values, setValues] = useState(ESTADO_INICIAL);
  const [categorias, setCategorias] = useState([]);
  const [error, setError] = useState('');
  const [exito, setExito] = useState('');
  const [duplicado, setDuplicado] = useState(null);
  const [mostrarModal, setMostrarModal] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    listarCategorias(accessToken)
      .then(setCategorias)
      .catch((err) => setError(err.message));
  }, [accessToken]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setValues((prev) => ({ ...prev, [name]: type === 'checkbox' ? checked : value }));
  };

  const resetForm = () => setValues({ ...ESTADO_INICIAL });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setExito('');
    setDuplicado(null);
    setMostrarModal(false);

    if (!values.nombre.trim()) {
      setError('El nombre es obligatorio.');
      return;
    }
    if (!values.idCategoria) {
      setError('Seleccione una categoría.');
      return;
    }
    if (!(Number(values.precioVenta) > 0)) {
      setError('El precio de venta debe ser mayor a cero.');
      return;
    }
    if (values.conStock) {
      if (!(Number(values.cantidad) > 0)) {
        setError('La cantidad del stock inicial debe ser mayor a cero.');
        return;
      }
      if (!(Number(values.precioCompra) > 0)) {
        setError('El precio de compra del stock inicial debe ser mayor a cero.');
        return;
      }
      if (values.esPerecedero && !values.fechaVencimiento) {
        setError('El producto es perecedero: indique la fecha de vencimiento del lote inicial.');
        return;
      }
    }

    const payload = {
      codigoBarras: values.codigoBarras.trim() || undefined,
      nombre: values.nombre.trim(),
      idCategoria: Number(values.idCategoria),
      marca: values.marca.trim() || undefined,
      unidadMedida: values.unidadMedida,
      precioVenta: Number(values.precioVenta),
      porcentajeIva: values.porcentajeIva === '' ? undefined : Number(values.porcentajeIva),
      cantidadMinima: values.cantidadMinima === '' ? undefined : Number(values.cantidadMinima),
      ubicacion: values.ubicacion.trim() || undefined,
      esPerecedero: values.esPerecedero,
    };

    if (values.conStock) {
      payload.stockInicial = {
        cantidad: Number(values.cantidad),
        precioCompra: Number(values.precioCompra),
        numeroLote: values.numeroLote.trim() || undefined,
        fechaVencimiento: values.fechaVencimiento || undefined,
      };
    }

    setLoading(true);
    try {
      const creado = await registrarProducto(accessToken, payload);
      setExito(`Producto "${creado.nombre}" registrado. Stock actual: ${creado.stockActual}.`);
      resetForm();
    } catch (err) {
      if (err.status === 409 && err.data?.codigo === 'PRODUCTO_DUPLICADO') {
        setDuplicado(err.data.productoExistente);
      } else {
        setError(err.message);
      }
    } finally {
      setLoading(false);
    }
  };

  const opcionesCategoria = categorias.map((c) => ({ value: c.idCategoria, label: c.nombre }));

  return (
    <AppLayout title="Registrar producto">
      <div className="page-header">
        <div>
          <h2>Registrar producto</h2>
          <p>Da de alta un producto nuevo. Si ya existe, podrás agregarle stock en su lugar.</p>
        </div>
      </div>

      <div className="card" style={{ maxWidth: 920 }}>
        {error && <div className="alert alert--error">{error}</div>}
        {exito && <div className="alert alert--success">{exito}</div>}

        {duplicado && (
          <div className="alert alert--warning">
            Este producto ya existe: <strong>{duplicado.nombre}</strong>
            {duplicado.marca ? ` (${duplicado.marca})` : ''} · Stock actual:{' '}
            <strong>{duplicado.stockActual}</strong>. No se puede registrar de nuevo.
            <div className="alert__actions">
              <Button variant="primary" className="btn--sm" onClick={() => setMostrarModal(true)}>
                Agregar stock a este producto
              </Button>
            </div>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-grid">
            <InputField
              label="Nombre del producto *"
              name="nombre"
              value={values.nombre}
              onChange={handleChange}
              placeholder="Ej: Arroz Diana 500g"
              required
            />
            <SelectField
              label="Categoría *"
              name="idCategoria"
              value={values.idCategoria}
              onChange={handleChange}
              options={opcionesCategoria}
              placeholder="Selecciona una categoría"
              required
            />
            <InputField
              label="Marca"
              name="marca"
              value={values.marca}
              onChange={handleChange}
              placeholder="Ej: Diana"
            />
            <SelectField
              label="Unidad de medida *"
              name="unidadMedida"
              value={values.unidadMedida}
              onChange={handleChange}
              options={UNIDADES_MEDIDA}
              required
            />
            <InputField
              label="Precio de venta *"
              name="precioVenta"
              type="number"
              min="0"
              step="0.01"
              value={values.precioVenta}
              onChange={handleChange}
              required
            />
            <InputField
              label="% IVA"
              name="porcentajeIva"
              type="number"
              min="0"
              step="0.01"
              value={values.porcentajeIva}
              onChange={handleChange}
              placeholder="0"
            />
            <InputField
              label="Código de barras"
              name="codigoBarras"
              value={values.codigoBarras}
              onChange={handleChange}
              placeholder="Escanea o escribe el código"
            />
            <InputField
              label="Stock mínimo (alertas)"
              name="cantidadMinima"
              type="number"
              min="0"
              step="0.001"
              value={values.cantidadMinima}
              onChange={handleChange}
              placeholder="0"
            />
            <InputField
              label="Ubicación en tienda"
              name="ubicacion"
              value={values.ubicacion}
              onChange={handleChange}
              placeholder="Ej: Góndola 3"
            />
          </div>

          <label className="checkbox-row" style={{ marginTop: '1rem' }}>
            <input
              type="checkbox"
              name="esPerecedero"
              checked={values.esPerecedero}
              onChange={handleChange}
            />
            El producto es perecedero (requiere fecha de vencimiento por lote)
          </label>

          <div className="form-section">
            <label className="checkbox-row">
              <input
                type="checkbox"
                name="conStock"
                checked={values.conStock}
                onChange={handleChange}
              />
              Cargar stock inicial ahora
            </label>

            {values.conStock && (
              <div className="form-grid" style={{ marginTop: '1rem' }}>
                <InputField
                  label="Cantidad inicial *"
                  name="cantidad"
                  type="number"
                  min="0"
                  step="0.001"
                  value={values.cantidad}
                  onChange={handleChange}
                />
                <InputField
                  label="Precio de compra *"
                  name="precioCompra"
                  type="number"
                  min="0"
                  step="0.01"
                  value={values.precioCompra}
                  onChange={handleChange}
                />
                <InputField
                  label="Número de lote"
                  name="numeroLote"
                  value={values.numeroLote}
                  onChange={handleChange}
                  placeholder="Opcional"
                />
                {values.esPerecedero && (
                  <InputField
                    label="Fecha de vencimiento *"
                    name="fechaVencimiento"
                    type="date"
                    value={values.fechaVencimiento}
                    onChange={handleChange}
                  />
                )}
              </div>
            )}
          </div>

          <div className="form-actions">
            <Button type="submit" disabled={loading}>
              {loading ? 'Registrando…' : 'Registrar producto'}
            </Button>
            <Button type="button" variant="secondary" onClick={resetForm} disabled={loading}>
              Limpiar
            </Button>
          </div>
        </form>
      </div>

      {mostrarModal && duplicado && (
        <AgregarStockModal
          producto={duplicado}
          onClose={() => setMostrarModal(false)}
          onSuccess={(actualizado) => {
            setExito(`Stock agregado a "${actualizado.nombre}". Stock actual: ${actualizado.stockActual}.`);
            setDuplicado(null);
            setMostrarModal(false);
          }}
        />
      )}
    </AppLayout>
  );
}
