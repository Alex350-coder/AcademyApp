import { useForm } from 'react-hook-form';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/Card';
import { Button } from '@/shared/components/Button';
import { Input } from '@/shared/components/Input';

interface UserFormValues {
  fullName: string;
  email: string;
  specialty?: string;
  guardian?: string;
}

interface UserFormDialogProps {
  title: string;
  initialValues?: Partial<UserFormValues>;
  onSubmit: (values: UserFormValues) => Promise<void>;
  onClose: () => void;
  fields: Array<keyof UserFormValues>;
  loading?: boolean;
}

export function UserFormDialog({
  title,
  initialValues,
  onSubmit,
  onClose,
  fields,
  loading,
}: UserFormDialogProps) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<UserFormValues>({
    defaultValues: initialValues,
  });

  const fieldLabels: Record<keyof UserFormValues, string> = {
    fullName: 'Nombre Completo',
    email: 'Email',
    specialty: 'Especialidad',
    guardian: 'Apoderado',
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle>{title}</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            {fields.map((field) => (
              <Input
                key={field}
                label={fieldLabels[field]}
                error={errors[field]?.message}
                {...register(field, { required: `${fieldLabels[field]} es requerido` })}
              />
            ))}
            <div className="flex justify-end gap-3 pt-2">
              <Button variant="secondary" type="button" onClick={onClose}>
                Cancelar
              </Button>
              <Button type="submit" loading={loading}>
                Guardar
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
