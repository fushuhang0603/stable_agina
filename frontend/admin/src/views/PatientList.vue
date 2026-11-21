<template>
  <div class="patient-list">
    <el-card shadow="never" class="filter-card">
      <div class="filter-bar">
        <el-input v-model.trim="filters.keyword" placeholder="姓名 / 患者编号 / 手机号" clearable>
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="filters.risk" placeholder="危险分层" clearable>
          <el-option v-for="o in riskOptions" :key="o.value" :label="o.label" :value="o.value" />
        </el-select>
        <el-select v-model="filters.status" placeholder="随访状态" clearable>
          <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
        </el-select>
        <el-select v-model="filters.doctor" placeholder="随访医生" clearable>
          <el-option v-for="o in doctorOptions" :key="o.value" :label="o.label" :value="o.value" />
        </el-select>
        <el-button type="primary" @click="page = 1">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <div class="filter-spacer"></div>
        <el-button @click="toast('演示环境：已按当前筛选条件创建导出任务')">导出</el-button>
        <el-button type="primary" @click="toast('演示环境：新增患者需接入后端接口')">新增患者</el-button>
      </div>
    </el-card>

    <Card title="患者列表">
      <template #extra>
        <span class="card-hint">共 {{ filtered.length }} 位患者，按危险分层与最近随访排序</span>
      </template>

      <el-table :data="pageRows" row-class-name="clickable-row" @row-click="(row) => goDetail(row.id)">
        <el-table-column label="患者" min-width="180">
          <template #default="{ row }">
            <div class="patient-cell">
              <span class="avatar" style="width: 26px; height: 26px">{{ row.name.charAt(0) }}</span>
              <div>
                <b>{{ row.name }}</b>
                <div class="mono">{{ row.id }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="性别 / 年龄" width="104">
          <template #default="{ row }">{{ row.sex }} / {{ row.age }} 岁</template>
        </el-table-column>
        <el-table-column prop="diagnoseDate" label="确诊日期" width="110" />
        <el-table-column label="危险分层" width="96">
          <template #default="{ row }"><Chip :text="row.risk" /></template>
        </el-table-column>
        <el-table-column prop="lastVisit" label="最近随访" width="110" />
        <el-table-column prop="nextVisit" label="下次随访" width="110" />
        <el-table-column label="30 天发作" width="100">
          <template #default="{ row }">
            <span :class="row.attacks30 >= 5 ? 'txt-red' : ''">{{ row.attacks30 }} 次</span>
          </template>
        </el-table-column>
        <el-table-column label="用药依从率" width="140">
          <template #default="{ row }">
            <div class="adherence">
              <span class="bar-track">
                <span
                  class="bar-fill"
                  :class="row.adherence < 70 ? 'red' : row.adherence < 85 ? 'amber' : ''"
                  :style="{ width: row.adherence + '%' }"
                ></span>
              </span>
              <span class="pct">{{ row.adherence }}%</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="96">
          <template #default="{ row }"><Chip :text="row.status" /></template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="right">
          <template #default="{ row }">
            <el-button link type="primary" @click.stop="goDetail(row.id)">查看档案</el-button>
            <el-button link type="primary" @click.stop="toast(`已为 ${row.name} 生成随访提醒`)">随访</el-button>
          </template>
        </el-table-column>
        <template #empty>未找到符合条件的患者</template>
      </el-table>

      <ListPager v-model:page="page" :total="filtered.length" :size="pageSize" />
    </Card>
  </div>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import Card from '../components/Card.vue'
import Chip from '../components/Chip.vue'
import ListPager from '../components/ListPager.vue'
import { PATIENTS } from '../mock'
import { toast } from '../utils/toast'

const router = useRouter()

const filters = reactive({ keyword: '', risk: undefined, status: undefined, doctor: undefined })
const page = ref(1)
const pageSize = 8

const riskOptions = [
  { value: '高危', label: '高危' },
  { value: '中危', label: '中危' },
  { value: '低危', label: '低危' }
]
const statusOptions = [
  { value: '随访中', label: '随访中' },
  { value: '已逾期', label: '已逾期' }
]
const doctorOptions = [
  { value: '李医生', label: '李医生' },
  { value: '王医生', label: '王医生' }
]

const filtered = computed(() =>
  PATIENTS.filter((row) => {
    const kw = filters.keyword
    if (kw && !row.name.includes(kw) && !row.id.includes(kw) && !row.phone.includes(kw)) return false
    if (filters.risk && row.risk !== filters.risk) return false
    if (filters.status && row.status !== filters.status) return false
    if (filters.doctor && row.doctor !== filters.doctor) return false
    return true
  })
)

const totalPages = computed(() => Math.max(1, Math.ceil(filtered.value.length / pageSize)))
const pageRows = computed(() => filtered.value.slice((page.value - 1) * pageSize, page.value * pageSize))

watch(filtered, () => {
  if (page.value > totalPages.value) page.value = totalPages.value
})

function reset() {
  filters.keyword = ''
  filters.risk = undefined
  filters.status = undefined
  filters.doctor = undefined
  page.value = 1
}

function goDetail(id) {
  router.push(`/patient-detail/${id}`)
}
</script>

<style scoped>
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
.adherence {
  display: flex;
  align-items: center;
  gap: 6px;
}
.adherence .bar-track {
  width: 64px;
  flex: none;
}
.adherence .pct {
  color: var(--ink-2);
  font-variant-numeric: tabular-nums;
}
.txt-red {
  color: var(--red);
  font-weight: 500;
}
:deep(.clickable-row) {
  cursor: pointer;
}
</style>
