import { useState } from 'react';
import { Button } from '@/shared/components/Button';
import { Input } from '@/shared/components/Input';
import { useEvaluationTypes, useCreateEvaluation } from '../api/useEvaluations';

interface NewEvaluationFormProps {
  sectionId: string;
  onCreated: () => void;
  onCancel: () => void;
}

export function NewEvaluationForm({ sectionId, onCreated, onCancel }: NewEvaluationFormProps) {
  const { data: types, isLoading: typesLoading } = useEvaluationTypes();
  const createEvaluation = useCreateEvaluation();
  const [evaluationTypeId, setEvaluationTypeId] = useState('');
  const [name, setName] = useState('');
  const [date, setDate] = useState(new Date().toISOString().split('T')[0]);
  const [maxScore, setMaxScore] = useState('100');

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    const maxScoreValue = Number(maxScore);
    if (!evaluationTypeId || !name.trim() || Number.isNaN(maxScoreValue) || maxScoreValue <= 0) {
      return;
    }
    createEvaluation.mutate(
      { sectionId, evaluationTypeId, name: name.trim(), date, maxScore: maxScoreValue },
      { onSuccess: onCreated },
    );
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label className="text-sm font-medium text-text block mb-1">Tipo de Evaluación</label>
        {typesLoading ? (
          <div className="h-10 bg-surface-hover rounded animate-pulse" />
        ) : (
          <select
            value={evaluationTypeId}
            onChange={(e) => setEvaluationTypeId(e.target.value)}
            className="w-full px-3 py-2 rounded-md bg-surface text-text border border-border focus:outline-none focus:ring-2 focus:ring-[var(--color-border-focus)]"
          >
            <option value="">Selecciona un tipo...</option>
            {types?.map((t) => (
              <option key={t.id} value={t.id}>
                {t.name}
              </option>
            ))}
          </select>
        )}
      </div>
      <Input
        label="Nombre"
        value={name}
        onChange={(e) => setName(e.target.value)}
        placeholder="ej. Prueba 3"
      />
      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="text-sm font-medium text-text block mb-1">Fecha</label>
          <input
            type="date"
            value={date}
            onChange={(e) => setDate(e.target.value)}
            className="w-full px-3 py-2 rounded-md bg-surface text-text border border-border focus:outline-none focus:ring-2 focus:ring-[var(--color-border-focus)]"
          />
        </div>
        <Input
          label="Puntaje Máximo"
          type="number"
          min={1}
          value={maxScore}
          onChange={(e) => setMaxScore(e.target.value)}
        />
      </div>
      <div className="flex gap-2">
        <Button type="submit" loading={createEvaluation.isPending}>
          Crear Evaluación
        </Button>
        <Button type="button" variant="secondary" onClick={onCancel}>
          Cancelar
        </Button>
      </div>
    </form>
  );
}
