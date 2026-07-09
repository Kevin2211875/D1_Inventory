export default function D1Logo({ size = 72, className = '' }) {
  return (
    <svg
      className={className}
      width={size}
      height={size}
      viewBox="0 0 120 120"
      role="img"
      aria-label="Tiendas D1"
    >
      <rect width="120" height="120" rx="8" fill="#E30613" />
      <path
        d="M34 24h34c22 0 34 12 34 36s-12 36-34 36H34V24zm18 18v36h16c11 0 17-6 17-18s-6-18-17-18H52z"
        fill="#FFFFFF"
      />
      <circle cx="82" cy="38" r="14" fill="#0057B8" />
      <text
        x="82"
        y="44"
        textAnchor="middle"
        fill="#FFD100"
        fontSize="18"
        fontWeight="700"
        fontFamily="Arial, sans-serif"
      >
        1
      </text>
    </svg>
  );
}
