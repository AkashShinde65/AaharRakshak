import { X } from 'lucide-react';
import Button from './Button.jsx';

export default function Modal({ open, title, children, onClose }) {
  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/60 p-4">
      <div className="surface w-full max-w-lg rounded-lg p-5">
        <div className="mb-4 flex items-center justify-between gap-4">
          <h2 className="text-lg font-semibold text-slate-950 dark:text-white">{title}</h2>
          <Button variant="ghost" size="sm" icon={X} aria-label="Close modal" onClick={onClose} />
        </div>
        {children}
      </div>
    </div>
  );
}
