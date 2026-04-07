<template>
  <div class="layout-wrapper">
    <!-- Sidebar -->
    <aside class="sidebar glass-panel">
      <div class="sidebar-header">
        <div class="logo-icon"></div>
        <h2>Lin Admin</h2>
      </div>
      
      <nav class="sidebar-nav">
        <router-link to="/dashboard" class="nav-item" active-class="active">
          <LayoutDashboard class="nav-icon" />
          Dashboard
        </router-link>
        <router-link to="/products" class="nav-item" active-class="active">
          <Package class="nav-icon" />
          Products
        </router-link>
        <router-link to="/orders" class="nav-item" active-class="active">
          <ShoppingCart class="nav-icon" />
          Orders
        </router-link>
      </nav>
      
      <div class="sidebar-footer">
        <div class="user-info" v-if="authStore.user">
          <div class="avatar">{{ authStore.user.username.charAt(0).toUpperCase() }}</div>
          <span>{{ authStore.user.username }}</span>
        </div>
        <button @click="logout" class="btn btn-secondary logout-btn">
          <LogOut size="16" /> Logout
        </button>
      </div>
    </aside>

    <!-- Main Content -->
    <main class="main-content">
      <header class="top-header glass-panel">
        <div class="header-title">{{ currentRouteName }}</div>
        <div class="header-actions">
          <button class="icon-btn"><Bell size="20" /></button>
          <button class="icon-btn"><Settings size="20" /></button>
        </div>
      </header>
      
      <div class="content-scroll">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '../store/auth';
import { 
  LayoutDashboard, 
  Package, 
  ShoppingCart, 
  LogOut, 
  Bell, 
  Settings 
} from 'lucide-vue-next';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const currentRouteName = computed(() => {
  return route.name || 'Dashboard';
});

const logout = () => {
  authStore.logout();
};

onMounted(() => {
  if (authStore.token && !authStore.user) {
    authStore.fetchUser();
  }
});
</script>

<style scoped>
.layout-wrapper {
  display: flex;
  height: 100vh;
  padding: 16px;
  gap: 16px;
  background: var(--bg-gradient);
}

/* Sidebar */
.sidebar {
  width: 260px;
  display: flex;
  flex-direction: column;
  padding: 24px;
}

.sidebar-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 40px;
}

.logo-icon {
  width: 28px;
  height: 28px;
  background: var(--accent-gradient);
  border-radius: 8px;
}

.sidebar-header h2 {
  font-size: 1.4rem;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  color: var(--text-secondary);
  text-decoration: none;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s;
}

.nav-item:hover {
  background: rgba(255,255,255,0.05);
  color: var(--text-primary);
}

.nav-item.active {
  background: var(--accent-gradient);
  color: #fff;
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.3);
}

.nav-icon {
  width: 20px;
  height: 20px;
}

.sidebar-footer {
  margin-top: auto;
  padding-top: 24px;
  border-top: 1px solid var(--panel-border);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.avatar {
  width: 36px;
  height: 36px;
  background: rgba(255,255,255,0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  color: var(--accent-color);
}

.logout-btn {
  width: 100%;
}

/* Main Content */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 0;
}

.top-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
}

.header-title {
  font-size: 1.2rem;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.icon-btn {
  background: rgba(255,255,255,0.05);
  border: 1px solid var(--panel-border);
  color: var(--text-primary);
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.icon-btn:hover {
  background: rgba(255,255,255,0.1);
  transform: translateY(-2px);
}

.content-scroll {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}
</style>
