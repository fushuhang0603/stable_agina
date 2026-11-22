<template>
  <div v-loading="loading" class="patient-detail">
    <el-card v-if="notFound" shadow="never">
      <el-empty description="未找到该患者档案" />
    </el-card>

    <template v-else-if="patient.id">
      <el-card shadow="never" class="hero">
        <div class="hero-body">
          <div class="hero-avatar">{{ (patient.name || '?').charAt(0) }}</div>
          <div class="hero-main">
            <div class="hero-title">
              <h2>{{ patient.name || '—' }}</h2>
              <Chip :text="patient.riskLevelText || '—'" />
              <Chip :text="patient.statusText || '—'" />
            </div>
            <div class="hero-meta">
              <span>{{ patient.genderText || '—' }} · {{ patient.age != null ? patient.age + ' 岁' : '—' }}</span>
              <span>{{ patient.patientNo || '—' }}</span>
              <span>{{ patient.phone || '—' }}</span>
              <span>确诊 {{ patient.diagnoseDate || '—' }}</span>
              <span>管床医生 {{ patient.doctorName || '—' }}</span>
            </div>
          </div>
          <div class="hero-actions">
            <el-button @click="openEdit">编辑档案</el-button>
            <el-button type="primary" @click="goAi">生成 AI 摘要</el-button>
          </div>
        </div>
      </el-card>

      <div class="detail-grid">
        <div class="detail-main">
          <el-tabs v-model="activeTab" class="detail-tabs">
            <el-tab-pane label="基本信息" name="basic">
              <Card title="基线档案">
                <dl class="kv">
                  <dt>首次发作</dt>
                  <dd>{{ baseline ? dash(baseline.firstAttack) : '—' }}</dd>
                  <dt>检查结果</dt>
                  <dd>{{ baseline ? dash(baseline.examResult) : '—' }}</dd>
                  <dt>危险分层</dt>
                  <dd>
                    <Chip :text="patient.riskLevelText || '—'" />
                    <span v-if="baseline && baseline.riskBasis" class="kv-note">{{ baseline.riskBasis }}</span>
                  </dd>
                  <dt>建档信息</dt>
                  <dd>
                    {{ baseline ? dash(baseline.registerDoctorName) : '—' }} ·
                    {{ baseline ? dash(baseline.registerDate) : '—' }}
                  </dd>
                  <dt>过敏史</dt>
                  <dd>{{ dash(patient.allergy) }}</dd>
                  <dt>合并症</dt>
                  <dd>{{ dash(patient.comorbidity) }}</dd>
                  <dt>备注</dt>
                  <dd>{{ dash(patient.remark) }}</dd>
                </dl>
              </Card>

              <Card title="档案变更留痕">
                <div class="timeline">
                  <div v-for="(log, i) in changeLogs" :key="log.id" class="tl" :class="{ now: i === 0 }">
                    <div class="t">{{ log.fieldName }}：{{ log.beforeValue || '—' }} → {{ log.afterValue || '—' }}</div>
                    <div class="s">{{ log.changeTime }} · {{ log.operatorName }}</div>
                  </div>
                  <div v-if="!changeLogs.length" class="t-muted">暂无变更留痕</div>
                </div>
              </Card>
            </el-tab-pane>

            <el-tab-pane name="angina">
              <template #label>
                发作打卡 <span class="tab-count">{{ attacks.length }}</span>
              </template>
              <Card title="心绞痛发作打卡">
                <el-table :data="attacks">
                  <el-table-column prop="time" label="发作时间" width="150" />
                  <el-table-column label="疼痛评分" width="150">
                    <template #default="{ row }">
                      <b :class="row.nrs >= 7 ? 'txt-red' : ''">{{ row.nrs }} 分</b>
                      <Chip v-if="row.high" text="高危提醒" style="margin-left: 6px" />
                    </template>
                  </el-table-column>
                  <el-table-column prop="duration" label="持续时间" width="110" />
                  <el-table-column prop="trigger" label="诱因" width="110" />
                  <el-table-column prop="relief" label="缓解方式" width="120" />
                  <el-table-column label="缓解用时" width="110">
                    <template #default="{ row }">{{ row.reliefMin }} 分钟</template>
                  </el-table-column>
                  <el-table-column label="备注" min-width="140">
                    <template #default="{ row }"><span class="t-muted">{{ row.note || '—' }}</span></template>
                  </el-table-column>
                  <template #empty>该患者暂无发作打卡记录</template>
                </el-table>
              </Card>
            </el-tab-pane>

            <el-tab-pane name="bp">
              <template #label>
                血压监测 <span class="tab-count">{{ bpRecords.length }}</span>
              </template>
              <Card title="近 14 天血压趋势">
                <EChart :option="bpOption" :height="240" />
              </Card>

              <Card title="血压记录明细">
                <el-table :data="bpRecords">
                  <el-table-column prop="date" label="日期" width="120" />
                  <el-table-column prop="period" label="时段" width="100" />
                  <el-table-column label="收缩压" width="130">
                    <template #default="{ row }">
                      <span :class="row.sbp >= 140 ? 'txt-red' : ''">{{ row.sbp }} mmHg</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="舒张压" width="130">
                    <template #default="{ row }">
                      <span :class="row.dbp >= 90 ? 'txt-red' : ''">{{ row.dbp }} mmHg</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="心率" width="110">
                    <template #default="{ row }">{{ row.hr }} 次/分</template>
                  </el-table-column>
                  <el-table-column label="判定" min-width="100">
                    <template #default="{ row }"><Chip :text="row.level" /></template>
                  </el-table-column>
                  <template #empty>该患者暂无血压记录</template>
                </el-table>
              </Card>
            </el-tab-pane>

            <el-tab-pane name="medication">
              <template #label>
                用药情况 <span class="tab-count">{{ medications.length }}</span>
              </template>
              <Card title="用药医嘱">
                <el-table :data="medications">
                  <el-table-column prop="drug" label="药品" min-width="150" />
                  <el-table-column label="规格" width="120">
                    <template #default="{ row }"><span class="t-muted">{{ row.spec }}</span></template>
                  </el-table-column>
                  <el-table-column prop="dosage" label="用法用量" min-width="140" />
                  <el-table-column label="用药周期" width="110">
                    <template #default="{ row }"><span class="mono">{{ row.cycle }}</span></template>
                  </el-table-column>
                  <el-table-column label="依从率" width="96">
                    <template #default="{ row }">
                      <span :class="row.adherence < 70 ? 'txt-red' : ''">{{ row.adherence }}%</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="状态" width="96">
                    <template #default="{ row }"><Chip :text="row.status" /></template>
                  </el-table-column>
                  <template #empty>该患者暂无用药医嘱</template>
                </el-table>
              </Card>

              <Card title="近 7 天服药打卡">
                <el-table :data="medChecks">
                  <el-table-column prop="date" label="日期" width="130" />
                  <el-table-column prop="period" label="时段" width="120" />
                  <el-table-column prop="drug" label="药品" min-width="160" />
                  <el-table-column label="结果" min-width="110">
                    <template #default="{ row }"><Chip :text="row.result" /></template>
                  </el-table-column>
                  <template #empty>该患者暂无服药打卡</template>
                </el-table>
              </Card>
            </el-tab-pane>

            <el-tab-pane name="followup">
              <template #label>
                随访记录 <span class="tab-count">{{ followups.length }}</span>
              </template>
              <Card title="随访记录">
                <div class="timeline">
                  <div v-for="item in followups" :key="item.id" class="tl" :class="followupTone(item.status)">
                    <div class="t">
                      {{ item.type }} · {{ item.status }}
                      <span class="mono" style="margin-left: 6px">{{ item.id }}</span>
                    </div>
                    <div class="s">计划 {{ item.dueDate }}{{ item.doneAt ? ` · 完成 ${item.doneAt}` : '' }} · {{ item.doctor }}</div>
                    <div v-if="item.conclusion" class="fc">{{ item.conclusion }}</div>
                  </div>
                  <div v-if="!followups.length" class="t-muted">该患者暂无随访记录</div>
                </div>
              </Card>
            </el-tab-pane>

            <el-tab-pane name="adverse">
              <template #label>
                不良反应 <span class="tab-count">{{ adverseList.length }}</span>
              </template>
              <Card title="不良反应上报">
                <div v-for="item in adverseList" :key="item.id" class="adverse-item">
                  <div class="adverse-head">
                    <b>{{ item.type }}</b>
                    <Chip :text="item.status" />
                    <span class="t-muted" style="margin-left: auto">{{ item.reportedAt }}</span>
                  </div>
                  <div class="adverse-desc">可疑药品：{{ item.drug }}｜{{ item.desc }}</div>
                  <div v-if="item.reply" class="adverse-reply">医生回复：{{ item.reply }}</div>
                </div>
                <div v-if="!adverseList.length" class="t-muted" style="padding: 20px 0">该患者暂无不良反应上报</div>
              </Card>
            </el-tab-pane>
          </el-tabs>
        </div>

        <aside class="detail-side">
          <Card title="关键指标">
            <div class="metric-row">
              <span class="mk">30 天发作次数</span>
              <span class="mv">—</span>
            </div>
            <div class="metric-row">
              <span class="mk">30 天血压异常</span>
              <span class="mv">—</span>
            </div>
            <div class="metric-row">
              <span class="mk">用药依从率</span>
              <span class="mv">—</span>
            </div>
            <div class="metric-row">
              <span class="mk">最近随访</span>
              <span class="mv">{{ dash(patient.lastVisitDate) }}</span>
            </div>
            <div class="metric-row">
              <span class="mk">下次随访</span>
              <span class="mv">{{ dash(patient.nextVisitDate) }}</span>
            </div>
          </Card>

          <Card title="患者标签">
            <div class="t-muted">暂无标签</div>
          </Card>

          <Card title="快捷操作">
            <div class="quick-list">
              <el-button class="quick-btn" @click="toast('演示环境：已发送随访提醒')">
                <el-icon><Bell /></el-icon>
                <span>发送随访提醒</span>
              </el-button>
              <el-button class="quick-btn" @click="router.push('/followup-board')">
                <el-icon><Tickets /></el-icon>
                <span>进入随访工单</span>
              </el-button>
              <el-button class="quick-btn" @click="router.push('/medication-order')">
                <el-icon><FirstAidKit /></el-icon>
                <span>查看用药医嘱</span>
              </el-button>
            </div>
          </Card>
        </aside>
      </div>

      <el-dialog v-model="showEdit" title="编辑患者档案" width="720px">
        <el-form label-position="top" class="edit-form">
          <div class="form-section">基础信息</div>
          <div class="form-2">
            <el-form-item label="姓名" required>
              <el-input v-model.trim="form.name" placeholder="请输入姓名" />
            </el-form-item>
            <el-form-item label="性别">
              <el-select v-model="form.gender" clearable style="width: 100%">
                <el-option v-for="o in genderOptions" :key="o.value" :value="o.value" :label="o.label" />
              </el-select>
            </el-form-item>
            <el-form-item label="出生日期">
              <el-date-picker
                v-model="form.birthDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择出生日期"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model.trim="form.phone" placeholder="手机号" />
            </el-form-item>
            <el-form-item label="过敏史" class="form-span">
              <el-input v-model="form.allergy" placeholder="如 磺胺类过敏" />
            </el-form-item>
            <el-form-item label="合并症" class="form-span">
              <el-input v-model="form.comorbidity" placeholder="如 高血压、2 型糖尿病" />
            </el-form-item>
          </div>

          <div class="form-section">随访管理</div>
          <div class="form-2">
            <el-form-item label="危险分层">
              <el-select v-model="form.riskLevel" clearable style="width: 100%">
                <el-option v-for="o in riskOptions" :key="o.value" :value="o.value" :label="o.label" />
              </el-select>
            </el-form-item>
            <el-form-item label="随访状态">
              <el-select v-model="form.status" clearable style="width: 100%">
                <el-option v-for="o in statusOptions" :key="o.value" :value="o.value" :label="o.label" />
              </el-select>
            </el-form-item>
            <el-form-item label="下次随访日期">
              <el-date-picker
                v-model="form.nextVisitDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择下次随访日期"
                style="width: 100%"
              />
            </el-form-item>
          </div>
          <el-form-item label="备注">
            <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="备注信息" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showEdit = false">取消</el-button>
          <el-button type="primary" :loading="saving" @click="submit">保存档案</el-button>
        </template>
      </el-dialog>
    </template>
  </div>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Card from '../components/Card.vue'
