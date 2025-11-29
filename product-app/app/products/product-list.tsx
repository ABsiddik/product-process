"use client";

import { useState, useEffect } from "react";
import { getAllProducts } from "../services/productService.js";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE;

export default function Products() {
    const [products, setProducts] = useState([]);

    useEffect(() => {
        document.title = 'Product List';
        const fetchData = async () => {
            try {
                const response = await getAllProducts();
                setProducts(response.data);
                console.log(response);
                
            } catch (error) {
                console.error('Failed to fetch products', error);
            }
        };

        fetchData();
    }, []);

    const product_images = `${API_BASE}/product-images`;

    return (
        <div className="max-w-7xl mx-auto p-6">
            <h1 className="text-2xl font-bold mb-4">Product List</h1>

            <div className="mt-5">
                {products.map((product: any, index) => (
                    <div key={index} className="flex p-2 border mb-2 rounded">
                        <div className="flex-auto flex">
                            <div className="flex-auto flex flex-col">
                                <span><b>SKU</b> : {product?.sku}</span>
                                <span><b>Name</b> : {product?.name}</span>
                                <span><b>Price</b> : {product?.price}</span>
                            </div>
                            <div className="flex-auto flex flex-col">
                                <span>Brand : {product?.brand}</span>
                                <span>Category : {product?.category}</span>
                                <span>Details : {product?.description}</span>
                            </div>
                        </div>
                        <div className="flex-auto flex gap-2">
                            {product.imagesPaths.map((path: any, k: number) => (
                                <img key={`${index}${k}`} src={path.replace('/home/user/product-data', product_images)} alt="sku" style={{width: '80px', height: '80px'}} className="rounded"/>
                            ))}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}