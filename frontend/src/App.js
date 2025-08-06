import React, { useState, useEffect } from 'react';
import Login from './components/Login';
import Signup from './components/Signup';
import MedicineManager from './components/MedicineManager';
import api from './services/api';
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';

function App() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [showSignup, setShowSignup] = useState(false);

    useEffect(() => {
        // Check if user is already logged in from a previous session
        api.checkAuthStatus()
            .then(authStatus => {
                if (authStatus.authenticated) {
                    setIsLoggedIn(true);
                } else {
                    // Clear invalid user data
                    localStorage.removeItem('user');
                    setIsLoggedIn(false);
                }
            })
            .catch(() => {
                // Clear invalid user data on error
                localStorage.removeItem('user');
                setIsLoggedIn(false);
            });
    }, []);

    const handleLoginSuccess = () => {
        setIsLoggedIn(true);
        setShowSignup(false);
    };

    const handleSignupSuccess = () => {
        setShowSignup(false);
        // User can now login with their new credentials
    };

    const handleSwitchToSignup = () => {
        setShowSignup(true);
    };

    const handleSwitchToLogin = () => {
        setShowSignup(false);
    };

    const handleLogout = () => {
        api.logout();
        setIsLoggedIn(false);
    };

    return (
        <div className="App">
            <nav className="navbar navbar-dark bg-dark">
                <div className="container-fluid">
                    <span className="navbar-brand mb-0 h1">Local Medicine Inventory</span>
                </div>
            </nav>
            <main>
                {isLoggedIn ? (
                    <MedicineManager onLogout={handleLogout} />
                ) : showSignup ? (
                    <Signup 
                        onSignupSuccess={handleSignupSuccess} 
                        onSwitchToLogin={handleSwitchToLogin} 
                    />
                ) : (
                    <Login 
                        onLoginSuccess={handleLoginSuccess} 
                        onSwitchToSignup={handleSwitchToSignup} 
                    />
                )}
            </main>
        </div>
    );
}

export default App;