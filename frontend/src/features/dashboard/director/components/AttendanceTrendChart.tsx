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
    return <ErrorState message="Could not load attendance trend" onRetry={() => refetch()} />;
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Attendance Trend</CardTitle>
      </CardHeader>
      <CardContent>
        {isLoading ? (
          <div className="h-64 bg-[#1e2a3a] rounded animate-pulse" />
        ) : chartData.length > 0 ? (
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={chartData} margin={{ top: 8, right: 8, bottom: 8, left: -16 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="#1e3048" />
                <XAxis
                  dataKey="date"
                  tick={{ fontSize: 11, fill: '#94a3b8' }}
                  axisLine={{ stroke: '#1e3048' }}
                  tickLine={false}
                  interval="preserveStartEnd"
                />
                <YAxis
                  domain={[0, 100]}
                  tick={{ fontSize: 11, fill: '#94a3b8' }}
                  axisLine={{ stroke: '#1e3048' }}
                  tickLine={false}
                  tickFormatter={(v: number) => `${v}%`}
                />
                <Tooltip
                  contentStyle={{
                    backgroundColor: '#1a2332',
                    border: '1px solid #1e3048',
                    borderRadius: '6px',
                    color: '#f1f5f9',
                    fontSize: '12px',
                  }}
                  formatter={(value) => [`${Number(value).toFixed(1)}%`, 'Attendance']}
                />
                <Line
                  type="monotone"
                  dataKey="rate"
                  stroke="#3b82f6"
                  strokeWidth={2}
                  dot={{ fill: '#3b82f6', r: 3, strokeWidth: 0 }}
                  activeDot={{ r: 5, fill: '#3b82f6', stroke: '#f1f5f9', strokeWidth: 2 }}
                />
              </LineChart>
            </ResponsiveContainer>
          </div>
        ) : (
          <EmptyState title="No attendance data" description="Attendance trend will appear here" />
        )}
      </CardContent>
    </Card>
  );
}
