import { useLocation } from 'react-router-dom';
import PageHeader from '../components/layout/PageHeader.jsx';
import Button from '../components/ui/Button.jsx';
import Card, { CardHeader } from '../components/ui/Card.jsx';
import Input from '../components/ui/Input.jsx';
import Badge from '../components/ui/Badge.jsx';
import EmptyState from '../components/ui/EmptyState.jsx';
import StatusTimeline from '../components/dashboard/StatusTimeline.jsx';
import { useForm } from '../hooks/useForm.js';
import { useToast } from '../hooks/useToast.js';
import { complaintService } from '../services/complaintService.js';
import { validateTracking } from '../utils/validators.js';
import { useState } from 'react';

export default function ComplaintTrackingPage() {
  const location = useLocation();
  const { showToast } = useToast();
  const [tracking, setTracking] = useState(null);

  const form = useForm({
    initialValues: { ticketNumber: location.state?.ticketNumber || 'ARK-SEED-0006' },
    validate: validateTracking,
    onSubmit: async (values) => {
      try {
        const data = await complaintService.trackComplaint(values.ticketNumber);
        setTracking(data);
        showToast({ type: 'success', title: 'Tracking loaded', message: values.ticketNumber });
      } catch (error) {
        showToast({
          type: 'error',
          title: 'Tracking failed',
          message: error.response?.data?.message || 'No complaint found for this number.',
        });
      }
    },
  });

  const timeline = tracking?.timeline || tracking?.history || [];

  return (
    <div>
      <PageHeader
        eyebrow="Complaint tracking"
        title="Track complaint status"
        description="Public tracking is privacy-safe and avoids exposing citizen identity or sensitive investigation details."
      />

      <Card>
        <form className="grid gap-4 sm:grid-cols-[1fr_auto] sm:items-end" onSubmit={form.handleSubmit}>
          <Input
            label="Tracking number"
            name="ticketNumber"
            value={form.values.ticketNumber}
            onChange={form.handleChange}
            onBlur={form.handleBlur}
            error={form.touched.ticketNumber && form.errors.ticketNumber}
            required
          />
          <Button type="submit" loading={form.submitting}>
            Track
          </Button>
        </form>
      </Card>

      <div className="mt-6">
        {tracking ? (
          <Card>
            <CardHeader
              title={tracking.ticketNumber || form.values.ticketNumber}
              subtitle="Latest public complaint status"
              action={<Badge tone="green">{tracking.status || 'REPORT_PUBLISHED'}</Badge>}
            />
            <StatusTimeline items={timeline} />
          </Card>
        ) : (
          <EmptyState
            title="No tracking loaded"
            description="Enter a complaint number to see the privacy-safe public timeline."
            actionLabel="Load demo ticket"
            onAction={() => form.handleSubmit({ preventDefault: () => {} })}
          />
        )}
      </div>
    </div>
  );
}
