import { useEffect, useState } from 'react';
import { useToastStore, type ToastVariant, type ToastMessage } from '@/shared/store/useToastStore';

const variantStyles: Record<ToastVariant, string> = {
  success: 'bg-success-bg border-success text-success',
  error: 'bg-danger-bg border-danger text-danger',
  info: 'bg-primary/10 border-primary text-primary',
  warning: 'bg-warning-bg border-warning text-warning',
};

export function ToastContainer() {
  const toasts = useToastStore((s) => s.toasts);
  const removeToast = useToastStore((s) => s.removeToast);

  return (
    <div className="fixed bottom-4 right-4 z-50 flex flex-col gap-2">
      {toasts.map((toast) => (
        <ToastItem key={toast.id} toast={toast} onDismiss={() => removeToast(toast.id)} />
      ))}
    </div>
  );
}

function ToastItem({ toast, onDismiss }: { toast: ToastMessage; onDismiss: () => void }) {
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    requestAnimationFrame(() => setVisible(true));
  }, []);

  return (
    <div
      className={`flex items-center gap-3 px-4 py-3 rounded-lg border shadow-elevation-2 text-sm min-w-[280px] max-w-sm transition-all duration-300 ${
        variantStyles[toast.variant]
      } ${visible ? 'translate-x-0 opacity-100' : 'translate-x-full opacity-0'}`}
      role="alert"
    >
      <span className="flex-1">{toast.message}</span>
      <button
        onClick={onDismiss}
        className="flex-shrink-0 opacity-60 hover:opacity-100 transition-opacity"
        aria-label="Dismiss"
      >
        ✕
      </button>
    </div>
  );
}
