import { useState, useMemo, type ReactNode } from 'react';
import { Search, SearchX } from 'lucide-react';
import { Button } from '@/shared/components/Button';
import { EmptyState } from '@/shared/components/feedback/EmptyState';

export interface Column<T> {
  key: string;
  header: string;
  sortable?: boolean;
  render?: (item: T) => ReactNode;
}

interface DataTableProps<T> {
  columns: Column<T>[];
  data: T[];
  loading?: boolean;
  emptyMessage?: string;
  pageSize?: number;
  keyExtractor: (item: T) => string;
  searchable?: boolean;
  searchKeys?: (keyof T)[];
  emptyTitle?: string;
  emptyDescription?: string;
  emptyAction?: ReactNode;
}

export function DataTable<T>({
  columns,
  data,
  loading = false,
  emptyMessage = 'No data found',
  pageSize = 20,
  keyExtractor,
  searchable = false,
  searchKeys,
  emptyTitle,
  emptyDescription,
  emptyAction,
}: DataTableProps<T>) {
  const [sortKey, setSortKey] = useState<string | null>(null);
  const [sortDir, setSortDir] = useState<'asc' | 'desc'>('asc');
  const [currentPage, setCurrentPage] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');

  const filtered = useMemo(() => {
    if (!searchQuery.trim()) return data;
    const q = searchQuery.toLowerCase();
    const keys = searchKeys ?? (columns.map((c) => c.key as keyof T));
    return data.filter((item) =>
      keys.some((key) => {
        const val = item[key];
        return val != null && String(val).toLowerCase().includes(q);
      }),
    );
  }, [data, searchQuery, searchKeys, columns]);

  const sorted = useMemo(() => {
    if (!sortKey) return filtered;
    return [...filtered].sort((a, b) => {
      const aVal = a[sortKey as keyof T];
      const bVal = b[sortKey as keyof T];
      if (aVal == null) return 1;
      if (bVal == null) return -1;
      const cmp = String(aVal).localeCompare(String(bVal));
      return sortDir === 'asc' ? cmp : -cmp;
    });
  }, [filtered, sortKey, sortDir]);

  const pageCount = Math.ceil(sorted.length / pageSize);
  const page = sorted.slice(currentPage * pageSize, (currentPage + 1) * pageSize);

  const hasSearchFilter = searchQuery.trim().length > 0;

  function handleSort(key: string) {
    if (sortKey === key) {
      setSortDir((d) => (d === 'asc' ? 'desc' : 'asc'));
    } else {
      setSortKey(key);
      setSortDir('asc');
    }
  }

  if (loading) {
    return (
      <div className="bg-surface border border-border rounded-lg p-6">
        <div className="space-y-3 animate-pulse">
          {searchable && <div className="h-9 bg-surface-hover rounded w-full" />}
          {Array.from({ length: 5 }).map((_, i) => (
            <div key={i} className="h-8 bg-surface-hover rounded" />
          ))}
        </div>
      </div>
    );
  }

  if (sorted.length === 0) {
    return (
      <div className="bg-surface border border-border rounded-lg p-6">
        {searchable && (
          <div className="relative mb-4">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => { setSearchQuery(e.target.value); setCurrentPage(0); }}
              placeholder="Search..."
              className="w-full pl-10 pr-4 py-2 rounded-md bg-surface-hover text-text border border-border placeholder:text-muted text-sm focus:outline-none focus:border-primary"
            />
          </div>
        )}
        <EmptyState
          title={emptyTitle ?? (hasSearchFilter ? 'No results found' : emptyMessage)}
          description={emptyDescription ?? (hasSearchFilter ? 'Try a different search term' : '')}
          icon={hasSearchFilter ? <SearchX className="h-8 w-8 text-muted" /> : undefined}
          action={
            hasSearchFilter ? (
              <Button variant="secondary" onClick={() => setSearchQuery('')}>
                Clear search
              </Button>
            ) : (
              emptyAction
            )
          }
        />
      </div>
    );
  }

  return (
    <div className="bg-surface border border-border rounded-lg overflow-hidden">
      {searchable && (
        <div className="relative p-4 pb-0">
          <Search className="absolute left-7 top-1/2 -translate-y-1/2 h-4 w-4 text-muted" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => { setSearchQuery(e.target.value); setCurrentPage(0); }}
            placeholder="Search..."
            className="w-full pl-10 pr-4 py-2 rounded-md bg-surface-hover text-text border border-border placeholder:text-muted text-sm focus:outline-none focus:border-primary"
          />
        </div>
      )}
      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-border bg-surface-hover">
              {columns.map((col) => (
                <th
                  key={col.key}
                  className={`px-4 py-3 text-left font-medium text-muted ${
                    col.sortable ? 'cursor-pointer hover:text-text select-none' : ''
                  }`}
                  onClick={() => col.sortable && handleSort(col.key)}
                >
                  <span className="inline-flex items-center gap-1">
                    {col.header}
                    {sortKey === col.key && (
                      <span className="text-xs">{sortDir === 'asc' ? '▲' : '▼'}</span>
                    )}
                  </span>
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {page.map((item) => (
              <tr
                key={keyExtractor(item)}
                className="border-b border-border last:border-b-0 hover:bg-surface-hover transition-colors duration-150"
              >
                {columns.map((col) => (
                  <td key={col.key} className="px-4 py-3 text-text">
                    {col.render ? col.render(item) : String(item[col.key as keyof T] ?? '')}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {pageCount > 1 && (
        <div className="flex items-center justify-between px-4 py-3 border-t border-border">
          <span className="text-sm text-muted">
            {currentPage * pageSize + 1}–{Math.min((currentPage + 1) * pageSize, sorted.length)} of {sorted.length}
          </span>
          <div className="flex gap-1">
            <Button
              size="sm"
              variant="secondary"
              onClick={() => setCurrentPage((p) => Math.max(0, p - 1))}
              disabled={currentPage === 0}
            >
              Prev
            </Button>
            <Button
              size="sm"
              variant="secondary"
              onClick={() => setCurrentPage((p) => Math.min(pageCount - 1, p + 1))}
              disabled={currentPage >= pageCount - 1}
            >
              Next
            </Button>
          </div>
        </div>
      )}
    </div>
  );
}
