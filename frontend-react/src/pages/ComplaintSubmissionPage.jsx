import { useNavigate } from 'react-router-dom';
import { UploadCloud } from 'lucide-react';
import PageHeader from '../components/layout/PageHeader.jsx';
import Button from '../components/ui/Button.jsx';
import Card from '../components/ui/Card.jsx';
import Input from '../components/ui/Input.jsx';
import Select from '../components/ui/Select.jsx';
import { useForm } from '../hooks/useForm.js';
import { useToast } from '../hooks/useToast.js';
import { complaintService } from '../services/complaintService.js';
import { validateComplaint } from '../utils/validators.js';

export default function ComplaintSubmissionPage() {
  const navigate = useNavigate();
  const { showToast } = useToast();

  const form = useForm({
    initialValues: {
      complaintType: 'PACKAGED_FOOD',
      category: '',
      barcode: '8901234567890',
      productName: '',
      companyName: '',
      fssaiLicenceNumber: '',
      batchNumber: '',
      vendorName: '',
      description: '',
      address: '',
      latitude: '',
      longitude: '',
      gpsConsent: false,
      evidence: [],
    },
    validate: validateComplaint,
    onSubmit: async (values) => {
      try {
        const result = await complaintService.submitComplaint(values);
        showToast({
          type: 'success',
          title: 'Complaint submitted',
          message: `Tracking number: ${result.ticketNumber || result.trackingNumber || 'generated'}`,
        });
        navigate('/dashboard/complaints/track', { state: { ticketNumber: result.ticketNumber || result.trackingNumber } });
      } catch (error) {
        showToast({
          type: 'error',
          title: 'Submission failed',
          message: error.response?.data?.message || 'Check validation errors and backend availability.',
        });
      }
    },
  });

  const isPackaged = form.values.complaintType === 'PACKAGED_FOOD';

  return (
    <div>
      <PageHeader
        eyebrow="Citizen complaint"
        title="Submit food safety complaint"
        description="Images, OCR and barcode data are triage aids only. Chemical adulteration must be confirmed by inspection and laboratory testing."
      />

      <Card>
        <form className="grid gap-5 lg:grid-cols-2" onSubmit={form.handleSubmit}>
          <Select label="Complaint type" name="complaintType" value={form.values.complaintType} onChange={form.handleChange} error={form.touched.complaintType && form.errors.complaintType} required>
            <option value="PACKAGED_FOOD">Packaged food</option>
            <option value="PREPARED_DISH">Prepared dish / street food</option>
          </Select>

          <Select label="Category" name="category" value={form.values.category} onChange={form.handleChange} error={form.touched.category && form.errors.category} required>
            <option value="">Select category</option>
            <option value="SUSPECTED_ADULTERATION">Suspected adulteration</option>
            <option value="HYGIENE_ISSUE">Hygiene issue</option>
            <option value="MISLABELLED_PRODUCT">Mislabeling</option>
            <option value="EXPIRED_PRODUCT">Expired product</option>
          </Select>

          {isPackaged ? (
            <>
              <Input label="Barcode / GTIN" name="barcode" value={form.values.barcode} onChange={form.handleChange} hint="Backend can lookup product by barcode before manual entry." />
              <Input label="Product name" name="productName" value={form.values.productName} onChange={form.handleChange} onBlur={form.handleBlur} error={form.touched.productName && form.errors.productName} required />
              <Input label="Company name" name="companyName" value={form.values.companyName} onChange={form.handleChange} />
              <Input label="FSSAI licence" name="fssaiLicenceNumber" value={form.values.fssaiLicenceNumber} onChange={form.handleChange} />
              <Input label="Batch number" name="batchNumber" value={form.values.batchNumber} onChange={form.handleChange} />
            </>
          ) : (
            <Input label="Vendor or stall name" name="vendorName" value={form.values.vendorName} onChange={form.handleChange} onBlur={form.handleBlur} error={form.touched.vendorName && form.errors.vendorName} required />
          )}

          <Input label="Location / landmark" name="address" value={form.values.address} onChange={form.handleChange} onBlur={form.handleBlur} error={form.touched.address && form.errors.address} required />
          <div className="grid gap-5 sm:grid-cols-2">
            <Input label="Latitude" name="latitude" value={form.values.latitude} onChange={form.handleChange} />
            <Input label="Longitude" name="longitude" value={form.values.longitude} onChange={form.handleChange} />
          </div>

          <div className="lg:col-span-2">
            <Input
              as="textarea"
              label="Complaint details"
              name="description"
              value={form.values.description}
              onChange={form.handleChange}
              onBlur={form.handleBlur}
              error={form.touched.description && form.errors.description}
              placeholder="Describe what happened, product condition, purchase location and any visible label details."
              required
            />
          </div>

          <label className="rounded-lg border border-dashed border-slate-300 p-5 dark:border-slate-700 lg:col-span-2">
            <div className="flex flex-col items-center justify-center gap-3 text-center">
              <UploadCloud className="h-8 w-8 text-brand-600" />
              <div>
                <p className="font-semibold text-slate-950 dark:text-white">Upload evidence metadata</p>
                <p className="mt-1 text-sm text-slate-500 dark:text-slate-400">Images, videos or receipts. Backend validators enforce file type, size and checksum.</p>
              </div>
              <input className="sr-only" type="file" name="evidence" multiple onChange={form.handleChange} />
              <span className="rounded-md bg-slate-100 px-3 py-2 text-sm font-semibold text-slate-700 dark:bg-slate-800 dark:text-slate-200">
                {form.values.evidence.length ? `${form.values.evidence.length} file(s) selected` : 'Choose files'}
              </span>
            </div>
          </label>

          <label className="flex items-start gap-3 rounded-lg border border-slate-200 p-4 text-sm dark:border-slate-800 lg:col-span-2">
            <input
              className="mt-1 h-4 w-4 rounded border-slate-300 text-brand-600 focus:ring-brand-500"
              type="checkbox"
              name="gpsConsent"
              checked={form.values.gpsConsent}
              onChange={form.handleChange}
            />
            <span className="text-slate-600 dark:text-slate-300">
              I consent to sharing complaint location for routing, investigation and regional safety alerts.
              {form.touched.gpsConsent && form.errors.gpsConsent ? (
                <span className="mt-1 block text-red-600 dark:text-red-400">{form.errors.gpsConsent}</span>
              ) : null}
            </span>
          </label>

          <div className="flex justify-end lg:col-span-2">
            <Button type="submit" loading={form.submitting}>
              Submit complaint
            </Button>
          </div>
        </form>
      </Card>
    </div>
  );
}
