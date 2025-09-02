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

export const hotelComplete = (params) =>
    request.post('/hotel/payment/complete', params, { headers: getAuthHeader() });

export const hotelFail = (params) =>
    request.post('/hotel/payment/fail', params, { headers: getAuthHeader() });

export const hotelFinish = (params) =>
    request.post('/hotel/payment/finish', params, { headers: getAuthHeader() });

export const trainComplete = (params) =>
    request.post('/train/payment/complete', params, { headers: getAuthHeader() });

export const trainFail = (params) =>
    request.post('/train/payment/fail', params, { headers: getAuthHeader() });

export const trainFinish = (params) =>
    request.post('/train/payment/finish', params, { headers: getAuthHeader() });
