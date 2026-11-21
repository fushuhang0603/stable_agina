<template>
  <div class="followup-board">
    <el-card shadow="never" class="board-bar">
      <div class="board-bar-in">
        <div class="board-stat">
          <span class="k">待随访</span>
          <b>{{ groups.pending.length }}</b>
        </div>
        <div class="board-stat">
          <span class="k">随访中</span>
          <b class="blue">{{ groups.processing.length }}</b>
        </div>
        <div class="board-stat">
          <span class="k">已逾期</span>
          <b class="red">{{ groups.overdue.length }}</b>
        </div>
        <div class="board-stat">
          <span class="k">已完成</span>
          <b class="green">{{ groups.done.length }}</b>
        </div>
        <div class="board-stat">
          <span class="k">已驳回</span>
          <b class="amber">{{ groups.rejected.length }}</b>
        </div>
        <div class="board-filter">
          <el-select v-model="doctor" placeholder="随访医生" clearable style="width: 132px">
            <el-option v-for="o in doctorOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
          <el-button @click="toast('演示环境：已按当前条件刷新工单')">刷新</el-button>
        </div>
      </div>
    </el-card>

    <div class="board">
      <section v-for="col in columns" :key="col.key" class="board-col">
        <header class="board-col-h">
          <span class="dot" :style="{ background: col.color }"></span>
          <b>{{ col.title }}</b>
          <span class="cnt">{{ col.rows.length }}</span>
        </header>
        <div class="board-col-b">
          <article v-for="row in col.rows" :key="row.id" class="fu-card" :class="{ overdue: row.status === '已逾期' }">
            <div class="fu-top">
              <span class="fu-name" @click="goPatient(row.patientId)">{{ row.patientName }}</span>
              <Chip :text="row.risk" />
              <span class="fu-due" :class="{ red: row.status === '已逾期' }">{{ row.dueDate }}</span>
            </div>
            <div class="fu-meta">
              <span>{{ row.type }}</span>
              <span>{{ row.doctor }}</span>
            </div>
            <div class="fu-no mono">{{ row.id }}</div>
            <div v-if="row.conclusion" class="fu-conclusion">{{ row.conclusion }}</div>
            <div class="fu-ops">
              <el-button v-if="row.status !== '已完成'" size="small" type="primary" @click="openRecord(row)">记录随访</el-button>
              <el-button v-if="row.status === '已完成'" size="small" @click="archive(row)">归档</el-button>
              <el-button v-if="row.status === '随访中'" size="small" type="danger" @click="reject(row)">驳回</el-button>
              <el-button size="small" @click="goPatient(row.patientId)">档案</el-button>
            </div>
          </article>
          <el-empty v-if="!col.rows.length" description="暂无工单" :image-size="56" />
        </div>
      </section>
    </div>

    <el-dialog v-model="showModal" title="记录随访结论" width="560px">
      <el-form label-width="92px">
        <el-form-item label="工单">
          <div class="order-line">
            <b>{{ current.patientName }}</b>
            <span class="mono">{{ current.id }}</span>
            <Chip :text="current.risk" />
            <span class="t-muted">{{ current.type }} · 计划 {{ current.dueDate }}</span>
          </div>
        </el-form-item>
        <div class="form-2">
          <el-form-item label="症状变化" required>
            <el-select v-model="form.symptom">
              <el-option value="明显好转" label="明显好转" />
              <el-option value="基本稳定" label="基本稳定" />
              <el-option value="加重" label="加重" />
            </el-select>
          </el-form-item>
          <el-form-item label="随访方式">
            <el-select v-model="form.channel">
              <el-option value="电话" label="电话" />
              <el-option value="门诊" label="门诊" />
              <el-option value="线上图文" label="线上图文" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="随访结论" required>
          <el-input
            v-model="form.conclusion"
            type="textarea"
            :rows="4"
            resize="none"
            placeholder="请填写症状评估、用药调整与下次随访安排"
          />
        </el-form-item>
        <el-form-item label="是否升级处理">
          <el-radio-group v-model="form.escalate">
            <el-radio value="否">继续观察</el-radio>
            <el-radio value="是">建议门诊复评</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showModal = false">取消</el-button>
        <el-button type="primary" @click="submit">提交并完成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import Chip from '../components/Chip.vue'
import { FOLLOWUPS, nowText } from '../mock'
import { toast } from '../utils/toast'

const router = useRouter()
const doctor = ref('')
const doctorOptions = [
  { value: '李医生', label: '李医生' },
  { value: '王医生', label: '王医生' }
]

const rows = computed(() => FOLLOWUPS.filter((row) => !doctor.value || row.doctor === doctor.value))
const groups = computed(() => ({
  pending: rows.value.filter((row) => row.status === '待随访'),
  processing: rows.value.filter((row) => row.status === '随访中'),
  overdue: rows.value.filter((row) => row.status === '已逾期'),
  done: rows.value.filter((row) => row.status === '已完成'),
  rejected: rows.value.filter((row) => row.status === '已驳回')
}))