import Chip from '../components/Chip.vue'
import EChart from '../components/EChart.vue'
import { ADVERSE_REACTIONS, ATTACKS, BP_RECORDS, BP_TREND, FOLLOWUPS, MED_CHECKS, MED_ORDERS } from '../mock'
import request from '../utils/request'
import { toast } from '../utils/toast'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const saving = ref(false)
const notFound = ref(false)
const detail = ref(null)

const patient = computed(() => detail.value?.patient || {})
const baseline = computed(() => detail.value?.baseline || null)
const changeLogs = computed(() => detail.value?.changeLogs || [])

// 发作/血压/用药/随访/不良反应仍使用演示数据，按患者编号关联
const mockKey = computed(() => patient.value.patientNo)
const attacks = computed(() => ATTACKS.filter((item) => item.patientId === mockKey.value))
const bpRecords = computed(() => BP_RECORDS.filter((item) => item.patientId === mockKey.value))
const medications = computed(() => MED_ORDERS.filter((item) => item.patientId === mockKey.value))
const medChecks = computed(() => MED_CHECKS.filter((item) => item.patientId === mockKey.value))
const followups = computed(() => FOLLOWUPS.filter((item) => item.patientId === mockKey.value))
const adverseList = computed(() => ADVERSE_REACTIONS.filter((item) => item.patientId === mockKey.value))

