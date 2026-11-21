import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { AD_NAV, PAGES } from '../utils/pages'

const routes = [
  { path: '/login', name: 'login', component: () => import('../views/Login.vue') },
  { path: '/403', name: 'forbidden', component: () => import('../views/Forbidden.vue') },
  {
    path: '/',
    component: () => import('../layout/BasicLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'dashboard', component: () => import('../views/Dashboard.vue') },
      { path: 'patient-list', name: 'patient-list', component: () => import('../views/PatientList.vue') },
      {
        path: 'patient-detail/:id',
        name: 'patient-detail',
        component: () => import('../views/PatientDetail.vue')
      },
      { path: 'followup-board', name: 'followup-board', component: () => import('../views/FollowupBoard.vue') },
      { path: 'ai-summary', name: 'ai-summary', component: () => import('../views/AiSummary.vue') },
      { path: 'medication-order', name: 'medication-order', component: () => import('../views/MedicationOrder.vue') },
      { path: 'adverse-reaction', name: 'adverse-reaction', component: () => import('../views/AdverseReaction.vue') },
      { path: 'user-management', name: 'user-management', component: () => import('../views/UserManagement.vue') },
      { path: 'role-management', name: 'role-management', component: () => import('../views/RoleManagement.vue') },
      { path: 'system-config', name: 'system-config', component: () => import('../views/SystemConfig.vue') },
      { path: 'dict-management', name: 'dict-management', component: () => import('../views/DictManagement.vue') },
      { path: 'operation-log', name: 'operation-log', component: () => import('../views/OperationLog.vue') },
      { path: 'ai-call-log', name: 'ai-call-log', component: () => import('../views/AiCallLog.vue') }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

/** 当前账号可访问的第一个菜单，用于无权限时兜底跳转 */
function firstAccessible(auth) {
  for (const group of AD_NAV) {
    for (const item of group.items) {
      if (auth.hasPerm(PAGES[item.id]?.perm)) return item.id
    }
  }
  return null
}

router.beforeEach(async (to) => {
  const auth = useAuthStore()

  if (to.path === '/login') {
    return auth.state.token ? { path: '/' } : true
  }
  if (!auth.state.token) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (!auth.state.user) {
    try {
      await auth.fetchMe()
    } catch {
      auth.clearSession()
      return { path: '/login', query: { redirect: to.fullPath } }
    }
  }
  if (to.name === 'forbidden') return true

  const perm = PAGES[to.name]?.perm
  if (perm && !auth.hasPerm(perm)) {
    const fallback = firstAccessible(auth)
    if (fallback && fallback !== to.name) return { name: fallback }
    return { path: '/403' }
  }
  return true
})

export default router
