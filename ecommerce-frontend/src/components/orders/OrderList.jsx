import { useNavigate } from 'react-router-dom';

export const OrderList = ({ orders, isAdmin = false, onUpdateStatus }) => {
    const navigate = useNavigate();

    return (
        <div className="space-y-4">
            {orders.map((order) => (
                <div key={order.id} className="border p-4 rounded shadow-sm">
                    <div className="flex justify-between items-start mb-2">
                        <div>
                            <p className="font-bold">Orden #{order.id.slice(0, 8)}</p>
                            <p className="text-gray-500 text-sm">
                                {new Date(order.createdAt).toLocaleDateString()}
                            </p>
                        </div>
                        <span
                            className={`px-3 py-1 rounded text-sm ${order.status === 'DELIVERED'
                                    ? 'bg-green-100 text-green-800'
                                    : order.status === 'CANCELLED'
                                        ? 'bg-red-100 text-red-800'
                                        : 'bg-yellow-100 text-yellow-800'
                                }`}
                        >
                            {order.status}
                        </span>
                    </div>

                    <div className="mb-2">
                        <p>{order.items.length} items</p>
                        <p className="font-bold">Total: ${order.totalAmount}</p>
                    </div>

                    <div className="flex gap-2 mt-4">
                        {/* 
            <button
              onClick={() => navigate(`/orders/${order.id}`)}
              className="text-blue-600 hover:underline text-sm"
            >
              Ver Detalle
            </button> 
            */}

                        {isAdmin && onUpdateStatus && (
                            <div className="flex gap-2">
                                <button onClick={() => onUpdateStatus(order.id, 'SHIPPED')} className="text-xs bg-blue-100 p-1 rounded">Ship</button>
                                <button onClick={() => onUpdateStatus(order.id, 'DELIVERED')} className="text-xs bg-green-100 p-1 rounded">Deliver</button>
                                <button onClick={() => onUpdateStatus(order.id, 'CANCELLED')} className="text-xs bg-red-100 p-1 rounded">Cancel</button>
                            </div>
                        )}
                    </div>
                </div>
            ))}
        </div>
    );
};
