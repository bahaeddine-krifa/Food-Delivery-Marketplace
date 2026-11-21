import { useState, useEffect, useCallback } from 'react';
import {
  Plus, RefreshCw,
  Edit, Trash2, ToggleLeft, ToggleRight,
  Package
} from 'lucide-react';
import { useRestaurants } from '@/hooks/useRestaurant';
import { restaurantApi } from '@/lib/restaurantApi';
import { Card, CardContent } from '@/components/ui/Card';
import { Table, type Column, Pagination } from '@/components/ui/Table';
import { Button } from '@/components/ui/Button';
import { Input, Textarea, Select } from '@/components/ui/Input';
import { Badge, ProductAvailabilityBadge } from '@/components/ui/Badge';
import { Modal } from '@/components/ui/Modal';
import type { Product, CreateProductRequest, UpdateProductRequest } from '@/types/api';

interface ProductFormData {
  name: string;
  description: string;
  price: string;
  categoryId: string;
  isAvailable: boolean;
  isVegetarian: boolean;
  isVegan: boolean;
  isGlutenFree: boolean;
  allergens: string;
  tags: string;
  preparationTimeMinutes: string;
  displayOrder: number;
}

const CATEGORY_OPTIONS: { value: string; label: string }[] = [
  { value: '', label: 'All Categories' },
];

