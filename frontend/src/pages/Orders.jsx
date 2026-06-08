import { useEffect, useState } from "react";
import api from "../api/axiosConfig";
import LocationPicker from "../components/LocationPicker";

export default function Orders() {
  const [customerName, setCustomerName] = useState("");
  const [selectedLocation, setSelectedLocation] = useState(null);

  const [products, setProducts] = useState([]);
  const [productId, setProductId] = useState("");
  const [quantity, setQuantity] = useState(1);
  const [cartItems, setCartItems] = useState([]);

  const [orderResponse, setOrderResponse] = useState(null);

  const fetchProducts = async () => {
    const res = await api.get("/products");
    setProducts(res.data);
  };

  useEffect(() => {
    fetchProducts();
  }, []);

  const addItemToCart = () => {
    if (!productId || quantity <= 0) {
      alert("Select product and quantity");
      return;
    }

    const product = products.find((p) => p.id === productId);

    setCartItems((prev) => [
      ...prev,
      {
        productId,
        productName: product?.name,
        quantity: Number(quantity),
      },
    ]);

    setProductId("");
    setQuantity(1);
  };

  const placeOrder = async (e) => {
    e.preventDefault();

    if (!selectedLocation) {
      alert("Please select delivery location");
      return;
    }

    if (cartItems.length === 0) {
      alert("Please add at least one product");
      return;
    }

    const requestBody = {
      customerName,
      deliveryLocation: selectedLocation,
      items: cartItems.map((item) => ({
        productId: item.productId,
        quantity: item.quantity,
      })),
    };

    const res = await api.post("/orders", requestBody);

    setOrderResponse(res.data);
    setCustomerName("");
    setSelectedLocation(null);
    setCartItems([]);
  };

  return (
    <div>
      <h1>Place Order</h1>

      <form onSubmit={placeOrder}>
        <label>Customer Name</label>
        <input
          value={customerName}
          placeholder="Customer name"
          onChange={(e) => setCustomerName(e.target.value)}
        />

        <LocationPicker onLocationConfirm={setSelectedLocation} />

        {selectedLocation && (
          <div className="selected-location">
            <h4>Confirmed Delivery Location</h4>
            <p>{selectedLocation.formattedAddress}</p>
            <p>City: {selectedLocation.city}</p>
            <p>Pincode: {selectedLocation.pincode}</p>
          </div>
        )}

        <h3>Add Products</h3>

        <select value={productId} onChange={(e) => setProductId(e.target.value)}>
          <option value="">Select product</option>
          {products.map((product) => (
            <option key={product.id} value={product.id}>
              {product.name} - {product.sku}
            </option>
          ))}
        </select>

        <input
          type="number"
          min="1"
          value={quantity}
          onChange={(e) => setQuantity(e.target.value)}
        />

        <button type="button" onClick={addItemToCart}>
          Add Item
        </button>

        <h3>Cart</h3>

        {cartItems.map((item, index) => (
          <p key={index}>
            {item.productName} - Qty: {item.quantity}
          </p>
        ))}

        <button type="submit">Place Order</button>
      </form>

      {orderResponse && (
        <div className="card">
          <h3>Order Created</h3>
          <p>Status: {orderResponse.status}</p>
          <p>Total: ₹{orderResponse.totalAmount}</p>
          <p>Warehouse: {orderResponse.allocatedWarehouseName}</p>
          <p>Delivery: {orderResponse.deliveryAddress}</p>
        </div>
      )}
    </div>
  );
}