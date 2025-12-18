import { useState, useEffect } from 'react';
import { productService } from '../api/productService';
import { ProductCard } from '../components/products/ProductCard';

export const Products = () => {
    const [products, setProducts] = useState([]);
    const [categories, setCategories] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadProducts();
        loadCategories();
    }, [selectedCategory]);

    const loadProducts = async () => {
        try {
            const filters = selectedCategory ? { categoryId: selectedCategory } : {};
            const data = await productService.getProducts(filters);
            setProducts(data);
        } catch (error) {
            console.error('Error loading products:', error);
        } finally {
            setLoading(false);
        }
    };

    const loadCategories = async () => {
        try {
            const data = await productService.getCategories();
            setCategories(data);
        } catch (error) {
            console.error('Error loading categories:', error);
        }
    };

    if (loading) return <div>Cargando...</div>;

    return (
        <div className="container mx-auto p-4">
            <h1 className="text-3xl font-bold mb-6">Productos</h1>

            {/* Filtros */}
            <div className="mb-6">
                <select
                    value={selectedCategory}
                    onChange={(e) => setSelectedCategory(e.target.value)}
                    className="p-2 border rounded"
                >
                    <option value="">Todas las categorías</option>
                    {categories.map((cat) => (
                        <option key={cat.id} value={cat.id}>
                            {cat.name}
                        </option>
                    ))}
                </select>
            </div>

            {/* Grid de productos */}
            <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-6">
                {products.map((product) => (
                    <ProductCard key={product.id} product={product} />
                ))}
            </div>
        </div>
    );
};
