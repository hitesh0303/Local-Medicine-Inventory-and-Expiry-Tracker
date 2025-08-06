import React, { useState, useEffect, useMemo } from 'react';
import api from '../services/api';

const MedicineManager = ({ onLogout }) => {
    const [medicines, setMedicines] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [currentMedicine, setCurrentMedicine] = useState(null);

    useEffect(() => {
        fetchMedicines();
    }, []);

    const fetchMedicines = () => {
        api.getAllMedicines()
            .then(response => {
                setMedicines(response.data);
            })
            .catch(error => {
                console.error("Error fetching medicines:", error);
                if (error.response && error.response.status === 401) {
                    // Handle authentication error
                    alert('Session expired. Please login again.');
                    onLogout();
                }
            });
    };

    const handleFormChange = (e) => {
        const { name, value } = e.target;
        setCurrentMedicine({ ...currentMedicine, [name]: value });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const medicineToSave = { ...currentMedicine, quantity: parseInt(currentMedicine.quantity, 10) || 0 };

        if (medicineToSave.id) { // Update
            api.updateMedicine(medicineToSave.id, medicineToSave)
                .then(() => {
                    fetchMedicines();
                    closeModal();
                })
                .catch(error => {
                    console.error("Error updating medicine:", error);
                    if (error.response && error.response.status === 401) {
                        alert('Session expired. Please login again.');
                        onLogout();
                    }
                });
        } else { // Add
            api.addMedicine(medicineToSave)
                .then(() => {
                    fetchMedicines();
                    closeModal();
                })
                .catch(error => {
                    console.error("Error adding medicine:", error);
                    if (error.response && error.response.status === 401) {
                        alert('Session expired. Please login again.');
                        onLogout();
                    }
                });
        }
    };

    const openModal = (medicine = null) => {
        setCurrentMedicine(medicine || { name: '', quantity: '', batchNumber: '', expiryDate: '', manufacturer: '' });
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setCurrentMedicine(null);
    };

    const handleDelete = (id) => {
        if (window.confirm('Are you sure you want to delete this medicine?')) {
            api.deleteMedicine(id)
                .then(() => fetchMedicines())
                .catch(error => {
                    console.error("Error deleting medicine:", error);
                    if (error.response && error.response.status === 401) {
                        alert('Session expired. Please login again.');
                        onLogout();
                    }
                });
        }
    };

    const isExpiringSoon = (expiryDate) => {
        const today = new Date();
        const expiry = new Date(expiryDate);
        const sevenDaysFromNow = new Date(today.setDate(today.getDate() + 7));
        return expiry <= sevenDaysFromNow;
    };

    const filteredMedicines = useMemo(() =>
        medicines.filter(med =>
            med.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
            med.batchNumber.toLowerCase().includes(searchTerm.toLowerCase())
        ), [medicines, searchTerm]
    );

    return (
        <div className="container-fluid mt-4">
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>Medicine Inventory 💊</h2>
                <div>
                    <button className="btn btn-primary me-2" onClick={() => openModal()}>+ Add Medicine</button>
                    <button className="btn btn-secondary" onClick={onLogout}>Logout</button>
                </div>
            </div>

            <input
                type="text"
                className="form-control mb-4"
                placeholder="Search by name or batch number..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
            />

            <div className="table-responsive">
                <table className="table table-striped table-hover">
                    <thead className="table-dark">
                        <tr>
                            <th>Name</th>
                            <th>Manufacturer</th>
                            <th>Batch #</th>
                            <th>Quantity</th>
                            <th>Expiry Date</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {filteredMedicines.map(med => (
                            <tr key={med.id} className={isExpiringSoon(med.expiryDate) ? 'table-warning' : ''}>
                                <td>{med.name}</td>
                                <td>{med.manufacturer}</td>
                                <td>{med.batchNumber}</td>
                                <td>{med.quantity}</td>
                                <td>{med.expiryDate}</td>
                                <td>
                                    <button className="btn btn-sm btn-info me-2" onClick={() => openModal(med)}>Edit</button>
                                    <button className="btn btn-sm btn-danger" onClick={() => handleDelete(med.id)}>Delete</button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {isModalOpen && (
                <div className="modal show d-block" tabIndex="-1">
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <form onSubmit={handleSubmit}>
                                <div className="modal-header">
                                    <h5 className="modal-title">{currentMedicine.id ? 'Edit Medicine' : 'Add Medicine'}</h5>
                                    <button type="button" className="btn-close" onClick={closeModal}></button>
                                </div>
                                <div className="modal-body">
                                    <div className="form-group mb-2">
                                        <label>Name</label>
                                        <input type="text" name="name" className="form-control" value={currentMedicine.name} onChange={handleFormChange} required />
                                    </div>
                                    <div className="form-group mb-2">
                                        <label>Manufacturer</label>
                                        <input type="text" name="manufacturer" className="form-control" value={currentMedicine.manufacturer} onChange={handleFormChange} />
                                    </div>
                                    <div className="form-group mb-2">
                                        <label>Batch Number</label>
                                        <input type="text" name="batchNumber" className="form-control" value={currentMedicine.batchNumber} onChange={handleFormChange} required />
                                    </div>
                                    <div className="form-group mb-2">
                                        <label>Quantity</label>
                                        <input type="number" name="quantity" className="form-control" value={currentMedicine.quantity} onChange={handleFormChange} required />
                                    </div>
                                    <div className="form-group mb-2">
                                        <label>Expiry Date</label>
                                        <input type="date" name="expiryDate" className="form-control" value={currentMedicine.expiryDate} onChange={handleFormChange} required />
                                    </div>
                                </div>
                                <div className="modal-footer">
                                    <button type="button" className="btn btn-secondary" onClick={closeModal}>Close</button>
                                    <button type="submit" className="btn btn-primary">Save Changes</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            )}
            {isModalOpen && <div className="modal-backdrop fade show"></div>}
        </div>
    );
};

export default MedicineManager;