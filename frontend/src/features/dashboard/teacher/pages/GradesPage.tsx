import { useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/Card';
import { Button } from '@/shared/components/Button';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { EmptyState } from '@/shared/components/feedback/EmptyState';
import { useMySections } from '../api/useSections';
import { useEvaluationsBySection } from '../api/useEvaluations';
import { GradeEntryGrid } from '../components/GradeEntryGrid';
import { NewEvaluationForm } from '../components/NewEvaluationForm';

export default function GradesPage() {
  const [searchParams] = useSearchParams();
  const [selectedSectionId, setSelectedSectionId] = useState(searchParams.get('sectionId') ?? '');
  const [selectedEvaluationId, setSelectedEvaluationId] = useState('');
  const [showNewEvaluationForm, setShowNewEvaluationForm] = useState(false);

  const { data: sections, isLoading: sectionsLoading, isError: sectionsError, refetch: refetchSections } =
    useMySections();
  const {
    data: evaluations,
    isLoading: evaluationsLoading,
    isError: evaluationsError,
    refetch: refetchEvaluations,
  } = useEvaluationsBySection(selectedSectionId);

  if (sectionsError) {
    return <ErrorState message="Could not load your sections" onRetry={() => refetchSections()} />;
  }

  const selectedEvaluation = evaluations?.find((e) => e.id === selectedEvaluationId);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-text">Grade Management</h1>
        <p className="text-muted text-sm mt-1">Create evaluations and record grades for your sections</p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Select Section</CardTitle>
        </CardHeader>
        <CardContent>
          {sectionsLoading ? (
            <div className="h-10 bg-surface-hover rounded animate-pulse" />
          ) : sections && sections.length > 0 ? (
            <select
              value={selectedSectionId}
              onChange={(e) => {
                setSelectedSectionId(e.target.value);
                setSelectedEvaluationId('');
                setShowNewEvaluationForm(false);
              }}
              className="w-full max-w-md px-3 py-2 rounded-md bg-surface text-text border border-border focus:outline-none focus:ring-2 focus:ring-[var(--color-border-focus)]"
            >
              <option value="">Select a section...</option>
              {sections.map((section) => (
                <option key={section.id} value={section.id}>
                  {section.courseName ?? 'Untitled Course'} - {section.name}
                </option>
              ))}
            </select>
          ) : (
            <p className="text-sm text-muted">No sections assigned</p>
          )}
        </CardContent>
      </Card>

      {selectedSectionId && (
        <Card>
          <CardHeader>
            <CardTitle>Select Evaluation</CardTitle>
            {!showNewEvaluationForm && (
              <Button size="sm" variant="secondary" onClick={() => setShowNewEvaluationForm(true)}>
                + New Evaluation
              </Button>
            )}
          </CardHeader>
          <CardContent>
            {showNewEvaluationForm ? (
              <NewEvaluationForm
                sectionId={selectedSectionId}
                onCreated={() => {
                  setShowNewEvaluationForm(false);
                  refetchEvaluations();
                }}
                onCancel={() => setShowNewEvaluationForm(false)}
              />
            ) : evaluationsError ? (
              <ErrorState message="Could not load evaluations" onRetry={() => refetchEvaluations()} />
            ) : evaluationsLoading ? (
              <div className="h-10 bg-surface-hover rounded animate-pulse" />
            ) : evaluations && evaluations.length > 0 ? (
              <select
                value={selectedEvaluationId}
                onChange={(e) => setSelectedEvaluationId(e.target.value)}
                className="w-full max-w-md px-3 py-2 rounded-md bg-surface text-text border border-border focus:outline-none focus:ring-2 focus:ring-[var(--color-border-focus)]"
              >
                <option value="">Select an evaluation...</option>
                {evaluations.map((ev) => (
                  <option key={ev.id} value={ev.id}>
                    {ev.name} ({ev.evaluationTypeName})
                  </option>
                ))}
              </select>
            ) : (
              <p className="text-sm text-muted">
                No evaluations yet for this section. Create one to start grading.
              </p>
            )}
          </CardContent>
        </Card>
      )}

      {selectedEvaluation ? (
        <GradeEntryGrid sectionId={selectedSectionId} evaluation={selectedEvaluation} />
      ) : selectedSectionId && !showNewEvaluationForm ? (
        <EmptyState
          title="Select an evaluation"
          description="Choose an evaluation above to start entering grades"
        />
      ) : null}
    </div>
  );
}
