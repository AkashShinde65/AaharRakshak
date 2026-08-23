import { useEffect, useState } from 'react';
import { AlertTriangle, MapPin, ChevronDown, ChevronUp } from 'lucide-react';
import PageHeader from '../components/layout/PageHeader.jsx';
import Card, { CardHeader } from '../components/ui/Card.jsx';
import Button from '../components/ui/Button.jsx';
import { investigationService } from '../services/investigationService.js';
import { useAuth } from '../hooks/useAuth.js';

function StatusBadge({ status }) {
  const colors = {
    SUBMITTED: 'bg-slate-100 text-slate-700',
    ASSIGNED: 'bg-blue-100 text-blue-700',
    INSPECTION_SCHEDULED: 'bg-amber-100 text-amber-700',
    SAMPLE_COLLECTED: 'bg-purple-100 text-purple-700',
    LAB_TESTING: 'bg-indigo-100 text-indigo-700',
    REPORT_PUBLISHED: 'bg-teal-100 text-teal-700',
    ACTION_TAKEN: 'bg-emerald-100 text-emerald-700',
    ESCALATED: 'bg-red-100 text-red-700',
  };
  return (
    <span className={`rounded-full px-2.5 py-1 text-xs font-medium ${colors[status] || 'bg-slate-100 text-slate-700'}`}>
      {status?.replace(/_/g, ' ')}
    </span>
  );
}

