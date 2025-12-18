import { useNavigate } from 'react-router-dom';
import { useCart } from '../../hooks/useCart';
import { toast } from 'react-toastify';

export const ProductCard = ({ product }) => {
    const navigate = useNavigate();
    const { addItem } = useCart();

    const handleAddToCart = (e) => {
        e.stopPropagation();
        addItem(product);
        toast.success('Producto agregado al carrito');
    };

    return (
        <div
            onClick={() => navigate(`/products/${product.id}`)}
            className="border rounded-lg p-4 cursor-pointer hover:shadow-lg transition"
        >
            {product.imageUrl && (
                <img
                    src={product.imageUrl}
                    alt={product.name}
                    className="w-full h-48 object-cover rounded mb-4"
                />
            )}

            <h3 className="text-lg font-semibold">{product.name}</h3>
            <p className="text-gray-600 text-sm mb-2">{product.description}</p>
            <p className="text-green-600 font-bold text-xl mb-2">
                ${product.price}
            </p>
            <p className="text-sm text-gray-500 mb-4">
                Stock: {product.stock} unidades
            </p>

            <button
                onClick={handleAddToCart}
                disabled={product.stock === 0}
                className={`w-full py-2 rounded ${product.stock > 0
                        ? 'bg-blue-500 hover:bg-blue-600 text-white'
                        : 'bg-gray-300 text-gray-500 cursor-not-allowed'
                    }`}
            >
                {product.stock > 0 ? 'Agregar al Carrito' : 'Sin Stock'}
            </button>
        </div>
    );
};
