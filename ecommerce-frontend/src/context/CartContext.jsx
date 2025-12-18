import { createContext, useState, useEffect } from 'react';

export const CartContext = createContext();

export const CartProvider = ({ children }) => {
    const [items, setItems] = useState([]);

    // Cargar carrito del localStorage
    useEffect(() => {
        const savedCart = localStorage.getItem('cart');
        if (savedCart) {
            setItems(JSON.parse(savedCart));
        }
    }, []);

    // Guardar carrito en localStorage cuando cambie
    useEffect(() => {
        localStorage.setItem('cart', JSON.stringify(items));
    }, [items]);

    const addItem = (product, quantity = 1) => {
        setItems((prevItems) => {
            const existingItem = prevItems.find((item) => item.id === product.id);

            if (existingItem) {
                return prevItems.map((item) =>
                    item.id === product.id
                        ? { ...item, quantity: item.quantity + quantity }
                        : item
                );
            }

            return [...prevItems, { ...product, quantity }];
        });
    };

    const removeItem = (productId) => {
        setItems((prevItems) => prevItems.filter((item) => item.id !== productId));
    };

    const updateQuantity = (productId, quantity) => {
        if (quantity <= 0) {
            removeItem(productId);
            return;
        }

        setItems((prevItems) =>
            prevItems.map((item) =>
                item.id === productId ? { ...item, quantity } : item
            )
        );
    };

    const clearCart = () => {
        setItems([]);
    };

    const getTotal = () => {
        return items.reduce((total, item) => total + item.price * item.quantity, 0);
    };

    const getItemCount = () => {
        return items.reduce((count, item) => count + item.quantity, 0);
    };

    const value = {
        items,
        addItem,
        removeItem,
        updateQuantity,
        clearCart,
        getTotal,
        getItemCount,
    };

    return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
};
