<template>
  <div class="followup-board">
    <div class="grid g5">
      <StatCard title="待随访" :value="stats.pending" icon="clipboard" tone="amber" desc="计划日期未到，等待随访" />
      <StatCard title="随访中" :value="stats.processing" icon="clock" tone="blue" desc="已开始随访跟踪" />
      <StatCard title="已逾期" :value="stats.overdue" icon="bell" tone="red" desc="超过计划日期未完成" />
      <StatCard title="已完成" :value="stats.done" icon="check" tone="green" desc="已完成本次随访" />
      <StatCard title="已驳回" :value="stats.rejected" icon="alert" tone="red" desc="记录不完整被退回" />
    </div>

    <el-card shadow="never" class="filter-card">
      <div class="filter-bar">
        <el-input
          v-model.trim="filters.orderNo"
          placeholder="工单号"
          clearable
          style="width: 170px"
          @keyup.enter="search"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-input
          v-model.trim="filters.patientName"
          placeholder="患者姓名"
          clearable
          style="width: 140px"
          @keyup.enter="search"
        />
        <el-select v-model="filters.status" placeholder="工单状态" clearable style="width: 130px">
          <el-option v-for="s in statusOptions" :key="s" :label="s" :value="s" />
        </el-select>
        <el-select v-model="filters.type" placeholder="随访类型" clearable style="width: 140px">
          <el-option v-for="t in typeOptions" :key="t" :label="t" :value="t" />
        </el-select>
        <el-select v-model="filters.doctor" placeholder="随访医生" clearable style="width: 130px">
          <el-option v-for="d in doctorOptions" :key="d" :label="d" :value="d" />
        </el-select>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </div>
    </el-card>

    <Card title="随访工单">
      <template #extra>
        <span class="card-hint">共 {{ total }} 单，按计划随访日期排序</span>
      </template>

      <el-table
        :data="pagedRows"
        size="small"
        class="clickable-table"
        @row-click="(row) => goPatient(row.patientId)"
      >
        <el-table-column label="工单号" width="150">
          <template #default="{ row }"><span class="mono">{{ row.id }}</span></template>
        </el-table-column>
        <el-table-column label="患者" width="160">
          <template #default="{ row }">
            <div class="patient-cell">
              <span class="avatar">{{ (row.patientName || '?').charAt(0) }}</span>
              <div>
                <b>{{ row.patientName || '—' }}</b>
                <div class="mono t-muted">{{ row.patientId || '—' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="危险分层" width="92" align="center">
          <template #default="{ row }"><Chip :text="row.risk || '—'" /></template>
        </el-table-column>
        <el-table-column label="随访类型" min-width="102" align="center">
          <template #default="{ row }">{{ dash(row.type) }}</template>
        </el-table-column>
        <el-table-column label="计划随访" min-width="112" align="center">
          <template #default="{ row }">
            <span :class="{ 't-red': row.status === '已逾期' }">{{ dash(row.dueDate) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="随访医生" width="100" align="center">
          <template #default="{ row }">{{ dash(row.doctor) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="96" align="center">
          <template #default="{ row }"><Chip :text="row.status" /></template>
        </el-table-column>
        <el-table-column label="完成时间" min-width="150" align="center">
          <template #default="{ row }">{{ dash(row.doneAt) }}</template>
        </el-table-column>
        <el-table-column label="随访结论" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">{{ dash(row.conclusion) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status !== '已完成'"
              link
              type="primary"
              @click.stop="openRecord(row)"
            >记录随访</el-button>
            <el-button v-if="row.status === '已完成'" link @click.stop="archive(row)">归档</el-button>
            <el-button v-if="row.status === '随访中'" link type="danger" @click.stop="reject(row)">驳回</el-button>
            <el-button link @click.stop="goPatient(row.patientId)">档案</el-button>
          </template>
        </el-table-column>
        <template #empty>未找到符合条件的工单</template>
      </el-table>

      <ListPager v-model:page="page" :total="total" :size="pageSize" />
    </Card>

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
import { computed, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import Card from '../components/Card.vue'
import Chip from '../components/Chip.vue'
import ListPager from '../components/ListPager.vue'
import StatCard from '../components/StatCard.vue'
import { FOLLOWUPS, nowText } from '../mock'
import { toast } from '../utils/toast'

const router = useRouter()

const pageSize = 8
const page = ref(1)

const filters = reactive({
  orderNo: '',
  patientName: '',
  status: undefined,
  type: undefined,
  doctor: undefined
})

const statusOptions = ['待随访', '随访中', '已逾期', '已完成', '已驳回', '已归档']
const typeOptions = [...new Set(FOLLOWUPS.map((row) => row.type))]
const doctorOptions = [...new Set(FOLLOWUPS.map((row) => row.doctor))]

const stats = computed(() => ({
  pending: FOLLOWUPS.filter((row) => row.status === '待随访').length,
  processing: FOLLOWUPS.filter((row) => row.status === '随访中').length,
  overdue: FOLLOWUPS.filter((row) => row.status === '已逾期').length,
  done: FOLLOWUPS.filter((row) => row.status === '已完成').length,
  rejected: FOLLOWUPS.filter((row) => row.status === '已驳回').length
}))

const filteredRows = computed(() => {
  const list = FOLLOWUPS.filter((row) => {
    if (filters.orderNo && !row.id.toLowerCase().includes(filters.orderNo.toLowerCase())) return false
    if (filters.patientName && !row.patientName.includes(filters.patientName)) return false
    if (filters.status && row.status !== filters.status) return false
    if (filters.type && row.type !== filters.type) return false
    if (filters.doctor && row.doctor !== filters.doctor) return false
    return true
  })
  return [...list].sort((a, b) => a.dueDate.localeCompare(b.dueDate))
})

const total = computed(() => filteredRows.value.length)
const pagedRows = computed(() => filteredRows.value.slice((page.value - 1) * pageSize, page.value * pageSize))

watch(total, (len) => {
  const maxPage = Math.max(1, Math.ceil(len / pageSize))
  if (page.value > maxPage) page.value = maxPage
})

function dash(value) {
  return value === null || value === undefined || value === '' ? '—' : value
}

function search() {
  page.value = 1
}

function reset() {
  filters.orderNo = ''
  filters.patientName = ''
  filters.status = undefined
  filters.type = undefined
  filters.doctor = undefined
  page.value = 1
}

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
    toast.error('请填写随访结论')
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
.g5 {
  grid-template-columns: repeat(5, minmax(0, 1fr));
}
.filter-card {
  margin-bottom: 12px;
}
.patient-cell {
  display: flex;
  align-items: center;
  gap: 9px;
}
.patient-cell .avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #eef2ff;
  color: #4f6ef7;
  font-weight: 600;
  flex: 0 0 auto;
}
.patient-cell b {
  font-weight: 500;
}
.patient-cell .mono {
  font-size: 11.5px;
}
.clickable-table :deep(.el-table__row) {
  cursor: pointer;
}
.clickable-table :deep(.el-table__cell) {
  padding: 10px 0;
}
.clickable-table :deep(.el-table__cell .cell) {
  padding: 0 10px;
  line-height: 1.6;
}
.t-red {
  color: var(--red);
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
