<template>
  <div class="system-config">
    <el-card shadow="never" class="filter-card">
      <div class="filter-bar">
        <el-input
          v-model.trim="filters.paramKey"
          placeholder="参数键"
          clearable
          style="width: 170px"
          @keyup.enter="search"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-input
          v-model.trim="filters.paramName"
          placeholder="参数名称"
          clearable
          style="width: 160px"
          @keyup.enter="search"
        />
        <el-select v-model="filters.paramGroup" placeholder="参数分组" clearable style="width: 140px">
          <el-option v-for="g in groupOptions" :key="g" :label="g" :value="g" />
        </el-select>
        <el-select v-model="filters.status" placeholder="状态" clearable style="width: 120px">
          <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
        </el-select>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <div class="filter-spacer"></div>
        <el-button type="primary" @click="openCreate">新增参数</el-button>
      </div>
    </el-card>

    <Card title="随访参数">
      <template #extra>
        <span class="card-hint">共 {{ total }} 项参数</span>
      </template>

      <el-table v-loading="loading" :data="rows" size="small">
        <el-table-column prop="paramName" label="参数名称" min-width="150" />
        <el-table-column label="参数键" min-width="190">
          <template #default="{ row }"><span class="mono">{{ row.paramKey }}</span></template>
        </el-table-column>
        <el-table-column prop="paramGroup" label="分组" width="110" />
        <el-table-column label="参数值" min-width="150">
          <template #default="{ row }">{{ displayValue(row) }}</template>
        </el-table-column>
        <el-table-column label="值类型" width="100">
          <template #default="{ row }">{{ row.valueType || '—' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }"><Chip :text="row.statusText" /></template>
        </el-table-column>
        <el-table-column label="更新时间" width="160">
          <template #default="{ row }">{{ row.updateTime || '—' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="130" align="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>未找到符合条件的参数</template>
      </el-table>

      <ListPager v-model:page="page" :total="total" :size="pageSize" @change="loadPage" />
    </Card>

    <el-dialog v-model="showModal" :title="isEdit ? '编辑参数' : '新增参数'" width="560px">
      <el-form label-position="top">
        <div class="form-2">
          <el-form-item label="参数键" required>
            <el-input v-model.trim="form.paramKey" placeholder="如：bp.sbp.threshold" :disabled="isEdit" />
          </el-form-item>
          <el-form-item label="参数名称" required>
            <el-input v-model.trim="form.paramName" placeholder="如：收缩压告警阈值" />
          </el-form-item>
          <el-form-item label="参数分组" required>
            <el-select
              v-model="form.paramGroup"
              filterable
              allow-create
              default-first-option
              placeholder="选择或输入分组"
              style="width: 100%"
            >
              <el-option v-for="g in groupOptions" :key="g" :label="g" :value="g" />
            </el-select>
          </el-form-item>
          <el-form-item label="值类型">
            <el-select v-model="form.valueType" style="width: 100%">
              <el-option value="NUMBER" label="数值" />
              <el-option value="TEXT" label="文本" />
              <el-option value="BOOLEAN" label="开关" />
            </el-select>
          </el-form-item>
          <el-form-item label="参数值">
            <el-switch
              v-if="form.valueType === 'BOOLEAN'"
              v-model="form.paramValue"
              active-value="true"
              inactive-value="false"
            />
            <el-input v-else v-model.trim="form.paramValue" placeholder="参数值" />
          </el-form-item>
          <el-form-item label="单位">
            <el-input
              v-model.trim="form.unit"
              :disabled="form.valueType === 'BOOLEAN'"
              placeholder="如：mmHg / 天"
            />
          </el-form-item>
          <el-form-item label="排序">
            <el-input v-model.trim="form.sort" placeholder="数字，越小越靠前" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="form.status" style="width: 100%">
              <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="参数说明">
          <el-input
            v-model.trim="form.description"
            type="textarea"
            :rows="3"
            resize="none"
            placeholder="说明该参数的业务含义"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showModal = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">
          {{ isEdit ? '保存修改' : '创建参数' }}
        </el-button>
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

const statusOptions = [
  { value: 1, label: '启用' },
  { value: 0, label: '停用' }
]

const rows = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = 10
const loading = ref(false)
const saving = ref(false)
const groupOptions = ref([])

