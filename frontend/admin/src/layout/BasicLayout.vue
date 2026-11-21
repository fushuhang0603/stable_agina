<template>
  <el-container class="app">
    <el-header class="topbar" height="48px">
      <div class="brand">
        <BrandLogo :size="30" />
        <b>稳定性心绞痛患者随访管理系统</b>
      </div>
      <div class="topright">
        <div class="sys-status" title="AI 服务运行正常">
          <span class="status-dot"></span>
          <span>服务运行正常 · AI Mock</span>
        </div>
        <div class="clock">
          <span class="clock-date">{{ nowDate }}</span>
          <span class="clock-time">{{ nowTime }}</span>
        </div>
        <el-tooltip :content="isFullscreen ? '退出全屏' : '全屏'" placement="bottom">
          <el-button text circle @click="toggleFullscreen">
            <el-icon :size="16"><FullScreen v-if="!isFullscreen" /><Aim v-else /></el-icon>
          </el-button>
        </el-tooltip>
      </div>
    </el-header>

    <el-container class="body">
      <el-aside class="side" width="208px">
        <template v-for="g in navGroups" :key="g.group">
          <div class="side-title">{{ g.group }}</div>
          <el-menu :default-active="activeId" @select="onSelect">
            <el-menu-item v-for="it in g.items" :key="it.id" :index="it.id">
              <Icon :name="it.ico" :size="15" />
              <span>{{ it.title }}</span>
            </el-menu-item>
          </el-menu>
        </template>

        <div class="side-user">
          <div class="avatar" style="width:28px;height:28px">{{ userInitial }}</div>
          <div style="flex:1;min-width:0">
            <div style="font-size:12.5px;color:var(--ink)">{{ userName }}</div>
            <div style="font-size:11px;color:var(--ink-3)">{{ userOrg }}</div>
          </div>
          <el-button size="small" @click="logout">退出</el-button>
        </div>
      </el-aside>

      <el-container class="main-wrap" direction="vertical">
        <div class="tabsbar">
          <button class="tab-back" @click="router.push(homePath)">
            <Icon name="back" :size="12" />返回
          </button>
          <el-tag
            v-for="id in tabs"
            :key="id"
            :type="activeId === id ? 'primary' : 'info'"
            :effect="activeId === id ? 'light' : 'plain'"
            :closable="tabs.length > 1"
            disable-transitions
            class="tab-tag"
            @click="router.push('/' + id)"
            @close="closeTab(id)"
          >
            {{ PAGES[id].title }}
          </el-tag>
        </div>

        <el-main class="main">
          <div class="page-head">
            <h1>{{ page.title }}</h1>
            <span class="page-sub">{{ pageSub }}</span>
          </div>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Icon from '../components/Icon.vue'
import BrandLogo from '../components/BrandLogo.vue'
import { useAuthStore } from '../stores/auth'
import { AD_NAV, PAGES } from '../utils/pages'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const ROLE_LABEL = { SUPER_ADMIN: '系统管理员', ADMIN: '系统管理员', DOCTOR: '随访医生' }
const userName = computed(() => auth.state.user?.realName || auth.state.user?.username || '未登录')
const userInitial = computed(() => userName.value.charAt(0))
const userOrg = computed(() => {
  const roles = auth.state.user?.roles || []
  return roles.length ? roles.map((c) => ROLE_LABEL[c] || c).join(' / ') : ''
})

const navGroups = computed(() =>
  AD_NAV.map((group) => ({
    group: group.group,
    items: group.items.filter((item) => auth.hasPerm(PAGES[item.id]?.perm))
  })).filter((group) => group.items.length > 0)
)

const homePath = computed(() => '/' + (navGroups.value[0]?.items[0]?.id || 'dashboard'))

const WEEK = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
const now = ref(new Date())
const pad = (n) => String(n).padStart(2, '0')
const nowDate = computed(
  () => `${now.value.getFullYear()}-${pad(now.value.getMonth() + 1)}-${pad(now.value.getDate())} ${WEEK[now.value.getDay()]}`
)
const nowTime = computed(
  () => `${pad(now.value.getHours())}:${pad(now.value.getMinutes())}:${pad(now.value.getSeconds())}`
)

const isFullscreen = ref(false)
function onFullscreenChange() {
  isFullscreen.value = !!document.fullscreenElement
}
async function toggleFullscreen() {
  try {
    if (document.fullscreenElement) await document.exitFullscreen?.()
    else await document.documentElement.requestFullscreen?.()
  } catch {
    /* 浏览器不支持时忽略 */
  }
}

let clockTimer = null
onMounted(() => {
  onFullscreenChange()
  clockTimer = setInterval(() => (now.value = new Date()), 1000)
  document.addEventListener('fullscreenchange', onFullscreenChange)
})
onUnmounted(() => {
  if (clockTimer) clearInterval(clockTimer)
  document.removeEventListener('fullscreenchange', onFullscreenChange)
})

// 患者详情页复用「患者管理」菜单高亮与标签
const activeId = computed(() => (route.name === 'patient-detail' ? 'patient-list' : route.name))
const page = computed(() => PAGES[activeId.value] || PAGES['dashboard'])
const pageSub = computed(() => {
  if (route.name === 'patient-detail') return '档案 · 发作打卡 · 血压 · 用药 · 随访记录'
  return ''
})

const tabs = ref(['dashboard'])
watch(
  () => activeId.value,
  (name) => {
    if (!PAGES[name]) return
    if (!tabs.value.includes(name)) {
      tabs.value.push(name)
      if (tabs.value.length > 6) tabs.value.shift()
    }
  },
  { immediate: true }
)

function onSelect(id) {
  router.push('/' + id)
}

function closeTab(id) {
  tabs.value = tabs.value.filter((t) => t !== id)
  if (activeId.value === id) {
    router.push('/' + (tabs.value[tabs.value.length - 1] || 'dashboard'))
  }
}

async function logout() {
  await auth.logout()
  router.replace('/login')
}
</script>

<style scoped>
.tabsbar .el-tag {
  cursor: pointer;
  height: 24px;
  padding: 0 9px;
  font-size: 12px;
}
.tabsbar .el-tag + .el-tag {
  margin-left: 6px;
}
</style>
