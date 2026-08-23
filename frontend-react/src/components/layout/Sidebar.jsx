import { NavLink } from 'react-router-dom';
import { BarChart3, Beaker, Building2, ClipboardList, FilePlus2, Gavel, LayoutDashboard, ShieldCheck, Stethoscope, UserRound } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth.js';

const links = [
  { label: 'Overview', to: '/dashboard', icon: LayoutDashboard },
  { label: 'Submit complaint', to: '/dashboard/complaints/new', icon: FilePlus2 },
  { label: 'Track complaint', to: '/dashboard/complaints/track', icon: ClipboardList },
  { label: 'My inspections', to: '/dashboard/inspector', icon: Stethoscope, inspectorOnly: true },
  { label: 'Lab samples', to: '/dashboard/lab', icon: Beaker, labOnly: true },
  { label: 'Company portal', to: '/dashboard/company', icon: Building2, companyOnly: true },
  { label: 'District oversight', to: '/dashboard/district', icon: Gavel, districtOnly: true },
  { label: 'Admin analytics', to: '/dashboard/admin', icon: BarChart3, adminOnly: true },
  { label: 'Profile', to: '/dashboard/profile', icon: UserRound },
];

export default function Sidebar({ open, onClose }) {
  const { user } = useAuth();
  const isAdmin = ['CENTRAL_ADMINISTRATOR', 'DISTRICT_ESCALATION_OFFICER'].includes(user?.role);
  const isInspector = user?.role === 'FOOD_INSPECTOR';
  const isLab = user?.role === 'LABORATORY_OFFICER';
  const isCompany = user?.role === 'COMPANY';
  const isDistrict = ['DISTRICT_ESCALATION_OFFICER', 'CENTRAL_ADMINISTRATOR'].includes(user?.role);

  return (
    <>
      <div
        className={`fixed inset-0 z-30 bg-slate-950/40 transition md:hidden ${open ? 'opacity-100' : 'pointer-events-none opacity-0'}`}
        onClick={onClose}
      />
      <aside
        className={[
          'fixed inset-y-0 left-0 z-40 flex w-72 flex-col border-r border-slate-200 bg-white transition-transform dark:border-slate-800 dark:bg-slate-950 md:static md:translate-x-0',
          open ? 'translate-x-0' : '-translate-x-full',
        ].join(' ')}
      >
        <div className="flex h-16 items-center gap-2 border-b border-slate-200 px-5 dark:border-slate-800">
          <span className="flex h-9 w-9 items-center justify-center rounded-lg bg-brand-600 text-white">
            <ShieldCheck className="h-5 w-5" />
          </span>
          <div>
            <p className="font-semibold text-slate-950 dark:text-white">AaharRakshak</p>
            <p className="text-xs text-slate-500 dark:text-slate-400">Food safety workspace</p>
          </div>
        </div>

        <nav className="flex-1 space-y-1 px-3 py-4">
          {links
            .filter((link) => (!link.adminOnly || isAdmin) && (!link.inspectorOnly || isInspector) && (!link.labOnly || isLab) && (!link.companyOnly || isCompany) && (!link.districtOnly || isDistrict))
            .map((link) => (
              <NavLink
                key={link.to}
                to={link.to}
                end={link.to === '/dashboard'}
                onClick={onClose}
                className={({ isActive }) =>
                  [
                    'flex items-center gap-3 rounded-md px-3 py-2.5 text-sm font-medium transition',
                    isActive
                      ? 'bg-brand-50 text-brand-700 dark:bg-brand-950 dark:text-brand-200'
                      : 'text-slate-600 hover:bg-slate-100 dark:text-slate-300 dark:hover:bg-slate-900',
                  ].join(' ')
                }
              >
                <link.icon className="h-4 w-4" />
                {link.label}
              </NavLink>
            ))}
        </nav>

        <div className="border-t border-slate-200 p-4 dark:border-slate-800">
          <p className="text-sm font-semibold text-slate-900 dark:text-white">{user?.name || 'Demo User'}</p>
          <p className="mt-1 text-xs text-slate-500 dark:text-slate-400">{user?.role || 'CITIZEN'}</p>
        </div>
      </aside>
    </>
  );
}
