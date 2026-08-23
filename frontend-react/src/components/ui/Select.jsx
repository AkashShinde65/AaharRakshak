export default function Select({ label, error, children, required = false, ...props }) {
  return (
    <label className="block">
      <span className="mb-2 block text-sm font-medium text-slate-700 dark:text-slate-200">
        {label}
        {required ? <span className="text-harvest-600"> *</span> : null}
      </span>
      <select
        aria-invalid={Boolean(error)}
        className={[
          'focus-ring w-full rounded-md border bg-white px-3 py-2.5 text-sm text-slate-950 transition dark:bg-slate-950 dark:text-white',
          error ? 'border-red-400' : 'border-slate-200 dark:border-slate-700',
        ].join(' ')}
        {...props}
      >
        {children}
      </select>
      {error ? <p className="mt-1.5 text-sm text-red-600 dark:text-red-400">{error}</p> : null}
    </label>
  );
}
