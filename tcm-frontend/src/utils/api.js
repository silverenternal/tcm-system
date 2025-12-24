// API base URL
const API_BASE_URL = 'http://localhost:58081';

// API service functions
const ApiService = {
  get: (endpoint) => {
    return axios.get(`${API_BASE_URL}${endpoint}`);
  },
  post: (endpoint, data) => {
    return axios.post(`${API_BASE_URL}${endpoint}`, data);
  },
  put: (endpoint, data) => {
    return axios.put(`${API_BASE_URL}${endpoint}`, data);
  },
  delete: (endpoint) => {
    return axios.delete(`${API_BASE_URL}${endpoint}`);
  }
};

// Vue instance
const app = new Vue({
  el: '#app',
  data: {
    currentView: 'dashboard'
  },
  methods: {
    navigateTo(view) {
      this.currentView = view;
      window.location.hash = `#${view}`;
    }
  },
  mounted() {
    // Initialize routing
    window.addEventListener('hashchange', () => {
      const route = window.location.hash.slice(2) || 'dashboard';
      this.currentView = route;
    });
    
    // Set initial route
    const initialRoute = window.location.hash.slice(2) || 'dashboard';
    this.currentView = initialRoute;
  }
});