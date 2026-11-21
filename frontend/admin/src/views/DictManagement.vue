<template>
  <div class="dict-management">
    <div class="dict-grid">
      <Card title="字典类型" class="dict-left">
        <template #extra>
          <span class="card-hint">共 {{ DICTS.length }} 类</span>
        </template>
        <div class="tree dict-list">
          <div
            v-for="dict in DICTS"
            :key="dict.code"
            class="tree-row dict-row"
            :class="{ active: currentDict && dict.code === currentDict.code }"
            @click="activeCode = dict.code"
          >
            <el-icon><Notebook /></el-icon>
            <div class="dict-meta">
              <div class="dict-name">
                <b>{{ dict.name }}</b>
                <span class="dict-code">{{ dict.code }}</span>
              </div>
              <div class="dict-remark">{{ dict.remark }}</div>
            </div>
            <span class="cnt">{{ dict.items.length }}</span>
          </div>
        </div>
        <div class="dict-left-f">
          <el-button class="dict-add-type" @click="openTypeModal">新增字典类型</el-button>
        </div>
      </Card>

      <Card :title="currentDict ? `${currentDict.name} · 字典项` : '字典项'">
        <template #extra>
          <div class="dict-tools">
            <span v-if="currentDict" class="card-hint dict-code">{{ currentDict.code }}</span>
            <el-button type="primary" size="small" :disabled="!currentDict" @click="openItemModal(null)">
              新增字典项
            </el-button>
          </div>
        </template>

        <el-table v-if="currentDict" :data="currentDict.items" size="small">
          <el-table-column prop="label" label="标签" min-width="140" />
          <el-table-column label="值" min-width="140">
            <template #default="{ row }"><span class="mono">{{ row.value }}</span></template>
          </el-table-column>
          <el-table-column prop="sort" label="排序" width="80" />
          <el-table-column label="状态" width="90">
            <template #default="{ row }"><Chip :text="row.status" /></template>
          </el-table-column>
          <el-table-column label="操作" width="130" align="right">
            <template #default="{ row, $index }">
              <el-button link type="primary" @click="openItemModal(row)">编辑</el-button>
              <el-button link type="danger" @click="removeItem($index)">删除</el-button>
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
            <b>{{ currentDict.name }}</b>
            <span class="dict-code">{{ currentDict.code }}</span>
          </div>
        </el-form-item>
        <div class="form-2">
          <el-form-item label="标签" required>
            <el-input v-model.trim="itemForm.label" placeholder="如：劳累" />
          </el-form-item>
          <el-form-item label="值" required>
            <el-input v-model.trim="itemForm.value" placeholder="如：exertion" />
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
          <el-input v-model.trim="typeForm.code" placeholder="如：followup_type" />
        </el-form-item>
        <el-form-item label="字典名称" required>
          <el-input v-model.trim="typeForm.name" placeholder="如：随访类型" />
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
import { computed, reactive, ref } from 'vue'
import Card from '../components/Card.vue'
import Chip from '../components/Chip.vue'
import { DICTS } from '../mock'
import { toast } from '../utils/toast'

const activeCode = ref(DICTS[0]?.code)
const currentDict = computed(() => DICTS.find((dict) => dict.code === activeCode.value) || DICTS[0] || null)

const itemModal = ref(false)
const editingItem = ref(null)
const itemForm = reactive({ label: '', value: '', sort: '1', status: '启用' })

const typeModal = ref(false)
const typeForm = reactive({ code: '', name: '', remark: '' })

function openItemModal(item) {
  editingItem.value = item
  itemForm.label = item ? item.label : ''
  itemForm.value = item ? item.value : ''
  itemForm.sort = item ? String(item.sort) : String((currentDict.value?.items.length || 0) + 1)
  itemForm.status = item ? item.status : '启用'
  itemModal.value = true
}

function submitItem() {
  if (!currentDict.value) return
  if (!itemForm.label || !itemForm.value) {
    toast('请填写字典标签与值')
    return
  }
  const payload = {
    label: itemForm.label,
    value: itemForm.value,
    sort: Number(itemForm.sort) || 0,
    status: itemForm.status
  }
  const isEdit = !!editingItem.value
  if (isEdit) {
    Object.assign(editingItem.value, payload)
  } else {
    currentDict.value.items.push(payload)
  }
  itemModal.value = false
  editingItem.value = null
  toast(isEdit ? '字典项已更新（演示）' : '字典项已新增（演示）')
}

function removeItem(index) {
  if (!currentDict.value) return
  currentDict.value.items.splice(index, 1)
  toast('字典项已删除（演示）')
}

function openTypeModal() {
  typeForm.code = ''
  typeForm.name = ''
  typeForm.remark = ''
  typeModal.value = true
}

function submitType() {
  if (!typeForm.code || !typeForm.name) {
    toast('请填写字典编码与名称')
    return
  }
  if (DICTS.some((dict) => dict.code === typeForm.code)) {
    toast('字典编码已存在，请更换')
    return
  }
  DICTS.unshift({
    code: typeForm.code,
    name: typeForm.name,
    remark: typeForm.remark || '—',
    items: []
  })
  activeCode.value = typeForm.code
  typeModal.value = false
  toast('字典类型已新增（演示）')
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
