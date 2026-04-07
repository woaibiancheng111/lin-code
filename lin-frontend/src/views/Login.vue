<template>
  <div class="login-wrapper">
    <div class="glass-panel login-card">
      <div class="login-header">
        <div class="logo">
          <div class="logo-icon"></div>
          <h1>Lin StoreAdmin</h1>
        </div>
        <p class="subtitle">Welcome back! Please login to your account.</p>
      </div>
      
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="input-group">
          <label for="username">Username</label>
          <input 
            v-model="username" 
            id="username" 
            type="text" 
            class="input-field" 
            placeholder="admin"
            required
          />
        </div>
        
        <div class="input-group">
          <label for="password">Password</label>
          <input 
            v-model="password" 
            id="password" 
            type="password" 
            class="input-field" 
            placeholder="admin123"
            required
          />
        </div>
        
        <button type="submit" class="btn btn-primary login-btn" :disabled="loading">
          <span v-if="loading">Authenticating...</span>
          <span v-else>Login to Dashboard</span>
        </button>
      </form>
      
      <div v-if="errorMsg" class="error-msg">
        {{ errorMsg }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../store/auth';

const username = ref('admin');
const password = ref('admin123');
const loading = ref(false);
const errorMsg = ref('');
const router = useRouter();
const authStore = useAuthStore();

const handleLogin = async () => {
  loading.value = true;
  errorMsg.value = '';
  
  const success = await authStore.login(username.value, password.value);
  if (success) {
    router.push('/dashboard');
  } else {
    errorMsg.value = 'Invalid username or password';
  }
  
  loading.value = false;
};
</script>

<style scoped>
.login-wrapper {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

/* Add some dynamic background elements */
.login-wrapper::before,
.login-wrapper::after {
  content: '';
  position: absolute;
  filter: blur(100px);
  z-index: -1;
  border-radius: 50%;
  animation: float 10s ease-in-out infinite;
}

.login-wrapper::before {
  width: 400px;
  height: 400px;
  background: rgba(139, 92, 246, 0.3);
  top: -100px;
  left: -100px;
  animation-delay: -2s;
}

.login-wrapper::after {
  width: 300px;
  height: 300px;
  background: rgba(59, 130, 246, 0.3);
  bottom: -50px;
  right: -50px;
}

@keyframes float {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-30px) scale(1.05); }
}

.login-card {
  width: 100%;
  max-width: 420px;
  padding: 40px;
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 12px;
}

.logo-icon {
  width: 32px;
  height: 32px;
  background: var(--accent-gradient);
  border-radius: 8px;
  box-shadow: 0 0 15px rgba(139, 92, 246, 0.5);
}

.login-header h1 {
  font-size: 1.8rem;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.subtitle {
  color: var(--text-secondary);
  font-size: 0.95rem;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.login-btn {
  width: 100%;
  margin-top: 16px;
  padding: 14px;
  font-size: 1rem;
}

.error-msg {
  margin-top: 16px;
  color: var(--danger-color);
  background: rgba(239, 68, 68, 0.1);
  padding: 10px;
  border-radius: 8px;
  text-align: center;
  font-size: 0.9rem;
  border: 1px solid rgba(239, 68, 68, 0.3);
}
</style>
