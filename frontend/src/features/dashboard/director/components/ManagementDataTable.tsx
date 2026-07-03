import { Pencil, Trash2 } from 'lucide-react';
import { DataTable, type Column } from '@/shared/components/data-display/DataTable';

interface ManagementActions {
  onEdit: (id: string) => void;
  onDeactivate: (id: string) => void;
}

interface ManagementDataTableProps<T extends object> {
  columns: Column<T>[];
  data: T[];
  loading?: boolean;
  emptyMessage?: string;
  keyExtractor: (item: T) => string;
  actions: ManagementActions;
  showActions?: boolean;
}

export function ManagementDataTable<T extends object>({
  columns,
  data,
  loading,
  emptyMessage,
  keyExtractor,
  actions,
  showActions = true,
}: ManagementDataTableProps<T>) {
  const actionColumn: Column<T> = {
    key: 'actions',
    header: 'Actions',
    render: (item: T) => (
      <div className="flex items-center gap-1">
        <button
          title="Edit"
          onClick={(e) => {
            e.stopPropagation();
            actions.onEdit(keyExtractor(item));
          }}
          className="p-1.5 rounded-md text-muted hover:text-primary hover:bg-primary/10 transition-colors duration-150"
        >
          <Pencil className="h-4 w-4" />
        </button>
        <button
          title="Delete"
          onClick={(e) => {
            e.stopPropagation();
            actions.onDeactivate(keyExtractor(item));
          }}
          className="p-1.5 rounded-md text-muted hover:text-danger hover:bg-danger/10 transition-colors duration-150"
        >
          <Trash2 className="h-4 w-4" />
        </button>
      </div>
    ),
  };

  const allColumns = showActions ? [...columns, actionColumn] : columns;

  return (
    <DataTable<T>
      columns={allColumns}
      data={data}
      loading={loading}
      emptyMessage={emptyMessage}
      keyExtractor={keyExtractor}
    />
  );
}
