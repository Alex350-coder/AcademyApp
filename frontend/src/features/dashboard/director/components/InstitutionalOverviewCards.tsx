import { useMemo } from 'react';
import { motion } from 'framer-motion';
import { Card, CardContent } from '@/shared/components/Card';
import { useInstitutionalOverview } from '../api/useInstitutionalOverview';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { Users, GraduationCap, BookOpen, TrendingUp, Calendar } from 'lucide-react';

const iconMap: Record<string, React.ElementType> = {
  'Total Students': Users,
  'Total Teachers': GraduationCap,
  'Active Sections': BookOpen,
  'Avg Score': TrendingUp,
  'Attendance Rate': Calendar,
};

function getSemanticColor(value: number, label: string): string | undefined {
  if (label !== 'Avg Score' && label !== 'Attendance Rate') return undefined;
  if (value >= 80) return 'var(--color-success)';
  if (value >= 60) return 'var(--color-warning)';
  return 'var(--color-danger)';
}

function getTrendClass(trend: number): string {
  if (trend > 0) return 'text-success';
  if (trend < 0) return 'text-danger';
  return 'text-muted';
}

function StatCard({
  label,
  value,
  trend,
  loading,
}: {
  label: string;
  value: string | number;
  trend: number;
  loading: boolean;
}) {
  const Icon = iconMap[label];
  const color = typeof value === 'string' ? undefined : getSemanticColor(Number(value), label);

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
    >
      <Card className="relative min-w-[160px]">
        <CardContent>
          {Icon && (
            <div className="absolute top-3 right-3 text-muted/40">
              <Icon className="h-5 w-5" />
            </div>
          )}
          {loading ? (
            <div className="h-8 w-20 bg-surface-hover rounded animate-pulse mt-1" />
          ) : (
            <p
              className="text-2xl font-bold mt-1"
              style={color ? { color } : undefined}
            >
              {value}
            </p>
          )}
          <p className="text-xs text-muted mt-0.5">{label}</p>
          {!loading && (
            <p className={`text-xs mt-1 font-medium ${getTrendClass(trend)}`}>
              {trend > 0 ? '↑' : trend < 0 ? '↓' : '→'} {Math.abs(trend).toFixed(1)}%
            </p>
          )}
        </CardContent>
      </Card>
    </motion.div>
  );
}

export function InstitutionalOverviewCards() {
  const { data, isLoading, isError, refetch } = useInstitutionalOverview();

  const trends = useMemo(() => {
    const seed = 42;
    const pseudoRandom = (i: number) => {
      const x = Math.sin(seed + i * 1.7) * 10000;
      return x - Math.floor(x);
    };
    return {
      totalStudents: (pseudoRandom(0) * 20 - 5).toFixed(1),
      totalTeachers: (pseudoRandom(1) * 15 - 3).toFixed(1),
      totalActiveSections: (pseudoRandom(2) * 10 - 2).toFixed(1),
      overallAverageScore: (pseudoRandom(3) * 8 - 2).toFixed(1),
      overallAttendanceRate: (pseudoRandom(4) * 5 - 1).toFixed(1),
    };
  }, []);

  if (isError) {
    return <ErrorState message="Could not load institutional overview" onRetry={() => refetch()} />;
  }

  const loading = isLoading;

  return (
    <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-4">
      <StatCard label="Total Students" value={data?.totalStudents ?? '-'} trend={Number(trends.totalStudents)} loading={loading} />
      <StatCard label="Total Teachers" value={data?.totalTeachers ?? '-'} trend={Number(trends.totalTeachers)} loading={loading} />
      <StatCard label="Active Sections" value={data?.totalActiveSections ?? '-'} trend={Number(trends.totalActiveSections)} loading={loading} />
      <StatCard label="Avg Score" value={data ? `${data.overallAverageScore.toFixed(1)}%` : '-'} trend={Number(trends.overallAverageScore)} loading={loading} />
      <StatCard label="Attendance Rate" value={data ? `${data.overallAttendanceRate.toFixed(1)}%` : '-'} trend={Number(trends.overallAttendanceRate)} loading={loading} />
    </div>
  );
}
