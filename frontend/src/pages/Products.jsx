import { useEffect, useState, useCallback } from "react";
import api from "../api/axiosConfig";

export default function Products() {
  const [products, setProducts] = useState([]);
  const [search, setSearch] = useState("");

  const [form, setForm] = useState({
    sku: "",
    name: "",
    description: "",
    categoryId: "",
    price: "",
  });

  const fetchProducts = async () => {
  const res = await api.get("/products");
  setProducts(res.data);
};

useEffect(() => {
  fetchProducts();
  // eslint-disable-next-line react-hooks/exhaustive-deps, react-hooks/set-state-in-effect
}, []);

  const handleChange = (e) => {
    setForm((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const createProduct = async (e) => {
    e.preventDefault();

    await api.post("/products", {
      ...form,
      categoryId: form.categoryId || null,
      price: Number(form.price),
    });

    setForm({
      sku: "",
      name: "",
      description: "",
      categoryId: "",
      price: "",
    });

    await fetchProducts();
  };

  const deleteProduct = async (id) => {
    await api.delete(`/products/${id}`);
    await fetchProducts();
  };

  const filteredProducts = products.filter((product) =>
    product.name?.toLowerCase().includes(search.toLowerCase()) ||
    product.sku?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div>
      <h1>Products</h1>

      <form onSubmit={createProduct}>
        <input name="sku" placeholder="SKU" value={form.sku} onChange={handleChange} />
        <input name="name" placeholder="Name" value={form.name} onChange={handleChange} />
        <input name="description" placeholder="Description" value={form.description} onChange={handleChange} />
        <input name="categoryId" placeholder="Category UUID" value={form.categoryId} onChange={handleChange} />
        <input name="price" placeholder="Price" value={form.price} onChange={handleChange} />

        <button type="submit">Create Product</button>
      </form>

      <input
        placeholder="Search product..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />

      <table>
        <thead>
          <tr>
            <th>SKU</th>
            <th>Name</th>
            <th>Category</th>
            <th>Price</th>
            <th>Action</th>
          </tr>
        </thead>

        <tbody>
          {filteredProducts.map((product) => (
            <tr key={product.id}>
              <td>{product.sku}</td>
              <td>{product.name}</td>
              <td>{product.categoryName}</td>
              <td>{product.price}</td>
              <td>
                <button onClick={() => deleteProduct(product.id)}>
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}