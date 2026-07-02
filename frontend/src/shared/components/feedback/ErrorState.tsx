import { Button } from '@/shared/components/Button';

interface ErrorStateProps {
  message?: string;
  onRetry?: () => void;
}

export function ErrorState({
  message = 'Something went wrong',
  onRetry,
}: ErrorStateProps) {
  return (
    <div className="flex flex-col items-center justify-center py-12 px-4 text-center">
      <div className="w-12 h-12 rounded-full bg-danger-bg flex items-center justify-center mb-4">
        <span className="text-danger text-xl">!</span>
      </div>
      <h3 className="text-lg font-medium text-text">Error</h3>
      <p className="text-sm text-muted mt-1 max-w-sm">{message}</p>
      {onRetry && (
        <Button variant="secondary" size="sm" onClick={onRetry} className="mt-4">
          Try Again
        </Button>
      )}
    </div>
  );
}
