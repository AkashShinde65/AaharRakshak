export default function Skeleton({ className = '' }) {
  return <div className={`animate-pulse rounded-md bg-slate-200 dark:bg-slate-800 ${className}`} />;
}

export function StatSkeleton() {
  return (
    <div className="surface rounded-lg p-5">
      <Skeleton className="h-4 w-24" />
      <Skeleton className="mt-5 h-8 w-20" />
      <Skeleton className="mt-3 h-3 w-32" />
    </div>
  );
}
