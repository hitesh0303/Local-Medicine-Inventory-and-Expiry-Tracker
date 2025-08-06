import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';
const MEDICINES_URL = `${API_BASE_URL}/medicines`;

const getAuthHeaders = () => {
    const user = JSON.parse(localStorage.getItem('user'));
    if (user && user.authdata) {
        return { headers: { Authorization: 'Basic ' + user.authdata } };
    } else {
        return {};
    }
};

const login = (username, password) => {
    const authdata = window.btoa(username + ':' + password);
    const user = { username, authdata };
    
    // First, test authentication with the auth endpoint
    return axios.post(`${API_BASE_URL}/auth/login`, {}, { 
        headers: { Authorization: 'Basic ' + authdata } 
    })
    .then(response => {
        if (response.status === 200) {
            localStorage.setItem('user', JSON.stringify(user));
            return response;
        }
        throw new Error('Authentication failed');
    });
};

const logout = () => {
    const user = JSON.parse(localStorage.getItem('user'));
    if (user && user.authdata) {
        // Call logout endpoint if user is authenticated
        return axios.post(`${API_BASE_URL}/auth/logout`, {}, { 
            headers: { Authorization: 'Basic ' + user.authdata } 
        }).finally(() => {
            localStorage.removeItem('user');
        });
    } else {
        localStorage.removeItem('user');
        return Promise.resolve();
    }
};

const checkAuthStatus = () => {
    const user = JSON.parse(localStorage.getItem('user'));
    if (!user || !user.authdata) {
        return Promise.resolve({ authenticated: false });
    }
    
    return axios.get(`${API_BASE_URL}/auth/status`, { 
        headers: { Authorization: 'Basic ' + user.authdata } 
    }).then(response => response.data)
    .catch(() => ({ authenticated: false }));
};

const getAllMedicines = () => {
    return axios.get(MEDICINES_URL, getAuthHeaders());
};

const addMedicine = (medicine) => {
    return axios.post(MEDICINES_URL, medicine, getAuthHeaders());
};

const updateMedicine = (id, medicine) => {
    return axios.put(`${MEDICINES_URL}/${id}`, medicine, getAuthHeaders());
};

const deleteMedicine = (id) => {
    return axios.delete(`${MEDICINES_URL}/${id}`, getAuthHeaders());
};

const signup = (signupData) => {
    return axios.post(`${API_BASE_URL}/auth/signup`, signupData);
};

const api = {
    login,
    logout,
    signup,
    checkAuthStatus,
    getAllMedicines,
    addMedicine,
    updateMedicine,
    deleteMedicine,
};

export default api;