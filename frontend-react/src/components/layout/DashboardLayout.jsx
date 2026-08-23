import { Outlet } from 'react-router-dom';
import { Menu, Search } from 'lucide-react';
import { useState } from 'react';
import Sidebar from './Sidebar.jsx';
import Button from '../ui/Button.jsx';
import ThemeToggle from '../ui/ThemeToggle.jsx';
import { useAuth } from '../../hooks/useAuth.js';

export default function DashboardLayout() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const { logout } = useAuth();

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-950">
      <div className="flex min-h-screen">
        <Sidebar open={sidebarOpen} onClose={() => setSidebarOpen(false)} />
        <div className="min-w-0 flex-1">
          <header className="sticky top-0 z-20 flex h-16 items-center justify-between border-b border-slate-200 bg-white/90 px-4 backdrop-blur dark:border-slate-800 dark:bg-slate-950/90 sm:px-6">
            <div className="flex items-center gap-3">
              <Button variant="ghost" size="sm" icon={Menu} className="md:hidden" onClick={() => setSidebarOpen(true)} aria-label="Open sidebar" />
              <div className="hidden items-center gap-2 rounded-md border border-slate-200 bg-slate-50 px-3 py-2 dark:border-slate-800 dark:bg-slate-900 sm:flex">
                <Search className="h-4 w-4 text-slate-400" />
                <span className="text-sm text-slate-500 dark:text-slate-400">Search complaints, products, batches</span>
              </div>
            </div>
            <div className="flex items-center gap-2">
              <ThemeToggle />
              <Button variant="secondary" onClick={logout}>
                Logout
              </Button>
            </div>
          </header>
          <main className="mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8">
            <Outlet />
          </main>
        </div>
      </div>
    </div>
  );
}
