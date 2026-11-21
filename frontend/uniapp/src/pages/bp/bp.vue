<template>
  <view :class="user.pageClass">
    <g-message />
    <g-nav-bar title="血压记录" />
    <view class="fa-body">
      <!-- 记录血压 -->
      <view class="h5-card">
        <view class="h5-sec no-rule sec-top">
          <text class="b">记录血压</text>
          <text class="sp">{{ today }} 测量后请及时打卡</text>
        </view>
        <view class="seg">
          <view class="chip" :class="{ on: form.period === '晨起' }" @click="form.period = '晨起'">晨起</view>
          <view class="chip" :class="{ on: form.period === '晚间' }" @click="form.period = '晚间'">晚间</view>
        </view>
        <view class="bp-inputs">
          <view class="bp-cell">
            <input v-model="form.sbp" class="bp-num" type="number" placeholder="--" placeholder-class="ph" />
            <text class="bp-lbl">收缩压 mmHg</text>
          </view>
          <text class="bp-slash">/</text>
          <view class="bp-cell">
            <input v-model="form.dbp" class="bp-num" type="number" placeholder="--" placeholder-class="ph" />
            <text class="bp-lbl">舒张压 mmHg</text>
          </view>
          <view class="bp-divider" />
          <view class="bp-cell">
            <input v-model="form.hr" class="bp-num" type="number" placeholder="--" placeholder-class="ph" />
            <text class="bp-lbl">心率 次/分</text>
          </view>
        </view>
        <view v-if="previewLevel" class="bp-preview">
          <text class="chip" :class="previewLevel === '偏高' ? 'chip-amber' : 'chip-green'">{{ previewLevel }}</text>
          <text class="t-12 ink-3">{{ previewTip }}</text>
        </view>
        <view class="h5-btn primary block mt-9" @click="save">保存记录</view>
      </view>

      <!-- 血压趋势 -->
      <view class="h5-card">
        <view class="h5-sec no-rule sec-top">
          <text class="b">血压趋势</text>
          <view class="seg bp-range">
            <view class="chip" :class="{ on: range === 7 }" @click="range = 7">7 天</view>
            <view class="chip" :class="{ on: range === 14 }" @click="range = 14">14 天</view>
          </view>
        </view>
        <view class="chart-wrap" v-html="chartSvg" />
        <view class="legend">
          <view class="lg-item"><view class="lg-dot blue" />收缩压</view>
          <view class="lg-item"><view class="lg-dot teal" />舒张压</view>
          <view class="lg-item"><view class="lg-dash" />140 mmHg 阈值</view>
        </view>
        <view class="section-note warn mt-9">
          <g-icon name="warning" :size="28" class="note-ico" />
          <text>晨起血压连续多次超过 140/90 mmHg，建议复诊评估降压方案。</text>
        </view>
      </view>

      <!-- 历史记录 -->
      <view class="h5-card">
        <view class="h5-sec no-rule sec-top">
          <text class="b">历史记录</text>
          <text class="sp">共 {{ records.length }} 条</text>
        </view>
        <view v-if="records.length">
          <view v-for="r in records" :key="r.id" class="bp-row">
            <view class="bp-row-l">
              <text class="date">{{ r.date }}</text>
              <text class="period">{{ r.period }}</text>
            </view>
            <view class="bp-row-m">
              <text class="val" :class="{ red: r.level === '偏高' }">{{ r.sbp }}/{{ r.dbp }}</text>
              <text class="hr-txt">心率 {{ r.hr }}</text>
            </view>
            <text class="chip" :class="r.level === '偏高' ? 'chip-amber' : 'chip-green'">{{ r.level }}</text>
          </view>
        </view>
        <view v-else class="empty">暂无血压记录</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { BP_RECORDS, BP_TREND } from '@/mock'
import { bpLevel, todayText, toast, confirm } from '@/utils/format'

const user = useUserStore()
const records = BP_RECORDS
const range = ref(7)
const today = computed(() => todayText())

const form = reactive({ period: '晨起', sbp: '', dbp: '', hr: '' })

const previewLevel = computed(() => {
  if (!form.sbp || !form.dbp) return ''
  return bpLevel(form.sbp, form.dbp)
})
const previewTip = computed(() =>
  previewLevel.value === '偏高' ? '本次测量高于 140/90 mmHg 阈值' : '本次测量在达标范围内'
)

onShow(() => {
  user.requireLogin()
})

