<template>
  <view class="fa-page home-page" :class="user.pageClass">
    <g-message />

    <!-- 患者概览 -->
    <view class="h5-card home-head">
      <view class="fa-row">
        <view class="hh-avatar">
          <g-icon name="user" :size="42" />
        </view>
        <view class="fa-grow">
          <view class="fa-row">
            <text class="hh-name">{{ greet }}，{{ user.patient?.name || '患者' }}</text>
            <text class="chip" :class="user.patient?.risk === '高危' ? 'chip-red' : 'chip-blue'">
              {{ user.patient?.risk || '—' }}
            </text>
          </view>
          <text class="hh-sub">{{ today }} · 规律记录有助于医生评估病情</text>
        </view>
      </view>
      <view class="hh-metrics">
        <view class="hm">
          <text class="v">{{ user.patient?.age || '—' }}</text>
          <text class="k">年龄</text>
        </view>
        <view class="hm-line" />
        <view class="hm">
          <text class="v">{{ user.patient?.doctor || '—' }}</text>
          <text class="k">随访医生</text>
        </view>
        <view class="hm-line" />
        <view class="hm">
          <text class="v">{{ user.patient?.nextVisit || '—' }}</text>
          <text class="k">下次随访</text>
        </view>
      </view>
    </view>

    <!-- 重点提示 -->
    <view class="emg-card">
      <view class="emg-head">
        <text class="emg-title">{{ pendingFollowup ? '本月度随访待完成' : '今日健康提醒' }}</text>
        <text class="emg-tag">{{ pendingFollowup ? '待随访' : '日常' }}</text>
      </view>
      <text class="emg-sub">
        {{ pendingFollowup
          ? `请于 ${pendingFollowup.dueDate} 前完成随访问卷，逾期将影响医生对您病情的评估。`
          : '发作时立即停止活动并休息，按医嘱含服硝酸甘油，5 分钟不缓解请立即拨打 120。' }}
      </text>
      <view class="emg-btns">
        <view class="emg-btn emg-btn-call" @click="go('/pages/angina/angina')">
          <g-icon name="heart" :size="30" />
          <text>记录发作</text>
        </view>
        <view class="emg-btn emg-btn-post" @click="go('/pages/followup/followup')">
          <g-icon name="clipboard" :size="30" />
          <text>{{ pendingFollowup ? '填写问卷' : '查看随访' }}</text>
        </view>
      </view>
    </view>

    <!-- 最新消息 -->
    <view class="h5-card">
      <view class="h5-sec no-rule">
        <text class="b">最新消息</text>
        <text class="sp">共 {{ messageTotal }} 条 · 未读 {{ unreadCount }}</text>
      </view>
      <view
        v-for="m in latestMessages"
        :key="m.id"
        class="msg-item"
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
      <view v-if="!latestMessages.length" class="empty">暂无消息</view>
    </view>

    <!-- 今日待办 -->
    <view class="h5-card">
      <view class="h5-sec no-rule task-sec">
        <text class="b">今日待办</text>
        <text class="sp">已完成 {{ doneCount }} / {{ tasks.length }} · {{ taskPercent }}%</text>
      </view>
      <view class="prog">
        <view class="prog-in" :style="{ width: taskPercent + '%' }" />
      </view>
      <view
        v-for="item in tasks"
        :key="item.key"
        class="h5-card task-card"
        :class="{ urgent: !item.done && item.key === 'followup' }"
        @click="onTask(item)"
      >
        <view class="task-top">
          <view class="tk-ico" :class="{ done: item.done }">
            <g-icon :name="item.icon" :size="30" />
          </view>
          <text class="sym">{{ item.title }}</text>
          <text class="chip" :class="item.done ? 'chip-green' : 'chip-amber'">{{ item.done ? '已完成' : '待完成' }}</text>
        </view>
        <view class="task-meta">
          <view class="task-meta-row">
            <text>{{ item.desc }}</text>
          </view>
        </view>
        <view class="task-foot">
          <text class="t-12 ink-3">{{ item.done ? '今日已打卡，注意保持' : '点击前往填写' }}</text>
          <text class="ml-auto task-go">{{ item.done ? '查看' : '去完成' }}</text>
          <g-icon name="arrowRight" :size="22" class="task-arrow" />
        </view>
      </view>
    </view>

    <!-- 快捷记录 -->
    <view class="h5-card">
      <view class="h5-sec no-rule">
        <text class="b">快捷记录</text>
        <text class="sp">实时同步随访医生</text>
      </view>
      <view class="entry-grid cols-4">
        <view v-for="q in quickItems" :key="q.url" class="entry" @click="go(q.url)">
          <view class="e-ico" :class="q.cls">
            <g-icon :name="q.icon" :size="40" />
          </view>
          <text class="e-label">{{ q.text }}</text>
        </view>
      </view>
    </view>

    <!-- 我的服务 -->
    <view class="h5-card">
      <view class="h5-sec no-rule">
        <text class="b">我的服务</text>
        <text class="sp">常用功能</text>
      </view>
      <view class="pf-list">
        <view v-for="s in services" :key="s.url" class="pin-list-row" @click="go(s.url)">
          <view class="ic" :class="s.ic">
            <g-icon :name="s.icon" :size="36" />
          </view>
          <view class="fa-grow">
            <text class="nm">{{ s.title }}</text>
            <text class="sub">{{ s.desc }}</text>
          </view>
          <g-icon name="arrowRight" :size="26" class="pf-arrow" />
        </view>
      </view>
    </view>

    <!-- 近 7 天发作 -->
    <view class="h5-card">
      <view class="h5-sec no-rule">
        <text class="b">近 7 天发作</text>
        <text class="sp">共 {{ weekTotal }} 次</text>
      </view>
      <view class="week-chart">
        <view v-for="(d, i) in ATTACK_WEEK" :key="i" class="wc-col">
          <view class="wc-bar" :class="{ on: d.count > 0 }" :style="{ height: barHeight(d.count) }" />
          <text class="wc-lbl">{{ d.label }}</text>
        </view>
      </view>
      <view v-if="highRiskAttack" class="section-note red mt-9">
        <g-icon name="warning" :size="28" class="note-ico" />
        <text>
          最近一次发作 NRS {{ highRiskAttack.nrs }} 分，已同步医生。请注意休息，如持续不缓解请立即就医。
        </text>
      </view>
    </view>

    <!-- 健康提示 -->
    <view class="h5-card">
      <view class="h5-sec no-rule">
        <text class="b">健康提示</text>
      </view>
      <view class="tips">
        <text>· 发作时立即停止活动并舌下含服硝酸甘油，5 分钟不缓解可重复一次</text>
        <text>· 血压测量前静坐 5 分钟，晨起服药前测量更准确</text>
        <text>· 他汀类药物建议睡前服用，出现肌肉酸痛及时上报</text>
      </view>
    </view>

    <!-- 显示设置 -->
    <view class="h5-card">
      <view class="h5-sec no-rule">
        <text class="b">显示设置</text>
        <text class="sp">仅本机生效</text>
      </view>
      <view class="pf-list">
        <view class="pin-list-row">
          <view class="ic ic-blue">
            <g-icon name="user" :size="36" />
          </view>
          <view class="fa-grow">
            <text class="nm">适老模式</text>
            <text class="sub">放大字号、加大按钮，便于阅读操作</text>
          </view>
          <view class="right">
            <switch :checked="user.modeOld" color="#165dff" @change="onModeOld" />
          </view>
        </view>
        <view class="pin-list-row">
          <view class="ic ic-amber">
            <g-icon name="heart" :size="36" />
          </view>
          <view class="fa-grow">
            <text class="nm">中医风格皮肤</text>
            <text class="sub">暖棕配色，视觉更柔和</text>
          </view>
          <view class="right">
            <switch :checked="user.tcmStyle" color="#165dff" @change="onTcm" />
          </view>
        </view>
      </view>
    </view>

    <!-- 安全与帮助 -->
    <view class="h5-card">
      <view class="h5-sec no-rule">
        <text class="b">安全与帮助</text>
      </view>
      <view class="pf-list">
        <view class="pin-list-row" @click="showPrivacy">
          <view class="ic ic-green">
            <g-icon name="shield" :size="36" />
          </view>
          <view class="fa-grow">
            <text class="nm">隐私与数据说明</text>
            <text class="sub">健康数据仅用于随访管理</text>
          </view>
          <g-icon name="arrowRight" :size="26" class="pf-arrow" />
        </view>
        <view class="pin-list-row" @click="callDoctor">
          <view class="ic ic-red">
            <g-icon name="phone" :size="36" />
          </view>
          <view class="fa-grow">
            <text class="nm">联系随访医生</text>
            <text class="sub">工作时间 08:00 - 17:30</text>
          </view>
          <g-icon name="arrowRight" :size="26" class="pf-arrow" />
        </view>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="h5-card pf-out">
      <view class="h5-btn danger block" @click="logout">退出登录</view>
      <text class="version">稳定性心绞痛随访 v1.0.0</text>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { ATTACKS, ATTACK_WEEK, FOLLOWUPS, MESSAGES, TODAY_TASKS } from '@/mock'
