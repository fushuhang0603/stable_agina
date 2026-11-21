<template>
  <view :class="user.pageClass">
    <g-message />
    <g-nav-bar title="随访" :back="false" />
    <g-tabs v-model="tab" :tabs="tabs" />

    <view class="fa-body has-tabbar">
      <!-- 待随访 -->
      <template v-if="tab === 'todo'">
        <view class="h5-card fu-overview">
          <view class="fo-head">
            <text class="fo-title">随访概览</text>
            <text class="chip chip-blue">{{ user.patient?.risk || '—' }}风险</text>
          </view>
          <view class="fo-tiles">
            <view class="fo-tile fo-blue">
              <text class="v">{{ pendingCount }}</text>
              <text class="k">待随访</text>
            </view>
            <view class="fo-tile fo-green">
              <text class="v">{{ doneCount }}</text>
              <text class="k">已归档</text>
            </view>
            <view class="fo-tile" :class="overdueCount ? 'fo-red' : 'fo-gray'">
              <text class="v">{{ overdueCount }}</text>
              <text class="k">已逾期</text>
            </view>
          </view>
        </view>

        <view v-for="f in FOLLOWUPS" :key="f.id" class="h5-card fu-card">
          <view class="fu-head">
            <view class="fu-title-wrap">
              <text class="fu-type">{{ f.type }}</text>
              <text class="fu-id mono">{{ f.id }}</text>
            </view>
            <text class="chip" :class="statusCls(f.status)">{{ f.status }}</text>
          </view>

          <view class="fu-due" :class="{ urgent: f.risk === '高危' }">
            <g-icon name="clock" :size="26" />
            <text>截止 {{ f.dueDate }}</text>
            <text class="tip">{{ dueText(f.dueDate) }}</text>
          </view>

          <view class="fu-meta">
            <view class="fm">
              <text class="k">风险分层</text>
              <text class="v" :class="{ danger: f.risk === '高危' }">{{ f.risk }}</text>
            </view>
            <view class="fm">
              <text class="k">随访医生</text>
              <text class="v">{{ f.doctor }}</text>
            </view>
            <view class="fm">
              <text class="k">下发时间</text>
              <text class="v">{{ f.createdAt }}</text>
            </view>
            <view class="fm">
              <text class="k">填写方式</text>
              <text class="v">在线问卷</text>
            </view>
          </view>

          <view class="h5-btn primary block fu-btn" @click="openQuestionnaire(f)">
            <g-icon name="clipboard" :size="26" />
            <text>{{ f.status === '待随访' ? '填写随访问卷' : '问卷已提交' }}</text>
          </view>
        </view>

        <view v-if="!FOLLOWUPS.length" class="h5-card">
          <view class="empty">
            <g-icon name="check" :size="48" />
            <text>暂无待随访任务，保持规律记录</text>
          </view>
        </view>
      </template>

      <!-- 已归档 -->
      <template v-else-if="tab === 'done'">
        <view v-for="h in FOLLOWUP_HISTORY" :key="h.id" class="h5-card fu-card">
          <view class="fu-head">
            <view class="fu-title-wrap">
              <text class="fu-type">{{ h.type }}</text>
              <text class="fu-id mono">{{ h.id }}</text>
            </view>
            <text class="chip chip-green">已完成</text>
          </view>
          <view class="fu-done-row">
            <text class="t-12 ink-3">随访医生 {{ h.doctor }}</text>
            <text class="t-12 ink-3 ml-auto">{{ h.doneAt }}</text>
          </view>
          <view class="concl">
            <text class="concl-label">医生结论</text>
            <text class="concl-text">{{ h.conclusion }}</text>
          </view>
        </view>
        <view v-if="!FOLLOWUP_HISTORY.length" class="h5-card">
          <view class="empty">
            <g-icon name="doc" :size="48" />
            <text>暂无已归档随访记录</text>
          </view>
        </view>
      </template>

      <!-- 发起申请 -->
      <template v-else>
        <view class="h5-card">
          <view class="h5-sec no-rule sec-top">
            <text class="b">发起随访申请</text>
            <text class="sp">医生确认后与您联系</text>
          </view>

          <view class="form-row">
            <text class="lbl">随访方式</text>
            <view class="apply-types">
              <view
                v-for="t in APPLY_TYPES"
                :key="t.name"
                class="apply-type"
                :class="{ on: applyForm.type === t.name }"
                @click="applyForm.type = t.name"
              >
                <g-icon :name="t.icon" :size="34" />
                <text class="at-t">{{ t.name }}</text>
              </view>
            </view>
          </view>

          <view class="form-row">
            <text class="lbl">申请说明<text class="req">*</text></text>
            <textarea
              v-model="applyForm.reason"
              class="fa-textarea"
              maxlength="200"
              placeholder="请说明近期症状变化或希望咨询的问题"
              placeholder-class="ph"
            />
            <text class="word-count">{{ applyForm.reason.length }} / 200</text>
          </view>

          <view class="section-note warn">
            <g-icon name="warning" :size="28" class="note-ico" />
            <text>如出现持续胸痛超过 15 分钟、大汗、呼吸困难，请立即拨打 120，勿等待随访。</text>
          </view>
          <view class="h5-btn primary block mt-9" @click="submitApply">提交申请</view>
        </view>

        <view class="h5-sec">
          <text class="b">申请记录</text>
          <text class="ml-auto t-12 ink-3">{{ APPLY_LOGS.length }} 条</text>
        </view>
        <view class="h5-card">
          <view v-if="APPLY_LOGS.length" class="timeline">
            <view v-for="(a, i) in APPLY_LOGS" :key="i" class="tl" :class="i === 0 ? 'now' : 'done'">
              <view class="tl-rail">
                <view class="tl-dot" />
                <view v-if="i < APPLY_LOGS.length - 1" class="tl-line" />
              </view>
              <view class="tl-body">
                <view class="tl-top">
                  <text class="t">{{ a.type }}</text>
                  <text class="chip chip-blue tl-chip">{{ i === 0 ? '待确认' : '已结束' }}</text>
                </view>
                <view class="d">{{ a.time }} · {{ a.reason }}</view>
              </view>
            </view>
          </view>
          <view v-else class="empty">
            <g-icon name="message" :size="48" />
            <text>暂无申请记录</text>
          </view>
        </view>
      </template>
    </view>

    <g-tabbar current="followup" />

    <!-- 随访问卷弹层 -->
    <g-popup ref="quizPopup" title="随访问卷">
      <view class="quiz-tip">
        <g-icon name="clipboard" :size="26" />
        <text>{{ currentFollowup?.type }} · 截止 {{ currentFollowup?.dueDate }} · 共 {{ FOLLOWUP_QUESTIONS.length }} 题</text>
      </view>
      <view v-for="(q, i) in FOLLOWUP_QUESTIONS" :key="q.key" class="form-row">
        <text class="lbl">
          <text class="q-no">{{ i + 1 }}</text>{{ q.title }}<text class="req">*</text>
        </text>
        <view class="radio-row">
          <view
            v-for="opt in q.options"
            :key="opt"
            class="radio-box"
            :class="{ on: answers[q.key] === opt }"
            @click="answers[q.key] = opt"
          >
            {{ opt }}
          </view>
        </view>
      </view>
      <template #footer>
        <view class="h5-btn ghost" @click="quizPopup.hide()">取消</view>
        <view class="h5-btn primary" @click="submitQuiz">提交问卷</view>
      </template>
    </g-popup>
  </view>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { FOLLOWUPS, FOLLOWUP_HISTORY, FOLLOWUP_QUESTIONS } from '@/mock'
