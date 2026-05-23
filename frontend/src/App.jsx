import { BrowserRouter, Routes, Route } from "react-router-dom";
import Dashboard from "./pages/Dashboard";
import Products from "./pages/Products";
import Warehouses from "./pages/Warehouses";
import Inventory from "./pages/Inventory";
import Orders from "./pages/Orders";
import Notifications from "./pages/Notifications";
import AppLayout from "./components/AppLayout";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<AppLayout><Dashboard /></AppLayout>} />
        <Route path="/products" element={<AppLayout><Products /></AppLayout>} />
        <Route path="/warehouses" element={<AppLayout><Warehouses /></AppLayout>} />
        <Route path="/inventory" element={<AppLayout><Inventory /></AppLayout>} />
        <Route path="/orders" element={<AppLayout><Orders /></AppLayout>} />
        <Route path="/notifications" element={<AppLayout><Notifications /></AppLayout>} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;