import { confirm, greetText, shortTimeText, todayText, toast } from '@/utils/format'

const user = useUserStore()

const tasks = TODAY_TASKS
const quickItems = [
  { text: '发作打卡', icon: 'heart', url: '/pages/angina/angina', cls: 'e-red' },
  { text: '血压录入', icon: 'activity', url: '/pages/bp/bp', cls: 'e-blue' },
  { text: '用药打卡', icon: 'pill', url: '/pages/medication/medication', cls: 'e-green' },
  { text: '我的档案', icon: 'doc', url: '/pages/archive/archive', cls: 'e-amber' }
]
const services = [
  { title: '我的医生', desc: '绑定申请与随访医生', icon: 'user', ic: 'ic-blue', url: '/pages/my-doctor/my-doctor' },
  { title: '健康档案', desc: '基本信息与基线资料', icon: 'doc', ic: 'ic-blue', url: '/pages/archive/archive' },
  { title: '我的随访', desc: '问卷、结论与申请', icon: 'clipboard', ic: 'ic-amber', url: '/pages/followup/followup' },
  { title: '发作记录', desc: '心绞痛打卡与趋势', icon: 'activity', ic: 'ic-red', url: '/pages/angina/angina' },
  { title: '血压记录', desc: '晨起 / 晚间血压', icon: 'heart', ic: 'ic-green', url: '/pages/bp/bp' },
  { title: '用药管理', desc: '医嘱、打卡与不良反应', icon: 'pill', ic: 'ic-blue', url: '/pages/medication/medication' }
]

