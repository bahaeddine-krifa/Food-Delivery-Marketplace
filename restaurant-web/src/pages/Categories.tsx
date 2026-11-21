import { useState, useEffect, useCallback } from 'react';
import { Plus, RefreshCw, Edit, Trash2, GripVertical } from 'lucide-react';
import { useRestaurants } from '@/hooks/useRestaurant';
import { restaurantApi } from '@/lib/restaurantApi';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/Card';
import { Table, type Column } from '@/components/ui/Table';
import { Button } from '@/components/ui/Button';
import { Input, Textarea } from '@/components/ui/Input';
import { Badge } from '@/components/ui/Badge';
import { Modal } from '@/components/ui/Modal';
import type { Category, CreateCategoryRequest, UpdateCategoryRequest } from '@/types/api';

export function Categories() {
  const { restaurants } = useRestaurants();
  const [selectedRestaurantId, setSelectedRestaurantId] = useState<string>('');
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [search, setSearch] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [editingCategory, setEditingCategory] = useState<Category | null>(null);
  const [formData, setFormData] = useState({ name: '', description: '', imageUrl: '', displayOrder: 0, isActive: true });
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});
  const [submitting, setSubmitting] = useState(false);
  const [deleteConfirm, setDeleteConfirm] = useState<{ id: string; name: string } | null>(null);
  const [reordering, setReordering] = useState(false);

  // Auto-select first restaurant
  useEffect(() => {
    if (restaurants.length > 0 && !selectedRestaurantId) {
      setSelectedRestaurantId(restaurants[0].id);
    }
  }, [restaurants, selectedRestaurantId]);

  const fetchCategories = useCallback(async () => {
    if (!selectedRestaurantId) return;
    setLoading(true);
    setError(null);
    try {
      const response = await restaurantApi.listAllCategories(selectedRestaurantId);
      // Sort by displayOrder
      const sorted = [...response].sort((a, b) => a.displayOrder - b.displayOrder);
      setCategories(sorted);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to load categories';
      setError(message);
    } finally {
      setLoading(false);
    }
  }, [selectedRestaurantId]);

  useEffect(() => {
    fetchCategories();
  }, [fetchCategories]);

  const openCreateModal = () => {
    setEditingCategory(null);
    const maxOrder = Math.max(0, ...categories.map(c => c.displayOrder));
    setFormData({ name: '', description: '', imageUrl: '', displayOrder: maxOrder + 1, isActive: true });
    setFormErrors({});
    setModalOpen(true);
  };

  const openEditModal = (category: Category) => {
    setEditingCategory(category);
    setFormData({
      name: category.name,
      description: category.description || '',
      imageUrl: category.imageUrl || '',
      displayOrder: category.displayOrder,
      isActive: category.isActive,
    });
    setFormErrors({});
    setModalOpen(true);
  };

  const closeModal = () => {
    setModalOpen(false);
    setEditingCategory(null);
    setFormData({ name: '', description: '', imageUrl: '', displayOrder: 0, isActive: true });
    setFormErrors({});
  };

  const validateForm = () => {
    const errors: Record<string, string> = {};
    if (!formData.name?.trim()) errors.name = 'Name is required';
    if (formData.name?.length > 100) errors.name = 'Name must be 100 characters or less';
    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async () => {
    if (!validateForm() || !selectedRestaurantId) return;
    setSubmitting(true);
    try {
      const createData: CreateCategoryRequest = {
        name: formData.name,
        description: formData.description || undefined,
        imageUrl: formData.imageUrl || undefined,
        displayOrder: formData.displayOrder,
      };

      const updateData: UpdateCategoryRequest = {
        ...createData,
        isActive: formData.isActive,
      };

      if (editingCategory) {
        await restaurantApi.updateCategory(selectedRestaurantId, editingCategory.id, updateData);
      } else {
        await restaurantApi.createCategory(selectedRestaurantId, createData);
      }
      closeModal();
      fetchCategories();
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to save category';
      setFormErrors({ submit: message });
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteClick = (category: Category) => {
    setDeleteConfirm({ id: category.id, name: category.name });
  };

  const handleConfirmDelete = async () => {
    if (!deleteConfirm || !selectedRestaurantId) return;
    try {
      await restaurantApi.deleteCategory(selectedRestaurantId, deleteConfirm.id);
      setDeleteConfirm(null);
      fetchCategories();
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to delete category';
      setError(message);
      setDeleteConfirm(null);
    }
  };

  const handleReorder = async () => {
    setReordering(true);
    try {
      const categoryIds = categories.map(c => c.id);
      await restaurantApi.reorderCategories(selectedRestaurantId, { categoryIds });
      fetchCategories();
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to reorder categories';
      setError(message);
    } finally {
      setReordering(false);
    }
  };

  const categoryColumns: Column<Category>[] = [
    { key: 'displayOrder', header: '#', render: (row) => <span className="font-medium text-neutral-900">{row.displayOrder}</span> },
    { key: 'imageUrl', header: '', render: (row) => (
      <div className="w-10 h-10 rounded-lg bg-neutral-100 flex items-center justify-center overflow-hidden">
        {row.imageUrl ? (
          <img src={row.imageUrl} alt={row.name} className="w-full h-full object-cover" />
        ) : (
          <div className="w-6 h-6 bg-neutral-200 rounded" />
        )}
      </div>
    )},
    { key: 'name', header: 'Category', render: (row) => (
      <div>
        <p className="font-medium text-neutral-900">{row.name}</p>
        {row.description && <p className="text-xs text-neutral-500 line-clamp-1">{row.description}</p>}
      </div>
    )},
    { key: 'isActive', header: 'Status', render: (row) => (
      <Badge variant={row.isActive ? 'success' : 'neutral'}>
        {row.isActive ? 'Active' : 'Inactive'}
      </Badge>
    )},
    { key: 'createdAt', header: 'Created', render: (row) => (
      <span className="text-sm text-neutral-500">{new Date(row.createdAt).toLocaleDateString()}</span>
    )},
    {
      key: 'actions',
      header: 'Actions',
      render: (row) => (
        <div className="flex items-center gap-1">
          <Button variant="ghost" size="sm" onClick={() => openEditModal(row)} disabled={loading}>
            <Edit className="w-4 h-4" />
          </Button>
          <Button variant="ghost" size="sm" onClick={() => handleDeleteClick(row)} disabled={loading} className="text-red-600 hover:text-red-700">
            <Trash2 className="w-4 h-4" />
          </Button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-neutral-900">Categories</h1>
          <p className="text-neutral-500 mt-1">Organize your menu with categories</p>
        </div>
        <div className="flex items-center gap-4 flex-wrap">
          <select
            value={selectedRestaurantId}
            onChange={(e) => setSelectedRestaurantId(e.target.value)}
            className="px-4 py-2 border border-neutral-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 bg-white"
            disabled={restaurants.length <= 1}
          >
            {restaurants.map((restaurant) => (
              <option key={restaurant.id} value={restaurant.id}>
                {restaurant.name}
              </option>
            ))}
          </select>
          <Button variant="secondary" onClick={fetchCategories} loading={loading} size="sm">
            <RefreshCw className="w-4 h-4 mr-1" /> Refresh
          </Button>
          <Button onClick={openCreateModal} size="sm">
            <Plus className="w-4 h-4 mr-1" /> Add Category
          </Button>
        </div>
      </div>

      {/* Search */}
      <Card className="p-4">
        <div className="flex gap-4">
          <div className="flex-1 max-w-md">
            <Input
              placeholder="Search categories..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full"
            />
          </div>
        </div>
      </Card>

      {/* Categories Table */}
      <Card>
        <CardHeader className="flex flex-row items-center justify-between">
          <CardTitle>All Categories</CardTitle>
          <div className="flex items-center gap-2">
            <span className="text-sm text-neutral-500">Drag to reorder</span>
            <GripVertical className="w-5 h-5 text-neutral-400 cursor-grab" />
          </div>
        </CardHeader>
        <CardContent className="p-0">
          {error && (
            <div className="p-4 bg-red-50 border-b border-red-200 text-red-700">
              {error}
            </div>
          )}
          <Table
            columns={categoryColumns}
            data={categories.filter(c => c.name.toLowerCase().includes(search.toLowerCase()))}
            keyExtractor={(row) => row.id}
            isLoading={loading}
            emptyMessage="No categories found. Click 'Add Category' to create your first category."
          />
        </CardContent>
      </Card>

      {/* Reorder Button */}
      {categories.length > 1 && (
        <div className="flex justify-end">
          <Button variant="secondary" onClick={handleReorder} loading={reordering} size="sm">
            Save Order
          </Button>
        </div>
      )}

      {/* Category Modal */}
      <Modal
        isOpen={modalOpen}
        onClose={closeModal}
        title={editingCategory ? 'Edit Category' : 'Add Category'}
        size="md"
      >
        <div className="space-y-4">
          <Input
            label="Name *"
            value={formData.name}
            onChange={(e) => setFormData(prev => ({ ...prev, name: e.target.value }))}
            error={formErrors.name}
            placeholder="Category name"
            maxLength={100}
          />
          <Textarea
            label="Description"
            value={formData.description}
            onChange={(e) => setFormData(prev => ({ ...prev, description: e.target.value }))}
            placeholder="Category description"
            rows={3}
          />
          <Input
            label="Image URL"
            value={formData.imageUrl}
            onChange={(e) => setFormData(prev => ({ ...prev, imageUrl: e.target.value }))}
            placeholder="https://example.com/image.jpg"
          />
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Display Order"
              type="number"
              min="0"
              value={formData.displayOrder}
              onChange={(e) => setFormData(prev => ({ ...prev, displayOrder: parseInt(e.target.value) || 0 }))}
            />
            <label className="flex items-center gap-2 cursor-pointer">
              <input
                type="checkbox"
                checked={formData.isActive}
                onChange={(e) => setFormData(prev => ({ ...prev, isActive: e.target.checked }))}
                className="w-4 h-4 rounded border-neutral-300 text-primary-600 focus:ring-primary-500"
              />
              <span className="text-sm text-neutral-700">Active</span>
            </label>
          </div>

          {formErrors.submit && (
            <div className="p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">
              {formErrors.submit}
            </div>
          )}
        </div>
        <div className="flex justify-end gap-3 pt-4 border-t border-neutral-200">
          <Button variant="secondary" onClick={closeModal} disabled={submitting}>Cancel</Button>
          <Button onClick={handleSubmit} loading={submitting}>
            {editingCategory ? 'Save Changes' : 'Create Category'}
          </Button>
        </div>
      </Modal>

      {/* Delete Confirmation */}
      <Modal
        isOpen={!!deleteConfirm}
        onClose={() => setDeleteConfirm(null)}
        title="Delete Category"
        size="sm"
      >
        <p className="text-neutral-600">
          Are you sure you want to delete <strong>{deleteConfirm?.name}</strong>?
          This will archive the category and remove it from the menu.
        </p>
        <div className="flex justify-end gap-3 pt-4">
          <Button variant="secondary" onClick={() => setDeleteConfirm(null)}>Cancel</Button>
          <Button variant="danger" onClick={handleConfirmDelete}>Delete</Button>
        </div>
      </Modal>
    </div>
  );
}