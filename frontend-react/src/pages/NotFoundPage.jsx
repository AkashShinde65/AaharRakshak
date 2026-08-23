import { Link } from 'react-router-dom';
import Button from '../components/ui/Button.jsx';

export default function NotFoundPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-50 px-4 text-center dark:bg-slate-950">
      <div>
        <p className="text-sm font-semibold uppercase text-brand-700 dark:text-brand-300">404</p>
        <h1 className="mt-2 text-3xl font-bold text-slate-950 dark:text-white">Page not found</h1>
        <p className="mt-3 max-w-md text-slate-500 dark:text-slate-400">The page you are looking for is not available in this frontend route.</p>
        <Link to="/">
          <Button className="mt-6">Back to home</Button>
        </Link>
      </div>
    </div>
  );
}
