import type { ReactNode } from 'react';
import { Button } from '@/shared/components/Button';

type ActionConfig =
  | { label: string; onClick: () => void }
  | ReactNode;

interface EmptyStateProps {
  title: string;
  description?: string;
  action?: ActionConfig;
  icon?: ReactNode;
}

function isActionConfig(a: ActionConfig): a is { label: string; onClick: () => void } {
  return a != null && typeof a === 'object' && !('type' in a) && 'label' in a && 'onClick' in a;
}

export function EmptyState({ title, description, action, icon }: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center py-12 px-4 text-center">
      {icon && <div className="mb-4 text-muted">{icon}</div>}
      <h3 className="text-lg font-medium text-text">{title}</h3>
      {description && (
        <p className="text-sm text-muted mt-1 max-w-sm">{description}</p>
      )}
      {action && isActionConfig(action) ? (
        <Button variant="primary" size="sm" onClick={action.onClick} className="mt-4">
          {action.label}
        </Button>
      ) : action ? (
        <div className="mt-4">{action}</div>
      ) : null}
    </div>
  );
}
