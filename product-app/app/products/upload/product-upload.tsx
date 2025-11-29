"use client";

import { useState } from "react";
import { uploadBulkProducts } from "../../services/productService.js";

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

    const handleInputChange = <K extends keyof UploadForm>(index: number, field: K, value: UploadForm[K]) => {
        const updated = [...products];
        updated[index][field] = value as UploadForm[typeof field];
        setProducts(updated);
        
    };

    const handleAddProduct = () => {
        setProducts([...products, { sku: "", name: "", price: 0, description: "", photos: [] }
        ]);
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
            console.log("response:", response);
            console.log("Uploaded:", response.data);
        } catch (e) {
            console.error("Upload failed", e);
        }
        
    }

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

            <button onClick={handleAddProduct}
                className="bg-gray-800 text-white px-4 py-2 rounded">
                + Add Product
            </button>

            <button onClick={submitProducts}
                className="bg-blue-600 text-white px-4 py-2 rounded ml-4">
                Submit All
            </button>
        </div>
    );
}