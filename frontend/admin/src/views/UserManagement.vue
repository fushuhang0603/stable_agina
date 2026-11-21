<template>
  <div class="user-management">
    <div class="grid g4">
      <StatCard title="账号总数" :value="stats.total" icon="users" tone="blue" desc="后台全部账号" />
      <StatCard title="启用" :value="stats.enabled" icon="check" tone="green" desc="可正常登录系统" />
      <StatCard title="停用" :value="stats.disabled" icon="lock" tone="amber" desc="停用后无法登录" />
      <StatCard title="医生角色" :value="stats.doctor" icon="user" tone="blue" desc="含随访医生与护理账号" />
    </div>

    <el-card shadow="never" class="filter-card">
      <div class="filter-bar">
        <el-input
          v-model.trim="filters.keyword"
          placeholder="用户名 / 姓名 / 手机号"
          clearable
          style="width: 230px"
          @keyup.enter="search"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="filters.roleKey" placeholder="角色" clearable style="width: 130px">
          <el-option v-for="o in roleOptions" :key="o.value" :value="o.value" :label="o.label" />
        </el-select>
        <el-select v-model="filters.status" placeholder="状态" clearable style="width: 130px">
          <el-option v-for="o in statusOptions" :key="o.value" :value="o.value" :label="o.label" />
        </el-select>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <div class="filter-spacer"></div>
        <el-button type="primary" @click="openCreate">
          <el-icon><Plus /></el-icon>新增账号
        </el-button>
      </div>
    </el-card>

    <Card title="账号列表">
      <template #extra>
        <span class="card-hint">共 {{ total }} 个账号</span>
      </template>

      <el-table v-loading="loading" :data="rows" size="small">
        <el-table-column prop="username" label="账号" min-width="130">
          <template #default="{ row }"><span class="mono">{{ row.username }}</span></template>
        </el-table-column>
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="roleName" label="角色" width="110" />
        <el-table-column prop="org" label="所属机构" min-width="150" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="lastLoginTime" label="最近登录" width="160">
          <template #default="{ row }">{{ row.lastLoginTime || '—' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }"><Chip :text="row.statusText" /></template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="resetPassword(row)">重置密码</el-button>
            <el-button link type="primary" @click="toggleStatus(row)">
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
        <template #empty>未找到符合条件的账号</template>
      </el-table>

      <el-pagination
        v-model:current-page="page"
        class="list-pager"
        background
        layout="total, prev, pager, next"
        :total="total"
        :page-size="pageSize"
        @current-change="loadPage"
      />
    </Card>

    <el-dialog v-model="showModal" :title="isEdit ? '编辑账号' : '新增账号'" width="560px">
      <el-form label-position="top">
        <div class="form-2">
          <el-form-item label="用户名" required>
            <el-input v-model.trim="form.username" placeholder="登录用户名" :disabled="isEdit" />
          </el-form-item>
          <el-form-item label="姓名" required>
            <el-input v-model.trim="form.realName" placeholder="真实姓名" />
          </el-form-item>
          <el-form-item v-if="!isEdit" label="初始密码" required>
            <el-input v-model.trim="form.password" type="password" show-password placeholder="至少 6 位" />
          </el-form-item>
          <el-form-item label="角色" required>
            <el-select v-model="form.roleKey" style="width: 100%">
              <el-option v-for="o in roleOptions" :key="o.value" :value="o.value" :label="o.label" />
            </el-select>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model.trim="form.phone" placeholder="手机号" />
          </el-form-item>
          <el-form-item label="所属医院">
            <el-input v-model.trim="form.hospital" placeholder="所属医院" />
          </el-form-item>
          <el-form-item label="所属科室">
            <el-input v-model.trim="form.department" placeholder="所属科室" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="showModal = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">
          {{ isEdit ? '保存修改' : '创建账号' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import Card from '../components/Card.vue'
import Chip from '../components/Chip.vue'
import StatCard from '../components/StatCard.vue'
import request from '../utils/request'
import { toast } from '../utils/toast'

const roles = ref([])
const stats = reactive({ total: 0, enabled: 0, disabled: 0, doctor: 0 })
const rows = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = 8
const loading = ref(false)
const saving = ref(false)

const filters = reactive({ keyword: '', roleKey: undefined, status: undefined })

const roleOptions = computed(() => roles.value.map((role) => ({ value: role.roleKey, label: role.roleName })))
const statusOptions = [
  { value: 1, label: '启用' },
  { value: 0, label: '停用' }
]

async function loadRoles() {
  roles.value = await request.get('/api/system/roles')
}

async function loadStats() {
  Object.assign(stats, await request.get('/api/system/users/stats'))
}

async function loadPage() {
  loading.value = true
  try {
    const data = await request.get('/api/system/users', {
      params: {
        keyword: filters.keyword || undefined,
        roleKey: filters.roleKey || undefined,
        status: filters.status ?? undefined,
        pageNum: page.value,
        pageSize
      }
    })
    rows.value = data.list
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
  filters.keyword = ''
  filters.roleKey = undefined
  filters.status = undefined
  page.value = 1
  loadPage()
}

const showModal = ref(false)
const isEdit = ref(false)
const form = reactive({
  id: null,
  username: '',
  password: '',
  realName: '',
  roleKey: 'DOCTOR',
  phone: '',
  hospital: '附属医院',
  department: '心内科'
})

function openCreate() {
  isEdit.value = false
  Object.assign(form, {
    id: null,
    username: '',
    password: '',
    realName: '',
    roleKey: roles.value.find((role) => role.roleKey === 'DOCTOR')?.roleKey || roles.value[0]?.roleKey || '',
    phone: '',
    hospital: '附属医院',
    department: '心内科'
  })
  showModal.value = true
}

function openEdit(row) {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    username: row.username,
    password: '',
    realName: row.realName,
    roleKey: row.roleKey,
    phone: row.phone,
    hospital: row.hospital,
    department: row.department
  })
  showModal.value = true
}

async function submit() {
  if (!form.username || !form.realName) {
    toast('用户名与姓名为必填项')
    return
  }
  if (!isEdit.value && (!form.password || form.password.length < 6)) {
    toast('初始密码至少 6 位')
    return
  }
  if (!form.roleKey) {
    toast('请选择角色')
    return
  }
  saving.value = true
  try {
    if (isEdit.value) {
      await request.put(`/api/system/users/${form.id}`, {
        realName: form.realName,
        phone: form.phone || null,
        roleKey: form.roleKey,
        hospital: form.hospital || null,
        department: form.department || null
      })
      toast('账号信息已保存')
    } else {
      await request.post('/api/system/users', {
        username: form.username,
        password: form.password,
        realName: form.realName,
        roleKey: form.roleKey,
        phone: form.phone || null,
        hospital: form.hospital || null,
        department: form.department || null
      })
      toast(`账号 ${form.username} 已创建`)
    }
    showModal.value = false
    await Promise.all([loadPage(), loadStats(), loadRoles()])
  } catch (e) {
    toast(e.message || '操作失败')
  } finally {
    saving.value = false
  }
}

async function resetPassword(row) {
  let password
  try {
    const result = await ElMessageBox.prompt(`为 ${row.realName}（${row.username}）设置新密码`, '重置密码', {
      inputType: 'password',
      inputPlaceholder: '至少 6 位',
      inputValidator: (value) => (value && value.length >= 6 ? true : '密码至少 6 位'),
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    password = result.value
  } catch {
    return
  }
  await request.put(`/api/system/users/${row.id}/password`, { password })
  toast('密码已重置')
}

async function toggleStatus(row) {
  const nextStatus = row.status === 1 ? 0 : 1
  try {
    await request.put(`/api/system/users/${row.id}/status`, { status: nextStatus })
    toast(`账号 ${row.username} 已${nextStatus === 1 ? '启用' : '停用'}`)
    await Promise.all([loadPage(), loadStats()])
  } catch (e) {
    toast(e.message || '操作失败')
  }
}

onMounted(async () => {
  await loadRoles()
  await Promise.all([loadPage(), loadStats()])
})
</script>

<style scoped>
.filter-card {
  margin-bottom: 12px;
}
.form-2 {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 12px;
}
.list-pager {
  margin-top: 12px;
  justify-content: flex-end;
}
</style>
