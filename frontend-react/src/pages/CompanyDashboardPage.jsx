import { useEffect, useState } from 'react';
import PageHeader from '../components/layout/PageHeader.jsx';
import Card, { CardHeader } from '../components/ui/Card.jsx';
import Button from '../components/ui/Button.jsx';
import { companyService } from '../services/companyService.js';
import { useAuth } from '../hooks/useAuth.js';

function LicenceForm({ onDone }) {
  const [licenceNumber, setLicenceNumber] = useState('');
  const [issuingAuthority, setIssuingAuthority] = useState('FSSAI');
  const [validFrom, setValidFrom] = useState('');
  const [validTo, setValidTo] = useState('');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const submit = async () => {
    setSaving(true);
    setError('');
    try {
      await companyService.submitLicence({
        licenceNumber,
        issuingAuthority,
        validFrom: validFrom || null,
        validTo: validTo || null,
        licenceLabelImage: null,
      });
      setLicenceNumber('');
      onDone();
    } catch (err) {
      setError(err?.response?.data?.error || 'Failed to submit licence. Licence number must be 14 digits.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="space-y-2 rounded-md border border-slate-200 p-3 dark:border-slate-800">
      <p className="text-sm font-medium">Submit a new FSSAI licence</p>
      <input className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" placeholder="14-digit licence number" value={licenceNumber} onChange={(e) => setLicenceNumber(e.target.value)} />
      <input className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" placeholder="Issuing authority" value={issuingAuthority} onChange={(e) => setIssuingAuthority(e.target.value)} />
      <div className="flex gap-2">
        <input type="date" className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" value={validFrom} onChange={(e) => setValidFrom(e.target.value)} />
        <input type="date" className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" value={validTo} onChange={(e) => setValidTo(e.target.value)} />
      </div>
      {error && <p className="text-xs text-red-600">{error}</p>}
      <Button size="sm" onClick={submit} disabled={saving || licenceNumber.length !== 14}>{saving ? 'Submitting...' : 'Submit licence'}</Button>
    </div>
  );
}

export default function CompanyDashboardPage() {
  const { user } = useAuth();
  const [profile, setProfile] = useState(null);
  const [licences, setLicences] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = () => {
    setLoading(true);
    Promise.all([companyService.profile(), companyService.licences()])
      .then(([p, l]) => {
        setProfile(p);
        setLicences(l);
      })
      .catch(() => setError('Could not load company data.'))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  return (
    <div>
      <PageHeader
        eyebrow="Company workspace"
        title={`Welcome, ${user?.name || 'Company'}`}
        description="Manage your company profile and FSSAI licences."
      />

      {loading && <p className="text-sm text-slate-500">Loading...</p>}
      {error && <p className="text-sm text-red-600">{error}</p>}

      {profile && (
        <Card>
          <CardHeader title="Company profile" />
          <div className="grid gap-2 text-sm sm:grid-cols-2">
            <p><span className="text-slate-500">Legal name:</span> {profile.legalName}</p>
            <p><span className="text-slate-500">Trade name:</span> {profile.tradeName || '-'}</p>
            <p><span className="text-slate-500">GSTIN:</span> {profile.gstin || '-'}</p>
            <p><span className="text-slate-500">Status:</span> {profile.status}</p>
            <p><span className="text-slate-500">Email:</span> {profile.contactEmail}</p>
            <p><span className="text-slate-500">Mobile:</span> {profile.contactMobile}</p>
          </div>
        </Card>
      )}

      <div className="mt-6">
        <Card>
          <CardHeader title="Licences" subtitle="Submitted FSSAI licences and their verification status." />
          <div className="space-y-3">
            {licences.map((licence) => (
              <div key={licence.licenceId} className="flex items-center justify-between rounded-md border border-slate-200 p-3 text-sm dark:border-slate-800">
                <div>
                  <p className="font-medium">{licence.licenceNumber}</p>
                  <p className="text-slate-500">{licence.issuingAuthority}</p>
                </div>
                <span className="rounded-full bg-slate-100 px-2.5 py-1 text-xs font-medium dark:bg-slate-800">{licence.status}</span>
              </div>
            ))}
            {licences.length === 0 && <p className="text-sm text-slate-500">No licences submitted yet.</p>}
          </div>
          <div className="mt-4">
            <LicenceForm onDone={load} />
          </div>
        </Card>
      </div>
    </div>
  );
}