const greet = computed(() => greetText())
const today = computed(() => todayText())
const doneCount = computed(() => tasks.filter((t) => t.done).length)
const taskPercent = computed(() => (tasks.length ? Math.round((doneCount.value / tasks.length) * 100) : 0))
const weekTotal = computed(() => ATTACK_WEEK.reduce((sum, d) => sum + d.count, 0))
const highRiskAttack = computed(() => ATTACKS.find((a) => a.high))
const pendingFollowup = computed(() => FOLLOWUPS.find((f) => f.status === '待随访'))

const latestMessages = computed(() => MESSAGES.slice(0, 3))
const messageTotal = computed(() => MESSAGES.length)
const unreadCount = computed(() => MESSAGES.filter((m) => !m.read).length)

function typeClass(type) {
  if (type === '随访待办') return 'chip-amber'
  if (type === '医生批复') return 'chip-blue'
  if (type === '高危提醒') return 'chip-red'
  if (type === '血压提醒' || type === '用药提醒') return 'chip-green'
  return ''
}

function barHeight(count) {
  if (!count) return '8px'
  return `${10 + count * 22}px`
}

onShow(() => {
  if (!user.isLogin) {
    uni.reLaunch({ url: '/pages/login/login' })
    return
  }
  // 同步医生绑定、危险分层等医生侧维护的信息
  user.fetchProfile().catch(() => {})
})

function onTask(item) {
  if (item.done) {
    toast('该项今日已完成')
    return
  }
  if (item.url) go(item.url)
}

function openMessage(m) {
  m.read = true
  if (m.link) go(m.link)
}

function onModeOld(e) {
  const val = e.detail.value
  user.setModeOld(val)
  toast(val ? '已开启适老模式' : '已关闭适老模式')
}

