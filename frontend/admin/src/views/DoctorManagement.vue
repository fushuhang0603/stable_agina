<template>
  <div class="doctor-management">
    <div class="grid g4">
      <StatCard title="医生总数" :value="stats.total" icon="users" tone="blue" desc="具备医生角色的账号" />
      <StatCard title="已建档" :value="stats.profiled" icon="check" tone="green" desc="资质与经历已归档" />
      <StatCard title="未建档" :value="stats.unprofiled" icon="alert" tone="amber" desc="待补充医生档案" />
      <StatCard title="已排出诊" :value="stats.scheduled" icon="clock" tone="blue" desc="已设置出诊时间" />
    </div>

    <el-card shadow="never" class="filter-card">
      <div class="filter-bar">
        <el-input
          v-model.trim="filters.realName"
          placeholder="姓名"
          clearable
          style="width: 150px"
          @keyup.enter="search"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-input
          v-model.trim="filters.phone"
          placeholder="手机号"
          clearable
          style="width: 150px"
          @keyup.enter="search"
        />
        <el-input
          v-model.trim="filters.licenseNo"
          placeholder="执业证书编号"
          clearable
          style="width: 170px"
          @keyup.enter="search"
        />
        <el-select v-model="filters.profileStatus" placeholder="建档状态" clearable style="width: 130px">
          <el-option v-for="o in profileOptions" :key="o.value" :value="o.value" :label="o.label" />
        </el-select>
        <el-select v-model="filters.status" placeholder="账号状态" clearable style="width: 130px">
          <el-option v-for="o in statusOptions" :key="o.value" :value="o.value" :label="o.label" />
        </el-select>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </div>
    </el-card>

    <Card title="医生列表">
      <template #extra>
        <span class="card-hint">共 {{ total }} 位医生</span>
      </template>

      <el-table v-loading="loading" :data="rows" size="small">
        <el-table-column label="医生" min-width="170">
          <template #default="{ row }">
            <div class="doc-cell">
              <span class="avatar">{{ (row.realName || row.username || '?').charAt(0) }}</span>
              <div>
                <b>{{ row.realName || '—' }}</b>
                <div class="mono t-muted">{{ row.username }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="职称 / 职务" min-width="140">
          <template #default="{ row }">
            <div class="cell-main">{{ row.title || '—' }}</div>
            <div class="cell-sub">{{ row.position || '未设置职务' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="医院 / 科室" min-width="160">
          <template #default="{ row }">
            <div class="cell-main">{{ row.hospital || '—' }}</div>
            <div class="cell-sub">{{ row.department || '—' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="执业资质" min-width="190">
          <template #default="{ row }">
            <div class="cell-main mono">{{ row.licenseNo || '—' }}</div>
            <div class="cell-sub">
              {{ row.licenseType || '未填写类别' }}
              <template v-if="row.licenseExpireDate"> · 至 {{ row.licenseExpireDate }}</template>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="出诊 / 挂号费" min-width="150">
          <template #default="{ row }">
            <div class="cell-main">{{ row.schedule || '—' }}</div>
            <div class="cell-sub">
              {{ row.registrationFee != null ? `挂号费 ￥${row.registrationFee}` : '未设置挂号费' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="档案" width="94">
          <template #default="{ row }"><Chip :text="row.hasProfile ? '已建档' : '未建档'" /></template>
        </el-table-column>
        <el-table-column label="账号" width="86">
          <template #default="{ row }"><Chip :text="row.statusText" /></template>
        </el-table-column>
        <el-table-column label="操作" width="110" align="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">
              {{ row.hasProfile ? '编辑档案' : '完善档案' }}
            </el-button>
          </template>
        </el-table-column>
        <template #empty>未找到符合条件的医生</template>
      </el-table>

      <ListPager v-model:page="page" :total="total" :size="pageSize" @change="loadPage" />
    </Card>

    <el-dialog v-model="showModal" title="编辑医生档案" width="720px">
      <el-form label-position="top" class="doc-form">
        <div class="form-section">基础信息</div>
        <div class="form-2">
          <el-form-item label="姓名" required>
            <el-input v-model.trim="form.realName" placeholder="真实姓名" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model.trim="form.phone" placeholder="手机号" />
          </el-form-item>
          <el-form-item label="职称">
            <el-input v-model.trim="form.title" placeholder="如 主任医师" />
          </el-form-item>
          <el-form-item label="行政职务">
            <el-input v-model.trim="form.position" placeholder="如 心内科副主任" />
          </el-form-item>
          <el-form-item label="所属医院">
            <el-input v-model.trim="form.hospital" placeholder="所属医院" />
          </el-form-item>
          <el-form-item label="所属科室">
            <el-input v-model.trim="form.department" placeholder="所属科室" />
          </el-form-item>
        </div>

        <div class="form-section">执业资质</div>
        <div class="form-2">
          <el-form-item label="执业证书编号">
            <el-input v-model.trim="form.licenseNo" placeholder="医师执业证书编号" />
          </el-form-item>
          <el-form-item label="资格证书编号">
            <el-input v-model.trim="form.certNo" placeholder="医师资格证书编号" />
          </el-form-item>
          <el-form-item label="执业类别">
            <el-select v-model="form.licenseType" clearable style="width: 100%">
              <el-option v-for="o in licenseTypeOptions" :key="o" :value="o" :label="o" />
            </el-select>
          </el-form-item>
          <el-form-item label="执业范围">
            <el-input v-model.trim="form.practiceScope" placeholder="如 内科专业" />
          </el-form-item>
          <el-form-item label="发证机关">
            <el-input v-model.trim="form.issuingAuthority" placeholder="发证机关" />
          </el-form-item>
          <el-form-item label="有效期至">
            <el-date-picker
              v-model="form.licenseExpireDate"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="选择日期"
              style="width: 100%"
            />
          </el-form-item>
        </div>

        <div class="form-section">职务擅长与出诊</div>
        <div class="form-2">
          <el-form-item label="擅长领域">
            <el-input v-model.trim="form.specialty" placeholder="如 冠心病、稳定型心绞痛长期管理" />
          </el-form-item>
          <el-form-item label="出诊时间">
            <el-input v-model.trim="form.schedule" placeholder="如 周一至周三 上午" />
          </el-form-item>
          <el-form-item label="挂号费（元）">
            <el-input-number
              v-model="form.registrationFee"
              :min="0"
              :precision="2"
              :step="10"
              controls-position="right"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="在管患者上限">
            <el-input-number
              v-model="form.maxPatients"
              :min="0"
              :step="10"
              controls-position="right"
              style="width: 100%"
            />
          </el-form-item>
        </div>

        <div class="form-section">简介与经历</div>
        <el-form-item label="个人简介">
          <el-input v-model="form.introduction" type="textarea" :rows="3" placeholder="个人简介" />
        </el-form-item>
        <el-form-item label="工作与教育经历">
          <el-input
            v-model="form.experience"
            type="textarea"
            :rows="5"
            placeholder="按时间顺序填写教育经历与工作经历"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showModal = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存档案</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import Card from '../components/Card.vue'
import Chip from '../components/Chip.vue'
import ListPager from '../components/ListPager.vue'
import StatCard from '../components/StatCard.vue'
import request from '../utils/request'
import { toast } from '../utils/toast'

const stats = reactive({ total: 0, profiled: 0, unprofiled: 0, scheduled: 0 })
const rows = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = 8
const loading = ref(false)
const saving = ref(false)

const filters = reactive({ realName: '', phone: '', licenseNo: '', profileStatus: undefined, status: undefined })

const profileOptions = [
  { value: 1, label: '已建档' },
  { value: 0, label: '未建档' }
]
const statusOptions = [
  { value: 1, label: '启用' },
  { value: 0, label: '停用' }
]
const licenseTypeOptions = ['临床', '中医', '口腔', '公共卫生']

async function loadStats() {
  Object.assign(stats, await request.get('/api/business/doctors/stats'))
}

async function loadPage() {
  loading.value = true
  try {
    const data = await request.get('/api/business/doctors', {
      params: {
        realName: filters.realName || undefined,
        phone: filters.phone || undefined,
        licenseNo: filters.licenseNo || undefined,
        profileStatus: filters.profileStatus ?? undefined,
        status: filters.status ?? undefined,
        pageNum: page.value,
        pageSize
      }
    })
    rows.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function search() {
  page.value = 1
  loadPage()
}

function reset() {
  filters.realName = ''
  filters.phone = ''
  filters.licenseNo = ''
  filters.profileStatus = undefined
  filters.status = undefined
  page.value = 1
  loadPage()
}

const showModal = ref(false)
const form = reactive({
  userId: null,
  realName: '',
  phone: '',
  title: '',
  position: '',
  hospital: '附属医院',
  department: '心内科',
  licenseNo: '',
  certNo: '',
  licenseType: '临床',
  practiceScope: '',
  issuingAuthority: '',
  licenseExpireDate: '',
  specialty: '',
  schedule: '',
  registrationFee: null,
  maxPatients: null,
  introduction: '',
  experience: ''
})

function resetForm() {
  Object.assign(form, {
    userId: null,
    realName: '',
    phone: '',
    title: '',
    position: '',
    hospital: '附属医院',
    department: '心内科',
    licenseNo: '',
    certNo: '',
    licenseType: '临床',
    practiceScope: '',
    issuingAuthority: '',
    licenseExpireDate: '',
    specialty: '',
    schedule: '',
    registrationFee: null,
    maxPatients: null,
    introduction: '',
    experience: ''
  })
}

async function openEdit(row) {
  resetForm()
  const detail = await request.get(`/api/business/doctors/${row.userId}`)
  Object.assign(form, {
    userId: detail.userId,
    realName: detail.realName || '',
    phone: detail.phone || '',
    title: detail.title || '',
    position: detail.position || '',
    hospital: detail.hospital || '',
    department: detail.department || '',
    licenseNo: detail.licenseNo || '',
    certNo: detail.certNo || '',
    licenseType: detail.licenseType || '',
    practiceScope: detail.practiceScope || '',
    issuingAuthority: detail.issuingAuthority || '',
    licenseExpireDate: detail.licenseExpireDate || '',
    specialty: detail.specialty || '',
    schedule: detail.schedule || '',
    registrationFee: detail.registrationFee ?? null,
    maxPatients: detail.maxPatients ?? null,
    introduction: detail.introduction || '',
    experience: detail.experience || ''
  })
  showModal.value = true
}

function buildPayload() {
  return {
    realName: form.realName,
    phone: form.phone || null,
    title: form.title || null,
    position: form.position || null,
    hospital: form.hospital || null,
    department: form.department || null,
    licenseNo: form.licenseNo || null,
    certNo: form.certNo || null,
    licenseType: form.licenseType || null,
    practiceScope: form.practiceScope || null,
    issuingAuthority: form.issuingAuthority || null,
    licenseExpireDate: form.licenseExpireDate || null,
    specialty: form.specialty || null,
    schedule: form.schedule || null,
    registrationFee: form.registrationFee ?? null,
    maxPatients: form.maxPatients ?? null,
    introduction: form.introduction || null,
    experience: form.experience || null
  }
}

async function submit() {
  if (!form.realName) {
    toast('医生姓名为必填项')
    return
  }
  saving.value = true
  try {
    await request.put(`/api/business/doctors/${form.userId}`, buildPayload())
    toast('医生档案已保存')
    showModal.value = false
    await Promise.all([loadPage(), loadStats()])
  } catch (e) {
    toast(e.message || '操作失败')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  await Promise.all([loadPage(), loadStats()])
})
</script>

<style scoped>
.filter-card {
  margin-bottom: 12px;
}
.doc-cell {
  display: flex;
  align-items: center;
  gap: 9px;
}
.doc-cell .avatar {
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
.form-2 {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 12px;
}
.doc-form {
  max-height: 62vh;
  overflow-y: auto;
  padding-right: 4px;
}
.form-section {
  margin: 6px 0 12px;
  font-weight: 600;
  color: #303133;
}
</style>
