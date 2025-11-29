import axios from "axios";

const API_BASE = `${process.env.NEXT_PUBLIC_API_BASE}/api/v1`;

export const uploadBulkProducts = async (formData) => {
  return axios.post(`${API_BASE}/products`, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
};

export const getProgressData = async (batchId) => {
  return axios.get(`${API_BASE}/products/${batchId}/status`);
}

export const getAllProducts = async () => {
  return axios.get(`${API_BASE}/products`);
}