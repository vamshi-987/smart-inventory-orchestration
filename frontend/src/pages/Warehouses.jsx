import { useEffect, useState } from "react";
import api from "../api/axiosConfig";
import LocationPicker from "../components/LocationPicker";

export default function Warehouses() {
  const [warehouses, setWarehouses] = useState([]);
  const [name, setName] = useState("");
  const [serviceRadiusKm, setServiceRadiusKm] = useState("");
  const [selectedLocation, setSelectedLocation] = useState(null);

  const [loadingWarehouses, setLoadingWarehouses] = useState(false);
  const [error, setError] = useState("");

  const extractArray = (data) => {
    if (Array.isArray(data)) {
      return data;
    }

    if (Array.isArray(data?.content)) {
      return data.content;
    }

    return [];
  };

  const fetchWarehouses = async () => {
    try {
      setLoadingWarehouses(true);
      setError("");

      const res = await api.get("/warehouses");

      const warehouseList = extractArray(res.data);
      setWarehouses(warehouseList);
    } catch (err) {
      console.error("Failed to fetch warehouses:", err);
      setWarehouses([]);
      setError("Failed to load warehouses. Check backend or API URL.");
    } finally {
      setLoadingWarehouses(false);
    }
  };

  useEffect(() => {
    fetchWarehouses();
  }, []);

  const createWarehouse = async (e) => {
    e.preventDefault();

    if (!name.trim()) {
      alert("Please enter warehouse name");
      return;
    }

    if (!selectedLocation) {
      alert("Please select warehouse location");
      return;
    }

    if (!serviceRadiusKm || Number(serviceRadiusKm) <= 0) {
      alert("Please enter valid service radius");
      return;
    }

    try {
      setError("");

      await api.post("/warehouses", {
        name: name,
        city: selectedLocation.city,
        pincode: selectedLocation.pincode,
        address: selectedLocation.formattedAddress,
        latitude: selectedLocation.latitude,
        longitude: selectedLocation.longitude,
        serviceRadiusKm: Number(serviceRadiusKm),
      });

      setName("");
      setServiceRadiusKm("");
      setSelectedLocation(null);

      fetchWarehouses();
    } catch (err) {
      console.error("Failed to create warehouse:", err);

      const message =
        err.response?.data?.message ||
        err.response?.data?.error ||
        "Failed to create warehouse. Check backend logs.";

      setError(message);
      alert(message);
    }
  };

  return (
    <div>
      <h1>Warehouses</h1>

      {error && (
        <div className="error-message">
          <p>{error}</p>
        </div>
      )}

      <form onSubmit={createWarehouse}>
        <label>Warehouse Name</label>
        <input
          value={name}
          placeholder="Warehouse name"
          onChange={(e) => setName(e.target.value)}
        />

        <LocationPicker onLocationConfirm={setSelectedLocation} />

        {selectedLocation && (
          <div className="selected-location">
            <h4>Confirmed Warehouse Location</h4>
            <p>{selectedLocation.formattedAddress}</p>
            <p>City: {selectedLocation.city}</p>
            <p>Pincode: {selectedLocation.pincode}</p>
          </div>
        )}

        <label>Service Radius KM</label>
        <input
          type="number"
          value={serviceRadiusKm}
          placeholder="Example: 10"
          onChange={(e) => setServiceRadiusKm(e.target.value)}
        />

        <small>Example service radius: 10 km</small>

        <button type="submit">Create Warehouse</button>
      </form>

      <h3>Warehouse List</h3>

      {loadingWarehouses ? (
        <p>Loading warehouses...</p>
      ) : warehouses.length === 0 ? (
        <p>No warehouses found</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Name</th>
              <th>City</th>
              <th>Pincode</th>
              <th>Address</th>
              <th>Radius</th>
            </tr>
          </thead>

          <tbody>
            {warehouses.map((warehouse) => (
              <tr key={warehouse.id}>
                <td>{warehouse.name}</td>
                <td>{warehouse.city}</td>
                <td>{warehouse.pincode}</td>
                <td>{warehouse.address}</td>
                <td>{warehouse.serviceRadiusKm} km</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}