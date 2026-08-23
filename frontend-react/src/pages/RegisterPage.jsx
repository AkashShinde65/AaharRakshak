import { Link, useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { ShieldCheck } from 'lucide-react';
import Button from '../components/ui/Button.jsx';
import Card from '../components/ui/Card.jsx';
import Input from '../components/ui/Input.jsx';
import ThemeToggle from '../components/ui/ThemeToggle.jsx';
import { useAuth } from '../hooks/useAuth.js';
import { useForm } from '../hooks/useForm.js';
import { useToast } from '../hooks/useToast.js';
import { validateRegister } from '../utils/validators.js';

function registrationErrorMessage(error) {
  if (error.response?.data?.detail || error.response?.data?.message) {
    return error.response.data.detail || error.response.data.message;
  }
  if (error.response?.status === 409) {
    return 'Email or mobile number is already registered. Try logging in or use different details.';
  }
  if (error.response?.status === 400) {
    return 'Check the entered details and try again.';
  }
  return 'Check the form and backend connection.';
}

export default function RegisterPage() {
  const navigate = useNavigate();
  const { register, requestOtp, verifyOtp } = useAuth();
  const { showToast } = useToast();
  const [pendingVerification, setPendingVerification] = useState(null);
  const [otpCode, setOtpCode] = useState('123456');
  const [verifying, setVerifying] = useState(false);

  const form = useForm({
    initialValues: {
      fullName: '',
      email: '',
      mobileNumber: '',
      password: '',
      confirmPassword: '',
      acceptPrivacy: false,
    },
    validate: validateRegister,
    onSubmit: async (values) => {
      try {
        await register(values);
        const otp = await requestOtp(values.email, 'EMAIL');
        setPendingVerification({
          identifier: values.email,
          destination: otp.destination || values.email,
          mockCode: otp.mockCode || '123456',
        });
        showToast({
          type: 'success',
          title: 'Registration started',
          message: `Use mock OTP ${otp.mockCode || '123456'} to activate the account.`,
        });
      } catch (error) {
        showToast({
          type: 'error',
          title: 'Registration failed',
          message: registrationErrorMessage(error),
        });
      }
    },
  });

  async function handleVerifyOtp(event) {
    event.preventDefault();
    if (!pendingVerification) return;

    setVerifying(true);
    try {
      await verifyOtp(pendingVerification.identifier, otpCode, 'EMAIL');
      showToast({
        type: 'success',
        title: 'Account verified',
        message: 'You can login with your new citizen account now.',
      });
      navigate('/login');
    } catch (error) {
      showToast({
        type: 'error',
        title: 'OTP verification failed',
        message: error.response?.data?.detail || error.response?.data?.message || error.message || 'Use mock OTP 123456.',
      });
    } finally {
      setVerifying(false);
    }
  }

  return (
    <div className="min-h-screen bg-slate-50 px-4 py-8 dark:bg-slate-950">
      <div className="mx-auto flex max-w-5xl justify-end">
        <ThemeToggle />
      </div>
      <div className="mx-auto mt-8 max-w-3xl">
        <Link to="/" className="inline-flex items-center gap-2 font-semibold text-slate-950 dark:text-white">
          <span className="flex h-9 w-9 items-center justify-center rounded-lg bg-brand-600 text-white">
            <ShieldCheck className="h-5 w-5" />
          </span>
          AaharRakshak
        </Link>
        <Card className="mt-8 p-6">
          <h1 className="text-2xl font-bold text-slate-950 dark:text-white">Create citizen account</h1>
          <p className="mt-2 text-sm text-slate-500 dark:text-slate-400">
            Identity checks are mock/consent-based in this academic system. Do not enter real Aadhaar details.
          </p>
          {pendingVerification ? (
            <form className="mt-6 space-y-4" onSubmit={handleVerifyOtp}>
              <p className="rounded-lg border border-emerald-200 bg-emerald-50 p-3 text-sm text-emerald-900 dark:border-emerald-900/60 dark:bg-emerald-950/40 dark:text-emerald-100">
                OTP sent to {pendingVerification.destination}. Mock code: {pendingVerification.mockCode}
              </p>
              <Input
                label="Mock OTP"
                name="otpCode"
                value={otpCode}
                onChange={(event) => setOtpCode(event.target.value)}
                required
              />
              <Button type="submit" className="w-full" loading={verifying}>
                Verify account
              </Button>
            </form>
          ) : (
            <form className="mt-6 grid gap-4 sm:grid-cols-2" onSubmit={form.handleSubmit}>
              <Input label="Full name" name="fullName" value={form.values.fullName} onChange={form.handleChange} onBlur={form.handleBlur} error={form.touched.fullName && form.errors.fullName} required />
              <Input label="Email" name="email" type="email" value={form.values.email} onChange={form.handleChange} onBlur={form.handleBlur} error={form.touched.email && form.errors.email} required />
              <Input label="Mobile number" name="mobileNumber" value={form.values.mobileNumber} onChange={form.handleChange} onBlur={form.handleBlur} error={form.touched.mobileNumber && form.errors.mobileNumber} required />
              <Input label="Password" name="password" type="password" value={form.values.password} onChange={form.handleChange} onBlur={form.handleBlur} error={form.touched.password && form.errors.password} required />
              <Input label="Confirm password" name="confirmPassword" type="password" value={form.values.confirmPassword} onChange={form.handleChange} onBlur={form.handleBlur} error={form.touched.confirmPassword && form.errors.confirmPassword} required />
              <label className="flex items-start gap-3 rounded-lg border border-slate-200 p-3 text-sm dark:border-slate-800 sm:col-span-2">
                <input
                  className="mt-1 h-4 w-4 rounded border-slate-300 text-brand-600 focus:ring-brand-500"
                  type="checkbox"
                  name="acceptPrivacy"
                  checked={form.values.acceptPrivacy}
                  onChange={form.handleChange}
                />
                <span className="text-slate-600 dark:text-slate-300">
                  I consent to mock verification and understand that public reports hide citizen private details.
                  {form.touched.acceptPrivacy && form.errors.acceptPrivacy ? (
                    <span className="mt-1 block text-red-600 dark:text-red-400">{form.errors.acceptPrivacy}</span>
                  ) : null}
                </span>
              </label>
              <Button type="submit" className="sm:col-span-2" loading={form.submitting}>
                Create account
              </Button>
            </form>
          )}
          <p className="mt-5 text-center text-sm text-slate-500 dark:text-slate-400">
            Already registered?{' '}
            <Link className="font-semibold text-brand-700 dark:text-brand-300" to="/login">
              Login
            </Link>
          </p>
        </Card>
      </div>
    </div>
  );
}
