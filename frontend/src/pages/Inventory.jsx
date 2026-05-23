
import { useState } from "react";
import api from "../api/axiosConfig";

export default function Inventory() {
  const [warehouseId, setWarehouseId] = useState("");
  const [inventory, setInventory] = useState([]);
  const [lowStockOnly, setLowStockOnly] = useState(false);

  const fetchByWarehouse = async () => {
    const res = await api.get(`/inventory/warehouse/${warehouseId}`);
    setInventory(res.data);
  };

  const addStock = async (id) => {
    const quantity = prompt("Enter quantity to add:");
    if (!quantity) return;

    await api.put(`/inventory/${id}/add-stock`, {
      quantity: Number(quantity),
    });

    fetchByWarehouse();
  };

  const reduceStock = async (id) => {
    const quantity = prompt("Enter quantity to reduce:");
    if (!quantity) return;

    await api.put(`/inventory/${id}/reduce-stock`, {
      quantity: Number(quantity),
    });

    fetchByWarehouse();
  };

  const filteredInventory = lowStockOnly
    ? inventory.filter((item) => item.lowStock)
    : inventory;

  return (
    <div>
      <h1>Inventory</h1>

      <input
        placeholder="Warehouse UUID"
        value={warehouseId}
        onChange={(e) => setWarehouseId(e.target.value)}
      />

      <button onClick={fetchByWarehouse}>View Warehouse Stock</button>

      <label>
        <input
          type="checkbox"
          checked={lowStockOnly}
          onChange={(e) => setLowStockOnly(e.target.checked)}
        />
        Low stock only
      </label>

      <table>
        <thead>
          <tr>
            <th>Product</th>
            <th>SKU</th>
            <th>Available</th>
            <th>Reserved</th>
            <th>Threshold</th>
            <th>Low Stock</th>
            <th>Actions</th>
          </tr>
        </thead>

        <tbody>
          {filteredInventory.map((item) => (
            <tr key={item.id}>
              <td>{item.productName}</td>
              <td>{item.sku}</td>
              <td>{item.availableQuantity}</td>
              <td>{item.reservedQuantity}</td>
              <td>{item.lowStockThreshold}</td>
              <td>{item.lowStock ? "Yes" : "No"}</td>
              <td>
                <button onClick={() => addStock(item.id)}>Add</button>
                <button onClick={() => reduceStock(item.id)}>Reduce</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}