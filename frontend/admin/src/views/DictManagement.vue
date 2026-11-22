<template>
  <div class="dict-management">
    <div class="dict-grid">
      <Card title="字典类型" class="dict-left">
        <template #extra>
          <span class="card-hint">共 {{ types.length }} 类</span>
        </template>
        <div class="tree dict-list">
          <div
            v-for="dict in types"
            :key="dict.dictType"
            class="tree-row dict-row"
            :class="{ active: currentType && dict.dictType === currentType.dictType }"
            @click="selectType(dict.dictType)"
          >
            <el-icon><Notebook /></el-icon>
            <div class="dict-meta">
              <div class="dict-name">
                <b>{{ dict.dictName }}</b>
                <span class="dict-code">{{ dict.dictType }}</span>
              </div>
              <div class="dict-remark">{{ dict.remark || '—' }}</div>
            </div>
            <span class="cnt">{{ dict.itemCount }}</span>
          </div>
        </div>
        <div class="dict-left-f">
          <el-button class="dict-add-type" @click="openTypeModal">新增字典类型</el-button>
        </div>
      </Card>

      <Card :title="currentType ? `${currentType.dictName} · 字典项` : '字典项'">
        <template #extra>
          <div class="dict-tools">
            <span v-if="currentType" class="card-hint dict-code">{{ currentType.dictType }}</span>
            <el-button type="primary" size="small" :disabled="!currentType" @click="openItemModal(null)">
              新增字典项
            </el-button>
          </div>
        </template>

        <el-table v-if="currentType" v-loading="itemsLoading" :data="items" size="small">
          <el-table-column prop="dictLabel" label="标签" min-width="140" />
          <el-table-column label="值" min-width="140">
            <template #default="{ row }"><span class="mono">{{ row.dictValue }}</span></template>
          </el-table-column>
          <el-table-column prop="sort" label="排序" width="80" />
          <el-table-column label="状态" width="90">
            <template #default="{ row }"><Chip :text="row.statusText" /></template>
          </el-table-column>
          <el-table-column label="操作" width="130" align="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="openItemModal(row)">编辑</el-button>
              <el-button link type="danger" @click="removeItem(row)">删除</el-button>
            </template>
          </el-table-column>
          <template #empty>该字典暂无字典项，请点击右上角「新增字典项」</template>
        </el-table>
        <el-empty v-else description="请选择左侧字典类型" />
      </Card>
    </div>

    <el-dialog v-model="itemModal" :title="editingItem ? '编辑字典项' : '新增字典项'" width="480px">
      <el-form label-position="top">
        <el-form-item label="所属字典">
          <div class="dict-context">
            <b>{{ currentType?.dictName }}</b>
            <span class="dict-code">{{ currentType?.dictType }}</span>
          </div>
        </el-form-item>
        <div class="form-2">
          <el-form-item label="标签" required>
            <el-input v-model.trim="itemForm.dictLabel" placeholder="如：劳累" />
          </el-form-item>
          <el-form-item label="值" required>
            <el-input v-model.trim="itemForm.dictValue" placeholder="如：exertion" />
          </el-form-item>
          <el-form-item label="排序">
            <el-input v-model.trim="itemForm.sort" placeholder="数字，越小越靠前" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="itemForm.status" style="width: 100%">
              <el-option value="启用" label="启用" />
              <el-option value="停用" label="停用" />
            </el-select>
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="itemModal = false">取消</el-button>
        <el-button type="primary" @click="submitItem">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="typeModal" title="新增字典类型" width="480px">
      <el-form label-position="top">
        <el-form-item label="字典编码" required>
          <el-input v-model.trim="typeForm.dictType" placeholder="如：followup_type" />
        </el-form-item>
        <el-form-item label="字典名称" required>
          <el-input v-model.trim="typeForm.dictName" placeholder="如：随访类型" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model.trim="typeForm.remark" type="textarea" :rows="3" resize="none" placeholder="说明该字典的使用场景" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="typeModal = false">取消</el-button>
        <el-button type="primary" @click="submitType">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import Card from '../components/Card.vue'
import Chip from '../components/Chip.vue'
import request from '../utils/request'
import { toast } from '../utils/toast'

const types = ref([])
const items = ref([])
const activeCode = ref('')
const itemsLoading = ref(false)

const currentType = computed(
  () => types.value.find((dict) => dict.dictType === activeCode.value) || null
)

const itemModal = ref(false)
const editingItem = ref(null)
const itemForm = reactive({ dictLabel: '', dictValue: '', sort: '1', status: '启用' })

