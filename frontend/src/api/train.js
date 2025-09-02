// src/api/train.js
import axios from 'axios'

const request = axios.create({
    baseURL: '/api',
    timeout: 5000,
    withCredentials: true
})

const getAuthHeader = () => ({
    Authorization: `Bearer ${localStorage.getItem('jwtToken')}`
});

export const searchByDepartureTime = (params) =>
    request.post('/train/seat/by-departure-time', params, { headers: getAuthHeader() });

export const searchByDuration = (params) =>
    request.post('/train/seat/by-duration-time', params, { headers: getAuthHeader() });

export const startPayment = (params) =>
    request.post('/train/ticket/get', params, { headers: getAuthHeader() });

export const doAsync = (orderId) =>
    request.get(`/train/ticket/${orderId}`, { headers: getAuthHeader() });

export const searchTrainSeatOrder = (userId) =>
    request.get(`train/order/get/${userId}`, { headers: getAuthHeader() });

export const refundSeat = (params) =>
    request.post('train/refund', params, { headers: getAuthHeader() });

export const getTrainById = (id) =>
    request.get(`train/by-id/${id}`, { headers: getAuthHeader() });
