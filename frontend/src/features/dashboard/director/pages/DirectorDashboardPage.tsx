import { Suspense } from 'react';
import { InstitutionalOverviewCards } from '../components/InstitutionalOverviewCards';
import { AtRiskStudentsPanel } from '../components/AtRiskStudentsPanel';
import { CoursePerformanceChart } from '../components/CoursePerformanceChart';
import { AttendanceTrendChart } from '../components/AttendanceTrendChart';

export default function DirectorDashboardPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-text">Director Dashboard</h1>
        <p className="text-muted text-sm mt-1">Institutional overview and performance metrics</p>
      </div>

      <Suspense fallback={null}>
        <InstitutionalOverviewCards />
      </Suspense>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 items-stretch">
        <div className="flex flex-col h-full">
          <Suspense fallback={null}>
            <AtRiskStudentsPanel />
          </Suspense>
        </div>
        <div className="flex flex-col h-full">
          <Suspense fallback={null}>
            <CoursePerformanceChart />
          </Suspense>
        </div>
      </div>

      <Suspense fallback={null}>
        <AttendanceTrendChart />
      </Suspense>
    </div>
  );
}
