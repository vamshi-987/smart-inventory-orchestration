import { useEffect, useState } from "react";
import api from "../api/axiosConfig";

export default function Dashboard() {
  const [stats, setStats] = useState({
    products: 0,
    warehouses: 0,
    orders: 0,
    outOfServiceOrders: 0,
    notifications: 0,
  });

  const fetchStats = async () => {
    const [productsRes, warehousesRes, ordersRes, notificationsRes] =
      await Promise.all([
        api.get("/products"),
        api.get("/warehouses"),
        api.get("/orders"),
        api.get("/notifications"),
      ]);

    setStats({
      products: productsRes.data.length,
      warehouses: warehousesRes.data.length,
      orders: ordersRes.data.length,
      outOfServiceOrders: ordersRes.data.filter(
        (order) => order.status === "OUT_OF_SERVICE_AREA"
      ).length,
      notifications: notificationsRes.data.length,
    });
  };

  useEffect(() => {
    fetchStats();
  }, []);

  return (
    <div>
      <h1>Dashboard</h1>

      <div className="cards">
        <div className="card">Total Products: {stats.products}</div>
        <div className="card">Total Warehouses: {stats.warehouses}</div>
        <div className="card">Total Orders: {stats.orders}</div>
        <div className="card">Out of Service Orders: {stats.outOfServiceOrders}</div>
        <div className="card">Notifications: {stats.notifications}</div>
      </div>
    </div>
  );
}