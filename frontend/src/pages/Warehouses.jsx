import { useEffect, useState } from "react";
import api from "../api/axiosConfig";
import LocationPicker from "../components/LocationPicker";

export default function Warehouses() {
  const [warehouses, setWarehouses] = useState([]);
  const [name, setName] = useState("");
  const [serviceRadiusKm, setServiceRadiusKm] = useState("");
  const [selectedLocation, setSelectedLocation] = useState(null);

  const fetchWarehouses = async () => {
    const res = await api.get("/warehouses");
    setWarehouses(res.data);
  };

  useEffect(() => {
    fetchWarehouses();
  }, []);

  const createWarehouse = async (e) => {
    e.preventDefault();

    if (!selectedLocation) {
      alert("Please select warehouse location");
      return;
    }

    await api.post("/warehouses", {
      name,
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
  };

  return (
    <div>
      <h1>Warehouses</h1>

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
    </div>
  );
}