const activeTab = ref('basic')

function dash(value) {
  return value === null || value === undefined || value === '' ? '—' : value
}

async function loadDetail() {
  loading.value = true
  notFound.value = false
  try {
    const data = await request.get(`/api/business/patients/${route.params.id}`)
    if (!data || !data.patient) {
      detail.value = null
      notFound.value = true
      return
    }
    detail.value = data
  } catch (e) {
    detail.value = null
    toast.error(e.message || '患者档案加载失败')
  } finally {
    loading.value = false
  }
}

watch(
  () => route.params.id,
  () => {
    activeTab.value = 'basic'
    loadDetail()
  },
  { immediate: true }
)

const bpOption = computed(() => ({
  grid: { left: 44, right: 16, top: 30, bottom: 28 },
  tooltip: { trigger: 'axis' },
  legend: { top: 0, right: 0, icon: 'circle', itemWidth: 8, itemHeight: 8, textStyle: { color: '#4E5969', fontSize: 11 } },
  xAxis: {
    type: 'category',
    data: BP_TREND.dates,
    axisLine: { lineStyle: { color: '#E5E6EB' } },
    axisTick: { show: false },
    axisLabel: { color: '#86909C', fontSize: 11 }
  },
  yAxis: {
    type: 'value',
    min: 60,
    max: 170,
    splitLine: { lineStyle: { color: '#F2F3F5' } },
    axisLabel: { color: '#86909C', fontSize: 11 }
  },
  series: [
    {
      name: '收缩压',
      type: 'line',
      smooth: true,
      symbolSize: 5,
      data: BP_TREND.sbp,
      lineStyle: { color: '#165DFF', width: 2 },
      itemStyle: { color: '#165DFF' },
      markLine: {
        silent: true,
        symbol: 'none',
        lineStyle: { color: '#F53F3F', type: 'dashed' },
        data: [{ yAxis: 140 }]
      }
    },
    {
      name: '舒张压',
      type: 'line',
      smooth: true,
      symbolSize: 5,
      data: BP_TREND.dbp,
      lineStyle: { color: '#00B42A', width: 2 },
      itemStyle: { color: '#00B42A' },
      markLine: {
        silent: true,
        symbol: 'none',
        lineStyle: { color: '#FF7D00', type: 'dashed' },
        data: [{ yAxis: 90 }]
      }
    }
  ]
}))