const typeModal = ref(false)
const typeForm = reactive({ dictType: '', dictName: '', remark: '' })

async function loadTypes() {
  try {
    types.value = (await request.get('/api/system/dicts')) || []
  } catch (e) {
    toast.error(e.message || '字典类型加载失败')
  }
}

async function loadItems() {
  if (!activeCode.value) {
    items.value = []
    return
  }
  itemsLoading.value = true
  try {
    items.value = (await request.get(`/api/system/dicts/${activeCode.value}/items`)) || []
  } catch (e) {
    items.value = []
    toast.error(e.message || '字典项加载失败')
  } finally {
    itemsLoading.value = false
  }
}

function selectType(code) {
  if (code === activeCode.value) return
  activeCode.value = code
  loadItems()
}

onMounted(async () => {
  await loadTypes()
  if (types.value.length) {
    activeCode.value = types.value[0].dictType
    await loadItems()
  }
})

function openItemModal(item) {
  editingItem.value = item
  itemForm.dictLabel = item ? item.dictLabel : ''
  itemForm.dictValue = item ? item.dictValue : ''
  itemForm.sort = item ? String(item.sort) : String(items.value.length + 1)
  itemForm.status = item ? item.statusText : '启用'
  itemModal.value = true
}

async function submitItem() {
  if (!currentType.value) return
  if (!itemForm.dictLabel || !itemForm.dictValue) {
    toast('请填写字典标签与值')
    return
  }
  const payload = {
    dictLabel: itemForm.dictLabel,
    dictValue: itemForm.dictValue,
    sort: Number(itemForm.sort) || 0,
    status: itemForm.status === '启用' ? 1 : 0
  }
  const isEdit = !!editingItem.value
  try {
    if (isEdit) {
      await request.put(`/api/system/dicts/items/${editingItem.value.id}`, payload)
    } else {
      await request.post('/api/system/dicts/items', {
        ...payload,
        dictType: currentType.value.dictType
      })
    }
    itemModal.value = false
    editingItem.value = null
    await Promise.all([loadItems(), loadTypes()])
    toast(isEdit ? '字典项已更新' : '字典项已新增')
  } catch (e) {
    toast.error(e.message || '保存失败')
  }
}

async function removeItem(row) {
  try {
    await request.delete(`/api/system/dicts/items/${row.id}`)
    await Promise.all([loadItems(), loadTypes()])
    toast('字典项已删除')
  } catch (e) {
    toast.error(e.message || '删除失败')
  }
}

function openTypeModal() {
  typeForm.dictType = ''
  typeForm.dictName = ''
  typeForm.remark = ''
  typeModal.value = true
}

async function submitType() {
  if (!typeForm.dictType || !typeForm.dictName) {
    toast('请填写字典编码与名称')
    return
  }
  try {
    await request.post('/api/system/dicts', {
      dictType: typeForm.dictType,
      dictName: typeForm.dictName,
      status: 1,
      remark: typeForm.remark || null
    })
    typeModal.value = false
    await loadTypes()
    activeCode.value = typeForm.dictType
    await loadItems()
    toast('字典类型已新增')
  } catch (e) {
    toast.error(e.message || '创建失败')
  }
}
</script>

<style scoped>
.dict-grid {
  display: grid;
  grid-template-columns: 292px minmax(0, 1fr);
  gap: 12px;
  align-items: start;
  margin-top: 12px;
}
.dict-list {
  padding: 0;
}
.dict-row {
  align-items: flex-start;
}
.dict-meta {
  flex: 1;
  min-width: 0;
}
.dict-name {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}
.dict-name b {
  font-size: 13px;
  font-weight: 500;
}
.dict-code {
  color: var(--ink-3);
  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
  font-size: 11px;
}
.dict-row.active .dict-code {
  color: var(--brand);
}
.dict-remark {
  margin-top: 1px;
  color: var(--ink-3);
  font-size: 11.5px;
  line-height: 1.5;
}
.dict-row.active .dict-remark {
  color: var(--brand);
  opacity: 0.85;
}
.dict-row .cnt {
  flex: none;
}
.dict-left-f {
  padding-top: 8px;
  margin-top: 8px;
  border-top: 1px solid var(--line-2);
}
.dict-add-type {
  width: 100%;
}
.dict-tools {
  display: flex;
  align-items: center;
  gap: 10px;
}
.dict-context {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 5px 10px;
  border-radius: 8px;
  background: var(--surface-2);
  font-size: 12.5px;
}
.form-2 {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 12px;
}
</style>
