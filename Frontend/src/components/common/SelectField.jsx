
export default function SelectField({
  label,
  name,
  value,
  onChange,
  options = [],
  placeholder,
  required = false,
  disabled = false,
  error,
}) {
  return (
    <div className="form-group">
      <label htmlFor={name}>{label}</label>
      <select
        id={name}
        name={name}
        value={value}
        onChange={onChange}
        required={required}
        disabled={disabled}
        className={`form-control${error ? ' form-control--error' : ''}`}
        aria-invalid={Boolean(error)}
      >
        {placeholder && <option value="">{placeholder}</option>}
        {options.map((opt) => (
          <option key={opt.value} value={opt.value}>
            {opt.label}
          </option>
        ))}
      </select>
      {error && <span className="form-error">{error}</span>}
    </div>
  );
}
