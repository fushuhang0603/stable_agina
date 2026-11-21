<template>
  <div class="adverse-reaction">
    <div class="grid g3">
      <StatCard title="待处置" :value="pendingCount" icon="alert" tone="red" desc="需尽快评估并回复患者" />
      <StatCard title="处置中" :value="handlingCount" icon="activity" tone="blue" desc="已给出初步意见，待闭环" />
      <StatCard title="已回复" :value="repliedCount" icon="check" tone="green" desc="已完成回复的上报记录" />
    </div>

    <el-card shadow="never" class="filter-card">
      <div class="filter-bar">
        <el-input
          v-model.trim="filters.keyword"
          placeholder="患者姓名 / 药品名称"
          clearable
          style="width: 220px"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="filters.type" placeholder="反应类型" clearable style="width: 150px">
          <el-option v-for="o in typeOptions" :key="o.value" :value="o.value" :label="o.label" />
        </el-select>
        <el-select v-model="filters.status" placeholder="处置状态" clearable style="width: 140px">
          <el-option v-for="o in statusOptions" :key="o.value" :value="o.value" :label="o.label" />
        </el-select>
        <el-button type="primary" @click="toast('已按当前条件筛选不良反应记录')">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </div>
    </el-card>

    <Card title="不良反应上报">
      <template #extra>
        <span class="card-hint">共 {{ filtered.length }} 条上报记录</span>
      </template>

      <div class="ar-list">
        <article v-for="row in filtered" :key="row.id" class="ar-card">
          <div class="ar-top">
            <b class="ar-name">{{ row.patientName }}</b>
            <Chip :text="riskOf(row.patientId)" />
            <Chip :text="row.status" />
            <span class="ar-time">
              <el-icon><Clock /></el-icon>{{ row.reportedAt }}
            </span>
          </div>
          <div class="ar-meta">
            <span>可疑药品：{{ row.drug }}</span>
            <span>类型：{{ row.type }}</span>
            <span class="mono">{{ row.id }}</span>
          </div>
          <div class="ar-desc">{{ row.desc }}</div>
          <div v-if="row.reply" class="ar-reply">
            <b>医生回复：</b>{{ row.reply }}
            <span v-if="row.handledAt" class="t-muted"> · {{ row.handledAt }}</span>
          </div>
          <div class="ar-ops">
            <el-button size="small" type="primary" @click="openHandle(row)">
              <el-icon><Edit /></el-icon>处置/回复
            </el-button>
          </div>
        </article>
        <el-empty v-if="!filtered.length" description="暂无符合条件的不良反应上报" />
      </div>
    </Card>

    <div class="section-note warn">
      <el-icon><WarningFilled /></el-icon>
      <span>
        <b>严重不良反应提醒：</b>出现过敏性休克、黑便、意识障碍等情况时，需立即电话联系患者并上报医务处，不得仅在系统内回复。
      </span>
    </div>

    <el-dialog v-model="showModal" title="不良反应处置" width="580px">
      <dl class="kv ar-kv">
        <dt>患者</dt>
        <dd>
          {{ current.patientName }}
          <Chip :text="riskOf(current.patientId)" style="margin-left: 6px" />
        </dd>
        <dt>可疑药品</dt>
        <dd>{{ current.drug }}</dd>
        <dt>反应类型</dt>
        <dd>{{ current.type }}</dd>
        <dt>上报时间</dt>
        <dd>{{ current.reportedAt }}</dd>
        <dt>上报描述</dt>
        <dd>{{ current.desc }}</dd>
        <dt>当前状态</dt>
        <dd><Chip :text="current.status" /></dd>
      </dl>

      <el-form label-position="top" size="default">
        <el-form-item label="医生回复" required>
          <el-input
            v-model="form.reply"
            type="textarea"
            :rows="4"
            resize="none"
            placeholder="请填写处置意见、复查建议与随访安排"
          />
        </el-form-item>
        <el-form-item label="处置结果">
          <el-radio-group v-model="form.result">
            <el-radio v-for="item in resultOptions" :key="item" :value="item">{{ item }}</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showModal = false">取消</el-button>
        <el-button type="primary" @click="submit">提交处置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import Card from '../components/Card.vue'
