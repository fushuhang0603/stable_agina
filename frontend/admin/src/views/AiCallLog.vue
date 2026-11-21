<template>
  <div class="ai-call-log">
    <div class="grid g4 log-stats">
      <StatCard
        title="调用总次数"
        :value="AI_LOGS.length"
        icon="activity"
        tone="blue"
        desc="全部 AI 调用记录"
      />
      <StatCard
        title="成功率"
        :value="successRate"
        icon="check"
        tone="green"
        :formatter="percentText"
        desc="成功调用 / 总调用"
      />
      <StatCard
        title="平均耗时"
        :value="avgDuration"
        icon="clock"
        tone="amber"
        :formatter="durationText"
        desc="按日志 duration 字段求平均"
      />
      <StatCard
        title="人工修改率"
        :value="editRate"
        icon="edit"
        :formatter="percentText"
        desc="edited=true 的调用占比"
      />
    </div>

    <el-card shadow="never" class="filter-card">
      <div class="filter-bar">
        <el-input
          v-model.trim="filters.keyword"
          placeholder="traceId / 患者 / 调用人"
          clearable
          style="width: 240px"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="filters.result" placeholder="全部结果" clearable style="width: 110px">
          <el-option v-for="o in resultOptions" :key="o.value" :value="o.value" :label="o.label" />
        </el-select>
        <el-select v-model="filters.scene" placeholder="全部场景" clearable style="width: 130px">
          <el-option v-for="o in sceneOptions" :key="o.value" :value="o.value" :label="o.label" />
        </el-select>
        <el-button @click="reset">重置</el-button>
      </div>
    </el-card>

    <Card title="AI 调用日志">
      <template #extra>
        <span class="card-hint">共 {{ filtered.length }} 条，traceId 可关联后端 ai_call_log 表</span>
      </template>
      <el-table :data="filtered" size="small">
        <el-table-column label="traceId" width="150">
          <template #default="{ row }"><span class="mono">{{ row.traceId }}</span></template>
        </el-table-column>
        <el-table-column prop="time" label="时间" width="150" />
        <el-table-column prop="user" label="调用人" width="100" />
        <el-table-column prop="scene" label="场景" width="140" show-overflow-tooltip />
        <el-table-column prop="patient" label="患者" width="100" />
        <el-table-column label="模型" width="150">
          <template #default="{ row }"><span class="mono">{{ row.model }}</span></template>
        </el-table-column>
        <el-table-column prop="tokens" label="tokens" width="90" align="right" />
        <el-table-column prop="duration" label="耗时" width="90" align="right" />
        <el-table-column label="结果" width="80">
          <template #default="{ row }"><Chip :text="row.result" /></template>
        </el-table-column>
        <el-table-column label="是否人工修改" width="120">
          <template #default="{ row }"><Chip :text="row.edited ? '已修改' : '未修改'" /></template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="current = row">详情</el-button>
          </template>
        </el-table-column>
        <template #empty>未找到符合条件的 AI 调用日志</template>
      </el-table>
    </Card>

    <el-dialog v-model="currentVisible" title="AI 调用详情" width="540px">
      <dl v-if="current" class="kv">
        <dt>traceId</dt>
        <dd class="mono">{{ current.traceId }}</dd>
        <dt>调用时间</dt>
        <dd>{{ current.time }}</dd>
        <dt>调用人</dt>
        <dd>{{ current.user }}</dd>
        <dt>调用场景</dt>
        <dd>{{ current.scene }}</dd>
        <dt>患者</dt>
        <dd>{{ current.patient }}</dd>
        <dt>模型</dt>
        <dd class="mono">{{ current.model }}</dd>
        <dt>tokens</dt>
        <dd>{{ current.tokens }}</dd>
        <dt>耗时</dt>
        <dd>{{ current.duration }}</dd>
        <dt>调用结果</dt>
        <dd><Chip :text="current.result" /></dd>
        <dt>人工修改</dt>
        <dd><Chip :text="current.edited ? '已修改' : '未修改'" /></dd>
      </dl>

      <template #footer>
        <el-button @click="current = null">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import Card from '../components/Card.vue'
import Chip from '../components/Chip.vue'
import StatCard from '../components/StatCard.vue'
import { AI_LOGS } from '../mock'

const filters = reactive({ keyword: '', result: undefined, scene: undefined })
const current = ref(null)
const currentVisible = computed({
  get: () => !!current.value,
  set: (val) => {
    if (!val) current.value = null
  }
})

const successRate = computed(() => {
  if (!AI_LOGS.length) return 0
  const ok = AI_LOGS.filter((row) => row.result === '成功').length
  return Math.round((ok / AI_LOGS.length) * 1000) / 10
})

const avgDuration = computed(() => {
  if (!AI_LOGS.length) return 0
  const total = AI_LOGS.reduce((sum, row) => sum + (parseFloat(row.duration) || 0), 0)
  return Math.round((total / AI_LOGS.length) * 10) / 10
})

const editRate = computed(() => {
  if (!AI_LOGS.length) return 0
  const edited = AI_LOGS.filter((row) => row.edited).length
  return Math.round((edited / AI_LOGS.length) * 1000) / 10
})

const sceneOptions = computed(() =>
  [...new Set(AI_LOGS.map((row) => row.scene))].map((value) => ({ value, label: value }))
)
const resultOptions = [
  { value: '成功', label: '成功' },
  { value: '失败', label: '失败' }
]

const filtered = computed(() =>
  AI_LOGS.filter((row) => {
    const kw = filters.keyword
    if (kw && !row.traceId.includes(kw) && !row.patient.includes(kw) && !row.user.includes(kw)) return false
    if (filters.result && row.result !== filters.result) return false
    if (filters.scene && row.scene !== filters.scene) return false
    return true
  })
)

const percentText = (value) => `${Number(value).toFixed(1)}%`
const durationText = (value) => `${Number(value).toFixed(1)}s`

function reset() {
  filters.keyword = ''
  filters.result = undefined
  filters.scene = undefined
}
</script>

<style scoped>
.log-stats {
  margin-bottom: 12px;
}
.mono {
  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
  font-size: 12px;
}
</style>
