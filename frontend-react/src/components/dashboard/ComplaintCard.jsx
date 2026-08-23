import { CalendarDays, MapPin } from 'lucide-react';
import Card from '../ui/Card.jsx';
import Badge from '../ui/Badge.jsx';
import { formatDate, titleCase } from '../../utils/formatters.js';

function statusTone(status) {
  if (status === 'REPORT_PUBLISHED' || status === 'ACTION_TAKEN') return 'green';
  if (status === 'ESCALATED' || status === 'ASSIGNED') return 'orange';
  return 'slate';
}

function riskTone(risk) {
  if (risk === 'CRITICAL' || risk === 'HIGH') return 'red';
  if (risk === 'MEDIUM') return 'orange';
  return 'green';
}

export default function ComplaintCard({ complaint }) {
  return (
    <Card className="transition hover:-translate-y-0.5 hover:shadow-lg">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
        <div>
          <p className="text-xs font-semibold uppercase text-brand-700 dark:text-brand-300">{complaint.ticketNumber}</p>
          <h3 className="mt-1 text-base font-semibold text-slate-950 dark:text-white">{complaint.title}</h3>
        </div>
        <div className="flex flex-wrap gap-2">
          <Badge tone={statusTone(complaint.status)}>{titleCase(complaint.status)}</Badge>
          <Badge tone={riskTone(complaint.risk)}>{complaint.risk}</Badge>
        </div>
      </div>
      <div className="mt-4 grid gap-2 text-sm text-slate-500 dark:text-slate-400 sm:grid-cols-2">
        <span className="inline-flex items-center gap-2">
          <MapPin className="h-4 w-4" />
          {complaint.location}
        </span>
        <span className="inline-flex items-center gap-2">
          <CalendarDays className="h-4 w-4" />
          {formatDate(complaint.createdAt)}
        </span>
      </div>
    </Card>
  );
}