const columns = computed(() => [
  { key: 'pending', title: '待随访', color: '#FF7D00', rows: groups.value.pending },
  { key: 'processing', title: '随访中', color: '#165DFF', rows: [...groups.value.processing, ...groups.value.overdue] },
  { key: 'done', title: '已完成', color: '#00B42A', rows: groups.value.done },
  { key: 'rejected', title: '已驳回', color: '#F53F3F', rows: groups.value.rejected }
])

const showModal = ref(false)
const current = ref({})
const form = reactive({ symptom: '基本稳定', channel: '电话', conclusion: '', escalate: '否' })

function openRecord(row) {
  current.value = row
  form.symptom = '基本稳定'
  form.channel = '电话'
  form.conclusion = ''
  form.escalate = '否'
  showModal.value = true
}

function submit() {
  if (!form.conclusion.trim()) {
    toast('请填写随访结论')
    return
  }
  const row = current.value
  const rowRef = FOLLOWUPS.find((item) => item.id === row.id)
  if (rowRef) {
    rowRef.status = '已完成'
    rowRef.doneAt = nowText()
    rowRef.conclusion = `${form.symptom}，${form.conclusion}${form.escalate === '是' ? '（建议门诊复评）' : ''}`
  }
  showModal.value = false
  toast('随访结论已提交')
}

function archive(row) {
  const rowRef = FOLLOWUPS.find((item) => item.id === row.id)
  if (rowRef) rowRef.status = '已归档'
  toast('工单已归档')
}

function reject(row) {
  const rowRef = FOLLOWUPS.find((item) => item.id === row.id)
  if (rowRef) {
    rowRef.status = '已驳回'
    rowRef.conclusion = '随访记录不完整，已退回重新随访。'
  }
  toast('工单已驳回')
}

function goPatient(id) {
  router.push(`/patient-detail/${id}`)
}
</script>

<style scoped>
.board-bar {
  margin-bottom: 12px;
}
.board-bar-in {
  display: flex;
  align-items: center;
  gap: 26px;
  flex-wrap: wrap;
}
.board-stat {
  display: flex;
  align-items: baseline;
  gap: 6px;
}
.board-stat .k {
  color: var(--ink-3);
  font-size: 12px;
}
.board-stat b {
  font-size: 18px;
  font-weight: 600;
  font-variant-numeric: tabular-nums;
}
.board-stat b.blue {
  color: var(--brand);
}
.board-stat b.red {
  color: var(--red);
}
.board-stat b.green {
  color: var(--green);
}
.board-stat b.amber {
  color: var(--amber);
}
.board-filter {
  margin-left: auto;
  display: flex;
  gap: 8px;
}
.board {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
  align-items: start;
}
.board-col {
  background: var(--surface);
  border: 1px solid #eef0f4;
  border-radius: var(--radius-card);
  box-shadow: var(--shadow-card);
  display: flex;
  flex-direction: column;
  min-height: 320px;
}
.board-col-h {
  display: flex;
  align-items: center;
  gap: 7px;
  padding: 10px 12px;
  border-bottom: 1px solid var(--line-2);
  font-size: 13px;
}
.board-col-h .dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  flex: none;
}
.board-col-h .cnt {
  margin-left: auto;
  min-width: 20px;
  height: 18px;
  padding: 0 6px;
  border-radius: 9px;
  background: var(--surface-2);
  color: var(--ink-3);
  font-size: 11px;
  display: grid;
  place-items: center;
}
.board-col-b {
  flex: 1;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.fu-card {
  border: 1px solid #eef0f4;
  border-radius: var(--radius-sub);
  padding: 12px 14px;
  background: #fff;
  box-shadow: var(--shadow-card);
  transition: box-shadow 0.15s, border-color 0.15s;
}
.fu-card:hover {
  border-color: #bedaff;
  box-shadow: var(--shadow-card-hover);
}
.fu-card.overdue {
  background: #fffbfa;
}
.fu-top {
  display: flex;
  align-items: center;
  gap: 6px;
}
.fu-name {
  color: var(--ink);
  font-weight: 600;
  cursor: pointer;
}
.fu-name:hover {
  color: var(--brand);
}
.fu-due {
  margin-left: auto;
  color: var(--ink-3);
  font-size: 11.5px;
}
.fu-due.red {
  color: var(--red);
}
.fu-meta {
  display: flex;
  gap: 10px;
  margin-top: 4px;
  color: var(--ink-3);
  font-size: 11.5px;
}
.fu-no {
  margin-top: 2px;
  font-size: 11px;
  color: var(--ink-3);
}
.fu-conclusion {
  margin-top: 6px;
  padding: 6px 8px;
  border-radius: 8px;
  background: var(--surface-2);
  color: var(--ink-2);
  font-size: 12px;
  line-height: 1.6;
}
.fu-ops {
  display: flex;
  gap: 6px;
  margin-top: 8px;
  flex-wrap: wrap;
}
.form-2 {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 12px;
}
.order-line {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 7px 10px;
  border-radius: 8px;
  background: var(--surface-2);
  font-size: 12.5px;
  width: 100%;
}
</style>
