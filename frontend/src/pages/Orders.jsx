import { useState } from "react";
import api from "../api/axiosConfig";

export default function Orders() {
  const [form, setForm] = useState({
    customerName: "",
    deliveryCity: "",
    deliveryPincode: "",
    deliveryLatitude: "",
    deliveryLongitude: "",
    productId: "",
    quantity: "",
  });

  const [response, setResponse] = useState(null);

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const useMyLocation = () => {
    navigator.geolocation.getCurrentPosition((position) => {
      setForm((prev) => ({
        ...prev,
        deliveryLatitude: position.coords.latitude,
        deliveryLongitude: position.coords.longitude,
      }));
    });
  };

  const placeOrder = async (e) => {
    e.preventDefault();

    const payload = {
      customerName: form.customerName,
      deliveryCity: form.deliveryCity,
      deliveryPincode: form.deliveryPincode,
      deliveryLatitude: Number(form.deliveryLatitude),
      deliveryLongitude: Number(form.deliveryLongitude),
      items: [
        {
          productId: form.productId,
          quantity: Number(form.quantity),
        },
      ],
    };

    const res = await api.post("/orders", payload);
    setResponse(res.data);
  };

  return (
    <div>
      <h1>Place Order</h1>

      <form onSubmit={placeOrder}>
        <input name="customerName" placeholder="Customer name" value={form.customerName} onChange={handleChange} />
        <input name="deliveryCity" placeholder="Delivery city" value={form.deliveryCity} onChange={handleChange} />
        <input name="deliveryPincode" placeholder="Delivery pincode" value={form.deliveryPincode} onChange={handleChange} />
        <input name="deliveryLatitude" placeholder="Latitude" value={form.deliveryLatitude} onChange={handleChange} />
        <input name="deliveryLongitude" placeholder="Longitude" value={form.deliveryLongitude} onChange={handleChange} />
        <input name="productId" placeholder="Product UUID" value={form.productId} onChange={handleChange} />
        <input name="quantity" placeholder="Quantity" value={form.quantity} onChange={handleChange} />

        <button type="button" onClick={useMyLocation}>
          Use My Location
        </button>

        <button type="submit">Place Order</button>
      </form>

      {response && (
        <div>
          <h3>Order Created</h3>
          <p>Status: {response.status}</p>
          <p>Total: {response.totalAmount}</p>
          <p>Warehouse: {response.allocatedWarehouseName}</p>
        </div>
      )}
    </div>
  );
}