import { Link } from 'react-router-dom';
import { FilePlus2 } from 'lucide-react';
import PageHeader from '../components/layout/PageHeader.jsx';
import Button from '../components/ui/Button.jsx';
import Card, { CardHeader } from '../components/ui/Card.jsx';
import ComplaintCard from '../components/dashboard/ComplaintCard.jsx';
import StatCard from '../components/dashboard/StatCard.jsx';
import { complaints, stats } from '../data/mockData.js';
import { useAuth } from '../hooks/useAuth.js';

export default function DashboardPage() {
  const { user } = useAuth();

  return (
    <div>
      <PageHeader
        eyebrow="Dashboard"
        title={`Good to see you, ${user?.name || 'Citizen'}`}
        description="A single workspace for complaint status, investigation alerts and public transparency signals."
        action={
          <Link to="/dashboard/complaints/new">
            <Button icon={FilePlus2}>New complaint</Button>
          </Link>
        }
      />

      <div className="grid gap-5 sm:grid-cols-2 xl:grid-cols-4">
        {stats.map((stat) => (
          <StatCard key={stat.label} {...stat} />
        ))}
      </div>

      <div className="mt-6 grid gap-6 xl:grid-cols-[1fr_0.8fr]">
        <Card>
          <CardHeader title="Recent complaints" subtitle="Mock dashboard data. Connect API services for live records." />
          <div className="space-y-4">
            {complaints.map((complaint) => (
              <ComplaintCard key={complaint.ticketNumber} complaint={complaint} />
            ))}
          </div>
        </Card>

        <Card>
          <CardHeader title="Risk distribution" subtitle="Rule-based risk is explainable and lab confirmation remains mandatory." />
          <div className="space-y-5">
            {[
              ['Low', 38, 'bg-brand-500'],
              ['Medium', 29, 'bg-harvest-500'],
              ['High', 21, 'bg-red-500'],
              ['Critical', 12, 'bg-red-700'],
            ].map(([label, value, color]) => (
              <div key={label}>
                <div className="flex items-center justify-between text-sm">
                  <span className="font-medium text-slate-700 dark:text-slate-200">{label}</span>
                  <span className="text-slate-500 dark:text-slate-400">{value}%</span>
                </div>
                <div className="mt-2 h-2 rounded-md bg-slate-100 dark:bg-slate-800">
                  <div className={`h-2 rounded-md ${color}`} style={{ width: `${value}%` }} />
                </div>
              </div>
            ))}
          </div>
        </Card>
      </div>
    </div>
  );
}
