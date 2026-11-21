<template>
  <view :class="user.pageClass">
    <g-message />
    <g-nav-bar title="消息" :back="false" right-text="全部已读" @right="readAll" />
    <g-tabs v-model="tab" :tabs="tabs" />
    <view class="fa-body has-tabbar">
      <template v-if="list.length">
        <view
          v-for="m in list"
          :key="m.id"
          class="h5-card msg-item"
          :class="{ unread: !m.read }"
          @click="openMessage(m)"
        >
          <view class="msg-head">
            <text class="chip" :class="typeClass(m.type)">{{ m.type }}</text>
            <text class="msg-time">{{ shortTimeText(m.time) }}</text>
          </view>
          <view class="msg-title-row">
            <text class="msg-title">{{ m.title }}</text>
            <view v-if="!m.read" class="unread-dot" />
          </view>
          <text class="msg-content">{{ m.content }}</text>
        </view>
      </template>
      <view v-else class="h5-card">
        <view class="empty">{{ tab === 'unread' ? '没有未读消息' : '暂无消息' }}</view>
      </view>
    </view>

    <g-tabbar current="message" />
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { MESSAGES } from '@/mock'
import { shortTimeText, toast } from '@/utils/format'

const user = useUserStore()

const tab = ref('all')
const unreadCount = computed(() => MESSAGES.filter((m) => !m.read).length)
const tabs = computed(() => [
  { label: '全部', value: 'all' },
  { label: `未读${unreadCount.value ? `(${unreadCount.value})` : ''}`, value: 'unread' }
])

const list = computed(() => (tab.value === 'unread' ? MESSAGES.filter((m) => !m.read) : MESSAGES))

onShow(() => {
  user.requireLogin()
})

function typeClass(type) {
  if (type === '随访待办') return 'chip-amber'
  if (type === '医生批复') return 'chip-blue'
  if (type === '高危提醒') return 'chip-red'
  if (type === '血压提醒' || type === '用药提醒') return 'chip-green'
  return ''
}

/** 自绘 tabbar 对应的 4 个主页面，需用 reLaunch 切换而非入栈 */
const TAB_PAGES = ['/pages/index/index', '/pages/followup/followup', '/pages/message/message', '/pages/profile/profile']

function openMessage(m) {
  m.read = true
  if (!m.link) return
  if (TAB_PAGES.includes(m.link)) uni.reLaunch({ url: m.link })
  else uni.navigateTo({ url: m.link })
}

function readAll() {
  if (!unreadCount.value) {
    toast('暂无未读消息')
    return
  }
  MESSAGES.forEach((m) => {
    m.read = true
  })
  toast('已全部标记为已读')
}
</script>

<style lang="scss" scoped>
.msg-item {
  position: relative;
  overflow: hidden;
}
.msg-item.unread::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: var(--brand);
}
.msg-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}
.msg-time {
  font-size: 12px;
  color: var(--ink-3);
}
.msg-title-row {
  display: flex;
  align-items: center;
  gap: 6px;
}
.msg-title {
  flex: 1;
  min-width: 0;
  font-size: 14.5px;
  font-weight: 600;
  color: var(--ink);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.unread-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: var(--red);
  flex-shrink: 0;
}
.msg-content {
  margin-top: 6px;
  font-size: 13px;
  color: var(--ink-2);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
