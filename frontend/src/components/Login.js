import React, { useState } from 'react';
import api from '../services/api';

const Login = ({ onLoginSuccess, onSwitchToSignup }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleLogin = (e) => {
        e.preventDefault();
        setError('');
        
        if (!username || !password) {
            setError('Please enter both username and password.');
            return;
        }
        
        api.login(username, password)
            .then(() => {
                onLoginSuccess();
            })
            .catch(err => {
                if (err.response && err.response.status === 401) {
                    setError('Invalid username or password. Please try again.');
                } else if (err.response && err.response.status === 0) {
                    setError('Cannot connect to server. Please check if the backend is running.');
                } else {
                    setError('Login failed. Please try again.');
                }
                console.error('Login error:', err);
            });
    };

    return (
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-6 col-lg-4">
                    <div className="card">
                        <div className="card-body">
                            <h3 className="card-title text-center mb-4">Pharmacist Login</h3>
                            <div className="alert alert-info mb-3">
                                <small>
                                    <strong>Default Credentials:</strong><br/>
                                    Username: <code>hitesh</code><br/>
                                    Password: <code>hitesh33</code>
                                </small>
                            </div>
                            <form onSubmit={handleLogin}>
                                <div className="form-group mb-3">
                                    <label>Username</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        value={username}
                                        onChange={(e) => setUsername(e.target.value)}
                                        required
                                    />
                                </div>
                                <div className="form-group mb-4">
                                    <label>Password</label>
                                    <input
                                        type="password"
                                        className="form-control"
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                        required
                                    />
                                </div>
                                {error && <div className="alert alert-danger">{error}</div>}
                                <button type="submit" className="btn btn-primary w-100 mb-3">Login</button>
                                
                                <div className="text-center">
                                    <small>
                                        Don't have an account?{' '}
                                        <button 
                                            type="button" 
                                            className="btn btn-link p-0"
                                            onClick={onSwitchToSignup}
                                        >
                                            Sign up here
                                        </button>
                                    </small>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Login;