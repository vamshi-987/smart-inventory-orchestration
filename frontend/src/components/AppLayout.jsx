import Sidebar from "./Sidebar";
import Navbar from "./Navbar";

export default function AppLayout({ children }) {
  return (
    <div className="app">
      <Sidebar />

      <main className="main">
        <Navbar />
        <div className="page">{children}</div>
      </main>
    </div>
  );
}