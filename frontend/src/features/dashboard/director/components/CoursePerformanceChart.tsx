import { useState, useEffect, useRef, useLayoutEffect } from 'react';
import { motion } from 'framer-motion';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/Card';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { EmptyState } from '@/shared/components/feedback/EmptyState';
import { useCoursePerformance } from '../api/useCoursePerformance';

const ITEM_HEIGHT = 52;
const GAP = 16;

function getBarColor(score: number): string {
  if (score === 0) return '#374151';
  if (score >= 80) return '#22c55e';
  if (score >= 60) return '#eab308';
  if (score >= 40) return '#f97316';
  return '#ef4444';
}

export function CoursePerformanceChart() {
  const { data, isLoading, isError, refetch } = useCoursePerformance();
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(1);
  const listRef = useRef<HTMLDivElement>(null);

  const courses = data ?? [];
  const totalPages = Math.ceil(courses.length / itemsPerPage);
  const paginatedCourses = courses.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage,
  );

  const calcItemsPerPage = (h: number) =>
    Math.max(Math.floor((h + GAP) / (ITEM_HEIGHT + GAP)), 1);

  useLayoutEffect(() => {
    const el = listRef.current;
    if (!el) return;
    setItemsPerPage(calcItemsPerPage(el.clientHeight));
  }, []);

  useEffect(() => {
    const el = listRef.current;
    if (!el) return;

    const calc = () => setItemsPerPage(calcItemsPerPage(el.clientHeight));

    const observer = new ResizeObserver(calc);
    observer.observe(el);
    return () => observer.disconnect();
  }, []);

  useEffect(() => {
    setCurrentPage(1);
  }, [data, itemsPerPage]);

  if (isError) {
    return <ErrorState message="Could not load course performance" onRetry={() => refetch()} />;
  }

  return (
    <Card className="flex flex-col h-full">
      <CardHeader>
        <CardTitle>Course Performance</CardTitle>
      </CardHeader>
      <CardContent className="flex-1 flex flex-col">
        {isLoading ? (
          <div className="space-y-4 animate-pulse flex-1">
            {Array.from({ length: 4 }).map((_, i) => (
              <div key={i} className="space-y-1">
                <div className="h-4 w-32 bg-[#1e2a3a] rounded" />
                <div className="h-6 bg-[#1e2a3a] rounded" />
              </div>
            ))}
          </div>
        ) : courses.length > 0 ? (
          <>
            <div ref={listRef} className="flex-1 overflow-hidden">
              <div className="space-y-4">
                {paginatedCourses.map((course, index) => {
                  const barColor = getBarColor(course.averageScore);
                  const widthPercent = course.averageScore > 0 ? course.averageScore : 0;
                  return (
                    <div key={course.courseId}>
                      <div className="flex items-center justify-between mb-1">
                        <div className="min-w-0 flex-1">
                          <p className="text-sm font-medium text-[#f1f5f9] truncate">
                            {course.courseName}
                          </p>
                          <p className="text-xs text-[#94a3b8]">
                            {course.courseCode} &middot; {course.enrolledStudents} enrolled &middot; {course.attendanceRate.toFixed(0)}% attendance
                          </p>
                        </div>
                        <span
                          className="text-sm font-semibold ml-3"
                          style={{ color: course.averageScore === 0 ? '#94a3b8' : barColor }}
                        >
                          {course.averageScore === 0 ? 'Sin datos' : `${course.averageScore.toFixed(1)}%`}
                        </span>
                      </div>
                      <div className="w-full h-3 bg-[#1e2a3a] rounded-full overflow-hidden">
                        <motion.div
                          className="h-full rounded-full"
                          style={{ backgroundColor: barColor }}
                          initial={{ width: 0 }}
                          animate={{ width: `${widthPercent}%` }}
                          transition={{ duration: 0.8, delay: index * 0.1, ease: 'easeOut' }}
                        />
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>

            {totalPages > 1 && (
              <div className="flex items-center justify-between pt-3 mt-3 border-t border-[#1e3048]">
                <span className="text-xs text-[#94a3b8]">
                  {(currentPage - 1) * itemsPerPage + 1}–
                  {Math.min(currentPage * itemsPerPage, courses.length)} of {courses.length}
                </span>
                <div className="flex items-center gap-1">
                  <button
                    onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
                    disabled={currentPage === 1}
                    className="p-1 rounded hover:bg-white/10 disabled:opacity-30 disabled:cursor-not-allowed transition-colors text-[#94a3b8]"
                  >
                    <ChevronLeft size={16} />
                  </button>
                  <span className="text-xs text-[#94a3b8] px-2">
                    {currentPage} / {totalPages}
                  </span>
                  <button
                    onClick={() => setCurrentPage((p) => Math.min(totalPages, p + 1))}
                    disabled={currentPage === totalPages}
                    className="p-1 rounded hover:bg-white/10 disabled:opacity-30 disabled:cursor-not-allowed transition-colors text-[#94a3b8]"
                  >
                    <ChevronRight size={16} />
                  </button>
                </div>
              </div>
            )}
          </>
        ) : (
          <div className="flex-1 flex items-center justify-center">
            <EmptyState title="No course data available" description="Course performance data will appear here" />
          </div>
        )}
      </CardContent>
    </Card>
  );
}
