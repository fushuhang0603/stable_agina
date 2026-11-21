<template>
  <view :class="user.pageClass">
    <g-message />
    <g-nav-bar title="用药管理" right-text="上报反应" @right="openReport('')" />
    <view class="fa-body has-footer">
      <!-- 今日服药打卡 -->
      <view class="h5-card">
        <view class="h5-sec no-rule sec-top">
          <text class="b">今日服药打卡</text>
          <text class="sp">已服 {{ checkedCount }} / {{ MED_ORDERS.length }}</text>
        </view>
        <view class="prog">
          <view class="prog-in" :class="checkPercent >= 100 ? 'green' : 'amber'" :style="{ width: checkPercent + '%' }" />
        </view>
        <view v-for="o in MED_ORDERS" :key="o.id" class="chk-row" @click="toggleCheck(o)">
          <view class="fa-check" :class="{ on: MED_CHECKS[o.id] }">
            <g-icon v-if="MED_CHECKS[o.id]" name="check" :size="24" />
          </view>
          <view class="fa-grow">
            <text class="chk-name">{{ o.drug }}</text>
            <text class="chk-desc">{{ o.dosage }}</text>
          </view>
          <text class="chip chip-blue">{{ o.period }}</text>
        </view>
      </view>

      <!-- 长期医嘱 -->
      <view class="h5-sec">
        <text class="b">长期医嘱</text>
        <text class="ml-auto t-12 ink-3">共 {{ MED_ORDERS.length }} 条</text>
      </view>
      <view class="h5-card">
        <view v-for="(o, i) in MED_ORDERS" :key="o.id" class="med-item" :class="{ first: i === 0 }">
          <view class="med-top">
            <text class="med-name">{{ o.drug }}</text>
            <text class="chip chip-green">{{ o.status }}</text>
          </view>
          <view class="med-meta">
            <text>{{ o.spec }}</text>
            <text class="dot">·</text>
            <text>{{ o.period }}</text>
          </view>
          <text class="med-dosage">{{ o.dosage }}</text>
          <text class="med-cycle">用药周期：{{ o.cycle }}</text>
          <view class="adh">
            <text class="adh-label">近 30 天依从率</text>
            <view class="adh-bar">
              <view class="adh-in" :class="adhClass(o.adherence)" :style="{ width: o.adherence + '%' }" />
            </view>
            <text class="adh-num" :class="adhClass(o.adherence)">{{ o.adherence }}%</text>
          </view>
        </view>
      </view>

      <!-- 不良反应上报 -->
      <view class="h5-sec">
        <text class="b">不良反应上报</text>
        <text class="ml-auto t-12 ink-3">{{ ADVERSE_REACTIONS.length }} 条</text>
      </view>
      <view class="h5-card">
        <view v-if="ADVERSE_REACTIONS.length">
          <view v-for="(r, i) in ADVERSE_REACTIONS" :key="r.id" class="ar-item" :class="{ first: i === 0 }">
            <view class="ar-top">
              <text class="ar-drug">{{ r.drug }} · {{ r.type }}</text>
              <text class="chip" :class="statusClass(r.status)">{{ r.status }}</text>
            </view>
            <text class="ar-desc">{{ r.desc }}</text>
            <text class="ar-time">上报时间：{{ r.reportedAt }}</text>
            <view v-if="r.reply" class="ar-reply">
              <text class="reply-label">医生回复</text>
              <text class="reply-text">{{ r.reply }}</text>
            </view>
          </view>
        </view>
        <view v-else class="empty">暂无不良反应上报</view>
      </view>
    </view>

    <view class="fa-footer">
      <view class="h5-btn primary" @click="openReport('')">
        <g-icon name="plus" :size="26" />
        <text>上报不良反应</text>
      </view>
    </view>

    <!-- 不良反应上报弹层 -->
    <g-popup ref="reportPopup" title="上报不良反应">
      <view class="form-row">
        <text class="lbl">涉及药物<text class="req">*</text></text>
        <view class="radio-row">
          <view
            v-for="o in MED_ORDERS"
            :key="o.id"
            class="radio-box"
            :class="{ on: form.drug === o.drug }"
            @click="form.drug = o.drug"
          >
            {{ o.drug }}
          </view>
        </view>
      </view>
      <view class="form-row">
        <text class="lbl">反应类型<text class="req">*</text></text>
        <view class="radio-row">
          <view
            v-for="t in REACTION_TYPES"
            :key="t"
            class="radio-box"
            :class="{ on: form.type === t }"
            @click="form.type = t"
          >
            {{ t }}
          </view>
        </view>
      </view>
      <view class="form-row">
        <text class="lbl">具体情况说明<text class="req">*</text></text>
        <textarea
          v-model="form.desc"
          class="fa-textarea"
          maxlength="200"
          placeholder="请描述症状出现时间、部位、程度及是否伴随其他不适"
          placeholder-class="ph"
        />
        <text class="word-count">{{ form.desc.length }} / 200</text>
      </view>
      <view class="section-note warn">
        <g-icon name="warning" :size="28" class="note-ico" />
        <text>上报后医生将收到提醒，请勿自行停药或调整剂量。</text>
      </view>
      <template #footer>
        <view class="h5-btn ghost" @click="reportPopup.hide()">取消</view>
        <view class="h5-btn primary" @click="submitReport">提交上报</view>
      </template>
    </g-popup>
  </view>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { ADVERSE_REACTIONS, MED_CHECKS, MED_ORDERS } from '@/mock'
import { nowText, toast } from '@/utils/format'

