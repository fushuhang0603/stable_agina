<template>
  <div class="medication-order">
    <div class="grid g4">
      <StatCard title="医嘱总数" :value="MED_ORDERS.length" icon="clipboard" tone="blue" desc="全部在册用药医嘱" />
      <StatCard title="服用中" :value="takingCount" icon="check" tone="green" desc="当前仍在执行的医嘱" />
      <StatCard title="依从率低于 70%" :value="lowAdherenceCount" icon="alert" tone="red" desc="需重点干预的医嘱" />
      <StatCard title="今日漏服次数" :value="missedCount" icon="clock" tone="amber" desc="来源：服药打卡记录" />
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
        <el-select v-model="filters.status" placeholder="医嘱状态" clearable style="width: 140px">
          <el-option v-for="o in statusOptions" :key="o.value" :value="o.value" :label="o.label" />
        </el-select>
        <el-select v-model="filters.adherence" placeholder="依从率区间" clearable style="width: 150px">
          <el-option v-for="o in adherenceOptions" :key="o.value" :value="o.value" :label="o.label" />
        </el-select>
        <el-button type="primary" @click="page = 1">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <div class="filter-spacer"></div>
        <el-button @click="toast('演示环境：已按当前筛选条件创建医嘱导出任务')">导出</el-button>
        <el-button type="primary" @click="toast('演示环境：新增医嘱需接入后端接口')">新增医嘱</el-button>
      </div>
    </el-card>

    <Card title="医嘱列表">
      <template #extra>
        <span class="card-hint">共 {{ filtered.length }} 条医嘱，依从率低于 70% 标记为红色</span>
      </template>

      <el-table :data="pageRows" size="small">
        <el-table-column label="患者" min-width="140">
          <template #default="{ row }">
            <div class="patient-cell">
              <b>{{ row.patientName }}</b>
              <span class="mono">{{ row.patientId }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="药品" min-width="150">
          <template #default="{ row }">
            <div class="drug-cell">
              <b>{{ row.drug }}</b>
              <span class="spec">{{ row.spec }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="dosage" label="用法用量" min-width="170" />
        <el-table-column prop="cycle" label="用药周期" width="150">
          <template #default="{ row }"><span class="mono">{{ row.cycle }}</span></template>
        </el-table-column>
        <el-table-column label="依从率" width="150">
          <template #default="{ row }">
            <div class="adherence">
              <span class="bar-track">
                <span
                  class="bar-fill"
                  :class="adherenceClass(row.adherence)"
                  :style="{ width: row.adherence + '%' }"
                ></span>
              </span>
              <span class="pct" :class="{ red: row.adherence < 70 }">{{ row.adherence }}%</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }"><Chip :text="row.status" /></template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewChecks(row)">查看打卡</el-button>
            <el-button link type="primary" @click="toggleStatus(row)">
              {{ row.status === '服用中' ? '停用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
        <template #empty>未找到符合条件的医嘱</template>
      </el-table>

      <ListPager v-model:page="page" :total="filtered.length" :size="pageSize" />
    </Card>

    <Card title="近 7 天服药打卡">
      <template #extra>
        <el-input v-model.trim="checkKeyword" placeholder="按患者姓名过滤" clearable size="small" style="width: 180px">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
      </template>

      <div ref="checkArea">
        <el-table :data="checkRows" size="small">
          <el-table-column prop="date" label="日期" width="120" />
          <el-table-column prop="period" label="时段" width="100" />
          <el-table-column prop="patientName" label="患者" min-width="120" />
          <el-table-column prop="drug" label="药品" min-width="160" />
          <el-table-column label="结果" width="110">
            <template #default="{ row }"><Chip :text="row.result" /></template>
          </el-table-column>
          <template #empty>暂无符合条件的打卡记录</template>
        </el-table>
      </div>
    </Card>
  </div>
</template>

<script setup>
import { computed, nextTick, reactive, ref, watch } from 'vue'
import Card from '../components/Card.vue'
import Chip from '../components/Chip.vue'
import ListPager from '../components/ListPager.vue'
import StatCard from '../components/StatCard.vue'
import { MED_CHECKS, MED_ORDERS } from '../mock'
import { toast } from '../utils/toast'

const filters = reactive({ keyword: '', status: undefined, adherence: undefined })
const page = ref(1)
const pageSize = 8
const checkKeyword = ref('')
const checkArea = ref(null)

const statusOptions = [
  { value: '服用中', label: '服用中' },
  { value: '已停用', label: '已停用' }
]
const adherenceOptions = [
  { value: 'low', label: '低于 70%' },
  { value: 'mid', label: '70% ~ 85%' },
  { value: 'high', label: '85% 及以上' }
]

const takingCount = computed(() => MED_ORDERS.filter((row) => row.status === '服用中').length)
const lowAdherenceCount = computed(() => MED_ORDERS.filter((row) => row.adherence < 70).length)
const missedCount = computed(() => MED_CHECKS.filter((row) => row.result === '漏服').length)

const filtered = computed(() =>
  MED_ORDERS.filter((row) => {
    const kw = filters.keyword
    if (kw && !row.patientName.includes(kw) && !row.drug.includes(kw)) return false
    if (filters.status && row.status !== filters.status) return false
    if (filters.adherence === 'low' && !(row.adherence < 70)) return false
    if (filters.adherence === 'mid' && !(row.adherence >= 70 && row.adherence < 85)) return false
    if (filters.adherence === 'high' && !(row.adherence >= 85)) return false
    return true
  })
)

const totalPages = computed(() => Math.max(1, Math.ceil(filtered.value.length / pageSize)))
const pageRows = computed(() => filtered.value.slice((page.value - 1) * pageSize, page.value * pageSize))

const checkRows = computed(() =>
  MED_CHECKS.filter((row) => !checkKeyword.value || row.patientName.includes(checkKeyword.value))
)

watch(filtered, () => {
  if (page.value > totalPages.value) page.value = totalPages.value
})

function reset() {
  filters.keyword = ''
  filters.status = undefined
  filters.adherence = undefined
  page.value = 1
}

function adherenceClass(value) {
  if (value < 70) return 'red'
  if (value < 85) return 'amber'
  return ''
}

function viewChecks(row) {
  checkKeyword.value = row.patientName
  nextTick(() => {
    if (checkArea.value) checkArea.value.scrollIntoView({ behavior: 'smooth', block: 'start' })
  })
  toast(`已切换到 ${row.patientName} 的服药打卡记录`)
}

function toggleStatus(row) {
  const target = MED_ORDERS.find((item) => item.id === row.id)
  if (!target) return
  target.status = target.status === '服用中' ? '已停用' : '服用中'
  toast(`${target.drug} 已${target.status === '服用中' ? '启用' : '停用'}`)
}
</script>

<style scoped>
.filter-card {
  margin-bottom: 12px;
}
.patient-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
.patient-cell b {
  font-weight: 500;
}
.patient-cell .mono {
  font-size: 11.5px;
  color: var(--ink-3);
}
.drug-cell {
  display: flex;
  flex-direction: column;
  gap: 1px;
}
.drug-cell b {
  font-weight: 500;
}
.drug-cell .spec {
  font-size: 11.5px;
  color: var(--ink-3);
}
.adherence {
  display: flex;
  align-items: center;
  gap: 8px;
}
.adherence .bar-track {
  width: 88px;
  flex: none;
}
.adherence .pct {
  font-variant-numeric: tabular-nums;
  color: var(--ink-2);
}
.adherence .pct.red {
  color: var(--red);
  font-weight: 500;
}
</style>
