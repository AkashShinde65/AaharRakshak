import Badge from '../ui/Badge.jsx';

export default function AdminTable({ rows }) {
  return (
    <div className="overflow-hidden rounded-lg border border-slate-200 dark:border-slate-800">
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-slate-200 text-sm dark:divide-slate-800">
          <thead className="bg-slate-50 text-left text-xs uppercase text-slate-500 dark:bg-slate-900 dark:text-slate-400">
            <tr>
              <th className="px-4 py-3 font-semibold">Officer</th>
              <th className="px-4 py-3 font-semibold">District</th>
              <th className="px-4 py-3 font-semibold">Workload</th>
              <th className="px-4 py-3 font-semibold">SLA</th>
              <th className="px-4 py-3 font-semibold">Status</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-200 bg-white dark:divide-slate-800 dark:bg-slate-950">
            {rows.map((row) => (
              <tr key={row.id} className="hover:bg-slate-50 dark:hover:bg-slate-900">
                <td className="whitespace-nowrap px-4 py-4 font-medium text-slate-950 dark:text-white">{row.officer}</td>
                <td className="whitespace-nowrap px-4 py-4 text-slate-500 dark:text-slate-400">{row.district}</td>
                <td className="whitespace-nowrap px-4 py-4 text-slate-500 dark:text-slate-400">{row.workload}</td>
                <td className="whitespace-nowrap px-4 py-4 text-slate-500 dark:text-slate-400">{row.sla}</td>
                <td className="whitespace-nowrap px-4 py-4">
                  <Badge tone={row.status === 'Healthy' ? 'green' : 'orange'}>{row.status}</Badge>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
