<template>
  <div class="doctor-bind-apply">
    <el-card shadow="never" class="filter-card">
      <div class="filter-bar">
        <el-input
          v-model.trim="filters.patientName"
          placeholder="患者姓名"
          clearable
          style="width: 140px"
          @keyup.enter="search"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-input
          v-model.trim="filters.doctorName"
          placeholder="医生姓名"
          clearable
          style="width: 140px"
          @keyup.enter="search"
        />
        <el-select v-model="filters.applyType" placeholder="申请类型" clearable style="width: 130px">
          <el-option v-for="o in applyTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
        </el-select>
        <el-select v-model="filters.status" placeholder="审核状态" clearable style="width: 130px">
          <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
        </el-select>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </div>
    </el-card>

    <Card title="绑定申请">
      <template #extra>
        <span class="card-hint">共 {{ total }} 条申请，待审核优先展示</span>
      </template>

      <el-table v-loading="loading" :data="rows" size="small">
        <el-table-column label="患者" width="168">
          <template #default="{ row }">
            <div class="patient-cell">
              <span class="avatar">{{ (row.patientName || '?').charAt(0) }}</span>
              <div>
                <b>{{ dash(row.patientName) }}</b>
                <div class="mono t-muted">{{ dash(row.patientNo) }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="危险分层" width="96" align="center">
          <template #default="{ row }"><Chip :text="row.riskLevelText || '—'" /></template>
        </el-table-column>
        <el-table-column label="申请类型" width="96" align="center">
          <template #default="{ row }">{{ dash(row.applyTypeText) }}</template>
        </el-table-column>
        <el-table-column label="目标医生" width="168">
          <template #default="{ row }">
            <div>
              <b>{{ dash(row.doctorName) }}</b>
              <div class="mono t-muted">
                {{ [row.doctorTitle, row.doctorDepartment].filter(Boolean).join(' · ') || '—' }}
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="申请说明" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">{{ dash(row.applyReason) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="96" align="center">
          <template #default="{ row }"><Chip :text="row.statusText || '—'" /></template>
        </el-table-column>
        <el-table-column label="申请时间" min-width="150" align="center">
          <template #default="{ row }">{{ dash(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="处理信息" min-width="170">
          <template #default="{ row }">
            <template v-if="row.handleTime">
              <div>{{ dash(row.handleName) }}</div>
              <div class="mono t-muted">{{ row.handleTime }}</div>
            </template>
            <template v-else>—</template>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="130" align="right">
          <template #default="{ row }">
            <template v-if="row.status === 1">
              <el-button link type="primary" @click="approve(row)">通过</el-button>
              <el-button link type="danger" @click="openReject(row)">驳回</el-button>
            </template>
            <template v-else>—</template>
          </template>
        </el-table-column>
        <template #empty>暂无绑定申请</template>
      </el-table>

      <ListPager v-model:page="page" :total="total" :size="pageSize" @change="loadPage" />
    </Card>

    <el-dialog v-model="rejectVisible" title="驳回绑定申请" width="440px">
      <div class="reject-tip">
        驳回 {{ rejectRow ? `${rejectRow.patientName} 申请绑定 ${rejectRow.doctorName}` : '' }}
      </div>
      <el-input
        v-model.trim="rejectReason"
        type="textarea"
        :rows="4"
        maxlength="200"
        show-word-limit
        placeholder="请填写驳回原因，患者端可见"
      />
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" :loading="submitting" @click="submitReject">确认驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import Card from '../components/Card.vue'
import Chip from '../components/Chip.vue'
import ListPager from '../components/ListPager.vue'
import request from '../utils/request'
import { toast } from '../utils/toast'

const rows = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = 10
const loading = ref(false)
const submitting = ref(false)

const rejectVisible = ref(false)
const rejectRow = ref(null)
const rejectReason = ref('')

const filters = reactive({
  patientName: '',
  doctorName: '',
  applyType: undefined,
  status: undefined
})

const applyTypeOptions = [
  { value: 1, label: '首次绑定' },
  { value: 2, label: '换绑' }
]
const statusOptions = [
  { value: 1, label: '待审核' },
  { value: 2, label: '已通过' },
  { value: 3, label: '已驳回' },
  { value: 4, label: '已取消' }
]

function dash(value) {
  return value === null || value === undefined || value === '' ? '—' : value
}

async function loadPage() {
  loading.value = true
  try {
    const data = await request.get('/api/business/doctor-applies', {
      params: {
        patientName: filters.patientName || undefined,
        doctorName: filters.doctorName || undefined,
        applyType: filters.applyType || undefined,
        status: filters.status || undefined,
        pageNum: page.value,
        pageSize
      }
    })
    rows.value = data.records
    total.value = data.total
  } catch (e) {
    toast.error(e.message || '绑定申请加载失败')
  } finally {
    loading.value = false
  }
}

function search() {
  page.value = 1
  loadPage()
}

function reset() {
  filters.patientName = ''
  filters.doctorName = ''
  filters.applyType = undefined
  filters.status = undefined
  page.value = 1
  loadPage()
}

async function approve(row) {
  try {
    await ElMessageBox.confirm(
      `确认通过 ${row.patientName} 绑定 ${row.doctorName} 的申请吗？通过后绑定立即生效。`,
      '审核通过',
      { type: 'warning', confirmButtonText: '通过', cancelButtonText: '取消' }
    )
  } catch (e) {
    return
  }
  try {
    await request.put(`/api/business/doctor-applies/${row.id}/approve`)
    await afterHandled()
    toast('已通过绑定申请')
  } catch (e) {
    toast.error(e.message || '审核操作失败')
  }
}

function openReject(row) {
  rejectRow.value = row
  rejectReason.value = ''
  rejectVisible.value = true
}

async function submitReject() {
  if (!rejectReason.value) {
    toast.error('请填写驳回原因')
    return
  }
  submitting.value = true
  try {
    await request.put(`/api/business/doctor-applies/${rejectRow.value.id}/reject`, {
      rejectReason: rejectReason.value
    })
    rejectVisible.value = false
    await afterHandled()
    toast('已驳回绑定申请')
  } catch (e) {
    toast.error(e.message || '驳回操作失败')
  } finally {
    submitting.value = false
  }
}

async function afterHandled() {
  if (rows.value.length === 1 && page.value > 1) {
    page.value -= 1
  }
  await loadPage()
}

onMounted(() => {
  loadPage()
})
</script>

<style scoped>
.filter-card {
  margin-bottom: 12px;
}
.card-hint {
  font-size: 12px;
  color: var(--ink-3);
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
.reject-tip {
  margin-bottom: 10px;
  font-size: 13px;
  color: var(--ink-2);
}
</style>
