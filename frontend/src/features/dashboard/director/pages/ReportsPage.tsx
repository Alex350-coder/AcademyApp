import { useState, useMemo } from 'react';
import { InstitutionalOverviewCards } from '../components/InstitutionalOverviewCards';
import { AtRiskStudentsPanel } from '../components/AtRiskStudentsPanel';
import { CoursePerformanceChart } from '../components/CoursePerformanceChart';
import { AttendanceTrendChart } from '../components/AttendanceTrendChart';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/Card';
import { Button } from '@/shared/components/Button';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Cell,
} from 'recharts';

const gradeDistributionData = [
  { grade: 'A (90-100)', count: 42, fill: '#22c55e' },
  { grade: 'B (80-89)', count: 68, fill: '#16a34a' },
  { grade: 'C (70-79)', count: 55, fill: '#eab308' },
  { grade: 'D (60-69)', count: 23, fill: '#f97316' },
  { grade: 'F (<60)', count: 12, fill: '#ef4444' },
];

export default function ReportsPage() {
  const [dateFrom, setDateFrom] = useState(
    () => new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
  );
  const [dateTo, setDateTo] = useState(
    () => new Date().toISOString().split('T')[0],
  );
  const [_appliedFrom, setAppliedFrom] = useState(dateFrom);
  const [_appliedTo, setAppliedTo] = useState(dateTo);

  function handleApply() {
    setAppliedFrom(dateFrom);
    setAppliedTo(dateTo);
  }

  const totalStudents = useMemo(
    () => gradeDistributionData.reduce((sum, d) => sum + d.count, 0),
    [],
  );

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-[#f1f5f9]">Reports</h1>
          <p className="text-[#94a3b8] text-sm mt-1">Detailed institutional reports and analytics</p>
        </div>
        <div className="flex items-center gap-2">
          <Button variant="secondary" onClick={() => window.print()}>
            Export PDF
          </Button>
          <Button variant="secondary">
            Export CSV
          </Button>
        </div>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Date Range</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex items-center gap-4">
            <div>
              <label className="text-sm font-medium text-[#f1f5f9]">From</label>
              <input
                type="date"
                value={dateFrom}
                onChange={(e) => setDateFrom(e.target.value)}
                className="block mt-1 px-3 py-2 rounded-md bg-[#1e2a3a] text-[#f1f5f9] border border-[#1e3048]"
              />
            </div>
            <div>
              <label className="text-sm font-medium text-[#f1f5f9]">To</label>
              <input
                type="date"
                value={dateTo}
                onChange={(e) => setDateTo(e.target.value)}
                className="block mt-1 px-3 py-2 rounded-md bg-[#1e2a3a] text-[#f1f5f9] border border-[#1e3048]"
              />
            </div>
            <Button variant="primary" className="mt-6" onClick={handleApply}>
              Apply
            </Button>
          </div>
        </CardContent>
      </Card>

      <InstitutionalOverviewCards />

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 items-stretch">
        <div className="flex flex-col h-full">
          <AtRiskStudentsPanel />
        </div>
        <div className="flex flex-col h-full">
          <CoursePerformanceChart />
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card>
          <CardHeader>
            <CardTitle>Grade Distribution</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="h-64">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={gradeDistributionData} margin={{ top: 8, right: 8, bottom: 8, left: -16 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#1e3048" />
                  <XAxis
                    dataKey="grade"
                    tick={{ fontSize: 11, fill: '#94a3b8' }}
                    axisLine={{ stroke: '#1e3048' }}
                    tickLine={false}
                  />
                  <YAxis
                    tick={{ fontSize: 11, fill: '#94a3b8' }}
                    axisLine={{ stroke: '#1e3048' }}
                    tickLine={false}
                  />
                  <Tooltip
                    contentStyle={{
                      backgroundColor: '#1a2332',
                      border: '1px solid #1e3048',
                      borderRadius: '6px',
                      color: '#f1f5f9',
                      fontSize: '12px',
                    }}
                    formatter={(value) => [value, 'Students']}
                  />
                  <Bar dataKey="count" radius={[4, 4, 0, 0]}>
                    {gradeDistributionData.map((entry, i) => (
                      <Cell key={i} fill={entry.fill} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </div>
            <p className="text-xs text-[#94a3b8] mt-3 text-center">
              Total students graded: {totalStudents}
            </p>
          </CardContent>
        </Card>

        <AttendanceTrendChart />
      </div>
    </div>
  );
}
