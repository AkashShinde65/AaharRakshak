import { ArrowDownRight, ArrowUpRight } from 'lucide-react';
import Card from '../ui/Card.jsx';

const toneClasses = {
  green: 'bg-brand-50 text-brand-700 dark:bg-brand-950 dark:text-brand-200',
  orange: 'bg-harvest-50 text-harvest-700 dark:bg-harvest-950/40 dark:text-harvest-200',
  red: 'bg-red-50 text-red-700 dark:bg-red-950/40 dark:text-red-200',
};

export default function StatCard({ label, value, trend, tone = 'green' }) {
  const isNegative = trend?.startsWith('-');
  const TrendIcon = isNegative ? ArrowDownRight : ArrowUpRight;

  return (
    <Card>
      <div className="flex items-start justify-between gap-4">
        <div>
          <p className="text-sm font-medium text-slate-500 dark:text-slate-400">{label}</p>
          <p className="mt-3 text-3xl font-bold text-slate-950 dark:text-white">{value}</p>
        </div>
        <span className={`inline-flex items-center gap-1 rounded-md px-2 py-1 text-xs font-semibold ${toneClasses[tone]}`}>
          <TrendIcon className="h-3.5 w-3.5" />
          {trend}
        </span>
      </div>
    </Card>
  );
}
