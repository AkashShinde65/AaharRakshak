import { ShieldCheck, UserRound } from 'lucide-react';
import PageHeader from '../components/layout/PageHeader.jsx';
import Button from '../components/ui/Button.jsx';
import Card, { CardHeader } from '../components/ui/Card.jsx';
import Input from '../components/ui/Input.jsx';
import Badge from '../components/ui/Badge.jsx';
import { useAuth } from '../hooks/useAuth.js';
import { useToast } from '../hooks/useToast.js';

export default function ProfilePage() {
  const { user } = useAuth();
  const { showToast } = useToast();

  return (
    <div>
      <PageHeader
        eyebrow="Profile"
        title="Account and privacy"
        description="Profile settings stay simple on the frontend; identity and role permissions remain controlled by the Spring Boot backend."
      />

      <div className="grid gap-6 lg:grid-cols-[0.8fr_1.2fr]">
        <Card>
          <div className="flex items-center gap-4">
            <div className="flex h-16 w-16 items-center justify-center rounded-lg bg-brand-50 text-brand-700 dark:bg-brand-950 dark:text-brand-200">
              <UserRound className="h-8 w-8" />
            </div>
            <div>
              <h2 className="text-lg font-semibold text-slate-950 dark:text-white">{user?.name || 'Demo User'}</h2>
              <p className="text-sm text-slate-500 dark:text-slate-400">{user?.email}</p>
            </div>
          </div>
          <div className="mt-6 space-y-3">
            <div className="flex items-center justify-between rounded-lg bg-slate-50 p-3 dark:bg-slate-900">
              <span className="text-sm text-slate-500 dark:text-slate-400">Role</span>
              <Badge tone="green">{user?.role || 'CITIZEN'}</Badge>
            </div>
            <div className="flex items-center justify-between rounded-lg bg-slate-50 p-3 dark:bg-slate-900">
              <span className="text-sm text-slate-500 dark:text-slate-400">Verification</span>
              <Badge tone="orange">Mock verified</Badge>
            </div>
          </div>
        </Card>

        <Card>
          <CardHeader title="Contact details" subtitle="Use backend profile APIs when these become editable." />
          <div className="grid gap-4 sm:grid-cols-2">
            <Input label="Full name" value={user?.name || ''} readOnly />
            <Input label="Email" value={user?.email || ''} readOnly />
            <Input label="Mobile" value="+91 98765 43210" readOnly />
            <Input label="District" value="Pune" readOnly />
          </div>
          <div className="mt-6 rounded-lg border border-brand-200 bg-brand-50 p-4 text-sm text-brand-900 dark:border-brand-800 dark:bg-brand-950 dark:text-brand-100">
            <div className="flex gap-3">
              <ShieldCheck className="h-5 w-5 shrink-0" />
              <p>
                Public pages and company views must never expose citizen private details. Backend privacy-redaction tests should remain the source of truth.
              </p>
            </div>
          </div>
          <Button className="mt-5" variant="secondary" onClick={() => showToast({ title: 'Profile saved', message: 'Connect this button to the backend profile endpoint.' })}>
            Save preferences
          </Button>
        </Card>
      </div>
    </div>
  );
}
