<template>
  <div class="dashboard-page">
    <div class="stats-grid">
      <div v-for="(stat, index) in stats" :key="index" class="glass-panel stat-card" :style="{ animationDelay: `${index * 0.1}s` }">
        <div class="stat-icon" :style="{ background: stat.bg }">
          <component :is="stat.icon" :color="stat.color" size="24" />
        </div>
        <div class="stat-info">
          <p class="stat-title">{{ stat.title }}</p>
          <h3 class="stat-value">{{ stat.value }}</h3>
        </div>
        <div class="stat-trend" :class="stat.trend > 0 ? 'positive' : 'negative'">
          <TrendingUp v-if="stat.trend > 0" size="16" />
          <TrendingDown v-else size="16" />
          {{ Math.abs(stat.trend) }}%
        </div>
      </div>
    </div>

    <div class="charts-container">
      <div class="glass-panel chart-card fade-in-up" style="animation-delay: 0.4s;">
        <h3 class="card-title">Revenue Overview</h3>
        <div class="dummy-chart">
          <!-- A CSS-based dummy bar chart for visual appeal -->
          <div class="bars">
            <div class="bar" style="height: 40%"></div>
            <div class="bar" style="height: 60%"></div>
            <div class="bar" style="height: 30%"></div>
            <div class="bar" style="height: 80%"></div>
            <div class="bar" style="height: 50%"></div>
            <div class="bar" style="height: 90%"></div>
            <div class="bar" style="height: 70%"></div>
          </div>
        </div>
      </div>
      
      <div class="glass-panel chart-card fade-in-up" style="animation-delay: 0.5s;">
        <h3 class="card-title">Recent Activity</h3>
        <ul class="activity-list">
          <li v-for="i in 5" :key="i">
            <div class="activity-dot"></div>
            <div class="activity-content">
              <p>New order #100{{ i }} placed</p>
              <span>{{ i }} hours ago</span>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { 
  DollarSign, 
  ShoppingCart, 
  Users, 
  Activity,
  TrendingUp,
  TrendingDown
} from 'lucide-vue-next';

const stats = ref([
  { title: 'Total Revenue', value: '$84,590', trend: 12.5, icon: DollarSign, color: '#10b981', bg: 'rgba(16, 185, 129, 0.15)' },
  { title: 'Total Orders', value: '1,240', trend: 8.2, icon: ShoppingCart, color: '#8b5cf6', bg: 'rgba(139, 92, 246, 0.15)' },
  { title: 'New Customers', value: '342', trend: -2.4, icon: Users, color: '#ef4444', bg: 'rgba(239, 68, 68, 0.15)' },
  { title: 'Active Sessions', value: '1,893', trend: 5.1, icon: Activity, color: '#3b82f6', bg: 'rgba(59, 130, 246, 0.15)' },
]);
</script>

<style scoped>
.dashboard-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 24px;
}

.stat-card {
  padding: 24px;
  display: flex;
  align-items: flex-start;
  position: relative;
  overflow: hidden;
  animation: slideIn 0.5s ease-out backwards;
}

@keyframes slideIn {
  from { opacity: 0; transform: translateX(-20px); }
  to { opacity: 1; transform: translateX(0); }
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.stat-info {
  flex: 1;
}

.stat-title {
  color: var(--text-secondary);
  font-size: 0.9rem;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 1.6rem;
  font-weight: 700;
}

.stat-trend {
  position: absolute;
  top: 24px;
  right: 24px;
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.85rem;
  font-weight: 600;
}

.stat-trend.positive {
  color: var(--success-color);
}

.stat-trend.negative {
  color: var(--danger-color);
}

.charts-container {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

.chart-card {
  padding: 24px;
  min-height: 350px;
}

.card-title {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 24px;
}

.fade-in-up {
  animation: fadeInUp 0.6s ease-out backwards;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Dummy Chart CSS */
.dummy-chart {
  height: 250px;
  display: flex;
  align-items: flex-end;
  padding-top: 20px;
}

.bars {
  display: flex;
  width: 100%;
  height: 100%;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
}

.bar {
  flex: 1;
  background: var(--accent-gradient);
  border-radius: 8px 8px 0 0;
  animation: growUp 1s ease-out backwards;
  opacity: 0.8;
  transition: opacity 0.3s, transform 0.3s;
}

.bar:hover {
  opacity: 1;
  transform: scaleY(1.05);
  transform-origin: bottom;
}

@keyframes growUp {
  from { height: 0; }
}

/* Activity List */
.activity-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-list li {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.activity-dot {
  width: 10px;
  height: 10px;
  background: var(--accent-color);
  border-radius: 50%;
  margin-top: 6px;
  position: relative;
}

.activity-dot::after {
  content: '';
  position: absolute;
  width: 2px;
  height: 32px;
  background: var(--panel-border);
  top: 10px;
  left: 4px;
}

.activity-list li:last-child .activity-dot::after {
  display: none;
}

.activity-content p {
  font-size: 0.95rem;
  margin-bottom: 4px;
}

.activity-content span {
  font-size: 0.8rem;
  color: var(--text-secondary);
}
</style>
