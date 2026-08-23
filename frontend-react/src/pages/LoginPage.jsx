import { Link, useNavigate } from 'react-router-dom';
import { ShieldCheck } from 'lucide-react';
import Button from '../components/ui/Button.jsx';
import Card from '../components/ui/Card.jsx';
import Input from '../components/ui/Input.jsx';
import ThemeToggle from '../components/ui/ThemeToggle.jsx';
import { useAuth } from '../hooks/useAuth.js';
import { useForm } from '../hooks/useForm.js';
import { useToast } from '../hooks/useToast.js';
import { validateLogin } from '../utils/validators.js';

export default function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const { showToast } = useToast();

  const form = useForm({
    initialValues: { identifier: 'citizen@aaharrakshak.dev', password: 'password' },
    validate: validateLogin,
    onSubmit: async (values) => {
      try {
        await login(values);
        showToast({ type: 'success', title: 'Welcome back', message: 'Dashboard session started.' });
        navigate('/dashboard');
      } catch (error) {
        showToast({
          type: 'error',
          title: 'Login failed',
          message: error.response?.data?.message || error.message || 'Check your credentials and backend URL.',
        });
      }
    },
  });

  return (
    <div className="min-h-screen bg-slate-50 px-4 py-8 dark:bg-slate-950">
      <div className="mx-auto flex max-w-5xl justify-end">
        <ThemeToggle />
      </div>
      <div className="mx-auto mt-10 grid max-w-5xl gap-8 lg:grid-cols-[0.9fr_1.1fr] lg:items-center">
        <div>
          <Link to="/" className="inline-flex items-center gap-2 font-semibold text-slate-950 dark:text-white">
            <span className="flex h-9 w-9 items-center justify-center rounded-lg bg-brand-600 text-white">
              <ShieldCheck className="h-5 w-5" />
            </span>
            AaharRakshak
          </Link>
          <h1 className="mt-8 text-3xl font-bold text-slate-950 dark:text-white sm:text-4xl">Secure access for citizens and officials.</h1>
          <p className="mt-4 text-slate-600 dark:text-slate-300">
            Connect to your Spring Boot backend or enable mock mode while designing the UI.
          </p>
        </div>

        <Card className="p-6">
          <h2 className="text-xl font-semibold text-slate-950 dark:text-white">Login</h2>
          <form className="mt-6 space-y-4" onSubmit={form.handleSubmit}>
            <Input
              label="Email or mobile"
              name="identifier"
              value={form.values.identifier}
              onChange={form.handleChange}
              onBlur={form.handleBlur}
              error={form.touched.identifier && form.errors.identifier}
              required
            />
            <Input
              label="Password"
              name="password"
              type="password"
              value={form.values.password}
              onChange={form.handleChange}
              onBlur={form.handleBlur}
              error={form.touched.password && form.errors.password}
              required
            />
            <Button type="submit" className="w-full" loading={form.submitting}>
              Login
            </Button>
          </form>
          <p className="mt-5 text-center text-sm text-slate-500 dark:text-slate-400">
            New here?{' '}
            <Link className="font-semibold text-brand-700 dark:text-brand-300" to="/register">
              Create citizen account
            </Link>
          </p>
        </Card>
      </div>
    </div>
  );
}