export function Products() {
  const { restaurants } = useRestaurants();
  const [selectedRestaurantId, setSelectedRestaurantId] = useState<string>('');
  const [products, setProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<{ value: string; label: string }[]>(CATEGORY_OPTIONS);
  const [loading, setLoading] = useState(false);
  const [categoriesLoading, setCategoriesLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [pagination, setPagination] = useState({ page: 0, size: 20, totalPages: 1, totalElements: 0 });
  const [filters, setFilters] = useState({ categoryId: '', availableOnly: true, search: '' });
  const [modalOpen, setModalOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);
  const [formData, setFormData] = useState<ProductFormData>({
    name: '',
    description: '',
    price: '',
    categoryId: '',
    isAvailable: true,
    isVegetarian: false,
    isVegan: false,
    isGlutenFree: false,
    allergens: '',
    tags: '',
    preparationTimeMinutes: '',
    displayOrder: 0,
  });
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});
  const [submitting, setSubmitting] = useState(false);
  const [deleteConfirm, setDeleteConfirm] = useState<{ id: string; name: string } | null>(null);

  // Auto-select first restaurant
  useEffect(() => {
    if (restaurants.length > 0 && !selectedRestaurantId) {
      setSelectedRestaurantId(restaurants[0].id);
    }
  }, [restaurants, selectedRestaurantId]);

  // Fetch categories for filter dropdown
  useEffect(() => {
    if (!selectedRestaurantId) return;
    const fetchCategories = async () => {
      setCategoriesLoading(true);
      try {
        // We'll use the list all categories endpoint
        const response = await restaurantApi.listCategories(selectedRestaurantId, { page: 0, size: 100 });
        const cats = response.content.map(c => ({ value: c.id, label: c.name }));
        setCategories([CATEGORY_OPTIONS[0], ...cats]);
      } catch (err: unknown) {
        console.error('Failed to load categories:', err);
      } finally {
        setCategoriesLoading(false);
      }
    };
    fetchCategories();
  }, [selectedRestaurantId]);

  const fetchProducts = useCallback(async () => {
    if (!selectedRestaurantId) return;
    setLoading(true);
    setError(null);
    try {
      const params = {
        page: pagination.page,
        size: pagination.size,
        categoryId: filters.categoryId || undefined,
        availableOnly: filters.availableOnly,
        search: filters.search || undefined,
      };
      const response = await restaurantApi.listProducts(selectedRestaurantId, params);
      setProducts(response.content);
      setPagination(prev => ({
        ...prev,
        totalPages: response.totalPages,
        totalElements: response.totalElements,
      }));
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to load products';
      setError(message);
    } finally {
      setLoading(false);
    }
  }, [selectedRestaurantId, pagination.page, pagination.size, filters]);

  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

  const handlePageChange = (page: number) => {
    setPagination(prev => ({ ...prev, page }));
  };

  const handlePageSizeChange = (size: number) => {
    setPagination(prev => ({ ...prev, size, page: 0 }));
  };

  const handleFilterChange = (key: string, value: unknown) => {
    setFilters(prev => ({ ...prev, [key]: value }));
    setPagination(prev => ({ ...prev, page: 0 }));
  };

  const openCreateModal = () => {
    setEditingProduct(null);
    setFormData({
      name: '',
      description: '',
      price: '',
      categoryId: '',
      isAvailable: true,
      isVegetarian: false,
      isVegan: false,
      isGlutenFree: false,
      allergens: '',
      tags: '',
      preparationTimeMinutes: '',
      displayOrder: 0,
    });
    setFormErrors({});
    setModalOpen(true);
  };

  const openEditModal = (product: Product) => {
    setEditingProduct(product);
    setFormData({
      name: product.name,
      description: product.description || '',
      price: product.price,
      categoryId: product.categoryId || '',
      isAvailable: product.isAvailable,
      isVegetarian: product.isVegetarian,
      isVegan: product.isVegan,
      isGlutenFree: product.isGlutenFree,
      allergens: product.allergens || '',
      tags: product.tags?.join(', ') || '',
      preparationTimeMinutes: product.preparationTimeMinutes?.toString() || '',
      displayOrder: product.displayOrder,
    });
    setFormErrors({});
    setModalOpen(true);
  };

  const closeModal = () => {
    setModalOpen(false);
    setEditingProduct(null);
    setFormData({
      name: '',
      description: '',
      price: '',
      categoryId: '',
      isAvailable: true,
      isVegetarian: false,
      isVegan: false,
      isGlutenFree: false,
      allergens: '',
      tags: '',
      preparationTimeMinutes: '',
      displayOrder: 0,
    });
    setFormErrors({});
  };

  const validateForm = () => {
    const errors: Record<string, string> = {};
    if (!formData.name?.trim()) errors.name = 'Name is required';
    if (!formData.price || parseFloat(formData.price) <= 0) errors.price = 'Valid price is required';
    if (!formData.categoryId) errors.categoryId = 'Category is required';
    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async () => {
    if (!validateForm() || !selectedRestaurantId) return;
    setSubmitting(true);
    try {
      const data: CreateProductRequest = {
        name: formData.name,
        description: formData.description || undefined,
        price: formData.price,
        categoryId: formData.categoryId,
        isAvailable: formData.isAvailable,
        isVegetarian: formData.isVegetarian,
        isVegan: formData.isVegan,
        isGlutenFree: formData.isGlutenFree,
        allergens: formData.allergens || undefined,
        tags: formData.tags?.split(',').map(t => t.trim()).filter(Boolean) || undefined,
        preparationTimeMinutes: formData.preparationTimeMinutes ? parseInt(formData.preparationTimeMinutes) : undefined,
        displayOrder: formData.displayOrder,
      };

      if (editingProduct) {
        await restaurantApi.updateProduct(selectedRestaurantId, editingProduct.id, data as UpdateProductRequest);
      } else {
        await restaurantApi.createProduct(selectedRestaurantId, data);
      }
      closeModal();
      fetchProducts();
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to save product';
      setFormErrors({ submit: message });
    } finally {
      setSubmitting(false);
    }
  };

  const handleToggleAvailability = async (product: Product) => {
    try {
      await restaurantApi.toggleAvailability(selectedRestaurantId, product.id);
      fetchProducts();
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to toggle availability';
      setError(message);
    }
  };

  const handleDeleteClick = (product: Product) => {
    setDeleteConfirm({ id: product.id, name: product.name });
  };

  const handleConfirmDelete = async () => {
    if (!deleteConfirm || !selectedRestaurantId) return;
    try {
      await restaurantApi.deleteProduct(selectedRestaurantId, deleteConfirm.id);
      setDeleteConfirm(null);
      fetchProducts();
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to delete product';
      setError(message);
      setDeleteConfirm(null);
    }
  };

  const productColumns: Column<Product>[] = [
    { key: 'imageUrl', header: '', render: (row) => (
      <div className="w-12 h-12 rounded-lg bg-neutral-100 flex items-center justify-center overflow-hidden">
        {row.imageUrl ? (
          <img src={row.imageUrl} alt={row.name} className="w-full h-full object-cover" />
        ) : (
          <Package className="w-6 h-6 text-neutral-400" />
        )}
      </div>
    )},
    { key: 'name', header: 'Product', render: (row) => (
      <div>
        <p className="font-medium text-neutral-900">{row.name}</p>
        {row.description && <p className="text-xs text-neutral-500 line-clamp-1">{row.description}</p>}
      </div>
    )},
    { key: 'categoryId', header: 'Category', render: (row) => (
      <span className="text-sm text-neutral-600">{categories.find(c => c.value === row.categoryId)?.label || 'Uncategorized'}</span>
    )},
    { key: 'price', header: 'Price', render: (row) => <span className="font-medium">${row.price}</span> },
    { key: 'isAvailable', header: 'Status', render: (row) => <ProductAvailabilityBadge isAvailable={row.isAvailable} /> },
    { key: 'tags', header: 'Dietary', render: (row) => (
      <div className="flex gap-1 flex-wrap">
        {row.isVegetarian && <Badge variant="info" className="text-xs">Veg</Badge>}
        {row.isVegan && <Badge variant="success" className="text-xs">Vegan</Badge>}
        {row.isGlutenFree && <Badge variant="warning" className="text-xs">GF</Badge>}
      </div>
    )},
    { key: 'displayOrder', header: 'Order', render: (row) => <span className="text-sm text-neutral-500">#{row.displayOrder}</span> },
    {
      key: 'actions',
      header: 'Actions',
      render: (row) => (
        <div className="flex items-center gap-1">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => handleToggleAvailability(row)}
            disabled={loading}
            aria-label={row.isAvailable ? 'Mark unavailable' : 'Mark available'}
          >
            {row.isAvailable ? <ToggleRight className="w-4 h-4 text-green-600" /> : <ToggleLeft className="w-4 h-4 text-neutral-400" />}
          </Button>
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
          <h1 className="text-2xl font-bold text-neutral-900">Products</h1>
          <p className="text-neutral-500 mt-1">Manage your restaurant menu items</p>
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
          <Button variant="secondary" onClick={fetchProducts} loading={loading} size="sm">
            <RefreshCw className="w-4 h-4 mr-1" /> Refresh
          </Button>
          <Button onClick={openCreateModal} size="sm">
            <Plus className="w-4 h-4 mr-1" /> Add Product
          </Button>
        </div>
      </div>

      {/* Filters */}
      <Card className="p-4">
        <div className="flex flex-col sm:flex-row gap-4">
          <div className="flex-1 max-w-xs">
            <Input
              placeholder="Search products..."
              value={filters.search}
              onChange={(e) => handleFilterChange('search', e.target.value)}
              className="w-full"
            />
          </div>
          <Select
            value={filters.categoryId}
            onChange={(e) => handleFilterChange('categoryId', e.target.value)}
            options={categories}
            className="w-full max-w-xs"
            disabled={categoriesLoading}
          />
          <label className="flex items-center gap-2 cursor-pointer self-start">
            <input
              type="checkbox"
              checked={filters.availableOnly}
              onChange={(e) => handleFilterChange('availableOnly', e.target.checked)}
              className="w-4 h-4 rounded border-neutral-300 text-primary-600 focus:ring-primary-500"
            />
            <span className="text-sm text-neutral-600">Available only</span>
          </label>
        </div>
      </Card>

      {/* Products Table */}
      <Card>
        <CardContent className="p-0">
          {error && (
            <div className="p-4 bg-red-50 border-b border-red-200 text-red-700">
              {error}
            </div>
          )}
          <Table
            columns={productColumns}
            data={products}
            keyExtractor={(row) => row.id}
            isLoading={loading}
            emptyMessage="No products found. Click 'Add Product' to create your first item."
          />
          <Pagination
            currentPage={pagination.page}
            totalPages={pagination.totalPages}
            onPageChange={handlePageChange}
            showPageSize
            pageSize={pagination.size}
            onPageSizeChange={handlePageSizeChange}
            totalElements={pagination.totalElements}
          />
        </CardContent>
      </Card>

      {/* Product Modal */}
      <Modal
        isOpen={modalOpen}
        onClose={closeModal}
        title={editingProduct ? 'Edit Product' : 'Add Product'}
        size="lg"
      >
        <div className="space-y-4 max-h-[70vh] overflow-y-auto">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Input
              label="Name *"
              value={formData.name || ''}
              onChange={(e) => setFormData(prev => ({ ...prev, name: e.target.value }))}
              error={formErrors.name}
              placeholder="Product name"
            />
            <Select
              label="Category *"
              value={formData.categoryId || ''}
              onChange={(e) => setFormData(prev => ({ ...prev, categoryId: e.target.value }))}
              options={categories.filter(c => c.value)}
              placeholder="Select category"
              error={formErrors.categoryId}
            />
            <Input
              label="Price *"
              type="number"
              step="0.01"
              min="0"
              value={formData.price || ''}
              onChange={(e) => setFormData(prev => ({ ...prev, price: e.target.value }))}
              error={formErrors.price}
              placeholder="0.00"
            />
            <Input
              label="Prep Time (min)"
              type="number"
              min="0"
              value={formData.preparationTimeMinutes || ''}
              onChange={(e) => setFormData(prev => ({ ...prev, preparationTimeMinutes: e.target.value }))}
              placeholder="30"
            />
            <Input
              label="Display Order"
              type="number"
              min="0"
              value={formData.displayOrder?.toString() || ''}
              onChange={(e) => setFormData(prev => ({ ...prev, displayOrder: parseInt(e.target.value) || 0 }))}
              placeholder="0"
            />
          </div>

          <Textarea
            label="Description"
            value={formData.description || ''}
            onChange={(e) => setFormData(prev => ({ ...prev, description: e.target.value }))}
            placeholder="Product description"
            rows={3}
          />

          <Input
            label="Allergens"
            value={formData.allergens || ''}
            onChange={(e) => setFormData(prev => ({ ...prev, allergens: e.target.value }))}
            placeholder="e.g., nuts, dairy, gluten"
          />

          <Input
            label="Tags (comma-separated)"
            value={formData.tags || ''}
            onChange={(e) => setFormData(prev => ({ ...prev, tags: e.target.value }))}
            placeholder="spicy, popular, lunch"
          />

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <label className="flex items-center gap-2 cursor-pointer">
              <input
                type="checkbox"
                checked={formData.isAvailable}
                onChange={(e) => setFormData(prev => ({ ...prev, isAvailable: e.target.checked }))}
                className="w-4 h-4 rounded border-neutral-300 text-primary-600 focus:ring-primary-500"
              />
              <span className="text-sm text-neutral-700">Available</span>
            </label>
            <label className="flex items-center gap-2 cursor-pointer">
              <input
                type="checkbox"
                checked={formData.isVegetarian}
                onChange={(e) => setFormData(prev => ({ ...prev, isVegetarian: e.target.checked }))}
                className="w-4 h-4 rounded border-neutral-300 text-primary-600 focus:ring-primary-500"
              />
              <span className="text-sm text-neutral-700">Vegetarian</span>
            </label>
            <label className="flex items-center gap-2 cursor-pointer">
              <input
                type="checkbox"
                checked={formData.isVegan}
                onChange={(e) => setFormData(prev => ({ ...prev, isVegan: e.target.checked }))}
                className="w-4 h-4 rounded border-neutral-300 text-primary-600 focus:ring-primary-500"
              />
              <span className="text-sm text-neutral-700">Vegan</span>
            </label>
            <label className="flex items-center gap-2 cursor-pointer sm:col-span-3">
              <input
                type="checkbox"
                checked={formData.isGlutenFree}
                onChange={(e) => setFormData(prev => ({ ...prev, isGlutenFree: e.target.checked }))}
                className="w-4 h-4 rounded border-neutral-300 text-primary-600 focus:ring-primary-500"
              />
              <span className="text-sm text-neutral-700">Gluten Free</span>
            </label>
          </div>

          {formErrors.submit && (
            <div className="p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">
              {formErrors.submit}
            </div>
          )}
        </div>
        <div className="flex justify-end gap-3 pt-4 border-t border-neutral-200 mt-4">
          <Button variant="secondary" onClick={closeModal} disabled={submitting}>Cancel</Button>
          <Button onClick={handleSubmit} loading={submitting}>
            {editingProduct ? 'Save Changes' : 'Create Product'}
          </Button>
        </div>
      </Modal>

      {/* Delete Confirmation */}
      <Modal
        isOpen={!!deleteConfirm}
        onClose={() => setDeleteConfirm(null)}
        title="Delete Product"
        size="sm"
      >
        <p className="text-neutral-600">
          Are you sure you want to delete <strong>{deleteConfirm?.name}</strong>? This action cannot be undone.
        </p>
        <div className="flex justify-end gap-3 pt-4">
          <Button variant="secondary" onClick={() => setDeleteConfirm(null)}>Cancel</Button>
          <Button variant="danger" onClick={handleConfirmDelete}>Delete</Button>
        </div>
      </Modal>
    </div>
  );
}