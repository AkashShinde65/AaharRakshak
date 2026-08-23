import { useMemo, useState } from 'react';

// Small reusable form helper. It keeps validation close to the page while avoiding repeated boilerplate.
export function useForm({ initialValues, validate, onSubmit }) {
  const [values, setValues] = useState(initialValues);
  const [touched, setTouched] = useState({});
  const [submitting, setSubmitting] = useState(false);

  const errors = useMemo(() => validate(values), [validate, values]);
  const hasErrors = Object.keys(errors).length > 0;

  function setField(name, value) {
    setValues((current) => ({ ...current, [name]: value }));
  }

  function handleChange(event) {
    const { name, type, checked, value, files } = event.target;
    if (type === 'checkbox') {
      setField(name, checked);
    } else if (type === 'file') {
      setField(name, Array.from(files || []));
    } else {
      setField(name, value);
    }
  }

  function handleBlur(event) {
    setTouched((current) => ({ ...current, [event.target.name]: true }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setTouched(Object.keys(values).reduce((acc, key) => ({ ...acc, [key]: true }), {}));
    if (hasErrors) return;

    setSubmitting(true);
    try {
      await onSubmit(values);
    } finally {
      setSubmitting(false);
    }
  }

  return {
    values,
    errors,
    touched,
    submitting,
    hasErrors,
    setField,
    handleChange,
    handleBlur,
    handleSubmit,
  };
}
