<template>
  <div class="operation-log">
    <div class="grid g3 log-stats">
      <StatCard
        title="今日操作数"
        :value="stats.today"
        icon="clock"
        tone="blue"
        desc="统计日期 2026-11-21"
      />
      <StatCard
        title="失败次数"
        :value="stats.failed"
        icon="alert"
        tone="red"
        desc="失败操作需重点核查"
      />
      <StatCard
        title="涉及用户数"
        :value="stats.users"
        icon="users"
        tone="amber"
        desc="去重后的操作账号数"
      />
    </div>

    <el-card shadow="never" class="filter-card">
      <div class="filter-bar">
        <el-input
          v-model.trim="filters.keyword"
          placeholder="操作人 / 操作对象"
          clearable
          style="width: 220px"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="filters.module" placeholder="全部模块" clearable style="width: 140px">
          <el-option v-for="o in moduleOptions" :key="o.value" :value="o.value" :label="o.label" />
        </el-select>
        <el-select v-model="filters.action" placeholder="全部操作" clearable style="width: 120px">
          <el-option v-for="o in actionOptions" :key="o.value" :value="o.value" :label="o.label" />
        </el-select>
        <el-select v-model="filters.result" placeholder="全部结果" clearable style="width: 110px">
          <el-option v-for="o in resultOptions" :key="o.value" :value="o.value" :label="o.label" />
        </el-select>
        <el-button @click="reset">重置</el-button>
        <div class="filter-spacer"></div>
        <el-button @click="toast('演示环境：已创建日志导出任务')">导出</el-button>
      </div>
    </el-card>

    <Card title="操作日志">
      <template #extra>
        <span class="card-hint">共 {{ filtered.length }} 条，按时间倒序展示</span>
      </template>
      <el-table :data="filtered" size="small">
        <el-table-column label="时间" width="150">
          <template #default="{ row }"><span class="mono">{{ row.time }}</span></template>
        </el-table-column>
        <el-table-column prop="user" label="操作人" width="110" />
        <el-table-column prop="module" label="模块" width="110" />
        <el-table-column prop="action" label="动作" width="100" />
        <el-table-column prop="target" label="操作对象" min-width="160" show-overflow-tooltip />
        <el-table-column label="结果" width="90">
          <template #default="{ row }"><Chip :text="row.result" /></template>
        </el-table-column>
        <el-table-column label="IP" width="130">
          <template #default="{ row }"><span class="mono">{{ row.ip }}</span></template>
        </el-table-column>
        <el-table-column prop="cost" label="耗时" width="90" />
        <el-table-column label="操作" width="80" align="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="detail = row">详情</el-button>
          </template>
        </el-table-column>
        <template #empty>未找到符合条件的操作日志</template>
      </el-table>
    </Card>

    <el-dialog v-model="detailVisible" title="操作日志详情" width="520px">
      <dl v-if="detail" class="kv">
        <dt>日志编号</dt>
        <dd class="mono">{{ detail.id }}</dd>
        <dt>操作时间</dt>
        <dd class="mono">{{ detail.time }}</dd>
        <dt>操作人</dt>
        <dd>{{ detail.user }}</dd>
        <dt>所属模块</dt>
        <dd>{{ detail.module }}</dd>
        <dt>动作</dt>
        <dd>{{ detail.action }}</dd>
        <dt>操作对象</dt>
        <dd>{{ detail.target }}</dd>
        <dt>执行结果</dt>
        <dd><Chip :text="detail.result" /></dd>
        <dt>来源 IP</dt>
        <dd class="mono">{{ detail.ip }}</dd>
        <dt>耗时时长</dt>
        <dd>{{ detail.cost }}</dd>
      </dl>
      <div class="section-note warn log-note">
        <el-icon><Lock /></el-icon>
        <span>日志不可修改，保留 180 天。</span>
      </div>
      <template #footer>
        <el-button @click="detail = null">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import Card from '../components/Card.vue'
import Chip from '../components/Chip.vue'
import StatCard from '../components/StatCard.vue'
import { OPER_LOGS } from '../mock'
import { toast } from '../utils/toast'

const filters = reactive({ keyword: '', module: undefined, action: undefined, result: undefined })
const detail = ref(null)
const detailVisible = computed({
  get: () => !!detail.value,
  set: (val) => {
    if (!val) detail.value = null
  }
})

const stats = computed(() => ({
  today: OPER_LOGS.filter((row) => row.time.startsWith('2026-11-21')).length,
  failed: OPER_LOGS.filter((row) => row.result === '失败').length,
  users: new Set(OPER_LOGS.map((row) => row.user)).size
}))

const moduleOptions = computed(() =>
  [...new Set(OPER_LOGS.map((row) => row.module))].map((value) => ({ value, label: value }))
)
const actionOptions = computed(() =>
  [...new Set(OPER_LOGS.map((row) => row.action))].map((value) => ({ value, label: value }))
)
const resultOptions = [
  { value: '成功', label: '成功' },
  { value: '失败', label: '失败' }
]

const filtered = computed(() =>
  OPER_LOGS.filter((row) => {
    const kw = filters.keyword
    if (kw && !row.user.includes(kw) && !row.target.includes(kw)) return false
    if (filters.module && row.module !== filters.module) return false
    if (filters.action && row.action !== filters.action) return false
    if (filters.result && row.result !== filters.result) return false
    return true
  })
)

function reset() {
  filters.keyword = ''
  filters.module = undefined
  filters.action = undefined
  filters.result = undefined
}
</script>

<style scoped>
.log-stats {
  margin-bottom: 12px;
}
.log-note {
  margin: 16px 0 0;
}
.mono {
  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
  font-size: 12px;
}
</style>
