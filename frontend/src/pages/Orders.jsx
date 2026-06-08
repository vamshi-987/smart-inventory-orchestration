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
  const [loadingProducts, setLoadingProducts] = useState(false);
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

  const fetchProducts = async () => {
    try {
      setLoadingProducts(true);
      setError("");

      const res = await api.get("/products");

      const productList = extractArray(res.data);
      setProducts(productList);
    } catch (err) {
      console.error("Failed to fetch products:", err);
      setProducts([]);
      setError("Failed to load products. Check backend or API URL.");
    } finally {
      setLoadingProducts(false);
    }
  };

  useEffect(() => {
    fetchProducts();
  }, []);

  const addItemToCart = () => {
    if (!productId || Number(quantity) <= 0) {
      alert("Select product and quantity");
      return;
    }

    const product = products.find((p) => String(p.id) === String(productId));

    if (!product) {
      alert("Selected product not found");
      return;
    }

    setCartItems((prev) => [
      ...prev,
      {
        productId: product.id,
        productName: product.name,
        sku: product.sku,
        quantity: Number(quantity),
      },
    ]);

    setProductId("");
    setQuantity(1);
  };

  const removeItemFromCart = (indexToRemove) => {
    setCartItems((prev) => prev.filter((_, index) => index !== indexToRemove));
  };

  const placeOrder = async (e) => {
    e.preventDefault();

    if (!customerName.trim()) {
      alert("Please enter customer name");
      return;
    }

    if (!selectedLocation) {
      alert("Please select delivery location");
      return;
    }

    if (cartItems.length === 0) {
      alert("Please add at least one product");
      return;
    }

    try {
      setError("");

      const requestBody = {
        customerName: customerName,

        deliveryAddress: selectedLocation.formattedAddress,
        deliveryCity: selectedLocation.city,
        deliveryPincode: selectedLocation.pincode,
        deliveryLatitude: selectedLocation.latitude,
        deliveryLongitude: selectedLocation.longitude,

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
      setProductId("");
      setQuantity(1);
    } catch (err) {
      console.error("Failed to place order:", err);

      const message =
        err.response?.data?.message ||
        err.response?.data?.error ||
        "Failed to place order. Check backend logs.";

      setError(message);
      alert(message);
    }
  };

  return (
    <div>
      <h1>Place Order</h1>

      {error && (
        <div className="error-message">
          <p>{error}</p>
        </div>
      )}

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

        {loadingProducts ? (
          <p>Loading products...</p>
        ) : (
          <select
            value={productId}
            onChange={(e) => setProductId(e.target.value)}
          >
            <option value="">Select product</option>

            {products.map((product) => (
              <option key={product.id} value={product.id}>
                {product.name} - {product.sku}
              </option>
            ))}
          </select>
        )}

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

        {cartItems.length === 0 ? (
          <p>No items added</p>
        ) : (
          cartItems.map((item, index) => (
            <div key={`${item.productId}-${index}`}>
              <p>
                {item.productName} - {item.sku} - Qty: {item.quantity}
              </p>

              <button type="button" onClick={() => removeItemFromCart(index)}>
                Remove
              </button>
            </div>
          ))
        )}

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