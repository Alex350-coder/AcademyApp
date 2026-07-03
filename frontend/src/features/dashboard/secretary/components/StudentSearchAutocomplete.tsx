import { useState, useRef, useEffect } from 'react';
import { Input } from '@/shared/components/Input';
import { useQuery } from '@tanstack/react-query';
import httpClient from '@/shared/api/httpClient';
import { secretaryEndpoints } from '../api/endpoints';
import type { Student } from '../api/types';

interface StudentSearchAutocompleteProps {
  onSelect: (student: Student) => void;
  placeholder?: string;
}

export function StudentSearchAutocomplete({
  onSelect,
  placeholder = 'Buscar alumnos...',
}: StudentSearchAutocompleteProps) {
  const [query, setQuery] = useState('');
  const [debounced, setDebounced] = useState('');
  const [isOpen, setIsOpen] = useState(false);
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const timer = setTimeout(() => setDebounced(query), 300);
    return () => clearTimeout(timer);
  }, [query]);

  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (ref.current && !ref.current.contains(e.target as Node)) {
        setIsOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const { data } = useQuery<Student[]>({
    queryKey: ['student-search', debounced],
    queryFn: async () => {
      const { data } = await httpClient.get(secretaryEndpoints.students, {
        params: { search: debounced },
      });
      return data;
    },
    enabled: debounced.length >= 2,
  });

  return (
    <div ref={ref} className="relative">
      <Input
        placeholder={placeholder}
        value={query}
        onChange={(e) => {
          setQuery(e.target.value);
          setIsOpen(true);
        }}
        onFocus={() => setIsOpen(true)}
      />
      {isOpen && data && data.length > 0 && (
        <div className="absolute z-10 mt-1 w-full bg-surface border border-border rounded-md shadow-elevation-2 max-h-60 overflow-y-auto">
          {data.map((student) => (
            <button
              key={student.id}
              type="button"
              className="w-full text-left px-3 py-2 text-sm text-text hover:bg-surface-hover transition-colors"
              onClick={() => {
                onSelect(student);
                setQuery(student.fullName);
                setIsOpen(false);
              }}
            >
              <span className="font-medium">{student.fullName}</span>
              <span className="text-muted ml-2">{student.enrollmentCode}</span>
            </button>
          ))}
        </div>
      )}
    </div>
  );
}