/** 折线图（内联 SVG，避免引入图表库） */
const chartSvg = computed(() => {
  const dates = BP_TREND.dates.slice(-range.value)
  const sbp = BP_TREND.sbp.slice(-range.value)
  const dbp = BP_TREND.dbp.slice(-range.value)
  const W = 340
  const H = 170
  const padL = 26
  const padR = 8
  const padT = 12
  const padB = 24
  const min = 60
  const max = 170
  const stepX = (W - padL - padR) / (dates.length - 1)
  const y = (v) => padT + ((max - v) / (max - min)) * (H - padT - padB)
  const x = (i) => padL + i * stepX
  const line = (arr) => arr.map((v, i) => `${x(i).toFixed(1)},${y(v).toFixed(1)}`).join(' ')
  const dots = (arr, color) =>
    arr
      .map((v, i) => `<circle cx="${x(i).toFixed(1)}" cy="${y(v).toFixed(1)}" r="${v >= 140 ? 3.2 : 2.2}" fill="${v >= 140 ? '#f53f3f' : color}"/>`)
      .join('')
  const labels = dates
    .map((d, i) => (i % Math.ceil(dates.length / 5) === 0 ? `<text x="${x(i).toFixed(1)}" y="${H - 6}" font-size="8" fill="#86909c" text-anchor="middle">${d}</text>` : ''))
    .join('')
  const grid = [90, 120, 140].map((v) => `<line x1="${padL}" y1="${y(v).toFixed(1)}" x2="${W - padR}" y2="${y(v).toFixed(1)}" stroke="${v === 140 ? '#ffd6a8' : '#e3ebf7'}" stroke-width="0.6" stroke-dasharray="${v === 140 ? '3 2' : '2 3'}"/><text x="${padL - 4}" y="${(y(v) + 3).toFixed(1)}" font-size="7" fill="#86909c" text-anchor="end">${v}</text>`).join('')
  return `<svg viewBox="0 0 ${W} ${H}" width="100%" height="100%" xmlns="http://www.w3.org/2000/svg">
    ${grid}
    <polyline points="${line(sbp)}" fill="none" stroke="#165dff" stroke-width="1.6" stroke-linejoin="round" stroke-linecap="round"/>
    <polyline points="${line(dbp)}" fill="none" stroke="#0fc6c2" stroke-width="1.6" stroke-linejoin="round" stroke-linecap="round"/>
    ${dots(sbp, '#165dff')}
    ${dots(dbp, '#0fc6c2')}
    ${labels}
  </svg>`
})

function save() {
  const sbp = Number(form.sbp)
  const dbp = Number(form.dbp)
  const hr = Number(form.hr)
  if (!sbp || !dbp) {
    toast('请填写收缩压与舒张压')
    return
  }
  if (sbp < 60 || sbp > 260 || dbp < 30 || dbp > 160) {
    toast('血压数值超出合理范围，请核对')
    return
  }
  if (sbp <= dbp) {
    toast('收缩压应大于舒张压')
    return
  }
  const level = bpLevel(sbp, dbp)
  records.unshift({
    id: `B${Date.now()}`,
    date: todayText(),
    period: form.period,
    sbp,
    dbp,
    hr: hr || 0,
    level
  })
  form.sbp = ''
  form.dbp = ''
  form.hr = ''

  if (sbp >= 180 || dbp >= 110) {
    confirm('本次血压显著升高，建议静坐 5 分钟后复测；若伴胸痛、头晕、视物模糊，请立即就医或拨打 120。', '血压预警')
  } else {
    toast('血压记录已保存')
  }
}
</script>

<style lang="scss" scoped>
.sec-top {
  padding-top: 0;
}
.bp-range {
  margin: 0 0 0 auto;
}
.bp-inputs {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 8px;
  padding: 4px 0 2px;
}
.bp-cell {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}
.bp-num {
  width: 100%;
  height: 48px;
  text-align: center;
  font-size: 20px;
  font-weight: 600;
  color: var(--ink);
  background: var(--surface-2);
  border: 1px solid var(--line);
  border-radius: 8px;
}
.bp-lbl {
  font-size: 11px;
  color: var(--ink-3);
}
.bp-slash {
  font-size: 18px;
  color: var(--ink-3);
  padding-top: 13px;
}
.bp-divider {
  width: 1px;
  height: 48px;
  background: var(--line);
  margin-top: 2px;
}
.bp-preview {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
}
.chart-wrap {
  width: 100%;
  height: 170px;
}
.legend {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 8px;
  font-size: 11px;
  color: var(--ink-3);
}
.lg-item {
  display: flex;
  align-items: center;
  gap: 4px;
}
.lg-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
}
.lg-dot.blue {
  background: var(--brand);
}
.lg-dot.teal {
  background: var(--teal);
}
.lg-dash {
  width: 12px;
  border-top: 1px dashed var(--amber);
}
.note-ico {
  color: var(--amber);
}
.bp-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid var(--line-2);
}
.bp-row:last-child {
  border-bottom: 0;
  padding-bottom: 0;
}
.bp-row-l {
  width: 76px;
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex: none;
}
.bp-row-l .date {
  font-size: 12px;
  color: var(--ink);
}
.bp-row-l .period {
  font-size: 11px;
  color: var(--ink-3);
}
.bp-row-m {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: baseline;
  gap: 7px;
}
.bp-row-m .val {
  font-size: 14px;
  font-weight: 600;
  color: var(--ink);
  font-variant-numeric: tabular-nums;
}
.bp-row-m .val.red {
  color: var(--red);
}
.bp-row-m .hr-txt {
  font-size: 11px;
  color: var(--ink-3);
}
</style>
