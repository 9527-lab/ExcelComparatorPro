import {createRouter, createWebHashHistory} from 'vue-router';

const routes = [
    {
        path: '/',
        redirect: '/index',
    },
    {
        path: '/index',
        component: () => import('@/pages/Index.vue'),
    },
];

const router = createRouter({
    history: createWebHashHistory(process.env.BASE_URL),
    // mode: 'hash',
    routes
});

export default router;
