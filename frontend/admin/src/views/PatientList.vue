<template>
  <div class="patient-list">
    <div class="grid g4">
      <StatCard title="患者总数" :value="stats.total" icon="users" tone="blue" desc="已纳入慢病管理的患者" />
      <StatCard title="高危患者" :value="stats.highRisk" icon="alert" tone="red" desc="需重点干预的危险分层" />
      <StatCard title="随访中" :value="stats.following" icon="clock" tone="blue" desc="正在按计划随访" />
      <StatCard title="已逾期" :value="stats.overdue" icon="bell" tone="amber" desc="超过计划随访日期" />
    </div>

    <el-card shadow="never" class="filter-card">
      <div class="filter-bar">
        <el-input
          v-model.trim="filters.name"
          placeholder="姓名"
          clearable
          style="width: 140px"
          @keyup.enter="search"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-input
          v-model.trim="filters.patientNo"
          placeholder="患者编号"
          clearable
          style="width: 150px"
          @keyup.enter="search"
        />
        <el-input
          v-model.trim="filters.phone"
          placeholder="手机号"
          clearable
          style="width: 150px"
          @keyup.enter="search"
        />
        <el-select v-model="filters.riskLevel" placeholder="危险分层" clearable style="width: 130px">
          <el-option v-for="o in riskOptions" :key="o.value" :label="o.label" :value="o.value" />
        </el-select>
        <el-select v-model="filters.status" placeholder="随访状态" clearable style="width: 130px">
          <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
        </el-select>
        <el-select v-model="filters.doctorId" placeholder="管床医生" clearable style="width: 150px">
          <el-option v-for="o in doctorOptions" :key="o.id" :label="o.name" :value="o.id" />
        </el-select>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </div>
    </el-card>

    <Card title="患者列表">
      <template #extra>
        <span class="card-hint">共 {{ total }} 位患者，按危险分层与下次随访排序</span>
      </template>

      <el-table
        v-loading="loading"
        :data="rows"
        size="small"
        class="clickable-table"
        @row-click="(row) => goDetail(row.id)"
      >
        <el-table-column label="患者" width="168">
          <template #default="{ row }">
            <div class="patient-cell">
              <span class="avatar">{{ (row.name || '?').charAt(0) }}</span>
              <div>
                <b>{{ row.name || '—' }}</b>
                <div class="mono t-muted">{{ row.patientNo || '—' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="性别" width="64" align="center">
          <template #default="{ row }">{{ dash(row.genderText) }}</template>
        </el-table-column>
        <el-table-column label="年龄" width="76" align="center">
          <template #default="{ row }">{{ row.age != null ? row.age + ' 岁' : '—' }}</template>
        </el-table-column>
        <el-table-column label="确诊日期" min-width="112" align="center">
          <template #default="{ row }">{{ dash(row.diagnoseDate) }}</template>
        </el-table-column>
        <el-table-column label="危险分层" width="96" align="center">
          <template #default="{ row }"><Chip :text="row.riskLevelText || '—'" /></template>
        </el-table-column>
        <el-table-column label="最近随访" min-width="112" align="center">
          <template #default="{ row }">{{ dash(row.lastVisitDate) }}</template>
        </el-table-column>
        <el-table-column label="下次随访" min-width="112" align="center">
          <template #default="{ row }">{{ dash(row.nextVisitDate) }}</template>
        </el-table-column>
        <el-table-column label="30 天发作" width="100" align="center">
          <template #default>—</template>
        </el-table-column>
        <el-table-column label="用药依从率" width="110" align="center">
          <template #default>—</template>
        </el-table-column>
        <el-table-column label="状态" width="96" align="center">
          <template #default="{ row }"><Chip :text="row.statusText || '—'" /></template>
        </el-table-column>
        <el-table-column label="管床医生" width="104" align="center">
          <template #default="{ row }">{{ dash(row.doctorName) }}</template>
        </el-table-column>
        <el-table-column label="操作" min-width="100" align="right">
          <template #default="{ row }">
            <el-button link type="primary" @click.stop="goDetail(row.id)">查看档案</el-button>
          </template>
        </el-table-column>
        <template #empty>未找到符合条件的患者</template>
      </el-table>

      <ListPager v-model:page="page" :total="total" :size="pageSize" @change="loadPage" />
    </Card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import Card from '../components/Card.vue'
import Chip from '../components/Chip.vue'
import ListPager from '../components/ListPager.vue'
import StatCard from '../components/StatCard.vue'
import request from '../utils/request'
import { toast } from '../utils/toast'

const router = useRouter()

const stats = reactive({ total: 0, highRisk: 0, following: 0, overdue: 0 })
const rows = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = 8
const loading = ref(false)

const filters = reactive({
  name: '',
  patientNo: '',
  phone: '',
  riskLevel: undefined,
  status: undefined,
  doctorId: undefined
})

const riskOptions = [
  { value: 'HIGH', label: '高危' },
  { value: 'MID', label: '中危' },
  { value: 'LOW', label: '低危' }
]
const statusOptions = [
  { value: 'FOLLOWING', label: '随访中' },
  { value: 'OVERDUE', label: '已逾期' }
]
const doctorOptions = ref([])

function dash(value) {
  return value === null || value === undefined || value === '' ? '—' : value
}

async function loadStats() {
  Object.assign(stats, await request.get('/api/business/patients/stats'))
}

async function loadDoctors() {
  doctorOptions.value = await request.get('/api/business/doctors/options')
}

async function loadPage() {
  loading.value = true
  try {
    const data = await request.get('/api/business/patients', {
      params: {
        name: filters.name || undefined,
        patientNo: filters.patientNo || undefined,
        phone: filters.phone || undefined,
        riskLevel: filters.riskLevel || undefined,
        status: filters.status || undefined,
        doctorId: filters.doctorId || undefined,
        pageNum: page.value,
        pageSize
      }
    })
    rows.value = data.records
    total.value = data.total
  } catch (e) {
    toast.error(e.message || '患者列表加载失败')
  } finally {
    loading.value = false
  }
}

function search() {
  page.value = 1
  loadPage()
}

function reset() {
  filters.name = ''
  filters.patientNo = ''
  filters.phone = ''
  filters.riskLevel = undefined
  filters.status = undefined
  filters.doctorId = undefined
  page.value = 1
  loadPage()
}

function goDetail(id) {
  router.push(`/patient-detail/${id}`)
}

onMounted(() => {
  Promise.all([loadPage(), loadStats(), loadDoctors()]).catch((e) => {
    toast.error(e.message || '患者数据加载失败')
  })
})
</script>

<style scoped>
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
</style>
