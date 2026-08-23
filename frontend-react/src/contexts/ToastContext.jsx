import { useCallback, useMemo, useState } from 'react';
import { ToastContext } from './toast.context.js';
import ToastStack from '../components/ui/ToastStack.jsx';

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);

  const removeToast = useCallback((id) => {
    setToasts((items) => items.filter((toast) => toast.id !== id));
  }, []);

  const showToast = useCallback((toast) => {
    const id = crypto.randomUUID?.() || `${Date.now()}-${Math.random()}`;
    setToasts((items) => [...items, { id, type: 'info', ...toast }]);
    window.setTimeout(() => removeToast(id), toast.duration || 4200);
  }, [removeToast]);

  const value = useMemo(() => ({ showToast, removeToast }), [showToast, removeToast]);

  return (
    <ToastContext.Provider value={value}>
      {children}
      <ToastStack toasts={toasts} onClose={removeToast} />
    </ToastContext.Provider>
  );
}