import Chip from '../components/Chip.vue'
import StatCard from '../components/StatCard.vue'
import { ADVERSE_REACTIONS, PATIENTS, nowText } from '../mock'
import { toast } from '../utils/toast'

const filters = reactive({ keyword: '', type: undefined, status: undefined })
const statusOptions = [
  { value: '待处置', label: '待处置' },
  { value: '处置中', label: '处置中' },
  { value: '已回复', label: '已回复' }
]
const typeOptions = computed(() =>
  [...new Set(ADVERSE_REACTIONS.map((row) => row.type))].map((type) => ({ value: type, label: type }))
)

const resultOptions = ['继续观察', '减量', '停药', '转诊']

const pendingCount = computed(() => ADVERSE_REACTIONS.filter((row) => row.status === '待处置').length)
const handlingCount = computed(() => ADVERSE_REACTIONS.filter((row) => row.status === '处置中').length)
const repliedCount = computed(() => ADVERSE_REACTIONS.filter((row) => row.status === '已回复').length)

const filtered = computed(() =>
  ADVERSE_REACTIONS.filter((row) => {
    const kw = filters.keyword
    if (kw && !row.patientName.includes(kw) && !row.drug.includes(kw)) return false
    if (filters.type && row.type !== filters.type) return false
    if (filters.status && row.status !== filters.status) return false
    return true
  })
)

const showModal = ref(false)
const current = ref({})
const form = reactive({ reply: '', result: '继续观察' })

function riskOf(patientId) {
  const patient = PATIENTS.find((item) => item.id === patientId)
  return patient ? patient.risk : '—'
}

function openHandle(row) {
  current.value = row
  form.reply = row.reply || ''
  form.result = '继续观察'
  showModal.value = true
}

function submit() {
  if (!form.reply.trim()) {
    toast('请填写医生回复')
    return
  }
  const target = ADVERSE_REACTIONS.find((item) => item.id === current.value.id)
  if (target) {
    target.reply = `${form.result}：${form.reply.trim()}`
    target.status = '已回复'
    target.handledAt = nowText()
  }
  showModal.value = false
  toast('不良反应处置已提交')
}

function reset() {
  filters.keyword = ''
  filters.type = undefined
  filters.status = undefined
}
</script>

<style scoped>
.filter-card {
  margin-bottom: 12px;
}
.ar-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.ar-card {
  border: 1px solid #eef0f4;
  border-radius: var(--radius-card);
  padding: 14px 16px;
  background: #fff;
  box-shadow: var(--shadow-card);
  transition: border-color 0.15s, box-shadow 0.15s;
}
.ar-card:hover {
  border-color: #bedaff;
  box-shadow: var(--shadow-card-hover);
}
.ar-top {
  display: flex;
  align-items: center;
  gap: 8px;
}
.ar-name {
  font-weight: 600;
}
.ar-time {
  margin-left: auto;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: var(--ink-3);
  font-size: 11.5px;
}
.ar-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 5px;
  color: var(--ink-2);
  font-size: 12px;
}
.ar-meta .mono {
  font-size: 11.5px;
  color: var(--ink-3);
}
.ar-desc {
  margin-top: 7px;
  padding: 7px 9px;
  border-radius: var(--radius-sub);
  background: var(--surface-2);
  color: var(--ink-2);
  font-size: 12.5px;
  line-height: 1.7;
}
.ar-reply {
  margin-top: 7px;
  padding: 7px 9px;
  border: 1px solid #d6e7ff;
  border-radius: var(--radius-sub);
  background: var(--brand-soft);
  color: var(--ink-2);
  font-size: 12.5px;
  line-height: 1.7;
}
.ar-reply b {
  color: var(--brand);
  font-weight: 500;
}
.ar-ops {
  display: flex;
  gap: 6px;
  margin-top: 9px;
}
.ar-kv {
  margin-bottom: 16px;
}
</style>
