import { useEffect, useState } from 'react';
import PageHeader from '../components/layout/PageHeader.jsx';
import Card, { CardHeader } from '../components/ui/Card.jsx';
import Button from '../components/ui/Button.jsx';
import { districtService } from '../services/districtService.js';
import { useAuth } from '../hooks/useAuth.js';

function AssignInspectorForm({ ticketNumber, onDone }) {
  const [inspectorUserId, setInspectorUserId] = useState('');
  const [district, setDistrict] = useState('Pune');
  const [slaHours, setSlaHours] = useState('48');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const submit = async () => {
    setSaving(true);
    setError('');
    try {
      await districtService.assignInspector(ticketNumber, {
        inspectorUserId: Number(inspectorUserId),
        district,
        slaHours: Number(slaHours),
        notes: 'Assigned via district dashboard',
      });
      onDone();
    } catch (err) {
      setError(err?.response?.data?.error || 'Failed to assign inspector.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="mt-3 space-y-2 rounded-md border border-slate-200 p-3 dark:border-slate-800">
      <p className="text-sm font-medium">Assign inspector</p>
      <input className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" placeholder="Inspector user ID (numeric)" value={inspectorUserId} onChange={(e) => setInspectorUserId(e.target.value)} />
      <input className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" placeholder="District" value={district} onChange={(e) => setDistrict(e.target.value)} />
      <input className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" placeholder="SLA hours" value={slaHours} onChange={(e) => setSlaHours(e.target.value)} />
      {error && <p className="text-xs text-red-600">{error}</p>}
      <Button size="sm" onClick={submit} disabled={saving || !inspectorUserId}>{saving ? 'Assigning...' : 'Assign inspector'}</Button>
    </div>
  );
}

function AssignLabForm({ sampleId, onDone }) {
  const [labOfficerUserId, setLabOfficerUserId] = useState('');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const submit = async () => {
    setSaving(true);
    setError('');
    try {
      await districtService.assignLab(sampleId, { labOfficerUserId: Number(labOfficerUserId), notes: 'Assigned via district dashboard' });
      onDone();
    } catch (err) {
      setError(err?.response?.data?.error || 'Failed to assign lab officer.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="mt-3 space-y-2 rounded-md border border-slate-200 p-3 dark:border-slate-800">
      <p className="text-sm font-medium">Assign sample #{sampleId} to lab officer</p>
      <input className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" placeholder="Lab officer user ID (numeric)" value={labOfficerUserId} onChange={(e) => setLabOfficerUserId(e.target.value)} />
      {error && <p className="text-xs text-red-600">{error}</p>}
      <Button size="sm" onClick={submit} disabled={saving || !labOfficerUserId}>{saving ? 'Assigning...' : 'Assign to lab'}</Button>
    </div>
  );
}

function ComplaintDetailPanel({ ticketNumber, onRefreshList }) {
  const [detail, setDetail] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [actionError, setActionError] = useState('');

  const reload = () => {
    setLoading(true);
    districtService
      .complaintDetail(ticketNumber)
      .then(setDetail)
      .catch(() => setError('Could not load complaint detail.'))
      .finally(() => setLoading(false));
  };

  useEffect(reload, [ticketNumber]);

  const handleDone = () => {
    reload();
    onRefreshList();
  };

  const verify = async () => {
    setActionError('');
    try {
      await districtService.verifyComplaint(ticketNumber);
      handleDone();
    } catch (err) {
      setActionError(err?.response?.data?.error || 'Failed to verify complaint.');
    }
  };

  if (loading) return <p className="mt-3 text-sm text-slate-500">Loading detail...</p>;
  if (error) return <p className="mt-3 text-sm text-red-600">{error}</p>;
  if (!detail) return null;

  const latestSample = detail.samples?.[detail.samples.length - 1];

  return (
    <div className="mt-3 border-t border-slate-200 pt-3 dark:border-slate-800">
      {actionError && <p className="text-xs text-red-600">{actionError}</p>}

      {detail.status === 'SUBMITTED' && (
        <Button size="sm" onClick={verify}>Verify complaint</Button>
      )}

      {detail.status === 'VERIFIED' && !detail.assignedInspectorId && (
        <AssignInspectorForm ticketNumber={ticketNumber} onDone={handleDone} />
      )}

      {latestSample && latestSample.status === 'COLLECTED' && (
        <AssignLabForm sampleId={latestSample.sampleId} onDone={handleDone} />
      )}

      {detail.assignedInspectorName && (
        <p className="mt-2 text-sm text-slate-500">Assigned inspector: {detail.assignedInspectorName}</p>
      )}
    </div>
  );
}

export default function DistrictDashboardPage() {
  const { user } = useAuth();
  const [complaints, setComplaints] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [expanded, setExpanded] = useState(null);

  const load = () => {
    setLoading(true);
    districtService
      .dashboard()
      .then(setComplaints)
      .catch(() => setError('Could not load district dashboard.'))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  return (
    <div>
      <PageHeader
        eyebrow="District workspace"
        title={`District oversight, ${user?.name || 'Officer'}`}
        description="Verify complaints, assign inspectors, assign lab officers, review and publish reports."
      />
      <Card>
        <CardHeader title="All district complaints" subtitle="Live data from the investigation dashboard API." />
        {loading && <p className="text-sm text-slate-500">Loading...</p>}
        {error && <p className="text-sm text-red-600">{error}</p>}
        {!loading && !error && complaints.length === 0 && <p className="text-sm text-slate-500">No complaints found.</p>}
        <div className="space-y-4">
          {complaints.map((complaint) => {
            const isOpen = expanded === complaint.ticketNumber;
            return (
              <div key={complaint.complaintId} className="rounded-lg border border-slate-200 p-4 dark:border-slate-800">
                <button type="button" className="flex w-full items-center justify-between text-left" onClick={() => setExpanded(isOpen ? null : complaint.ticketNumber)}>
                  <div>
                    <p className="font-semibold">{complaint.ticketNumber}</p>
                    <p className="text-sm text-slate-500">{complaint.category?.replace(/_/g, ' ')}</p>
                  </div>
                  <span className="rounded-full bg-slate-100 px-2.5 py-1 text-xs font-medium dark:bg-slate-800">{complaint.status}</span>
                </button>
                {isOpen && <ComplaintDetailPanel ticketNumber={complaint.ticketNumber} onRefreshList={load} />}
              </div>
            );
          })}
        </div>
      </Card>
    </div>
  );
}