import { nowText, toast } from '@/utils/format'

const user = useUserStore()

const APPLY_TYPES = [
  { name: '电话随访', icon: 'phone' },
  { name: '门诊复诊', icon: 'calendar' },
  { name: '图文咨询', icon: 'message' }
]

const tab = ref('todo')
const tabs = computed(() => [
  { label: '待随访', value: 'todo' },
  { label: '已归档', value: 'done' },
  { label: '发起申请', value: 'apply' }
])

const quizPopup = ref(null)
const currentFollowup = ref(null)
const answers = reactive({})

const applyForm = reactive({ type: '电话随访', reason: '' })
const APPLY_LOGS = reactive([
  { time: '2026-10-08 14:20', type: '图文咨询', reason: '咨询硝酸甘油含服频次' }
])

const pendingCount = computed(() => FOLLOWUPS.filter((f) => f.status === '待随访').length)
const doneCount = computed(() => FOLLOWUP_HISTORY.length)
const overdueCount = computed(
  () => FOLLOWUPS.filter((f) => f.status === '待随访' && daysLeft(f.dueDate) < 0).length
)

onShow(() => {
  user.requireLogin()
})

/** 剩余天数：负数表示已逾期 */
function daysLeft(dateStr) {
  const base = new Date()
  base.setHours(0, 0, 0, 0)
  return Math.round((new Date(`${dateStr}T00:00:00`) - base) / 86400000)
}

function dueText(dateStr) {
  const days = daysLeft(dateStr)
  if (days < 0) return `已逾期 ${-days} 天`
  if (days === 0) return '今天截止'
  return `剩余 ${days} 天`
}

function statusCls(status) {
  if (status === '待随访') return 'chip-amber'
  if (status === '待医生确认') return 'chip-blue'
  return 'chip-gray'
}

