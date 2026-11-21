<template>
  <div class="dashboard">
    <div class="grid g4">
      <StatCard
        title="在管患者"
        :value="FOLLOWUP_PROGRESS.total"
        icon="users"
        tone="blue"
        desc="低危 46 · 中危 28 · 高危 12"
      />
      <StatCard
        title="本周期随访完成"
        :value="FOLLOWUP_PROGRESS.done"
        icon="check"
        tone="green"
        :desc="`完成率 ${doneRate}%`"
      />
      <StatCard
        title="逾期未随访"
        :value="FOLLOWUP_PROGRESS.overdue"
        icon="alert"
        tone="red"
        desc="需优先处理，避免失访"
      />
      <StatCard
        title="AI 摘要待审核"
        :value="pendingAi"
        icon="activity"
        tone="amber"
        desc="生成后需医生确认归档"
      />
    </div>

    <div class="grid g3 dash-row">
      <Card class="span2" title="近 7 天心绞痛发作次数">
        <EChart :option="attackOption" :height="228" />
      </Card>

      <Card title="风险分层分布">
        <EChart :option="riskOption" :height="228" />
      </Card>
    </div>

    <div class="grid g3 dash-row">
      <Card class="span2" title="待随访工单">
        <template #extra>
          <el-button size="small" @click="router.push('/followup-board')">查看全部</el-button>
        </template>
        <el-table :data="pendingRows" size="small">
          <el-table-column prop="id" label="工单号" width="120">
            <template #default="{ row }"><span class="mono">{{ row.id }}</span></template>
          </el-table-column>
          <el-table-column label="患者" min-width="150">
            <template #default="{ row }">
              <span class="link" @click="goPatient(row.patientId)">{{ row.patientName }}</span>
              <Chip :text="row.risk" style="margin-left: 6px" />
            </template>
          </el-table-column>
          <el-table-column prop="type" label="随访类型" width="110" />
          <el-table-column prop="dueDate" label="计划日期" width="110" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }"><Chip :text="row.status" /></template>
          </el-table-column>
          <el-table-column label="操作" width="90" align="right">
            <template #default>
              <el-button link type="primary" @click="router.push('/followup-board')">去随访</el-button>
            </template>
          </el-table-column>
          <template #empty>暂无待随访工单</template>
        </el-table>
      </Card>

      <Card title="血压异常次数排行">
        <div v-for="(row, i) in RISK_RANK" :key="row.name" class="rank-row">
          <span class="no" :class="{ top: i < 3 }">{{ i + 1 }}</span>
          <span class="nm">{{ row.name }}</span>
          <span class="bar-track">
            <span class="bar-fill" :class="i === 0 ? 'red' : i === 1 ? 'amber' : ''" :style="{ width: rankWidth(row.value) }"></span>
          </span>
          <span class="val">{{ row.value }} 次</span>
        </div>
      </Card>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import Card from '../components/Card.vue'
import Chip from '../components/Chip.vue'
import EChart from '../components/EChart.vue'
import StatCard from '../components/StatCard.vue'
import { AI_SUMMARIES, ATTACK_TREND, FOLLOWUPS, FOLLOWUP_PROGRESS, RISK_DIST, RISK_RANK } from '../mock'

const router = useRouter()

const doneRate = computed(() => Math.round((FOLLOWUP_PROGRESS.done / FOLLOWUP_PROGRESS.total) * 1000) / 10)
const pendingAi = computed(() => AI_SUMMARIES.filter((item) => item.status === '待审核').length)

const pendingRows = computed(() =>
  FOLLOWUPS.filter((item) => item.status === '待随访' || item.status === '已逾期' || item.status === '随访中')
    .slice()
    .sort((a, b) => a.dueDate.localeCompare(b.dueDate))
    .slice(0, 5)
)

const maxRank = Math.max(...RISK_RANK.map((item) => item.value))
const rankWidth = (value) => `${Math.round((value / maxRank) * 100)}%`

const attackOption = computed(() => ({
  grid: { left: 38, right: 16, top: 22, bottom: 28 },
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    data: ATTACK_TREND.dates,
    axisLine: { lineStyle: { color: '#E5E6EB' } },
    axisTick: { show: false },
    axisLabel: { color: '#86909C', fontSize: 11 }
  },
  yAxis: {
    type: 'value',
    name: '次',
    nameTextStyle: { color: '#86909C', fontSize: 11 },
    splitLine: { lineStyle: { color: '#F2F3F5' } },
    axisLabel: { color: '#86909C', fontSize: 11 }
  },
  series: [
    {
      name: '发作次数',
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
      data: ATTACK_TREND.counts,
      lineStyle: { color: '#165DFF', width: 2.5 },
      itemStyle: { color: '#165DFF', borderColor: '#fff', borderWidth: 2 },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(22, 93, 255, 0.18)' },
            { offset: 1, color: 'rgba(22, 93, 255, 0.02)' }
          ]
        }
      },
      markLine: {
        silent: true,
        symbol: 'none',
        lineStyle: { color: '#FF7D00', type: 'dashed' },
        label: { formatter: '高危阈值 5 次', color: '#FF7D00', fontSize: 11 },
        data: [{ yAxis: 5 }]
      }
    }
  ]
}))

const riskOption = computed(() => ({
  tooltip: { trigger: 'item', formatter: '{b}：{c} 人（{d}%）' },
  legend: { bottom: 0, icon: 'circle', itemWidth: 8, itemHeight: 8, textStyle: { color: '#4E5969', fontSize: 12 } },
  series: [
    {
      type: 'pie',
      radius: ['48%', '68%'],
      center: ['50%', '44%'],
      avoidLabelOverlap: true,
      itemStyle: { borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      data: RISK_DIST.map((item) => ({ name: item.name, value: item.value, itemStyle: { color: item.color } }))
    }
  ]
}))

function goPatient(id) {
  router.push(`/patient-detail/${id}`)
}
</script>

<style scoped>
.dash-row {
  margin-top: 12px;
}
.link {
  color: var(--brand);
  cursor: pointer;
}
</style>
