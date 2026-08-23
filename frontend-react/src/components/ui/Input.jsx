export default function Input({
  label,
  error,
  hint,
  className = '',
  as: Component = 'input',
  required = false,
  ...props
}) {
  const describedBy = error ? `${props.name}-error` : hint ? `${props.name}-hint` : undefined;

  return (
    <label className="block">
      {label ? (
        <span className="mb-2 block text-sm font-medium text-slate-700 dark:text-slate-200">
          {label}
          {required ? <span className="text-harvest-600"> *</span> : null}
        </span>
      ) : null}
      <Component
        aria-invalid={Boolean(error)}
        aria-describedby={describedBy}
        className={[
          'focus-ring w-full rounded-md border bg-white px-3 py-2.5 text-sm text-slate-950 placeholder:text-slate-400 transition dark:bg-slate-950 dark:text-white',
          error ? 'border-red-400' : 'border-slate-200 dark:border-slate-700',
          Component === 'textarea' ? 'min-h-32 resize-y' : '',
          className,
        ].join(' ')}
        {...props}
      />
      {error ? (
        <p id={`${props.name}-error`} className="mt-1.5 text-sm text-red-600 dark:text-red-400">
          {error}
        </p>
      ) : hint ? (
        <p id={`${props.name}-hint`} className="mt-1.5 text-sm text-slate-500 dark:text-slate-400">
          {hint}
        </p>
      ) : null}
    </label>
  );
}
