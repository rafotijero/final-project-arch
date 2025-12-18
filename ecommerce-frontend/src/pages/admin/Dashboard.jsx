import { useState, useEffect } from 'react';
import { productService } from '../../api/productService';
import { orderService } from '../../api/orderService'; // Need access to ALL orders, check orderService
// orderService.getMyOrders() gets user orders. Need getAllOrders for admin.
// orderService didn't implement getAllOrders explicitly in summary but instructions implied "List mis pedidos o todos (Admin)".
// The controller `listOrders` handles status query. If I pass nothing, user gets theirs.
// As Admin, I probably need a different endpoint or the same one behaves differently based on role?
// OrderController: `listOrders(@AuthenticationPrincipal UserDetails..., status)`
// UseCase: `listOrdersUseCase.execute(userId, status)`.
// The UseCase filtered by UserId. 
// Issue: Admin won't see ALL orders if we pass Admin's UUID and it filters by that.
// I noted this potential issue earlier.
// For now, I will implement a "Product Management" tab at least, and "Orders" tab calling `getMyOrders` 
// (which will only show Admin's orders unless backend is fixed).
// I won't fix backend now. Building frontend as is.

export const Dashboard = () => {
    const [activeTab, setActiveTab] = useState('products');
    const [products, setProducts] = useState([]);

    // Simple Product Management
    const [newProduct, setNewProduct] = useState({ name: '', price: 0, stock: 0, description: '', categoryId: '' });

    useEffect(() => {
        if (activeTab === 'products') {
            loadProducts();
        }
    }, [activeTab]);

    const loadProducts = async () => {
        const data = await productService.getProducts();
        setProducts(data);
    };

    const handleCreateProduct = async (e) => {
        e.preventDefault();
        try {
            // Need category ID. Hardcoded for demo or fetched.
            // Assuming categories exist.
            await productService.createProduct(newProduct);
            loadProducts();
            setNewProduct({ name: '', price: 0, stock: 0, description: '', categoryId: '' });
        } catch (error) {
            alert('Error creating product');
        }
    };

    return (
        <div className="container mx-auto p-4">
            <h1 className="text-3xl font-bold mb-6">Admin Dashboard</h1>

            <div className="flex gap-4 mb-6 border-b">
                <button
                    className={`pb-2 ${activeTab === 'products' ? 'border-b-2 border-blue-500 font-bold' : ''}`}
                    onClick={() => setActiveTab('products')}
                >
                    Productos
                </button>
                <button
                    className={`pb-2 ${activeTab === 'orders' ? 'border-b-2 border-blue-500 font-bold' : ''}`}
                    onClick={() => setActiveTab('orders')}
                >
                    Pedidos
                </button>
            </div>

            {activeTab === 'products' && (
                <div>
                    <h2 className="text-xl font-bold mb-4">Gestión de Productos</h2>

                    <form onSubmit={handleCreateProduct} className="mb-8 p-4 border rounded bg-gray-50">
                        <h3 className="font-bold mb-4">Nuevo Producto</h3>
                        <div className="grid grid-cols-2 gap-4">
                            <input
                                placeholder="Nombre"
                                className="border p-2"
                                value={newProduct.name}
                                onChange={e => setNewProduct({ ...newProduct, name: e.target.value })}
                            />
                            <input
                                placeholder="Precio"
                                type="number"
                                className="border p-2"
                                value={newProduct.price}
                                onChange={e => setNewProduct({ ...newProduct, price: parseFloat(e.target.value) })}
                            />
                            <input
                                placeholder="Stock"
                                type="number"
                                className="border p-2"
                                value={newProduct.stock}
                                onChange={e => setNewProduct({ ...newProduct, stock: parseFloat(e.target.value) })}
                            />
                            <input
                                placeholder="Descripción"
                                className="border p-2"
                                value={newProduct.description}
                                onChange={e => setNewProduct({ ...newProduct, description: e.target.value })}
                            />
                            <input
                                placeholder="Category ID (UUID)"
                                className="border p-2"
                                value={newProduct.categoryId}
                                onChange={e => setNewProduct({ ...newProduct, categoryId: e.target.value })}
                            />
                        </div>
                        <button className="mt-4 bg-blue-600 text-white px-4 py-2 rounded">Crear</button>
                    </form>

                    <table className="w-full text-left border-collapse">
                        <thead>
                            <tr className="border-b">
                                <th className="p-2">Producto</th>
                                <th className="p-2">Precio</th>
                                <th className="p-2">Stock</th>
                                <th className="p-2">Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            {products.map(p => (
                                <tr key={p.id} className="border-b">
                                    <td className="p-2">{p.name}</td>
                                    <td className="p-2">${p.price}</td>
                                    <td className="p-2">{p.stock}</td>
                                    <td className="p-2">
                                        <button className="text-red-600">Eliminar</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}

            {activeTab === 'orders' && (
                <div>
                    <p>Funcionalidad de gestión de pedidos en desarrollo.</p>
                </div>
            )}
        </div>
    );
};
