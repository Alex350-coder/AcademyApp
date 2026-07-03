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
          <h1 className="text-2xl font-bold text-text">Reportes</h1>
          <p className="text-muted text-sm mt-1">Reportes detallados y analítica institucional</p>
        </div>
        <div className="flex items-center gap-2">
          <Button variant="secondary" onClick={() => window.print()}>
            Exportar PDF
          </Button>
          <Button variant="secondary">
            Exportar CSV
          </Button>
        </div>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Rango de Fechas</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex items-center gap-4">
            <div>
              <label className="text-sm font-medium text-text">Desde</label>
              <input
                type="date"
                value={dateFrom}
                onChange={(e) => setDateFrom(e.target.value)}
                className="block mt-1 px-3 py-2 rounded-md bg-surface-hover text-text border border-border"
              />
            </div>
            <div>
              <label className="text-sm font-medium text-text">Hasta</label>
              <input
                type="date"
                value={dateTo}
                onChange={(e) => setDateTo(e.target.value)}
                className="block mt-1 px-3 py-2 rounded-md bg-surface-hover text-text border border-border"
              />
            </div>
            <Button variant="primary" className="mt-6" onClick={handleApply}>
              Aplicar
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
            <CardTitle>Distribución de Notas</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="h-64">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={gradeDistributionData} margin={{ top: 8, right: 8, bottom: 8, left: -16 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)" />
                  <XAxis
                    dataKey="grade"
                    tick={{ fontSize: 11, fill: 'var(--color-muted)' }}
                    axisLine={{ stroke: 'var(--color-border)' }}
                    tickLine={false}
                  />
                  <YAxis
                    tick={{ fontSize: 11, fill: 'var(--color-muted)' }}
                    axisLine={{ stroke: 'var(--color-border)' }}
                    tickLine={false}
                  />
                  <Tooltip
                    contentStyle={{
                      backgroundColor: 'var(--color-surface)',
                      border: '1px solid var(--color-border)',
                      borderRadius: '6px',
                      color: 'var(--color-text)',
                      fontSize: '12px',
                      boxShadow: 'var(--shadow-elevation-2)',
                    }}
                    formatter={(value) => [value, 'Alumnos']}
                  />
                  <Bar dataKey="count" radius={[4, 4, 0, 0]}>
                    {gradeDistributionData.map((entry, i) => (
                      <Cell key={i} fill={entry.fill} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </div>
            <p className="text-xs text-muted mt-3 text-center">
              Total de alumnos calificados: {totalStudents}
            </p>
          </CardContent>
        </Card>

        <AttendanceTrendChart />
      </div>
    </div>
  );
}
