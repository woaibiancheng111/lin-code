<template>
  <div class="products-page fade-in-up">
    <div class="page-header">
      <h2>Products Management</h2>
      <button class="btn btn-primary" @click="openModal()"><Plus size="16" /> Add Product</button>
    </div>

    <div class="glass-panel table-container">
      <table class="custom-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Image</th>
            <th>Name</th>
            <th>Price</th>
            <th>Stock</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="7" class="text-center py-4">Loading products...</td>
          </tr>
          <tr v-else-if="products.length === 0">
            <td colspan="7" class="text-center py-4">No products found.</td>
          </tr>
          <tr v-for="product in products" :key="product.id">
            <td>#{{ product.id }}</td>
            <td>
              <div class="product-img">
                <img :src="product.image || 'https://via.placeholder.com/40'" alt="product" />
              </div>
            </td>
            <td>{{ product.name }}</td>
            <td class="price">¥{{ product.price.toFixed(2) }}</td>
            <td>{{ product.stock }}</td>
            <td>
              <span class="badge" :class="product.status === 1 ? 'badge-success' : 'badge-neutral'">
                {{ product.status === 1 ? 'Active' : 'Inactive' }}
              </span>
            </td>
            <td>
              <div class="action-btns">
                <button class="action-btn edit" @click="openModal(product)"><Edit2 size="16" /></button>
                <button class="action-btn delete" @click="deleteProduct(product.id)"><Trash2 size="16" /></button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal overlay simple implementation -->
    <div v-if="isModalOpen" class="modal-overlay">
      <div class="modal-content glass-panel">
        <h3>{{ currentProduct.id ? 'Edit Product' : 'Add Product' }}</h3>
        
        <form @submit.prevent="saveProduct" class="mt-4">
          <div class="input-group">
            <label>Name</label>
            <input v-model="currentProduct.name" type="text" class="input-field" required />
          </div>
          <div class="input-group">
            <label>Price</label>
            <input v-model="currentProduct.price" type="number" step="0.01" class="input-field" required />
          </div>
          <div class="input-group">
            <label>Stock</label>
            <input v-model="currentProduct.stock" type="number" class="input-field" required />
          </div>
          <div class="input-group">
            <label>Image URL</label>
            <input v-model="currentProduct.image" type="text" class="input-field" />
          </div>
          
          <div class="modal-actions">
            <button type="button" class="btn btn-secondary" @click="isModalOpen = false">Cancel</button>
            <button type="submit" class="btn btn-primary">Save Product</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { Plus, Edit2, Trash2 } from 'lucide-vue-next';
import api from '../api';

const products = ref([]);
const loading = ref(false);

const isModalOpen = ref(false);
const currentProduct = ref({});

const fetchProducts = async () => {
  loading.value = true;
  try {
    const res = await api.get('/api/products', { params: { current: 1, size: 50 } });
    if (res.code === 200) {
      // Depending on actual response structure, assuming res.data.records or res.data
      products.value = res.data.records || res.data || [];
    }
  } catch (error) {
    console.error('Failed to fetch products', error);
  }
  loading.value = false;
};

const openModal = (product = null) => {
  currentProduct.value = product ? { ...product } : { name: '', price: 0, stock: 0, status: 1, image: '' };
  isModalOpen.value = true;
};

const saveProduct = async () => {
  try {
    if (currentProduct.value.id) {
      await api.put(`/api/products/${currentProduct.value.id}`, currentProduct.value);
    } else {
      await api.post('/api/products', currentProduct.value);
    }
    isModalOpen.value = false;
    fetchProducts();
  } catch (error) {
    console.error('Save failed', error);
  }
};

const deleteProduct = async (id) => {
  if (confirm('Are you sure you want to delete this product?')) {
    try {
      await api.delete(`/api/products/${id}`);
      fetchProducts();
    } catch (error) {
      console.error('Delete failed', error);
    }
  }
};

onMounted(() => {
  fetchProducts();
});
</script>

<style scoped>
.products-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-header h2 {
  font-size: 1.5rem;
  font-weight: 600;
}

.fade-in-up {
  animation: fadeInUp 0.4s ease-out;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.product-img img {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  object-fit: cover;
}

.price {
  font-weight: 600;
  color: var(--success-color);
}

.badge {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
}

.badge-success {
  background: rgba(16, 185, 129, 0.2);
  color: #34d399;
}

.badge-neutral {
  background: rgba(148, 163, 184, 0.2);
  color: #94a3b8;
}

.action-btns {
  display: flex;
  gap: 8px;
}

.action-btn {
  background: rgba(255,255,255,0.05);
  border: none;
  color: var(--text-secondary);
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  transform: translateY(-2px);
}

.action-btn.edit:hover {
  background: rgba(59, 130, 246, 0.2);
  color: #60a5fa;
}

.action-btn.delete:hover {
  background: rgba(239, 68, 68, 0.2);
  color: #f87171;
}

.text-center {
  text-align: center;
}

.py-4 {
  padding-top: 1rem;
  padding-bottom: 1rem;
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-content {
  width: 100%;
  max-width: 500px;
  padding: 32px;
  animation: scaleUp 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

@keyframes scaleUp {
  from { opacity: 0; transform: scale(0.9); }
  to { opacity: 1; transform: scale(1); }
}

.modal-content h3 {
  font-size: 1.3rem;
  font-weight: 600;
  margin-bottom: 16px;
}

.mt-4 { margin-top: 1rem; }

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}
</style>