function openQuestionnaire(f) {
  if (f.status !== '待随访') {
    toast('问卷已提交，等待医生确认')
    return
  }
  currentFollowup.value = f
  Object.keys(answers).forEach((k) => {
    answers[k] = ''
  })
  quizPopup.value.show()
}

function submitQuiz() {
  const missing = FOLLOWUP_QUESTIONS.find((q) => !answers[q.key])
  if (missing) {
    toast(`请完成「${missing.title}」`)
    return
  }
  if (currentFollowup.value) currentFollowup.value.status = '待医生确认'
  quizPopup.value.hide()
  toast('问卷已提交，等待医生确认')
}

function submitApply() {
  if (!applyForm.reason.trim()) {
    toast('请填写申请说明')
    return
  }
  APPLY_LOGS.unshift({
    time: nowText(),
    type: applyForm.type,
    reason: applyForm.reason.trim()
  })
  applyForm.reason = ''
  toast('申请已提交，医生确认后与您联系')
}
</script>

<style lang="scss" scoped>
.sec-top {
  padding-top: 0;
}
/* 概览 */
.fu-overview {
  padding: 12px;
}
.fo-head {
  display: flex;
  align-items: center;
  gap: 8px;
}
.fo-title {
  flex: 1;
  font-size: 13.5px;
  font-weight: 600;
  color: var(--ink);
}
.fo-tiles {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
  margin-top: 10px;
}
.fo-tile {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 9px 4px;
  border-radius: 10px;
}
.fo-tile .v {
  font-size: 17px;
  font-weight: 600;
  font-variant-numeric: tabular-nums;
}
.fo-tile .k {
  font-size: 10.5px;
}
.fo-blue {
  background: var(--brand-soft);
  color: var(--brand);
}
.fo-green {
  background: var(--green-soft);
  color: var(--green);
}
.fo-red {
  background: var(--red-soft);
  color: var(--red);
}
.fo-gray {
  background: var(--surface-2);
  color: var(--ink-3);
}
/* 随访卡片 */
.fu-head {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}
.fu-title-wrap {
  flex: 1;
  min-width: 0;
}
.fu-type {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: var(--ink);
}
.fu-id {
  display: block;
  margin-top: 2px;
  font-size: 11px;
  color: var(--ink-3);
}
.fu-due {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 10px;
  padding: 8px 10px;
  border-radius: 10px;
  background: var(--brand-soft);
  color: var(--brand);
  font-size: 12px;
}
.fu-due.urgent {
  background: var(--red-soft);
  color: var(--red);
}
.fu-due .tip {
  margin-left: auto;
  font-weight: 600;
  font-variant-numeric: tabular-nums;
}
.fu-meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
  margin-top: 10px;
}
.fm {
  padding: 8px 10px;
  border-radius: 10px;
  background: var(--surface-2);
}
.fm .k {
  display: block;
  font-size: 10.5px;
  color: var(--ink-3);
}
.fm .v {
  display: block;
  margin-top: 2px;
  font-size: 12.5px;
  font-weight: 500;
  color: var(--ink);
}
.fm .v.danger {
  color: var(--red);
}
.fu-btn {
  margin-top: 12px;
}
.fu-done-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
}
/* 医生结论 */
.concl {
  margin-top: 9px;
  padding: 9px 11px;
  border-radius: 10px;
  background: var(--surface-2);
}
.concl-label {
  display: block;
  font-size: 11.5px;
  font-weight: 600;
  color: var(--brand);
}
.concl-text {
  display: block;
  margin-top: 3px;
  font-size: 12.5px;
  color: var(--ink-2);
  line-height: 1.7;
}
/* 申请方式 */
.apply-types {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
}
.apply-type {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 5px;
  padding: 10px 4px;
  border: 1px solid var(--line);
  border-radius: 10px;
  background: #fff;
  color: var(--ink-2);
  font-size: 12px;
}
.apply-type.on {
  border-color: var(--brand);
  background: var(--brand-soft);
  color: var(--brand);
}
.at-t {
  font-size: 12px;
}
.fa-textarea {
  border-radius: 10px;
}
.word-count {
  display: block;
  text-align: right;
  font-size: 11px;
  color: var(--ink-3);
}
.note-ico {
  color: var(--amber);
}
/* 时间轴附加 */
.tl-top {
  display: flex;
  align-items: center;
  gap: 6px;
}
.tl-chip {
  flex: none;
}
/* 问卷弹层 */
.quiz-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 12px;
  padding: 8px 10px;
  border-radius: 10px;
  background: var(--brand-soft);
  font-size: 11.5px;
  color: var(--brand);
}
.q-no {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 15px;
  height: 15px;
  margin-right: 5px;
  border-radius: 50%;
  background: var(--brand-soft);
  font-size: 10px;
  font-weight: 600;
  color: var(--brand);
}
.radio-box {
  border-radius: 10px;
}
</style>
