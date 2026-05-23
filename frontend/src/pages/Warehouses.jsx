import { useEffect, useState } from "react";
import api from "../api/axiosConfig";

export default function Warehouses() {
  const [warehouses, setWarehouses] = useState([]);

  const [form, setForm] = useState({
    name: "",
    city: "",
    pincode: "",
    address: "",
    latitude: "",
    longitude: "",
    serviceRadiusKm: "",
  });

  const fetchWarehouses = async () => {
    const res = await api.get("/warehouses");
    setWarehouses(res.data);
  };

  useEffect(() => {
    fetchWarehouses();
  }, []);

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const createWarehouse = async (e) => {
    e.preventDefault();

    await api.post("/warehouses", {
      ...form,
      latitude: form.latitude ? Number(form.latitude) : null,
      longitude: form.longitude ? Number(form.longitude) : null,
      serviceRadiusKm: Number(form.serviceRadiusKm),
    });

    setForm({
      name: "",
      city: "",
      pincode: "",
      address: "",
      latitude: "",
      longitude: "",
      serviceRadiusKm: "",
    });

    fetchWarehouses();
  };

  return (
    <div>
      <h1>Warehouses</h1>

      <form onSubmit={createWarehouse}>
        <input name="name" placeholder="Name" value={form.name} onChange={handleChange} />
        <input name="city" placeholder="City" value={form.city} onChange={handleChange} />
        <input name="pincode" placeholder="Pincode" value={form.pincode} onChange={handleChange} />
        <input name="address" placeholder="Address" value={form.address} onChange={handleChange} />
        <input name="latitude" placeholder="Latitude" value={form.latitude} onChange={handleChange} />
        <input name="longitude" placeholder="Longitude" value={form.longitude} onChange={handleChange} />
        <input name="serviceRadiusKm" placeholder="Service Radius KM" value={form.serviceRadiusKm} onChange={handleChange} />

        <small>Example service radius: 10 km</small>

        <button type="submit">Create Warehouse</button>
      </form>

      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>City</th>
            <th>Pincode</th>
            <th>Lat</th>
            <th>Lng</th>
            <th>Radius</th>
          </tr>
        </thead>

        <tbody>
          {warehouses.map((warehouse) => (
            <tr key={warehouse.id}>
              <td>{warehouse.name}</td>
              <td>{warehouse.city}</td>
              <td>{warehouse.pincode}</td>
              <td>{warehouse.latitude}</td>
              <td>{warehouse.longitude}</td>
              <td>{warehouse.serviceRadiusKm} km</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}