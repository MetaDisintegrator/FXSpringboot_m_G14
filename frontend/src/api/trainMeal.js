// src/api/trainMeal.js
import axios from 'axios'

const request = axios.create({
    baseURL: '/api',
    timeout: 5000,
    withCredentials: true
})

const getAuthHeader = () => ({
    Authorization: `Bearer ${localStorage.getItem('jwtToken')}`
});

export const searchByTrain = (trainId) =>
    request.get(`train/meal/${trainId}`, { headers: getAuthHeader() });

export const searchTrainMealOrder = (userId) =>
    request.get(`train/meal/orders/${userId}`, { headers: getAuthHeader() });

export const searchTrainMealOrderBySeatOrder = (seatOrderId) =>
    request.get(`train/meal/orders/by-ticket/${seatOrderId}`, { headers: getAuthHeader() });

export const startPayment = (params) =>
    request.post('/train/meal/get', params, { headers: getAuthHeader() });

export const doAsync = (orderId) =>
    request.get(`/train/meal/status/${orderId}`, { headers: getAuthHeader() });

export const refundMeal = (params) =>
    request.post(`train/meal/refund`, params, { headers: getAuthHeader() });

