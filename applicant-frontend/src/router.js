import { createRouter, createWebHistory } from 'vue-router';
import ApplicantsView from './views/ApplicantsView.vue';

const routes = [
  {
    path: '/',
    redirect: '/applicants',
  },
  {
    path: '/applicants',
    name: 'applicants',
    component: ApplicantsView,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
