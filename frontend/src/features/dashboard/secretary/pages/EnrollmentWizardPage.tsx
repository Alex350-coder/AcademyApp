import { useState } from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/Card';
import { Button } from '@/shared/components/Button';
import { Badge } from '@/shared/components/Badge';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { EmptyState } from '@/shared/components/feedback/EmptyState';
import { StudentSearchAutocomplete } from '../components/StudentSearchAutocomplete';
import { useAvailableSections } from '../api/useSections';
import { useCreateEnrollment } from '../api/useEnrollments';
import type { Student, Section } from '../api/types';

type Step = 'student' | 'section' | 'confirm';

export default function EnrollmentWizardPage() {
  const [step, setStep] = useState<Step>('student');
  const [selectedStudent, setSelectedStudent] = useState<Student | null>(null);
  const [selectedSection, setSelectedSection] = useState<Section | null>(null);
  const createEnrollment = useCreateEnrollment();

  const { data: sections, isLoading: sectionsLoading, isError: sectionsError, refetch } = useAvailableSections();

  function handleStudentSelect(student: Student) {
    setSelectedStudent(student);
  }

  function handleNext() {
    if (step === 'student' && selectedStudent) setStep('section');
    else if (step === 'section' && selectedSection) setStep('confirm');
  }

  function handleBack() {
    if (step === 'section') setStep('student');
    else if (step === 'confirm') setStep('section');
  }

  function handleConfirm() {
    if (!selectedStudent || !selectedSection) return;
    createEnrollment.mutate(
      { studentId: selectedStudent.id, sectionId: selectedSection.id },
      {
        onSuccess: () => {
          setSelectedStudent(null);
          setSelectedSection(null);
          setStep('student');
        },
      },
    );
  }

  const steps = [
    { key: 'student', label: 'Student' },
    { key: 'section', label: 'Section' },
    { key: 'confirm', label: 'Confirm' },
  ];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-text">Enrollment Wizard</h1>
        <p className="text-muted text-sm mt-1">Enroll students in academic sections</p>
      </div>

      <div className="flex items-center gap-2">
        {steps.map((s, i) => (
          <div key={s.key} className="flex items-center gap-2">
            <div
              className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-medium transition-colors ${
                step === s.key
                  ? 'bg-primary text-white'
                  : steps.findIndex((st) => st.key === step) > i
                  ? 'bg-success text-white'
                  : 'bg-surface-hover text-muted'
              }`}
            >
              {steps.findIndex((st) => st.key === step) > i ? '✓' : i + 1}
            </div>
            <span
              className={`text-sm ${
                step === s.key ? 'text-text font-medium' : 'text-muted'
              }`}
            >
              {s.label}
            </span>
            {i < steps.length - 1 && <div className="w-8 h-px bg-border" />}
          </div>
        ))}
      </div>

      {step === 'student' && (
        <Card>
          <CardHeader>
            <CardTitle>Select Student</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <StudentSearchAutocomplete onSelect={handleStudentSelect} />
            {selectedStudent && (
              <div className="p-3 rounded-md bg-success-bg border border-success/20">
                <p className="text-sm font-medium text-success">
                  Selected: {selectedStudent.fullName}
                </p>
                <p className="text-xs text-success/80">{selectedStudent.enrollmentCode}</p>
              </div>
            )}
            <div className="flex justify-end">
              <Button onClick={handleNext} disabled={!selectedStudent}>
                Next
              </Button>
            </div>
          </CardContent>
        </Card>
      )}

      {step === 'section' && (
        <Card>
          <CardHeader>
            <CardTitle>Select Section</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            {sectionsError ? (
              <ErrorState message="Could not load sections" onRetry={() => refetch()} />
            ) : sectionsLoading ? (
              <div className="space-y-3 animate-pulse">
                {Array.from({ length: 3 }).map((_, i) => (
                  <div key={i} className="h-16 bg-surface-hover rounded" />
                ))}
              </div>
            ) : sections && sections.length > 0 ? (
              <div className="space-y-2 max-h-96 overflow-y-auto">
                {sections.map((section) => {
                  const isSelected = selectedSection?.id === section.id;
                  const isFull = section.enrolledCount >= section.capacity;
                  return (
                    <button
                      key={section.id}
                      disabled={!section.available || isFull}
                      onClick={() => setSelectedSection(section)}
                      className={`w-full text-left p-4 rounded-lg border transition-colors ${
                        isSelected
                          ? 'border-primary bg-primary/5'
                          : isFull || !section.available
                          ? 'border-border bg-surface-hover opacity-50 cursor-not-allowed'
                          : 'border-border hover:border-primary/50'
                      }`}
                    >
                      <div className="flex items-center justify-between">
                        <div>
                          <p className="text-sm font-medium text-text">{section.courseName}</p>
                          <p className="text-xs text-muted mt-0.5">
                            {section.code} &middot; {section.teacherName} &middot; {section.schedule}
                          </p>
                        </div>
                        <div className="text-right">
                          <Badge variant={isFull ? 'danger' : section.enrolledCount >= section.capacity * 0.8 ? 'warning' : 'success'}>
                            {section.enrolledCount}/{section.capacity}
                          </Badge>
                        </div>
                      </div>
                    </button>
                  );
                })}
              </div>
            ) : (
              <EmptyState
                title="No available sections"
                description="All sections are currently full"
              />
            )}
            <div className="flex justify-between">
              <Button variant="secondary" onClick={handleBack}>
                Back
              </Button>
              <Button onClick={handleNext} disabled={!selectedSection}>
                Next
              </Button>
            </div>
          </CardContent>
        </Card>
      )}

      {step === 'confirm' && (
        <Card>
          <CardHeader>
            <CardTitle>Confirm Enrollment</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="p-4 rounded-lg bg-surface-hover space-y-3">
              <div>
                <p className="text-xs text-muted">Student</p>
                <p className="text-sm font-medium text-text">{selectedStudent?.fullName}</p>
                <p className="text-xs text-muted">{selectedStudent?.enrollmentCode}</p>
              </div>
              <div className="h-px bg-border" />
              <div>
                <p className="text-xs text-muted">Section</p>
                <p className="text-sm font-medium text-text">{selectedSection?.courseName}</p>
                <p className="text-xs text-muted">
                  {selectedSection?.code} &middot; {selectedSection?.teacherName} &middot; {selectedSection?.schedule}
                </p>
              </div>
            </div>
            <div className="flex justify-between">
              <Button variant="secondary" onClick={handleBack}>
                Back
              </Button>
              <Button onClick={handleConfirm} loading={createEnrollment.isPending}>
                Confirm Enrollment
              </Button>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
