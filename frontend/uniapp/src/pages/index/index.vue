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

    <g-tabbar current="home" />
  </view>
</template>

<script setup>
import { computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { ATTACKS, ATTACK_WEEK, FOLLOWUPS, TODAY_TASKS } from '@/mock'
import { greetText, todayText, toast } from '@/utils/format'

const user = useUserStore()

const tasks = TODAY_TASKS
const quickItems = [
  { text: '发作打卡', icon: 'heart', url: '/pages/angina/angina', cls: 'e-red' },
  { text: '血压录入', icon: 'activity', url: '/pages/bp/bp', cls: 'e-blue' },
  { text: '用药打卡', icon: 'pill', url: '/pages/medication/medication', cls: 'e-green' },
  { text: '我的档案', icon: 'doc', url: '/pages/archive/archive', cls: 'e-amber' }
]

const greet = computed(() => greetText())
const today = computed(() => todayText())
const doneCount = computed(() => tasks.filter((t) => t.done).length)
const taskPercent = computed(() => (tasks.length ? Math.round((doneCount.value / tasks.length) * 100) : 0))
const weekTotal = computed(() => ATTACK_WEEK.reduce((sum, d) => sum + d.count, 0))
const highRiskAttack = computed(() => ATTACKS.find((a) => a.high))
const pendingFollowup = computed(() => FOLLOWUPS.find((f) => f.status === '待随访'))

function barHeight(count) {
  if (!count) return '8px'
  return `${10 + count * 22}px`
}

onShow(() => {
  if (!user.isLogin) {
    uni.reLaunch({ url: '/pages/login/login' })
  }
})

function onTask(item) {
  if (item.done) {
    toast('该项今日已完成')
    return
  }
  if (item.url) go(item.url)
}

function go(url) {
  uni.navigateTo({ url })
}
</script>

<style lang="scss" scoped>
.home-page {
  padding-top: 10px;
  padding-bottom: calc(58px + env(safe-area-inset-bottom));
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
</style>