function followupTone(status) {
  if (status === '已完成') return 'done'
  if (status === '已驳回') return 'rej'
  return 'now'
}

function goAi() {
  router.push({ path: '/ai-summary', query: { patientId: patient.value.patientNo } })
}

const showEdit = ref(false)
const genderOptions = [
  { value: 1, label: '男' },
  { value: 2, label: '女' },
  { value: 0, label: '未知' }
]
const riskOptions = [
  { value: 'HIGH', label: '高危' },
  { value: 'MID', label: '中危' },
  { value: 'LOW', label: '低危' }
]
const statusOptions = [
  { value: 'FOLLOWING', label: '随访中' },
  { value: 'OVERDUE', label: '已逾期' }
]
const form = reactive({
  name: '',
  gender: null,
  birthDate: '',
  phone: '',
  allergy: '',
  comorbidity: '',
  riskLevel: '',
  status: '',
  nextVisitDate: '',
  remark: ''
})

function openEdit() {
  Object.assign(form, {
    name: patient.value.name || '',
    gender: patient.value.gender ?? null,
    birthDate: patient.value.birthDate || '',
    phone: patient.value.phone || '',
    allergy: patient.value.allergy || '',
    comorbidity: patient.value.comorbidity || '',
    riskLevel: patient.value.riskLevel || '',
    status: patient.value.status || '',
    nextVisitDate: patient.value.nextVisitDate || '',
    remark: patient.value.remark || ''
  })
  showEdit.value = true
}

