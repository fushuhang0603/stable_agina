<template>
  <div class="role-management">
    <div class="section-note">
      <el-icon><InfoFilled /></el-icon>
      <span>权限变更后需用户重新登录生效。</span>
    </div>

    <div class="role-layout">
      <Card title="角色列表">
        <div v-loading="loadingRoles" class="role-list">
          <div
            v-for="role in roles"
            :key="role.id"
            class="tree-row role-row"
            :class="{ active: role.id === selectedId }"
            @click="selectRole(role)"
          >
            <el-icon><Key /></el-icon>
            <div class="role-name">
              <b>{{ role.roleName }}</b>
              <span class="mono">{{ role.roleKey }}</span>
            </div>
            <span class="cnt">{{ role.userCount }} 人</span>
          </div>
          <el-empty v-if="!loadingRoles && !roles.length" description="暂无角色" :image-size="60" />
        </div>
      </Card>

      <Card :title="selectedRole ? `${selectedRole.roleName} · 权限配置` : '权限配置'">
        <template v-if="selectedRole">
          <dl v-loading="loadingPerms" class="kv role-kv">
            <dt>角色名称</dt>
            <dd>{{ selectedRole.roleName }}</dd>
            <dt>角色编码</dt>
            <dd class="mono">{{ selectedRole.roleKey }}</dd>
            <dt>数据权限</dt>
            <dd>
              <el-select v-model="dataScope" style="width: 180px">
                <el-option v-for="o in dataScopeOptions" :key="o.value" :value="o.value" :label="o.label" />
              </el-select>
            </dd>
            <dt>绑定用户</dt>
            <dd>{{ selectedRole.userCount }} 人</dd>
            <dt>权限项</dt>
            <dd>{{ checked.size }} 项</dd>
            <dt>角色说明</dt>
            <dd>{{ selectedRole.remark || '—' }}</dd>
          </dl>

          <div class="perm-head">
            <b>菜单权限</b>
            <span class="t-muted">已选 {{ checked.size }} 项</span>
            <el-button type="primary" size="small" class="perm-save" :loading="saving" @click="save">
              保存权限
            </el-button>
          </div>

          <div v-for="group in menuTree" :key="group.id" class="perm-group">
            <div class="perm-group-h">
              <el-icon><List /></el-icon>
              <b>{{ group.title }}</b>
              <span class="cnt">{{ groupItems(group).length }} 项</span>
              <div class="perm-ops">
                <el-button link type="primary" size="small" @click="selectGroup(group)">全选</el-button>
                <el-button link type="primary" size="small" @click="invertGroup(group)">反选</el-button>
              </div>
            </div>
            <div class="perm-items">
              <el-checkbox
                v-for="item in groupItems(group)"
                :key="item.id"
                :model-value="checked.has(item.id)"
                :disabled="loadingPerms"
                @change="toggle(item.id)"
              >
                {{ item.title }}
              </el-checkbox>
            </div>
          </div>

          <el-empty v-if="!menuTree.length" description="暂无菜单权限" :image-size="60" />
        </template>
        <el-empty v-else description="请选择角色" :image-size="60" />
      </Card>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import Card from '../components/Card.vue'
import request from '../utils/request'
import { toast } from '../utils/toast'

const roles = ref([])
const menuTree = ref([])
const selectedId = ref(null)
const checked = ref(new Set())
const dataScope = ref('ALL')
const loadingRoles = ref(false)
const loadingPerms = ref(false)
const saving = ref(false)

const selectedRole = computed(() => roles.value.find((role) => role.id === selectedId.value) || null)

const dataScopeOptions = [
  { value: 'ALL', label: '全部数据' },
  { value: 'ASSIGNED_PATIENTS', label: '本人负责患者' },
  { value: 'SELF', label: '仅本人' }
]

function groupItems(group) {
  return group.children && group.children.length ? group.children : [group]
}

async function loadRoles() {
  loadingRoles.value = true
  try {
    roles.value = await request.get('/api/system/roles')
    if (roles.value.length && !roles.value.some((role) => role.id === selectedId.value)) {
      selectedId.value = roles.value[0].id
    }
  } catch (e) {
    toast(e.message || '角色列表加载失败')
  } finally {
    loadingRoles.value = false
  }
}

async function loadMenuTree() {
  try {
    menuTree.value = await request.get('/api/system/menus/tree')
  } catch (e) {
    toast(e.message || '菜单权限加载失败')
  }
}

async function loadRoleMenus(roleId) {
  if (!roleId) {
    checked.value = new Set()
    return
  }
  loadingPerms.value = true
  try {
    const ids = await request.get(`/api/system/roles/${roleId}/menus`)
    checked.value = new Set(ids || [])
  } catch (e) {
    checked.value = new Set()
    toast(e.message || '权限加载失败')
  } finally {
    loadingPerms.value = false
  }
}

function selectRole(role) {
  selectedId.value = role.id
}

watch(selectedId, (id) => {
  const role = roles.value.find((item) => item.id === id)
  dataScope.value = role?.dataScope || 'ALL'
  loadRoleMenus(id)
})

function toggle(menuId) {
  const next = new Set(checked.value)
  if (next.has(menuId)) next.delete(menuId)
  else next.add(menuId)
  checked.value = next
}

function selectGroup(group) {
  const next = new Set(checked.value)
  groupItems(group).forEach((item) => next.add(item.id))
  checked.value = next
}

function invertGroup(group) {
  const next = new Set(checked.value)
  groupItems(group).forEach((item) => {
    if (next.has(item.id)) next.delete(item.id)
    else next.add(item.id)
  })
  checked.value = next
}

async function save() {
  if (!selectedRole.value) return
  saving.value = true
  try {
    await request.put(`/api/system/roles/${selectedRole.value.id}/grant`, {
      dataScope: dataScope.value,
      menuIds: Array.from(checked.value)
    })
    toast(`${selectedRole.value.roleName} 的权限已保存（共 ${checked.value.size} 项）`)
    await loadRoles()
    await loadRoleMenus(selectedRole.value.id)
  } catch (e) {
    toast(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  await Promise.all([loadRoles(), loadMenuTree()])
})
</script>

<style scoped>
.role-layout {
  display: grid;
  grid-template-columns: 250px minmax(0, 1fr);
  gap: 12px;
  align-items: start;
  margin-top: 12px;
}
.role-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-height: 60px;
}
.role-row {
  padding: 8px 9px;
}
.role-row .role-name {
  display: flex;
  flex-direction: column;
  min-width: 0;
  line-height: 1.4;
}
.role-row .role-name b {
  font-weight: 500;
}
.role-row .role-name .mono {
  font-size: 11px;
  color: var(--ink-3);
}
.role-kv {
  grid-template-columns: 90px 1fr;
  margin-bottom: 18px;
}
.perm-head {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-top: 12px;
  border-top: 1px solid var(--line-2);
  margin-bottom: 4px;
}
.perm-head b {
  font-size: 13px;
  font-weight: 600;
}
.perm-save {
  margin-left: auto;
}
.perm-group {
  margin-top: 12px;
}
.perm-group-h {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 8px;
  background: var(--surface-2);
  border-radius: 8px;
  color: var(--ink-2);
  font-size: 12.5px;
}
.perm-group-h b {
  font-weight: 500;
  color: var(--ink);
}
.perm-group-h .cnt {
  font-size: 11.5px;
  color: var(--ink-3);
}
.perm-ops {
  margin-left: auto;
  display: flex;
  gap: 2px;
}
.perm-ops .el-button + .el-button {
  margin-left: 0;
}
.perm-items {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 6px 14px;
  padding: 8px 8px 2px;
}
</style>
