import { ROLES } from '../utils/constants.js';

export const demoUsers = [
  { email: 'citizen@aaharrakshak.dev', password: 'password', role: ROLES.citizen, name: 'Demo Citizen' },
  { email: 'company@aaharrakshak.dev', password: 'password', role: ROLES.company, name: 'Demo Foods Pvt Ltd' },
  { email: 'inspector@aaharrakshak.dev', password: 'password', role: ROLES.inspector, name: 'Food Inspector' },
  { email: 'lab@aaharrakshak.dev', password: 'password', role: ROLES.lab, name: 'Lab Officer' },
  { email: 'district@aaharrakshak.dev', password: 'password', role: ROLES.district, name: 'District Officer' },
  { email: 'admin@aaharrakshak.dev', password: 'password', role: ROLES.admin, name: 'Central Admin' },
];

export const stats = [
  { label: 'Open complaints', value: '128', trend: '+12%', tone: 'green' },
  { label: 'Avg. response time', value: '18h', trend: '-6h', tone: 'orange' },
  { label: 'Published reports', value: '42', trend: '+8', tone: 'green' },
  { label: 'Critical hotspots', value: '3', trend: '+1', tone: 'red' },
];

export const complaints = [
  {
    ticketNumber: 'ARK-SEED-0006',
    title: 'Demo Turmeric Powder',
    type: 'PACKAGED_FOOD',
    status: 'REPORT_PUBLISHED',
    risk: 'HIGH',
    location: 'Pune demo market',
    createdAt: '2026-07-20T10:00:00Z',
  },
  {
    ticketNumber: 'ARK-HOT-0001',
    title: 'Clustered street food reports',
    type: 'PREPARED_DISH',
    status: 'ASSIGNED',
    risk: 'CRITICAL',
    location: 'Pune district',
    createdAt: '2026-07-21T14:00:00Z',
  },
  {
    ticketNumber: 'ARK-SLA-0007',
    title: 'Overdue high-risk inspection',
    type: 'PACKAGED_FOOD',
    status: 'ESCALATED',
    risk: 'HIGH',
    location: 'Pimpri-Chinchwad',
    createdAt: '2026-07-18T09:30:00Z',
  },
];

export const trackingTimeline = [
  { status: 'SUBMITTED', title: 'Complaint submitted', date: '2026-07-20T10:00:00Z' },
  { status: 'ASSIGNED', title: 'Assigned to food inspector', date: '2026-07-20T14:30:00Z' },
  { status: 'SAMPLE_COLLECTED', title: 'Sample collected with seal', date: '2026-07-21T09:45:00Z' },
  { status: 'REPORT_PUBLISHED', title: 'Anonymized lab report published', date: '2026-07-23T11:10:00Z' },
];

export const adminRows = [
  { id: 1, officer: 'District Officer', district: 'Pune', workload: 24, sla: '92%', status: 'Healthy' },
  { id: 2, officer: 'Food Inspector', district: 'Pune', workload: 15, sla: '84%', status: 'Watch' },
  { id: 3, officer: 'Lab Officer', district: 'Mumbai', workload: 9, sla: '97%', status: 'Healthy' },
  { id: 4, officer: 'Central Admin', district: 'Statewide', workload: 31, sla: '88%', status: 'Watch' },
];