async function submit() {
  if (!form.name) {
    toast('请输入姓名')
    return
  }
  saving.value = true
  try {
    await request.put(`/api/business/patients/${route.params.id}`, {
      name: form.name,
      gender: form.gender ?? null,
      birthDate: form.birthDate || null,
      phone: form.phone || null,
      allergy: form.allergy || null,
      comorbidity: form.comorbidity || null,
      riskLevel: form.riskLevel || null,
      status: form.status || null,
      nextVisitDate: form.nextVisitDate || null,
      remark: form.remark || null
    })
    toast('患者档案已保存')
    showEdit.value = false
    await loadDetail()
  } catch (e) {
    toast.error(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.hero {
  margin-bottom: 12px;
}
.hero :deep(.el-card__body) {
  padding: 16px 18px;
}
.hero-body {
  display: flex;
  align-items: center;
  gap: 14px;
}
.hero-main {
  min-width: 0;
  flex: 1;
}
.hero-title {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.hero-title h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}
.hero-actions {
  display: flex;
  gap: 8px;
  flex: none;
}
.detail-tabs :deep(.el-tabs__header) {
  margin-bottom: 12px;
}
.detail-tabs :deep(.el-tabs__item) {
  font-size: 13px;
  height: 38px;
}
.tab-count {
  margin-left: 4px;
  padding: 0 5px;
  border-radius: 8px;
  background: var(--surface-2);
  color: var(--ink-3);
  font-size: 11px;
  line-height: 16px;
  display: inline-block;
}
.detail-tabs :deep(.el-tabs__item.is-active) .tab-count {
  background: var(--brand-soft);
  color: var(--brand);
}
.kv-note {
  margin-left: 8px;
  color: var(--ink-3);
  font-size: 12px;
}
.txt-red {
  color: var(--red);
  font-weight: 500;
}
.fc {
  margin-top: 4px;
  padding: 8px 10px;
  border-radius: 8px;
  background: var(--surface-2);
  color: var(--ink-2);
  font-size: 12.5px;
  line-height: 1.7;
}
.adverse-item {
  padding: 10px 0;
  border-bottom: 1px solid var(--line-2);
}
.adverse-item:last-child {
  border-bottom: 0;
}
.adverse-head {
  display: flex;
  align-items: center;
  gap: 8px;
}
.adverse-desc {
  margin-top: 4px;
  color: var(--ink-2);
  font-size: 12.5px;
}
.adverse-reply {
  margin-top: 6px;
  padding: 7px 10px;
  border: 1px solid #d6e7ff;
  border-radius: 8px;
  background: var(--brand-soft);
  color: var(--ink-2);
  font-size: 12.5px;
}
.tag-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.quick-btn {
  justify-content: flex-start;
  width: 100%;
}
.quick-list .el-button + .el-button {
  margin-left: 0;
}
.edit-form {
  max-height: 62vh;
  overflow-y: auto;
  padding-right: 4px;
}
.form-2 {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 12px;
}
.form-span {
  grid-column: span 2;
}
.form-section {
  margin: 6px 0 12px;
  font-weight: 600;
  color: #303133;
}
</style>