function ScheduleForm({ ticketNumber, onDone }) {
  const [scheduledAt, setScheduledAt] = useState('');
  const [locationText, setLocationText] = useState('');
  const [notes, setNotes] = useState('');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const submit = async () => {
    setSaving(true);
    setError('');
    try {
      await investigationService.scheduleInspection(ticketNumber, {
        scheduledAt: new Date(scheduledAt).toISOString(),
        locationText,
        notes,
      });
      onDone();
    } catch (err) {
      setError(err?.response?.data?.error || 'Failed to schedule inspection.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="mt-3 space-y-2 rounded-md border border-slate-200 p-3 dark:border-slate-800">
      <p className="text-sm font-medium">Schedule inspection</p>
      <input type="datetime-local" className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" value={scheduledAt} onChange={(e) => setScheduledAt(e.target.value)} />
      <input type="text" placeholder="Location text" className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" value={locationText} onChange={(e) => setLocationText(e.target.value)} />
      <textarea placeholder="Notes" className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" value={notes} onChange={(e) => setNotes(e.target.value)} />
      {error && <p className="text-xs text-red-600">{error}</p>}
      <Button size="sm" onClick={submit} disabled={saving || !scheduledAt || !locationText}>
        {saving ? 'Scheduling...' : 'Schedule'}
      </Button>
    </div>
  );
}

function CheckInForm({ inspectionId, onDone }) {
  const [latitude, setLatitude] = useState('18.52043');
  const [longitude, setLongitude] = useState('73.85674');
  const [locationText, setLocationText] = useState('');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const submit = async () => {
    setSaving(true);
    setError('');
    try {
      await investigationService.checkIn(inspectionId, {
        latitude: Number(latitude),
        longitude: Number(longitude),
        locationText,
      });
      onDone();
    } catch (err) {
      setError(err?.response?.data?.error || 'Failed to check in.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="mt-3 space-y-2 rounded-md border border-slate-200 p-3 dark:border-slate-800">
      <p className="text-sm font-medium">Check in at location</p>
      <div className="flex gap-2">
        <input type="text" placeholder="Latitude" className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" value={latitude} onChange={(e) => setLatitude(e.target.value)} />
        <input type="text" placeholder="Longitude" className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" value={longitude} onChange={(e) => setLongitude(e.target.value)} />
      </div>
      <input type="text" placeholder="Location text" className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" value={locationText} onChange={(e) => setLocationText(e.target.value)} />
      {error && <p className="text-xs text-red-600">{error}</p>}
      <Button size="sm" onClick={submit} disabled={saving || !locationText}>
        {saving ? 'Checking in...' : 'Check in'}
      </Button>
    </div>
  );
}

function VisitForm({ inspectionId, onDone }) {
  const [notes, setNotes] = useState('');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const submit = async () => {
    setSaving(true);
    setError('');
    try {
      await investigationService.recordVisit(inspectionId, {
        visitedAt: new Date().toISOString(),
        notes,
        evidence: [],
      });
      onDone();
    } catch (err) {
      setError(err?.response?.data?.error || 'Failed to record visit.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="mt-3 space-y-2 rounded-md border border-slate-200 p-3 dark:border-slate-800">
      <p className="text-sm font-medium">Record visit notes</p>
      <textarea placeholder="What did you observe on site?" className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" value={notes} onChange={(e) => setNotes(e.target.value)} />
      {error && <p className="text-xs text-red-600">{error}</p>}
      <Button size="sm" onClick={submit} disabled={saving || !notes}>
        {saving ? 'Saving...' : 'Complete visit'}
      </Button>
    </div>
  );
}

function SampleForm({ inspectionId, onDone }) {
  const [sealNumber, setSealNumber] = useState('');
  const [quantity, setQuantity] = useState('');
  const [locationText, setLocationText] = useState('');
  const [storageDetails, setStorageDetails] = useState('');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const submit = async () => {
    setSaving(true);
    setError('');
    try {
      await investigationService.collectSample(inspectionId, {
        sealNumber,
        quantity,
        collectedAt: new Date().toISOString(),
        latitude: 18.52043,
        longitude: 73.85674,
        locationText,
        storageDetails,
        chainOfCustodyNotes: 'Collected during inspection',
      });
      onDone();
    } catch (err) {
      setError(err?.response?.data?.error || 'Failed to collect sample.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="mt-3 space-y-2 rounded-md border border-slate-200 p-3 dark:border-slate-800">
      <p className="text-sm font-medium">Collect sample</p>
      <input type="text" placeholder="Seal number" className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" value={sealNumber} onChange={(e) => setSealNumber(e.target.value)} />
      <input type="text" placeholder="Quantity (e.g. 250 g)" className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" value={quantity} onChange={(e) => setQuantity(e.target.value)} />
      <input type="text" placeholder="Location text" className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" value={locationText} onChange={(e) => setLocationText(e.target.value)} />
      <input type="text" placeholder="Storage details" className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" value={storageDetails} onChange={(e) => setStorageDetails(e.target.value)} />
      {error && <p className="text-xs text-red-600">{error}</p>}
      <Button size="sm" onClick={submit} disabled={saving || !sealNumber || !quantity}>
        {saving ? 'Saving...' : 'Collect sample'}
      </Button>
    </div>
  );
}

function ComplaintDetailPanel({ ticketNumber, onRefreshList }) {
  const [detail, setDetail] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const reload = () => {
    setLoading(true);
    investigationService
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

  if (loading) return <p className="mt-3 text-sm text-slate-500">Loading detail...</p>;
  if (error) return <p className="mt-3 text-sm text-red-600">{error}</p>;
  if (!detail) return null;

  const latestInspection = detail.inspections?.[detail.inspections.length - 1];

  return (
    <div className="mt-3 border-t border-slate-200 pt-3 dark:border-slate-800">
      {!latestInspection && <ScheduleForm ticketNumber={ticketNumber} onDone={handleDone} />}

      {latestInspection?.status === 'SCHEDULED' && (
        <CheckInForm inspectionId={latestInspection.inspectionId} onDone={handleDone} />
      )}

      {latestInspection?.status === 'CHECKED_IN' && (
        <VisitForm inspectionId={latestInspection.inspectionId} onDone={handleDone} />
      )}

      {latestInspection?.status === 'COMPLETED' && (!detail.samples || detail.samples.length === 0) && (
        <SampleForm inspectionId={latestInspection.inspectionId} onDone={handleDone} />
      )}

      {detail.samples?.length > 0 && (
        <p className="mt-3 text-sm text-emerald-700 dark:text-emerald-400">
          Sample collected: {detail.samples[detail.samples.length - 1].sealNumber || 'Recorded'}. Waiting for lab assignment.
        </p>
      )}
    </div>
  );
}

export default function InspectorDashboardPage() {
  const { user } = useAuth();
  const [complaints, setComplaints] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [expanded, setExpanded] = useState(null);

  const load = () => {
    setLoading(true);
    investigationService
      .assignedComplaints()
      .then(setComplaints)
      .catch(() => setError('Could not load assigned complaints. Check backend availability.'))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  return (
    <div>
      <PageHeader
        eyebrow="Inspector workspace"
        title={`Assigned complaints, ${user?.name || 'Inspector'}`}
        description="Click a complaint to schedule inspections, check in, record visits and collect samples."
      />

      <Card>
        <CardHeader title="My assigned complaints" subtitle="Live data from the investigation dashboard API." />

        {loading && <p className="text-sm text-slate-500 dark:text-slate-400">Loading assigned complaints...</p>}
        {error && <p className="text-sm text-red-600">{error}</p>}
        {!loading && !error && complaints.length === 0 && (
          <p className="text-sm text-slate-500 dark:text-slate-400">No complaints assigned to you yet.</p>
        )}

        <div className="space-y-4">
          {complaints.map((complaint) => {
            const isOpen = expanded === complaint.ticketNumber;
            return (
              <div key={complaint.complaintId} className="rounded-lg border border-slate-200 p-4 dark:border-slate-800">
                <button
                  type="button"
                  className="flex w-full flex-wrap items-center justify-between gap-2 text-left"
                  onClick={() => setExpanded(isOpen ? null : complaint.ticketNumber)}
                >
                  <div>
                    <p className="font-semibold text-slate-900 dark:text-white">{complaint.ticketNumber}</p>
                    <p className="text-sm text-slate-500 dark:text-slate-400">
                      {complaint.category?.replace(/_/g, ' ')} &middot; {complaint.complaintType?.replace(/_/g, ' ')}
                    </p>
                  </div>
                  <div className="flex items-center gap-2">
                    <StatusBadge status={complaint.status} />
                    {isOpen ? <ChevronUp className="h-4 w-4" /> : <ChevronDown className="h-4 w-4" />}
                  </div>
                </button>

                <div className="mt-3 flex flex-wrap items-center gap-4 text-sm text-slate-600 dark:text-slate-300">
                  {complaint.productName && <span>Product: {complaint.productName}</span>}
                  {complaint.companyName && <span>Company: {complaint.companyName}</span>}
                  {complaint.address && (
                    <span className="flex items-center gap-1">
                      <MapPin className="h-3.5 w-3.5" /> {complaint.address}
                    </span>
                  )}
                </div>

                <div className="mt-3 flex flex-wrap items-center gap-4 text-xs">
                  {complaint.riskScore != null && (
                    <span className="rounded-full bg-slate-100 px-2 py-1 font-medium text-slate-700 dark:bg-slate-800 dark:text-slate-200">
                      Risk score: {complaint.riskScore}
                    </span>
                  )}
                  {complaint.overdue && (
                    <span className="flex items-center gap-1 font-medium text-red-600">
                      <AlertTriangle className="h-3.5 w-3.5" /> Overdue
                    </span>
                  )}
                </div>

                {isOpen && <ComplaintDetailPanel ticketNumber={complaint.ticketNumber} onRefreshList={load} />}
              </div>
            );
          })}
        </div>
      </Card>
    </div>
  );
}
