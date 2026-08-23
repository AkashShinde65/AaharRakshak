import { useEffect, useState } from 'react';
import PageHeader from '../components/layout/PageHeader.jsx';
import Card, { CardHeader } from '../components/ui/Card.jsx';
import Button from '../components/ui/Button.jsx';
import { labService } from '../services/labService.js';
import { useAuth } from '../hooks/useAuth.js';

async function fakeChecksum(text) {
  const buffer = await crypto.subtle.digest('SHA-256', new TextEncoder().encode(text + Date.now()));
  return Array.from(new Uint8Array(buffer)).map((b) => b.toString(16).padStart(2, '0')).join('');
}

function ReceiveForm({ sampleId, onDone }) {
  const [locationText, setLocationText] = useState('Demo State Food Lab');
  const [storageCondition, setStorageCondition] = useState('Cold storage, seal intact');
  const [notes, setNotes] = useState('');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const submit = async () => {
    setSaving(true);
    setError('');
    try {
      await labService.receiveSample(sampleId, { locationText, storageCondition, notes });
      onDone();
    } catch (err) {
      setError(err?.response?.data?.error || 'Failed to confirm receipt.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="mt-3 space-y-2 rounded-md border border-slate-200 p-3 dark:border-slate-800">
      <p className="text-sm font-medium">Confirm sample receipt</p>
      <input className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" value={locationText} onChange={(e) => setLocationText(e.target.value)} />
      <input className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" value={storageCondition} onChange={(e) => setStorageCondition(e.target.value)} />
      <textarea placeholder="Notes" className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" value={notes} onChange={(e) => setNotes(e.target.value)} />
      {error && <p className="text-xs text-red-600">{error}</p>}
      <Button size="sm" onClick={submit} disabled={saving}>{saving ? 'Saving...' : 'Confirm receipt'}</Button>
    </div>
  );
}

function ReportForm({ sampleId, onDone }) {
  const [reportNumber, setReportNumber] = useState(`LAB-${Date.now()}`);
  const [resultSummary, setResultSummary] = useState('Parameters within demo limits');
  const [parameterName, setParameterName] = useState('Moisture');
  const [resultValue, setResultValue] = useState('8.2');
  const [unit, setUnit] = useState('%');
  const [compliant, setCompliant] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const submit = async () => {
    setSaving(true);
    setError('');
    try {
      const checksum = await fakeChecksum(reportNumber);
      const draft = await labService.createReportDraft(sampleId, {
        reportNumber,
        objectKey: `lab-reports/${reportNumber}.pdf`,
        originalFileName: `${reportNumber}.pdf`,
        contentType: 'application/pdf',
        sizeBytes: 102400,
        checksumSha256: checksum,
        resultSummary,
        results: [
          { parameterName, testMethod: 'Mock IS method', permissibleLimit: '< 12%', resultValue, unit, compliant, remarks: '' },
        ],
      });
      await labService.submitReport(draft.reportId || draft.id);
      onDone();
    } catch (err) {
      setError(err?.response?.data?.error || 'Failed to create/submit report.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="mt-3 space-y-2 rounded-md border border-slate-200 p-3 dark:border-slate-800">
      <p className="text-sm font-medium">Create and submit lab report</p>
      <input className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" value={reportNumber} onChange={(e) => setReportNumber(e.target.value)} placeholder="Report number" />
      <input className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" value={resultSummary} onChange={(e) => setResultSummary(e.target.value)} placeholder="Result summary" />
      <div className="flex gap-2">
        <input className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" value={parameterName} onChange={(e) => setParameterName(e.target.value)} placeholder="Parameter" />
        <input className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" value={resultValue} onChange={(e) => setResultValue(e.target.value)} placeholder="Value" />
        <input className="w-full rounded border px-2 py-1.5 text-sm dark:bg-slate-900" value={unit} onChange={(e) => setUnit(e.target.value)} placeholder="Unit" />
      </div>
      <label className="flex items-center gap-2 text-sm">
        <input type="checkbox" checked={compliant} onChange={(e) => setCompliant(e.target.checked)} /> Compliant with limit
      </label>
      {error && <p className="text-xs text-red-600">{error}</p>}
      <Button size="sm" onClick={submit} disabled={saving}>{saving ? 'Submitting...' : 'Create + submit report'}</Button>
    </div>
  );
}

export default function LabDashboardPage() {
  const { user } = useAuth();
  const [samples, setSamples] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [expanded, setExpanded] = useState(null);

  const load = () => {
    setLoading(true);
    labService
      .assignedSamples()
      .then(setSamples)
      .catch(() => setError('Could not load assigned samples.'))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  return (
    <div>
      <PageHeader
        eyebrow="Laboratory workspace"
        title={`Assigned samples, ${user?.name || 'Lab Officer'}`}
        description="Confirm receipt of samples, then draft and submit lab reports."
      />
      <Card>
        <CardHeader title="My assigned samples" subtitle="Live data from the laboratory investigations API." />
        {loading && <p className="text-sm text-slate-500">Loading...</p>}
        {error && <p className="text-sm text-red-600">{error}</p>}
        {!loading && !error && samples.length === 0 && <p className="text-sm text-slate-500">No samples assigned yet.</p>}
        <div className="space-y-4">
          {samples.map((sample) => {
            const isOpen = expanded === sample.assignmentId;
            return (
              <div key={sample.assignmentId} className="rounded-lg border border-slate-200 p-4 dark:border-slate-800">
                <button type="button" className="flex w-full items-center justify-between text-left" onClick={() => setExpanded(isOpen ? null : sample.assignmentId)}>
                  <div>
                    <p className="font-semibold">{sample.sampleNumber || `Sample #${sample.sampleId}`}</p>
                    <p className="text-sm text-slate-500">Ticket: {sample.ticketNumber}</p>
                  </div>
                  <span className="rounded-full bg-slate-100 px-2.5 py-1 text-xs font-medium dark:bg-slate-800">{sample.status}</span>
                </button>
                {isOpen && sample.status === 'ASSIGNED' && <ReceiveForm sampleId={sample.sampleId} onDone={load} />}
                {isOpen && sample.status === 'RECEIVED' && <ReportForm sampleId={sample.sampleId} onDone={load} />}
              </div>
            );
          })}
        </div>
      </Card>
    </div>
  );
}
