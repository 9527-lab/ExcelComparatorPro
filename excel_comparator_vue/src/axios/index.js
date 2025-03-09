import axios from 'axios';



// 创建一个axios实例
const api = axios.create({
    baseURL: '/api',
    timeout: 60000,
});

// 请求拦截器
api.interceptors.request.use(
    config => {
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

// 响应拦截器
api.interceptors.response.use(
    response => {
        return response.data;
    },
    error => {
        // 处理响应错误
        return Promise.reject(error);
    }
);

export default api;
