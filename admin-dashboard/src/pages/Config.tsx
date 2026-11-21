import { useState, useEffect } from 'react';
import { Settings, Save, RefreshCw, Eye, EyeOff, AlertCircle } from 'lucide-react';
import { api } from '@/lib/api';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/Card';
import { Badge } from '@/components/ui/Badge';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import type { SystemConfigResponse, UpdateConfigRequest } from '@/types/api';

export function Config() {
  const [configs, setConfigs] = useState<SystemConfigResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState<string | null>(null);
  const [editValues, setEditValues] = useState<Record<string, string>>({});
  const [showValues, setShowValues] = useState<Record<string, boolean>>({});

  const fetchConfigs = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.getSystemConfig();
      setConfigs(data);
      // Initialize edit values
      const initialValues: Record<string, string> = {};
      data.forEach((config) => {
        initialValues[config.key] = config.value;
      });
      setEditValues(initialValues);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to load configuration';
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchConfigs();
  }, []);

  const handleValueChange = (key: string, value: string) => {
    setEditValues((prev) => ({ ...prev, [key]: value }));
  };

  const handleSave = async (key: string) => {
    const value = editValues[key];
    if (value === undefined) return;

    setSaving(key);
    setError(null);
    try {
      const request: UpdateConfigRequest = { value };
      await api.updateSystemConfig(key, request);
      // Update local state with saved value
      setConfigs((prev) =>
        prev.map((config) => (config.key === key ? { ...config, value } : config))
      );
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to update configuration';
      setError(message);
      // Revert to original value
      const original = configs.find((c) => c.key === key)?.value || '';
      setEditValues((prev) => ({ ...prev, [key]: original }));
    } finally {
      setSaving(null);
    }
  };

  const handleReset = (key: string) => {
    const original = configs.find((c) => c.key === key)?.value || '';
    setEditValues((prev) => ({ ...prev, [key]: original }));
  };

  const toggleVisibility = (key: string) => {
    setShowValues((prev) => ({ ...prev, [key]: !prev[key] }));
  };

  if (loading) {
    return (
      <div className="space-y-6 animate-pulse">
        <div>
          <div className="h-8 bg-neutral-200 rounded w-1/4" />
          <div className="h-4 bg-neutral-200 rounded w-1/2 mt-1" />
        </div>
        <div className="space-y-4">
          {[...Array(5)].map((_, i) => (
            <div key={i} className="bg-white rounded-xl border border-neutral-200 p-6">
              <div className="flex items-center justify-between">
                <div className="h-4 bg-neutral-200 rounded w-1/3" />
                <div className="h-8 bg-neutral-200 rounded w-20" />
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center py-12">
        <AlertCircle className="mx-auto h-12 w-12 text-red-500" />
        <p className="mt-4 text-neutral-600">{error}</p>
        <Button onClick={fetchConfigs} className="mt-4">
          <RefreshCw className="w-4 h-4 mr-2" /> Retry
        </Button>
      </div>
    );
  }

  // Group configs by category
  const groupedConfigs = configs.reduce((acc, config) => {
    const category = config.key.split('.')[0] || 'general';
    if (!acc[category]) acc[category] = [];
    acc[category].push(config);
    return acc;
  }, {} as Record<string, SystemConfigResponse[]>);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-neutral-900">System Configuration</h1>
          <p className="text-neutral-500 mt-1">Manage platform-wide configuration settings</p>
        </div>
        <Button variant="ghost" onClick={fetchConfigs} disabled={loading}>
          <RefreshCw className="w-4 h-4 mr-2" /> Refresh
        </Button>
      </div>

      {Object.entries(groupedConfigs).map(([category, categoryConfigs]) => (
        <Card key={category}>
          <CardHeader>
            <div className="flex items-center gap-2">
              <Settings className="w-5 h-5 text-primary-600" />
              <CardTitle>{category.charAt(0).toUpperCase() + category.slice(1)}</CardTitle>
            </div>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {categoryConfigs.map((config) => (
                <div
                  key={config.key}
                  className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 p-4 bg-neutral-50 rounded-lg"
                >
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-3">
                      <span className="font-mono text-sm font-medium text-neutral-900">{config.key}</span>
                      {config.isEditable ? (
                        <Badge variant="info">Editable</Badge>
                      ) : (
                        <Badge variant="neutral">Read-only</Badge>
                      )}
                    </div>
                    <p className="text-sm text-neutral-500 mt-1">{config.description}</p>
                  </div>
                  <div className="flex items-center gap-3 sm:flex-1 sm:max-w-md">
                    <div className="relative flex-1">
                      <Input
                        type={showValues[config.key] ? 'text' : 'password'}
                        value={editValues[config.key] || config.value}
                        onChange={(e) => handleValueChange(config.key, e.target.value)}
                        disabled={!config.isEditable || saving === config.key}
                        className="font-mono text-sm pr-10"
                      />
                      <button
                        type="button"
                        onClick={() => toggleVisibility(config.key)}
                        className="absolute right-3 top-1/2 -translate-y-1/2 text-neutral-400 hover:text-neutral-600"
                        aria-label={showValues[config.key] ? 'Hide value' : 'Show value'}
                      >
                        {showValues[config.key] ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                      </button>
                    </div>
                    {config.isEditable && (
                      <div className="flex items-center gap-2">
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleReset(config.key)}
                          disabled={saving === config.key}
                          aria-label="Reset to default"
                        >
                          <RefreshCw className="w-4 h-4" />
                        </Button>
                        <Button
                          size="sm"
                          onClick={() => handleSave(config.key)}
                          loading={saving === config.key}
                          disabled={!config.isEditable}
                        >
                          <Save className="w-4 h-4 mr-1" /> Save
                        </Button>
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      ))}

      {configs.length === 0 && (
        <Card>
          <CardContent className="py-12 text-center">
            <Settings className="mx-auto h-12 w-12 text-neutral-300 mb-4" />
            <p className="text-neutral-500">No configuration settings found</p>
          </CardContent>
        </Card>
      )}
    </div>
  );
}