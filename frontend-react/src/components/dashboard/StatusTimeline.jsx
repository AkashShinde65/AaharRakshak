import { CheckCircle2 } from 'lucide-react';
import { formatDateTime } from '../../utils/formatters.js';

export default function StatusTimeline({ items = [] }) {
  return (
    <ol className="relative space-y-5 border-l border-slate-200 pl-6 dark:border-slate-800">
      {items.map((item, index) => (
        <li key={`${item.status}-${index}`} className="relative">
          <span className="absolute -left-[33px] flex h-5 w-5 items-center justify-center rounded-full bg-brand-600 text-white ring-4 ring-white dark:ring-slate-900">
            <CheckCircle2 className="h-3.5 w-3.5" />
          </span>
          <p className="text-sm font-semibold text-slate-950 dark:text-white">{item.title}</p>
          <p className="mt-1 text-xs text-slate-500 dark:text-slate-400">{formatDateTime(item.date)}</p>
        </li>
      ))}
    </ol>
  );
}