const user = useUserStore()

const REACTION_TYPES = ['肌肉酸痛', '头晕乏力', '胃肠道不适', '皮疹瘙痒', '心率偏慢', '其他']

const reportPopup = ref(null)
const form = reactive({ drug: '', type: '', desc: '' })

const checkedCount = computed(() => MED_ORDERS.filter((o) => MED_CHECKS[o.id]).length)
const checkPercent = computed(() => Math.round((checkedCount.value / MED_ORDERS.length) * 100))

onShow(() => {
  user.requireLogin()
})

/** 依从率颜色：<60 红 / <80 琥珀 / 其余绿 */
function adhClass(rate) {
  if (rate < 60) return 'red'
  if (rate < 80) return 'amber'
  return 'green'
}

/** 上报状态标签色 */
function statusClass(status) {
  if (status === '待处置') return 'chip-amber'
  if (status === '处置中') return 'chip-blue'
  return 'chip-green'
}

function toggleCheck(order) {
  MED_CHECKS[order.id] = !MED_CHECKS[order.id]
  toast(MED_CHECKS[order.id] ? `${order.drug} 已打卡` : `已取消 ${order.drug} 打卡`)
}

function openReport(drug) {
  form.drug = drug || ''
  form.type = ''
  form.desc = ''
  reportPopup.value.show()
}

function submitReport() {
  if (!form.drug) {
    toast('请选择涉及药物')
    return
  }
  if (!form.type) {
    toast('请选择反应类型')
    return
  }
  if (!form.desc.trim()) {
    toast('请填写具体情况说明')
    return
  }
  ADVERSE_REACTIONS.unshift({
    id: `AR${Date.now()}`,
    drug: form.drug,
    type: form.type,
    desc: form.desc.trim(),
    reportedAt: nowText(),
    status: '待处置',
    reply: ''
  })
  reportPopup.value.hide()
  toast('上报成功，已提醒随访医生')
}
</script>

<style lang="scss" scoped>
.sec-top {
  padding-top: 0;
}
/* 进度条 */
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
  transition: width 0.3s;
}
.prog-in.amber {
  background: var(--amber);
}
.prog-in.green {
  background: var(--green);
}
/* 打卡行 */
.chk-row {
  display: flex;
  align-items: center;
  gap: 9px;
  padding: 9px 0;
  border-top: 1px solid var(--line-2);
}
.chk-row:first-of-type {
  border-top: 0;
}
.chk-row .fa-check {
  color: #fff;
}
.chk-name {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: var(--ink);
}
.chk-desc {
  display: block;
  margin-top: 1px;
  font-size: 11.5px;
  color: var(--ink-3);
}
/* 长期医嘱 */
.med-item {
  padding: 11px 0;
  border-top: 1px solid var(--line-2);
}
.med-item.first {
  border-top: 0;
  padding-top: 1px;
}
.med-item:last-child {
  padding-bottom: 1px;
}
.med-top {
  display: flex;
  align-items: center;
  gap: 8px;
}
.med-name {
  flex: 1;
  min-width: 0;
  font-size: 13.5px;
  font-weight: 600;
  color: var(--ink);
}
.med-meta {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 3px;
  font-size: 11.5px;
  color: var(--ink-3);
}
.med-meta .dot {
  color: #c9cdd4;
}
.med-dosage {
  display: block;
  margin-top: 4px;
  font-size: 12.5px;
  color: var(--ink-2);
}
.med-cycle {
  display: block;
  margin-top: 2px;
  font-size: 11.5px;
  color: var(--ink-3);
}
.adh {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px dashed var(--line);
}
.adh-label {
  flex: none;
  font-size: 11.5px;
  color: var(--ink-3);
}
.adh-bar {
  flex: 1;
  height: 5px;
  border-radius: 99px;
  background: var(--surface-2);
  overflow: hidden;
}
.adh-in {
  height: 100%;
  border-radius: 99px;
}
.adh-in.red {
  background: var(--red);
}
.adh-in.amber {
  background: var(--amber);
}
.adh-in.green {
  background: var(--green);
}
.adh-num {
  width: 36px;
  text-align: right;
  font-size: 11.5px;
  font-weight: 600;
  font-variant-numeric: tabular-nums;
}
.adh-num.red {
  color: var(--red);
}
.adh-num.amber {
  color: var(--amber);
}
.adh-num.green {
  color: var(--green);
}
/* 不良反应 */
.ar-item {
  padding: 11px 0;
  border-top: 1px solid var(--line-2);
}
.ar-item.first {
  border-top: 0;
  padding-top: 1px;
}
.ar-item:last-child {
  padding-bottom: 1px;
}
.ar-top {
  display: flex;
  align-items: center;
  gap: 8px;
}
.ar-drug {
  flex: 1;
  min-width: 0;
  font-size: 13px;
  font-weight: 600;
  color: var(--ink);
}
.ar-desc {
  display: block;
  margin-top: 5px;
  font-size: 12.5px;
  color: var(--ink-2);
  line-height: 1.65;
}
.ar-time {
  display: block;
  margin-top: 4px;
  font-size: 11.5px;
  color: var(--ink-3);
}
.ar-reply {
  margin-top: 8px;
  padding: 8px 10px;
  border-radius: 8px;
  background: var(--brand-soft);
}
.reply-label {
  display: block;
  font-size: 11.5px;
  font-weight: 600;
  color: var(--brand);
}
.reply-text {
  display: block;
  margin-top: 3px;
  font-size: 12px;
  color: #2a55a8;
  line-height: 1.6;
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
</style>
