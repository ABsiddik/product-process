"use client";

import { useState, useRef, useEffect } from "react";
import { uploadBulkProducts, getProgressData } from "../../services/productService.js";

type UploadForm = {
    sku: string;
    name: string;
    description: string;
    price: number;
    photos: File[];
}

export default function ProductUploader() {
    const [products, setProducts] = useState<UploadForm[]>([
        { sku:"", name: "", price: 0, description: "", photos: [] }
    ]);
    const [totalProducts, setTotalProducts] = useState(0);
    const [totalCompleted, setTotalCompleted] = useState(0);
    const [completedPercent, setCompletedPercent] = useState(0);
    const [message, setMessage] = useState("Progess");
    const [processing, setProcessing] = useState(false);
    const [hasError, setHasError] = useState(false);

    const intervalRef = useRef<number | null>(null);

    const handleInputChange = <K extends keyof UploadForm>(index: number, field: K, value: UploadForm[K]) => {
        const updated = [...products];
        updated[index][field] = value as UploadForm[typeof field];
        setProducts(updated);
        
    };

    const handleAddProduct = () => {
        setProducts([...products, { sku: "", name: "", price: 0, description: "", photos: [] }]);
    };

    const submitProducts = async () => {
        const validProducts = products.filter(p => p.sku !== "" && p.name !== "");

        if (validProducts.length === 0) {
            console.log("No valid products to submit.");
            return;
        }

        const formData = new FormData();
        products.forEach((p, index) => {
            formData.append(`products[${index}].sku`, p.sku);
            formData.append(`products[${index}].name`, p.name);
            formData.append(`products[${index}].price`, p.price.toString());
            formData.append(`products[${index}].description`, p.description);

            p.photos.forEach((file) => {
                formData.append(`products[${index}].photos`, file);
            });
        });

        try {
            const response = await uploadBulkProducts(formData);
            
            if (response.data) {
                const data = response.data;
                setMessage(data.message);
                setTotalProducts(data.total);
                setProcessing(true);
                startProgressPolling(data.batchId);
            }
        } catch (e) {
            setProcessing(false);
            console.error("Upload failed", e);
        }
        
    }

    const startProgressPolling = (batchId: any) => {
        
        intervalRef.current = window.setInterval(async () => {
            try {
                const response = await getProgressData(batchId);
                
                const data = response.data;
                
                setMessage(data.message);
                const completed = data.processed;
                setTotalCompleted(completed);

                if (completed >= totalProducts) {
                    if (intervalRef.current !== null) {
                        clearInterval(intervalRef.current);
                        intervalRef.current = null;
                    }
                    setProcessing(false);
                    setProducts([]);
                }

            } catch (error: any) {
                const data = error?.response?.data;
                if (data) {
                    setMessage(data.message);
                    setHasError(true);
                    setProcessing(false);
                }
                console.error("Error on progress - ", data);
            }
        }, 1000);
    };

    useEffect(() => {
        return () => {
            if (intervalRef.current) clearInterval(intervalRef.current);
        };
    }, []);

    const percentage = (totalCompleted / totalProducts) * 100;

    return (
        <div className="p-6 max-w-7xl mx-auto">
        <h1 className="text-2xl font-bold mb-4">Bulk Product Upload</h1>

        <div className="flex">
            <b className="flex-auto">SKU</b>
            <b className="flex-auto">Name</b>
            <b className="flex-auto">Price</b>
            <b className="flex-auto">Description</b>
            <b className="flex-auto">Images</b>
        </div>
        {products.map((product, index) => (
            <div key={index} className="flex gap-2">
            <input
                placeholder="Product SKU"
                className="border p-2 w-full mb-2 flex-auto"
                onChange={(e) =>
                    handleInputChange(index, "sku", e.target.value)
                }
            />

            <input
                placeholder="Product Name"
                className="border p-2 w-full mb-2 flex-auto"
                onChange={(e) =>
                    handleInputChange(index, "name", e.target.value)
                }
            />

            <input
                placeholder="Price"
                className="border p-2 w-full mb-2 flex-auto"
                onChange={(e) =>
                    handleInputChange(index, "price", Number(e.target.value))
                }
            />

            <input
                placeholder="Description"
                className="border p-2 w-full mb-2 flex-auto"
                onChange={(e) =>
                    handleInputChange(index, "description", e.target.value)
                }
            />

            <input
                type="file"
                multiple
                className="mb-2"
                accept=".png, .jpg, .jpeg"
                onChange={(e) =>
                    handleInputChange(index, "photos", Array.from(e.target.files ?? []))
                }
            />
            </div>
        ))}

            {!processing &&
                <div className="mt-5">
                    <button onClick={handleAddProduct}
                        className="bg-gray-800 text-white px-4 py-2 rounded">
                        + Add Product
                    </button>

                    <button onClick={submitProducts}
                        className="bg-blue-600 text-white px-4 py-2 rounded ml-4">
                        Submit All
                    </button>
                    <a href="/products" className="bg-gray-600 text-white px-4 py-2 rounded ml-4">See Products</a>
                </div>
            }

            {processing &&
                <div className="space-y-2 w-full mt-5">
                    <div className="text-sm font-medium text-gray-700">
                        <span>{message}</span><span className="ml-2">{totalCompleted} / {totalProducts}</span>
                    </div>
                    <div className="w-full bg-gray-200 rounded-full h-3">
                    <div
                        className="bg-blue-600 h-3 rounded-full transition-all duration-500"
                        style={{ width: `${percentage}%` }}
                    ></div>
                    </div>
                </div>
            }
        </div>
    );
}