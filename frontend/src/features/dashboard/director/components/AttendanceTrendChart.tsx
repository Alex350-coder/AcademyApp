import { useMemo } from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/Card';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { EmptyState } from '@/shared/components/feedback/EmptyState';
import { useAttendanceTrend } from '../api/useAttendanceTrend';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

export function AttendanceTrendChart() {
  const { data, isLoading, isError, refetch } = useAttendanceTrend();

  const chartData = useMemo(() => {
    if (!data) return [];
    return data.map((point) => ({
      date: new Date(point.date).toLocaleDateString(undefined, { month: 'short', day: 'numeric' }),
      rate: Number(point.attendanceRate.toFixed(1)),
    }));
  }, [data]);

  if (isError) {
    return <ErrorState message="No se pudo cargar la tendencia de asistencia" onRetry={() => refetch()} />;
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Tendencia de Asistencia</CardTitle>
      </CardHeader>
      <CardContent>
        {isLoading ? (
          <div className="h-64 bg-surface-hover rounded animate-pulse" />
        ) : chartData.length > 0 ? (
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={chartData} margin={{ top: 8, right: 8, bottom: 8, left: -16 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)" />
                <XAxis
                  dataKey="date"
                  tick={{ fontSize: 11, fill: 'var(--color-muted)' }}
                  axisLine={{ stroke: 'var(--color-border)' }}
                  tickLine={false}
                  interval="preserveStartEnd"
                />
                <YAxis
                  domain={[0, 100]}
                  tick={{ fontSize: 11, fill: 'var(--color-muted)' }}
                  axisLine={{ stroke: 'var(--color-border)' }}
                  tickLine={false}
                  tickFormatter={(v: number) => `${v}%`}
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
                  formatter={(value) => [`${Number(value).toFixed(1)}%`, 'Asistencia']}
                />
                <Line
                  type="monotone"
                  dataKey="rate"
                  stroke="var(--color-primary)"
                  strokeWidth={2}
                  dot={{ fill: 'var(--color-primary)', r: 3, strokeWidth: 0 }}
                  activeDot={{ r: 5, fill: 'var(--color-primary)', stroke: '#ffffff', strokeWidth: 2 }}
                />
              </LineChart>
            </ResponsiveContainer>
          </div>
        ) : (
          <EmptyState title="Sin datos de asistencia" description="La tendencia de asistencia aparecerá aquí" />
        )}
      </CardContent>
    </Card>
  );
}
