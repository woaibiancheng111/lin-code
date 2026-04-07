<template>
  <div class="orders-page fade-in-up">
    <div class="page-header">
      <h2>Orders Management</h2>
      <div class="header-filters">
        <select class="input-field select-filter">
          <option value="">All Status</option>
          <option value="0">Pending Payment</option>
          <option value="1">Paid</option>
          <option value="2">Shipped</option>
          <option value="3">Completed</option>
          <option value="4">Cancelled</option>
        </select>
        <button class="btn btn-secondary" @click="fetchOrders"><RefreshCw size="16" /> Refresh</button>
      </div>
    </div>

    <div class="glass-panel table-container">
      <table class="custom-table">
        <thead>
          <tr>
            <th>Order No</th>
            <th>Customer Info</th>
            <th>Status</th>
            <th>Amount</th>
            <th>Time</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="6" class="text-center py-4">Loading orders...</td>
          </tr>
          <tr v-else-if="orders.length === 0">
            <td colspan="6" class="text-center py-4">No orders found.</td>
          </tr>
          <tr v-for="order in orders" :key="order.id">
            <td class="font-mono">#{{ order.id }}</td>
            <td>
              <div class="customer-info">
                <strong>{{ order.receiverName }}</strong>
                <span>{{ order.receiverPhone }}</span>
              </div>
            </td>
            <td>
              <span class="badge" :class="getStatusClass(order.status)">
                {{ getStatusText(order.status) }}
              </span>
            </td>
            <td class="price">¥{{ order.totalAmount?.toFixed(2) || '0.00' }}</td>
            <td class="time">{{ new Date(order.createTime).toLocaleString() }}</td>
            <td>
              <div class="action-btns">
                <button 
                  v-if="order.status === 1" 
                  class="btn btn-primary btn-sm" 
                  @click="deliverOrder(order.id)"
                >
                  <Truck size="14" /> Ship
                </button>
                <button class="btn btn-secondary btn-sm" @click="viewDetails(order)"><Eye size="14" /> View</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal for Order Details -->
    <div v-if="isModalOpen" class="modal-overlay">
      <div class="modal-content glass-panel">
        <div class="modal-header">
          <h3>Order Details #{{ currentOrder.id }}</h3>
          <button class="close-btn" @click="isModalOpen = false"><X size="20" /></button>
        </div>
        
        <div class="order-details mt-4">
          <div class="detail-section">
            <h4>Shipping Information</h4>
            <p><strong>Name:</strong> {{ currentOrder.receiverName }}</p>
            <p><strong>Phone:</strong> {{ currentOrder.receiverPhone }}</p>
            <p><strong>Address:</strong> {{ currentOrder.receiverAddress }}</p>
            <p><strong>Remark:</strong> {{ currentOrder.remark || 'N/A' }}</p>
          </div>
          
          <div class="detail-section mt-4">
            <h4>Order Items</h4>
            <ul class="items-list">
              <li v-for="item in currentOrder.items" :key="item.id" class="item-row">
                <div class="item-info">
                  <span class="item-name">{{ item.productName }}</span>
                  <span class="item-qty">x{{ item.productQuantity }}</span>
                </div>
                <span class="item-price">¥{{ (item.productPrice * item.productQuantity).toFixed(2) }}</span>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { RefreshCw, Truck, Eye, X } from 'lucide-vue-next';
import api from '../api';

const orders = ref([]);
const loading = ref(false);
const isModalOpen = ref(false);
const currentOrder = ref({});

const fetchOrders = async () => {
  loading.value = true;
  try {
    const res = await api.get('/api/orders/admin', { params: { current: 1, size: 50 } });
    if (res.code === 200) {
      orders.value = res.data.records || res.data || [];
    }
  } catch (error) {
    console.error('Failed to fetch orders', error);
  }
  loading.value = false;
};

const deliverOrder = async (id) => {
  if (confirm('Mark this order as shipped?')) {
    try {
      await api.post(`/api/orders/${id}/delivery`);
      fetchOrders();
    } catch (error) {
      console.error('Delivery failed', error);
    }
  }
};

const viewDetails = (order) => {
  currentOrder.value = order;
  isModalOpen.value = true;
};

const getStatusText = (status) => {
  const map = {
    0: 'Pending',
    1: 'Paid',
    2: 'Shipped',
    3: 'Completed',
    4: 'Cancelled'
  };
  return map[status] || 'Unknown';
};

const getStatusClass = (status) => {
  const map = {
    0: 'badge-warning',
    1: 'badge-info',
    2: 'badge-primary',
    3: 'badge-success',
    4: 'badge-neutral'
  };
  return map[status] || 'badge-neutral';
};

onMounted(() => {
  fetchOrders();
});
</script>

<style scoped>
.orders-page {
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

.header-filters {
  display: flex;
  gap: 12px;
}

.select-filter {
  width: 150px;
  padding: 8px 12px;
}

.fade-in-up {
  animation: fadeInUp 0.4s ease-out;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.customer-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.customer-info strong {
  font-size: 0.95rem;
}

.customer-info span {
  font-size: 0.8rem;
  color: var(--text-secondary);
}

.font-mono {
  font-family: monospace;
  color: var(--text-secondary);
}

.price {
  font-weight: 600;
  color: var(--success-color);
}

.time {
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.badge {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
}

.badge-warning { background: rgba(245, 158, 11, 0.2); color: #fbbf24; }
.badge-info { background: rgba(59, 130, 246, 0.2); color: #60a5fa; }
.badge-primary { background: rgba(139, 92, 246, 0.2); color: #a78bfa; }
.badge-success { background: rgba(16, 185, 129, 0.2); color: #34d399; }
.badge-neutral { background: rgba(148, 163, 184, 0.2); color: #94a3b8; }

.btn-sm {
  padding: 6px 12px;
  font-size: 0.85rem;
}

.action-btns {
  display: flex;
  gap: 8px;
}

.text-center { text-align: center; }
.py-4 { padding-top: 1rem; padding-bottom: 1rem; }
.mt-4 { margin-top: 1rem; }

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
  max-width: 600px;
  padding: 32px;
  animation: scaleUp 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

@keyframes scaleUp {
  from { opacity: 0; transform: scale(0.9); }
  to { opacity: 1; transform: scale(1); }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  font-size: 1.3rem;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 4px;
}

.close-btn:hover {
  color: var(--text-primary);
}

.detail-section h4 {
  font-size: 1rem;
  color: var(--accent-color);
  margin-bottom: 12px;
  border-bottom: 1px solid var(--panel-border);
  padding-bottom: 8px;
}

.detail-section p {
  margin-bottom: 8px;
  font-size: 0.95rem;
}

.items-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.item-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255,255,255,0.03);
  padding: 12px;
  border-radius: 8px;
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.item-name {
  font-weight: 500;
}

.item-qty {
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.item-price {
  font-weight: 600;
}
</style>
