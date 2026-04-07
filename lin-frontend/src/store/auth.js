import { defineStore } from 'pinia';
import api from '../api';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: null,
  }),
  actions: {
    async login(username, password) {
      try {
        const res = await api.post('/api/auth/login', { username, password });
        if (res.code === 200 && res.data) {
          this.token = res.data;
          localStorage.setItem('token', this.token);
          await this.fetchUser();
          return true;
        }
        return false;
      } catch (error) {
        console.error('Login failed', error);
        return false;
      }
    },
    async fetchUser() {
      try {
        const res = await api.get('/api/auth/info');
        if (res.code === 200) {
          this.user = res.data;
        }
      } catch (error) {
        console.error('Fetch user failed', error);
      }
    },
    logout() {
      this.token = '';
      this.user = null;
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
  }
});