const filters = reactive({ paramKey: '', paramName: '', paramGroup: undefined, status: undefined })

const showModal = ref(false)
const isEdit = ref(false)
const editingId = ref(null)
const form = reactive({
  paramKey: '',
  paramName: '',
  paramGroup: '',
  paramValue: '',
  unit: '',
  valueType: 'NUMBER',
  description: '',
  sort: '1',
  status: 1
})

async function loadGroups() {
  try {
    groupOptions.value = (await request.get('/api/business/followup-params/groups')) || []
  } catch (e) {
    groupOptions.value = []
  }
}

async function loadPage() {
  loading.value = true
  try {
    const data = await request.get('/api/business/followup-params', {
      params: {
        paramKey: filters.paramKey || undefined,
        paramName: filters.paramName || undefined,
        paramGroup: filters.paramGroup || undefined,
        status: filters.status ?? undefined,
        pageNum: page.value,
        pageSize
      }
    })
    rows.value = data?.records || []
    total.value = data?.total || 0
  } catch (e) {
    rows.value = []
    total.value = 0
    toast.error(e.message || '参数加载失败')
  } finally {
    loading.value = false
  }
}

function search() {
  page.value = 1
  loadPage()
}

function reset() {
  filters.paramKey = ''
  filters.paramName = ''
  filters.paramGroup = undefined
  filters.status = undefined
  page.value = 1
  loadPage()
}

onMounted(() => {
  loadGroups()
  loadPage()
})

function displayValue(row) {
  if (row.valueType === 'BOOLEAN') return row.paramValue === 'true' ? '开启' : '关闭'
  if (!row.paramValue) return '—'
  return row.unit ? `${row.paramValue} ${row.unit}` : row.paramValue
}

function openCreate() {
  isEdit.value = false
  editingId.value = null
  Object.assign(form, {
    paramKey: '',
    paramName: '',
    paramGroup: '',
    paramValue: '',
    unit: '',
    valueType: 'NUMBER',
    description: '',
    sort: String(total.value + 1),
    status: 1
  })
  showModal.value = true
}

function openEdit(row) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(form, {
    paramKey: row.paramKey,
    paramName: row.paramName,
    paramGroup: row.paramGroup,
    paramValue: row.paramValue || '',
    unit: row.unit || '',
    valueType: row.valueType || 'TEXT',
    description: row.description || '',
    sort: row.sort != null ? String(row.sort) : '0',
    status: row.status
  })
  showModal.value = true
}

async function submit() {
  if (!isEdit.value && !form.paramKey) {
    toast.error('请填写参数键')
    return
  }
  if (!form.paramName) {
    toast.error('请填写参数名称')
    return
  }
  if (!form.paramGroup) {
    toast.error('请选择或填写参数分组')
    return
  }
  const payload = {
    paramName: form.paramName,
    paramGroup: form.paramGroup,
    paramValue: form.valueType === 'BOOLEAN'
      ? (form.paramValue === 'true' ? 'true' : 'false')
      : form.paramValue,
    unit: form.valueType === 'BOOLEAN' ? null : (form.unit || null),
    valueType: form.valueType,
    description: form.description || null,
    sort: Number(form.sort) || 0,
    status: form.status
  }
  saving.value = true
  try {
    if (isEdit.value) {
      await request.put(`/api/business/followup-params/${editingId.value}`, payload)
    } else {
      await request.post('/api/business/followup-params', { paramKey: form.paramKey, ...payload })
    }
    showModal.value = false
    await Promise.all([loadPage(), loadGroups()])
    toast(isEdit.value ? '参数已更新' : '参数已新增')
  } catch (e) {
    toast.error(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  try {
    await ElMessageBox.confirm(`确定删除参数「${row.paramName}」吗？`, '删除确认', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
  } catch (e) {
    return
  }
  try {
    await request.delete(`/api/business/followup-params/${row.id}`)
    if (rows.value.length === 1 && page.value > 1) {
      page.value -= 1
    }
    await Promise.all([loadPage(), loadGroups()])
    toast('参数已删除')
  } catch (e) {
    toast.error(e.message || '删除失败')
  }
}
</script>

<style scoped>
.system-config :deep(.el-card) {
  margin-bottom: 12px;
}
.form-2 {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 12px;
}
</style>
