import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import {
  ArrowRight,
  BadgeCheck,
  BarChart3,
  Bell,
  ClipboardCheck,
  FileSearch,
  MapPinned,
  ScanBarcode,
  ShieldCheck,
} from 'lucide-react';
import Navbar from '../components/layout/Navbar.jsx';
import Button from '../components/ui/Button.jsx';
import Card from '../components/ui/Card.jsx';
import Badge from '../components/ui/Badge.jsx';

const features = [
  { title: 'Citizen complaints', description: 'Scan packaged food or report prepared dishes with evidence and consented location.', icon: ScanBarcode },
  { title: 'Official workflow', description: 'Assignment, inspection, sample custody, lab reports and due-process actions in one workspace.', icon: ClipboardCheck },
  { title: 'Public transparency', description: 'Anonymized reports, recalls, alerts, licence status and complaint tracking for citizens.', icon: FileSearch },
  { title: 'Hotspots and alerts', description: 'District-level hotspot maps, SLA escalation, recall alerts and trust-score insights.', icon: MapPinned },
];

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-950">
      <Navbar />
      <main>
        <section className="border-b border-slate-200 bg-white dark:border-slate-800 dark:bg-slate-950">
          <div className="mx-auto grid max-w-7xl gap-10 px-4 py-14 sm:px-6 lg:grid-cols-[1fr_0.9fr] lg:px-8 lg:py-20">
            <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.45 }}>
              <Badge tone="green">Food safety complaint intelligence platform</Badge>
              <h1 className="mt-5 max-w-3xl text-4xl font-bold tracking-normal text-slate-950 dark:text-white sm:text-5xl lg:text-6xl">
                AaharRakshak
              </h1>
              <p className="mt-5 max-w-2xl text-lg leading-8 text-slate-600 dark:text-slate-300">
                A modern citizen-to-official workflow for food complaints, inspection evidence, laboratory reports, public recalls and privacy-safe transparency.
              </p>
              <div className="mt-8 flex flex-col gap-3 sm:flex-row">
                <Link to="/register">
                  <Button size="lg" icon={ArrowRight}>
                    Start a complaint
                  </Button>
                </Link>
                <Link to="/login">
                  <Button size="lg" variant="secondary">
                    Open dashboard
                  </Button>
                </Link>
              </div>
              <div className="mt-8 grid gap-3 text-sm text-slate-600 dark:text-slate-300 sm:grid-cols-3">
                {['Privacy-first reports', 'Mock Aadhaar only', 'Lab confirmation required'].map((item) => (
                  <span key={item} className="inline-flex items-center gap-2">
                    <BadgeCheck className="h-4 w-4 text-brand-600" />
                    {item}
                  </span>
                ))}
              </div>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5, delay: 0.1 }}
              className="surface rounded-lg p-4"
            >
              <div className="rounded-lg border border-slate-200 bg-slate-50 p-4 dark:border-slate-800 dark:bg-slate-900">
                <div className="flex items-center justify-between gap-4 border-b border-slate-200 pb-4 dark:border-slate-800">
                  <div className="flex items-center gap-3">
                    <span className="flex h-10 w-10 items-center justify-center rounded-lg bg-brand-600 text-white">
                      <ShieldCheck className="h-5 w-5" />
                    </span>
                    <div>
                      <p className="font-semibold text-slate-950 dark:text-white">Live food-safety desk</p>
                      <p className="text-sm text-slate-500 dark:text-slate-400">Pune district</p>
                    </div>
                  </div>
                  <Badge tone="orange">High priority</Badge>
                </div>
                <div className="mt-5 grid gap-4 sm:grid-cols-2">
                  <Card className="bg-white dark:bg-slate-950">
                    <ScanBarcode className="h-5 w-5 text-brand-600" />
                    <p className="mt-4 text-sm text-slate-500 dark:text-slate-400">Barcode matched</p>
                    <p className="mt-1 text-2xl font-bold text-slate-950 dark:text-white">8901234567890</p>
                  </Card>
                  <Card className="bg-white dark:bg-slate-950">
                    <Bell className="h-5 w-5 text-harvest-600" />
                    <p className="mt-4 text-sm text-slate-500 dark:text-slate-400">Recall alert</p>
                    <p className="mt-1 text-2xl font-bold text-slate-950 dark:text-white">Batch recall</p>
                  </Card>
                </div>
                <div className="mt-4 rounded-lg border border-slate-200 bg-white p-4 dark:border-slate-800 dark:bg-slate-950">
                  <div className="flex items-center justify-between">
                    <p className="text-sm font-semibold text-slate-950 dark:text-white">Complaint status</p>
                    <Badge tone="green">Report published</Badge>
                  </div>
                  <div className="mt-4 space-y-3">
                    {['Submitted', 'Assigned', 'Sample collected', 'Anonymized report published'].map((item, index) => (
                      <div key={item} className="flex items-center gap-3">
                        <span className="flex h-6 w-6 items-center justify-center rounded-full bg-brand-600 text-xs font-bold text-white">{index + 1}</span>
                        <span className="text-sm text-slate-600 dark:text-slate-300">{item}</span>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </motion.div>
          </div>
        </section>

        <section id="features" className="mx-auto max-w-7xl px-4 py-16 sm:px-6 lg:px-8">
          <div className="max-w-2xl">
            <p className="text-sm font-semibold uppercase text-brand-700 dark:text-brand-300">Product modules</p>
            <h2 className="mt-2 text-3xl font-bold text-slate-950 dark:text-white">Everything a food-safety workflow needs</h2>
          </div>
          <div className="mt-8 grid gap-5 md:grid-cols-2 lg:grid-cols-4">
            {features.map((feature) => (
              <Card key={feature.title}>
                <feature.icon className="h-6 w-6 text-brand-600" />
                <h3 className="mt-4 font-semibold text-slate-950 dark:text-white">{feature.title}</h3>
                <p className="mt-2 text-sm leading-6 text-slate-500 dark:text-slate-400">{feature.description}</p>
              </Card>
            ))}
          </div>
        </section>

        <section id="workflow" className="border-y border-slate-200 bg-white dark:border-slate-800 dark:bg-slate-900">
          <div className="mx-auto grid max-w-7xl gap-8 px-4 py-16 sm:px-6 lg:grid-cols-3 lg:px-8">
            {[
              ['1', 'Scan and submit', 'Citizen scans a barcode or reports a prepared dish with evidence and GPS consent.'],
              ['2', 'Investigate and test', 'Officials assign, inspect, collect sealed samples and publish laboratory outcomes.'],
              ['3', 'Inform safely', 'Public reports are anonymized, recalls are simulated and alerts remain privacy-aware.'],
            ].map(([step, title, description]) => (
              <div key={step} className="flex gap-4">
                <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-harvest-500 font-bold text-white">{step}</span>
                <div>
                  <h3 className="font-semibold text-slate-950 dark:text-white">{title}</h3>
                  <p className="mt-2 text-sm leading-6 text-slate-500 dark:text-slate-400">{description}</p>
                </div>
              </div>
            ))}
          </div>
        </section>

        <section id="transparency" className="mx-auto max-w-7xl px-4 py-16 sm:px-6 lg:px-8">
          <Card className="grid gap-8 bg-brand-600 p-8 text-white dark:bg-brand-700 lg:grid-cols-[1fr_auto] lg:items-center">
            <div>
              <BarChart3 className="h-8 w-8" />
              <h2 className="mt-4 text-3xl font-bold">Build trust without exposing citizens.</h2>
              <p className="mt-3 max-w-2xl text-brand-50">
                The UI keeps citizen privacy at the center while giving officials, companies and the public the right view for each workflow.
              </p>
            </div>
            <Link to="/register">
              <Button variant="accent" size="lg">
                Try the workflow
              </Button>
            </Link>
          </Card>
        </section>
      </main>
    </div>
  );
}
