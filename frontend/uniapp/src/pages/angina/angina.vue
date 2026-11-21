<template>
  <view :class="user.pageClass">
    <g-message />
    <g-nav-bar title="心绞痛发作打卡" />

    <view class="fa-body has-footer">
      <!-- 发作概况 -->
      <view class="h5-card">
        <view class="h5-sec no-rule" style="padding-top: 0">
          <text class="b">发作概况</text>
          <text class="ml-auto t-12 ink-3">近 30 天</text>
        </view>
        <view class="ov-grid">
          <view class="stat">
            <text class="v">{{ monthCount }}</text>
            <text class="k">发作次数</text>
          </view>
          <view class="stat">
            <text class="v" :class="{ danger: maxNrs >= 7 }">{{ maxNrs }}</text>
            <text class="k">最高 NRS</text>
          </view>
          <view class="stat">
            <text class="v">{{ highCount }}</text>
            <text class="k">高危记录</text>
          </view>
        </view>
        <view v-if="maxNrs >= 7" class="section-note red mt-9">
          <g-icon name="warning" :size="28" class="note-ico" />
          <text>本周期出现 NRS ≥ 7 分的发作，已自动提醒随访医生，请注意避免劳累与情绪激动。</text>
        </view>
        <view v-else class="section-note mt-9">
          <g-icon name="heart" :size="28" class="note-ico" />
          <text>坚持记录每次发作的时间、诱因与缓解方式，有助于医生判断病情稳定性。</text>
        </view>
      </view>

      <!-- 发作记录 -->
      <view class="h5-sec">
        <text class="b">发作记录</text>
        <text class="ml-auto t-12 ink-3">共 {{ attacks.length }} 条</text>
      </view>

      <view v-if="attacks.length" class="h5-card" style="padding: 4px 12px">
        <view v-for="(item, i) in attacks" :key="item.id" class="atk" :class="{ first: i === 0 }">
          <view class="fa-row">
            <view class="atk-time">
              <text class="atk-date">{{ item.time.slice(5, 10) }}</text>
              <text class="atk-clock">{{ item.time.slice(11) }}</text>
            </view>
            <view class="fa-row ml-auto" style="gap: 4px">
              <text v-if="item.high" class="chip chip-red">高危</text>
              <text class="chip" :class="'chip-' + nrsLevel(item.nrs).cls">NRS {{ item.nrs }}</text>
            </view>
          </view>
          <view class="atk-rows">
            <view class="atk-cell"><text class="k">持续</text><text class="v">{{ item.duration }}</text></view>
            <view class="atk-cell"><text class="k">诱因</text><text class="v">{{ item.trigger }}</text></view>
            <view class="atk-cell"><text class="k">缓解</text><text class="v">{{ item.relief }}（{{ item.reliefMin }} 分钟）</text></view>
          </view>
          <text v-if="item.note" class="atk-note">备注：{{ item.note }}</text>
        </view>
      </view>
      <view v-else class="h5-card">
        <view class="empty">暂无发作记录，保持得很好</view>
      </view>
    </view>

    <view class="fa-footer">
      <view class="h5-btn primary" @click="openForm">
        <g-icon name="plus" :size="24" />
        <text>新增发作打卡</text>
      </view>
    </view>

    <g-popup ref="formPopup" title="记录本次发作">
      <view class="form-row">
        <text class="lbl">发作时间<text class="req">*</text></text>
        <view class="picker-row">
          <picker mode="date" :value="form.date" @change="onDate">
            <view class="fa-picker">{{ form.date }}</view>
          </picker>
          <picker mode="time" :value="form.clock" @change="onClock">
            <view class="fa-picker">
              <text :class="form.clock ? '' : 'ph'">{{ form.clock || '选择时间' }}</text>
            </view>
          </picker>
        </view>
      </view>

      <view class="form-row">
        <view class="fa-row">
          <text class="lbl">疼痛程度 NRS<text class="req">*</text></text>
          <text class="ml-auto nrs-now" :class="nrsLevel(form.nrs).cls">
            {{ form.nrs }} 分 · {{ nrsLevel(form.nrs).text }}
          </text>
        </view>
        <view class="nrs-grid">
          <view
            v-for="n in 11"
            :key="n"
            class="radio-box nrs-box"
            :class="{ on: form.nrs === n - 1, danger: n - 1 >= 7, mid: n - 1 >= 4 && n - 1 < 7 }"
            @click="form.nrs = n - 1"
          >
            {{ n - 1 }}
          </view>
        </view>
      </view>

      <view class="form-row">
        <text class="lbl">持续时间（分钟）<text class="req">*</text></text>
        <input v-model="form.durationMin" class="fa-input" type="number" placeholder="如 8" placeholder-class="ph" />
      </view>

      <view class="form-row">
        <text class="lbl">诱发因素（可多选）</text>
        <view class="radio-row">
          <view
            v-for="t in triggers"
            :key="t"
            class="radio-box"
            :class="{ on: form.triggers.includes(t) }"
            @click="toggleTrigger(t)"
          >
            {{ t }}
          </view>
        </view>
      </view>

      <view class="form-row">
        <text class="lbl">缓解方式<text class="req">*</text></text>
        <view class="radio-row">
          <view
            v-for="r in reliefs"
            :key="r"
            class="radio-box"
            :class="{ on: form.relief === r }"
            @click="form.relief = r"
          >
            {{ r }}
          </view>
        </view>
      </view>

      <view class="form-row">
        <text class="lbl">缓解耗时（分钟）</text>
        <input v-model="form.reliefMin" class="fa-input" type="number" placeholder="如 3" placeholder-class="ph" />
      </view>

      <view class="form-row">
        <text class="lbl">备注</text>
        <textarea
          v-model="form.note"
          class="fa-textarea"
          maxlength="200"
          placeholder="补充说明，如发作时的活动、伴随症状"
          placeholder-class="ph"
        />
      </view>

      <view v-if="form.nrs >= 7" class="section-note red">
        <g-icon name="warning" :size="28" class="note-ico" />
        <text>NRS ≥ 7 分属于高危发作，保存后将自动提醒随访医生并计入高危统计。</text>
      </view>

      <template #footer>
        <view class="h5-btn ghost" @click="formPopup.hide()">取消</view>
        <view class="h5-btn primary" @click="save">保存打卡</view>
      </template>
    </g-popup>
  </view>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { ATTACKS } from '@/mock'
