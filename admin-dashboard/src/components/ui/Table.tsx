import { useState } from 'react';
import { ChevronUp, ChevronDown } from 'lucide-react';

export interface Column<T> {
  key: keyof T | string;
  header: string;
  render?: (row: T, index: number) => React.ReactNode;
  sortable?: boolean;
  className?: string;
}

interface TableProps<T> {
  columns: Column<T>[];
  data: T[];
  keyExtractor: (row: T) => string;
  onRowClick?: (row: T) => void;
  isLoading?: boolean;
  emptyMessage?: string;
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
  onSort?: (key: string) => void;
  className?: string;
  striped?: boolean;
  hoverable?: boolean;
}

export function Table<T>({
  columns,
  data,
  keyExtractor,
  onRowClick,
  isLoading = false,
  emptyMessage = 'No data available',
  sortBy,
  sortOrder,
  onSort,
  className = '',
  striped = true,
  hoverable = true,
}: TableProps<T>) {
  const [sortConfig, setSortConfig] = useState<{ key: string; order: 'asc' | 'desc' } | null>(null);

  const handleSort = (key: string) => {
    if (onSort) {
      onSort(key);
      return;
    }

    setSortConfig((current) => {
      if (current?.key === key) {
        return { key, order: current.order === 'asc' ? 'desc' : 'asc' };
      }
      return { key, order: 'asc' };
    });
  };

  const getSortIcon = (key: string) => {
    if (!onSort && sortConfig?.key === key) {
      return sortConfig.order === 'asc' ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />;
    }
    if (onSort && sortBy === key) {
      return sortOrder === 'asc' ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />;
    }
    return null;
  };

  if (isLoading) {
    return (
      <div className="overflow-x-auto rounded-xl border border-neutral-200">
        <table className="w-full" role="table">
          <thead>
            <tr className="bg-neutral-50 border-b border-neutral-200">
              {columns.map((column) => (
                <th
                  key={String(column.key)}
                  className="px-4 py-3 text-left text-xs font-semibold text-neutral-500 uppercase tracking-wider"
                >
                  {column.header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {[...Array(5)].map((_, i) => (
              <tr key={i} className="border-b border-neutral-200 animate-pulse">
                {columns.map((column) => (
                  <td key={String(column.key)} className="px-4 py-3">
                    <div className="h-4 bg-neutral-200 rounded w-3/4" />
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    );
  }

  if (data.length === 0) {
    return (
      <div className="rounded-xl border border-neutral-200 p-12 text-center">
        <div className="text-neutral-400 text-lg">{emptyMessage}</div>
      </div>
    );
  }

  return (
    <div className={`overflow-x-auto rounded-xl border border-neutral-200 ${className}`}>
      <table className="w-full" role="table">
        <thead>
          <tr className="bg-neutral-50 border-b border-neutral-200">
            {columns.map((column) => (
              <th
                key={String(column.key)}
                className={`
                  px-4 py-3 text-left text-xs font-semibold text-neutral-500 uppercase tracking-wider
                  ${column.sortable || onSort ? 'cursor-pointer select-none hover:bg-neutral-100' : ''}
                  ${column.className || ''}
                `}
                onClick={() => (column.sortable || onSort) && handleSort(String(column.key))}
                style={{ userSelect: 'none' }}
              >
                <div className="flex items-center gap-1">
                  <span>{column.header}</span>
                  {getSortIcon(String(column.key))}
                </div>
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="divide-y divide-neutral-200">
          {data.map((row, rowIndex) => (
            <tr
              key={keyExtractor(row)}
              className={`
                transition-colors
                ${hoverable && onRowClick ? 'hover:bg-neutral-50 cursor-pointer' : ''}
                ${striped && rowIndex % 2 === 1 ? 'bg-neutral-50' : ''}
              `}
              onClick={() => onRowClick?.(row)}
            >
              {columns.map((column) => (
                <td
                  key={String(column.key)}
                  className={`px-4 py-3 text-sm text-neutral-900 ${column.className || ''}`}
                >
                  {column.render
                    ? column.render(row, rowIndex)
                    : String(row[column.key as keyof T] ?? '')}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  totalElements: number;
  pageSize: number;
  onPageChange: (page: number) => void;
  onPageSizeChange?: (size: number) => void;
  pageSizeOptions?: number[];
  showPageSizeSelector?: boolean;
}

export function Pagination({
  currentPage,
  totalPages,
  totalElements,
  pageSize,
  onPageChange,
  onPageSizeChange,
  pageSizeOptions = [10, 20, 50, 100],
  showPageSizeSelector = true,
}: PaginationProps) {
  const start = currentPage * pageSize + 1;
  const end = Math.min((currentPage + 1) * pageSize, totalElements);

  if (totalPages <= 1 && !showPageSizeSelector) return null;

  const pages = [];
  const maxVisiblePages = 5;
  let startPage = Math.max(0, currentPage - Math.floor(maxVisiblePages / 2));
  let endPage = Math.min(totalPages - 1, startPage + maxVisiblePages - 1);

  if (endPage - startPage + 1 < maxVisiblePages) {
    startPage = Math.max(0, endPage - maxVisiblePages + 1);
  }

  for (let i = startPage; i <= endPage; i++) {
    pages.push(i);
  }

  return (
    <div className="flex flex-col sm:flex-row items-center justify-between gap-4 px-4 py-3 border-t border-neutral-200">
      <div className="text-sm text-neutral-500">
        Showing {start} to {end} of {totalElements} results
      </div>

      <div className="flex items-center gap-2">
        {showPageSizeSelector && onPageSizeChange && (
          <select
            value={pageSize}
            onChange={(e) => onPageSizeChange(Number(e.target.value))}
            className="px-3 py-1.5 text-sm border border-neutral-300 rounded-lg focus-ring bg-white"
            aria-label="Rows per page"
          >
            {pageSizeOptions.map((size) => (
              <option key={size} value={size}>
                {size} per page
              </option>
            ))}
          </select>
        )}

        <nav className="flex items-center gap-1" aria-label="Pagination">
          <button
            onClick={() => onPageChange(0)}
            disabled={currentPage === 0}
            className="p-2 rounded-lg text-neutral-500 hover:bg-neutral-100 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            aria-label="First page"
          >
            <ChevronUp className="w-5 h-5 rotate-90" />
          </button>
          <button
            onClick={() => onPageChange(currentPage - 1)}
            disabled={currentPage === 0}
            className="p-2 rounded-lg text-neutral-500 hover:bg-neutral-100 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            aria-label="Previous page"
          >
            <ChevronUp className="w-5 h-5" />
          </button>

          {startPage > 0 && (
            <>
              <button
                onClick={() => onPageChange(0)}
                className="px-3 py-1.5 rounded-lg text-sm font-medium text-neutral-700 hover:bg-neutral-100 transition-colors"
              >
                1
              </button>
              {startPage > 1 && (
                <span className="px-2 text-neutral-400">...</span>
              )}
            </>
          )}

          {pages.map((page) => (
            <button
              key={page}
              onClick={() => onPageChange(page)}
              className={`px-3 py-1.5 rounded-lg text-sm font-medium transition-colors ${
                page === currentPage
                  ? 'bg-primary-600 text-white'
                  : 'text-neutral-700 hover:bg-neutral-100'
              }`}
              aria-current={page === currentPage ? 'page' : undefined}
            >
              {page + 1}
            </button>
          ))}

          {endPage < totalPages - 1 && (
            <>
              {endPage < totalPages - 2 && (
                <span className="px-2 text-neutral-400">...</span>
              )}
              <button
                onClick={() => onPageChange(totalPages - 1)}
                className="px-3 py-1.5 rounded-lg text-sm font-medium text-neutral-700 hover:bg-neutral-100 transition-colors"
              >
                {totalPages}
              </button>
            </>
          )}

          <button
            onClick={() => onPageChange(currentPage + 1)}
            disabled={currentPage === totalPages - 1}
            className="p-2 rounded-lg text-neutral-500 hover:bg-neutral-100 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            aria-label="Next page"
          >
            <ChevronDown className="w-5 h-5" />
          </button>
          <button
            onClick={() => onPageChange(totalPages - 1)}
            disabled={currentPage === totalPages - 1}
            className="p-2 rounded-lg text-neutral-500 hover:bg-neutral-100 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            aria-label="Last page"
          >
            <ChevronDown className="w-5 h-5 rotate-90" />
          </button>
        </nav>
      </div>
    </div>
  );
}