import { Download, RefreshCw } from 'lucide-react';
import PageHeader from '../components/layout/PageHeader.jsx';
import Button from '../components/ui/Button.jsx';
import Card, { CardHeader } from '../components/ui/Card.jsx';
import StatCard from '../components/dashboard/StatCard.jsx';
import AdminTable from '../components/dashboard/AdminTable.jsx';
import { adminRows } from '../data/mockData.js';
import { useToast } from '../hooks/useToast.js';

const adminStats = [
  { label: 'Cases awaiting action', value: '21', trend: '+4', tone: 'orange' },
  { label: 'SLA compliance', value: '91%', trend: '+3%', tone: 'green' },
  { label: 'Lab reports pending', value: '8', trend: '-2', tone: 'green' },
  { label: 'Active recalls', value: '5', trend: '+1', tone: 'red' },
];

export default function AdminDashboardPage() {
  const { showToast } = useToast();

  return (
    <div>
      <PageHeader
        eyebrow="Admin"
        title="Operations analytics"
        description="Monitor district workload, SLA health, lab-report throughput and public transparency outcomes."
        action={
          <div className="flex gap-2">
            <Button variant="secondary" icon={RefreshCw} onClick={() => showToast({ title: 'Dashboard refreshed', message: 'Latest demo metrics are visible.' })}>
              Refresh
            </Button>
            <Button icon={Download} onClick={() => showToast({ title: 'Export queued', message: 'Wire this to a backend export endpoint.' })}>
              Export
            </Button>
          </div>
        }
      />

      <div className="grid gap-5 sm:grid-cols-2 xl:grid-cols-4">
        {adminStats.map((stat) => (
          <StatCard key={stat.label} {...stat} />
        ))}
      </div>

      <div className="mt-6 grid gap-6 xl:grid-cols-[1fr_0.75fr]">
        <Card>
          <CardHeader title="Officer workload" subtitle="Role-aware table layout for administrative review." />
          <AdminTable rows={adminRows} />
        </Card>

        <Card>
          <CardHeader title="District performance" subtitle="Fast visual read for review meetings." />
          <div className="space-y-4">
            {[
              ['Pune', 92],
              ['Mumbai', 88],
              ['Nashik', 76],
              ['Nagpur', 81],
            ].map(([district, value]) => (
              <div key={district}>
                <div className="mb-2 flex justify-between text-sm">
                  <span className="font-medium text-slate-700 dark:text-slate-200">{district}</span>
                  <span className="text-slate-500 dark:text-slate-400">{value}%</span>
                </div>
                <div className="h-2 rounded-md bg-slate-100 dark:bg-slate-800">
                  <div className="h-2 rounded-md bg-brand-600" style={{ width: `${value}%` }} />
                </div>
              </div>
            ))}
          </div>
        </Card>
      </div>
    </div>
  );
}
