import axios from "axios";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE;

export const uploadBulkProducts = async (formData) => {
  return axios.post(`${API_BASE}/products`, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
};