import { NavLink } from "react-router-dom";

export default function Sidebar() {
  const links = [
    { path: "/", label: "Dashboard" },
    { path: "/products", label: "Products" },
    { path: "/warehouses", label: "Warehouses" },
    { path: "/inventory", label: "Inventory" },
    { path: "/orders", label: "Orders" },
    { path: "/notifications", label: "Notifications" },
  ];

  return (
    <aside className="sidebar">
      <h2>StockFlow</h2>

      {links.map((link) => (
        <NavLink key={link.path} to={link.path}>
          {link.label}
        </NavLink>
      ))}
    </aside>
  );
}