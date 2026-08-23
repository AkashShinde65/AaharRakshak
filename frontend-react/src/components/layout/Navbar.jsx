import { Link } from 'react-router-dom';
import { Menu, ShieldCheck, X } from 'lucide-react';
import { useState } from 'react';
import Button from '../ui/Button.jsx';
import ThemeToggle from '../ui/ThemeToggle.jsx';
import { useAuth } from '../../hooks/useAuth.js';

const navItems = [
  { label: 'Features', href: '/#features' },
  { label: 'How it works', href: '/#workflow' },
  { label: 'Transparency', href: '/#transparency' },
];

export default function Navbar() {
  const [open, setOpen] = useState(false);
  const { isAuthenticated } = useAuth();

  return (
    <header className="sticky top-0 z-40 border-b border-slate-200 bg-white/90 backdrop-blur dark:border-slate-800 dark:bg-slate-950/90">
      <nav className="mx-auto flex h-16 max-w-7xl items-center justify-between px-4 sm:px-6 lg:px-8">
        <Link to="/" className="flex items-center gap-2 font-semibold text-slate-950 dark:text-white">
          <span className="flex h-9 w-9 items-center justify-center rounded-lg bg-brand-600 text-white">
            <ShieldCheck className="h-5 w-5" />
          </span>
          AaharRakshak
        </Link>

        <div className="hidden items-center gap-6 md:flex">
          {navItems.map((item) => (
            <a key={item.label} href={item.href} className="text-sm font-medium text-slate-600 hover:text-brand-700 dark:text-slate-300">
              {item.label}
            </a>
          ))}
        </div>

        <div className="hidden items-center gap-2 md:flex">
          <ThemeToggle />
          {isAuthenticated ? (
            <Link to="/dashboard">
              <Button>Dashboard</Button>
            </Link>
          ) : (
            <>
              <Link to="/login">
                <Button variant="ghost">Login</Button>
              </Link>
              <Link to="/register">
                <Button>Register</Button>
              </Link>
            </>
          )}
        </div>

        <div className="flex items-center gap-2 md:hidden">
          <ThemeToggle />
          <Button variant="ghost" size="sm" icon={open ? X : Menu} aria-label="Toggle navigation" onClick={() => setOpen((value) => !value)} />
        </div>
      </nav>

      {open ? (
        <div className="border-t border-slate-200 bg-white px-4 py-4 dark:border-slate-800 dark:bg-slate-950 md:hidden">
          <div className="flex flex-col gap-3">
            {navItems.map((item) => (
              <a key={item.label} href={item.href} className="text-sm font-medium text-slate-700 dark:text-slate-200" onClick={() => setOpen(false)}>
                {item.label}
              </a>
            ))}
            <div className="grid grid-cols-2 gap-3 pt-2">
              <Link to="/login" onClick={() => setOpen(false)}>
                <Button className="w-full" variant="secondary">
                  Login
                </Button>
              </Link>
              <Link to={isAuthenticated ? '/dashboard' : '/register'} onClick={() => setOpen(false)}>
                <Button className="w-full">{isAuthenticated ? 'Dashboard' : 'Register'}</Button>
              </Link>
            </div>
          </div>
        </div>
      ) : null}
    </header>
  );
}