function onTcm(e) {
  const val = e.detail.value
  user.setTcmStyle(val)
  toast(val ? '已切换为中医风格' : '已恢复默认风格')
}

function showPrivacy() {
  confirm('您的健康数据仅用于随访管理与诊疗评估，医生端按权限查看，不会用于其他用途。', '隐私与数据说明')
}

function callDoctor() {
  uni.makePhoneCall({
    phoneNumber: '057100000000',
    fail: () => toast('演示环境暂不支持拨号')
  })
}

async function logout() {
  const ok = await confirm('退出后需重新登录，确定退出吗？', '退出登录')
  if (!ok) return
  user.logout()
  uni.reLaunch({ url: '/pages/login/login' })
}

function go(url) {
  uni.navigateTo({ url })
}
</script>

<style lang="scss" scoped>
.home-page {
  padding-top: 10px;
  padding-bottom: calc(16px + env(safe-area-inset-bottom));
}
/* 患者概览 */
.home-head {
  padding: 14px 12px 0;
}
.hh-avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: var(--brand-soft);
  color: var(--brand);
  display: flex;
  align-items: center;
  justify-content: center;
  flex: none;
}
.hh-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--ink);
}
.hh-sub {
  display: block;
  margin-top: 3px;
  font-size: 11.5px;
  color: var(--ink-3);
}
.hh-metrics {
  margin-top: 12px;
  display: flex;
  align-items: center;
  padding: 9px 0;
  border-top: 1px solid var(--line-2);
}
.hm {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}
.hm .v {
  font-size: 13px;
  font-weight: 600;
  color: var(--ink);
}
.hm .k {
  font-size: 10.5px;
  color: var(--ink-3);
}
.hm-line {
  width: 1px;
  height: 24px;
  background: var(--line-2);
}
/* 最新消息 */
.msg-item {
  position: relative;
  overflow: hidden;
  padding: 10px 0;
  border-bottom: 1px solid var(--line-2);
}
.msg-item:last-child {
  border-bottom: none;
}
.msg-item.unread::before {
  content: '';
  position: absolute;
  left: -12px;
  top: 8px;
  bottom: 8px;
  width: 3px;
  border-radius: 2px;
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
/* 待办进度 */
.task-sec {
  padding-top: 0;
}
.prog {
  height: 6px;
  border-radius: 99px;
  background: var(--brand-soft);
  overflow: hidden;
  margin-bottom: 9px;
}
.prog-in {
  height: 100%;
  border-radius: 99px;
  background: var(--brand);
  transition: width 0.3s;
}
.tk-ico {
  width: 26px;
  height: 26px;
  border-radius: 7px;
  background: var(--amber-soft);
  color: var(--amber);
  display: flex;
  align-items: center;
  justify-content: center;
  flex: none;
}
.tk-ico.done {
  background: var(--green-soft);
  color: var(--green);
}
.task-go {
  font-size: 11.5px;
  color: var(--brand);
  font-weight: 500;
}
.task-arrow {
  color: var(--brand);
}
/* 我的服务 / 显示设置 */
.pf-list {
  padding: 2px 0;
}
.pf-list .nm,
.pf-list .sub {
  display: block;
}
.pf-arrow {
  color: var(--ink-3);
  flex: none;
}
/* 近 7 天柱状图 */
.week-chart {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 6px;
  height: 66px;
  padding: 0 2px;
}
.wc-col {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-end;
  gap: 5px;
  height: 100%;
}
.wc-bar {
  width: 14px;
  border-radius: 3px;
  background: var(--line);
}
.wc-bar.on {
  background: linear-gradient(180deg, #5b8dff 0%, var(--brand) 100%);
}
.wc-lbl {
  font-size: 10px;
  color: var(--ink-3);
}
.note-ico {
  color: var(--red);
}
.tips {
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 12px;
  color: var(--ink-2);
  line-height: 1.7;
}
/* 退出登录 */
.pf-out {
  margin-top: 12px;
  padding: 14px 12px 12px;
}
.version {
  display: block;
  margin-top: 10px;
  text-align: center;
  font-size: 11.5px;
  color: var(--ink-3);
}
</style>
