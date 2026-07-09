
export default function InputField({
  label,
  name,
  value,
  onChange,
  type = 'text',
  placeholder = '',
  required = false,
  autoComplete,
  error,
}) {
  return (
    <div className="form-group">
      <label htmlFor={name}>{label}</label>
      <input
        id={name}
        name={name}
        type={type}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        required={required}
        autoComplete={autoComplete}
        className={`form-control${error ? ' form-control--error' : ''}`}
        aria-invalid={Boolean(error)}
      />
      {error && <span className="form-error">{error}</span>}
    </div>
  );
}