import { nrsLevel, todayText, toast } from '@/utils/format'

const user = useUserStore()
const attacks = ATTACKS

const triggers = ['劳累', '情绪激动', '饱餐', '受凉', '吸烟', '无明显诱因']
const reliefs = ['休息', '硝酸甘油含服', '其他']

const formPopup = ref(null)
const form = reactive({
  date: todayText(),
  clock: '',
  nrs: 5,
  durationMin: '',
  triggers: [],
  relief: '休息',
  reliefMin: '',
  note: ''
})

const monthCount = computed(() => attacks.filter((a) => a.time.startsWith('2026-11')).length)
const maxNrs = computed(() => (attacks.length ? Math.max(...attacks.map((a) => a.nrs)) : 0))
const highCount = computed(() => attacks.filter((a) => a.high).length)

onShow(() => {
  user.requireLogin()
})

function openForm() {
  const now = new Date()
  form.date = todayText(now)
  form.clock = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`
  form.nrs = 5
  form.durationMin = ''
  form.triggers = []
  form.relief = '休息'
  form.reliefMin = ''
  form.note = ''
  formPopup.value.show()
}

function onDate(e) {
  form.date = e.detail.value
}

function onClock(e) {
  form.clock = e.detail.value
}

function toggleTrigger(t) {
  const idx = form.triggers.indexOf(t)
  if (idx >= 0) form.triggers.splice(idx, 1)
  else form.triggers.push(t)
}

function save() {
  if (!form.clock) {
    toast('请选择发作时间')
    return
  }
  if (!form.durationMin) {
    toast('请填写持续时间')
    return
  }
  if (!form.triggers.length) {
    toast('请选择诱发因素')
    return
  }
  const high = form.nrs >= 7
  attacks.unshift({
    id: `A${Date.now()}`,
    time: `${form.date} ${form.clock}`,
    nrs: form.nrs,
    duration: `${form.durationMin} 分钟`,
    trigger: form.triggers.join('、'),
    relief: form.relief,
    reliefMin: Number(form.reliefMin) || 0,
    note: form.note.trim(),
    high
  })
  formPopup.value.hide()
  toast(high ? '已保存，并已提醒随访医生' : '打卡成功')
}
</script>

<style lang="scss" scoped>
.ov-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}
.stat .v.danger {
  color: var(--red);
}
/* 发作记录行 */
.atk {
  padding: 11px 0;
  border-top: 1px solid var(--line-2);
}
.atk.first {
  border-top: none;
}
.atk-time {
  display: flex;
  align-items: baseline;
  gap: 6px;
}
.atk-date {
  font-size: 14px;
  font-weight: 600;
  color: var(--ink);
}
.atk-clock {
  font-size: 11.5px;
  color: var(--ink-3);
}
.atk-rows {
  display: flex;
  flex-direction: column;
  gap: 3px;
  margin-top: 7px;
}
.atk-cell {
  display: flex;
  gap: 6px;
  font-size: 12px;
  line-height: 1.5;
}
.atk-cell .k {
  flex: none;
  width: 30px;
  color: var(--ink-3);
}
.atk-cell .v {
  flex: 1;
  min-width: 0;
  color: var(--ink-2);
}
.atk-note {
  display: block;
  margin-top: 7px;
  padding-top: 7px;
  border-top: 1px dashed var(--line);
  font-size: 11.5px;
  line-height: 1.6;
  color: var(--ink-3);
}
/* 表单 */
.picker-row {
  display: flex;
  gap: 8px;
}
.picker-row picker {
  flex: 1;
  min-width: 0;
}
.picker-row .fa-picker {
  width: 100%;
}
.nrs-now {
  font-size: 12px;
  color: var(--ink-3);
}
.nrs-now.red {
  color: var(--red);
}
.nrs-now.amber {
  color: var(--amber);
}
.nrs-now.green {
  color: var(--green);
}
.nrs-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 6px;
}
.nrs-grid .nrs-box {
  min-width: 0;
  height: 32px;
  padding: 0;
  font-size: 13px;
}
.nrs-box.mid.on {
  border-color: var(--amber);
  background: var(--amber-soft);
  color: var(--amber);
}
.nrs-box.danger.on {
  border-color: var(--red);
  background: var(--red-soft);
  color: var(--red);
}
</style>
