<template>
  <div class="ai-summary">
    <div class="ai-grid">
      <aside class="ai-side">
        <Card title="生成参数">
          <el-form label-width="78px" label-position="left" size="small">
            <el-form-item label="选择患者">
              <el-select v-model="patientId" placeholder="请选择患者" style="width: 100%">
                <el-option v-for="o in patientOptions" :key="o.value" :value="o.value" :label="o.label" />
              </el-select>
            </el-form-item>
            <el-form-item label="统计范围">
              <el-select v-model="rangeType" placeholder="请选择范围" style="width: 100%">
                <el-option v-for="o in rangeOptions" :key="o.value" :value="o.value" :label="o.label" />
              </el-select>
            </el-form-item>
          </el-form>
          <div class="param-box">
            <div><span>模型</span><b>deepseek-chat</b></div>
            <div><span>temperature</span><b>0.2</b></div>
            <div><span>最大输出</span><b>1200 tokens</b></div>
            <div><span>运行模式</span><b class="green">Mock</b></div>
          </div>
          <el-button type="primary" class="generate-btn" :loading="generating" @click="generate">
            <el-icon v-if="!generating"><MagicStick /></el-icon>
            {{ generating ? '正在生成摘要…' : '生成 AI 随访摘要' }}
          </el-button>
          <div class="section-note" style="margin-top: 12px">
            <el-icon><InfoFilled /></el-icon>
            <span>AI 仅生成六段摘要供医生参考，不做诊断结论、不修改医嘱，必须经医生审核后方可用于随访记录。</span>
          </div>
        </Card>

        <Card title="历史摘要">
          <div
            v-for="item in AI_SUMMARIES"
            :key="item.id"
            class="history-item"
            :class="{ active: current && current.id === item.id }"
            @click="load(item)"
          >
            <div class="history-top">
              <b>{{ item.patientName }}</b>
              <Chip :text="item.status" />
            </div>
            <div class="history-sub">{{ item.range }}</div>
            <div class="history-sub mono">{{ item.createdAt }} · {{ item.tokens }} tokens</div>
          </div>
        </Card>
      </aside>

      <section class="ai-main">
        <el-card v-if="!current" shadow="never" class="empty-panel">
          <el-icon class="empty-icon"><DataAnalysis /></el-icon>
          <p>尚未生成该患者的随访摘要</p>
          <el-button type="primary" :loading="generating" @click="generate">
            {{ generating ? '正在生成…' : '立即生成' }}
          </el-button>
        </el-card>

        <template v-else>
          <el-card shadow="never" class="summary-head">
            <div class="summary-title">
              <h2>{{ current.patientName }} · AI 随访摘要</h2>
              <Chip :text="current.status" />
            </div>
            <div class="summary-meta">
              <span>统计范围 {{ current.range }}</span>
              <span>生成时间 {{ current.createdAt }}</span>
              <span>{{ current.model }} · {{ current.tokens }} tokens · {{ current.duration }}</span>
              <span v-if="current.editor">审核人 {{ current.editor }} · {{ current.editedAt }}</span>
            </div>
            <div class="summary-ops">
              <el-button @click="saveDraft">
                <el-icon><Document /></el-icon>保存草稿
              </el-button>
              <el-button @click="regenerate">
                <el-icon><Refresh /></el-icon>重新生成
              </el-button>
              <el-button type="danger" plain @click="reject">驳回</el-button>
              <el-button type="primary" class="op-approve" @click="approve">
                <el-icon><Check /></el-icon>审核通过并归档
              </el-button>
            </div>
          </el-card>

          <Card title="六段固定结构摘要" class="section-card">
            <template #extra>
              <span class="card-hint">可直接编辑，保存后记录修改人</span>
            </template>
            <div class="section-body">
              <div v-for="(section, index) in AI_SECTIONS" :key="section.key" class="ai-section">
                <div class="ai-section-h">
                  <span class="no">{{ index + 1 }}</span>
                  <b>{{ section.title }}</b>
                  <span class="len">{{ (draft[section.key] || '').length }} 字</span>
                </div>
                <el-input
                  v-model="draft[section.key]"
                  type="textarea"
                  :rows="4"
                  resize="vertical"
                  :disabled="current.status === '已归档'"
                />
              </div>
            </div>
          </Card>

          <Card title="数据来源与约束" class="evidence">
            <div class="evidence-grid">
              <div class="evidence-item">
                <span class="k">引用数据</span>
                <span>发作打卡 {{ patient.attacks30 }} 次 / 血压异常 {{ patient.bpAbnormal }} 次 / 用药依从率 {{ patient.adherence }}%</span>
              </div>
              <div class="evidence-item">
                <span class="k">风险分层</span>
                <span>{{ patient.risk }}（{{ patient.doctor }} 管理）</span>
              </div>
              <div class="evidence-item">
                <span class="k">人工修改</span>
                <span>{{ current.editedAt || '暂无修改记录' }}</span>
              </div>
            </div>
          </Card>
        </template>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import Card from '../components/Card.vue'
import Chip from '../components/Chip.vue'
import { AI_SECTIONS, AI_SUMMARIES, PATIENTS, getPatient, nowText } from '../mock'
import { toast } from '../utils/toast'

const route = useRoute()

const patientOptions = PATIENTS.map((item) => ({ value: item.id, label: `${item.name}（${item.id}）` }))
const rangeOptions = [
  { value: '30', label: '近 30 天' },
  { value: '90', label: '近 90 天' },
  { value: '180', label: '近 180 天' }
]

const patientId = ref(route.query.patientId || PATIENTS[0].id)
const rangeType = ref('30')
const generating = ref(false)
const current = ref(null)
const draft = reactive({})

const patient = computed(() => getPatient(patientId.value))

function pickSummary() {
  return (
    AI_SUMMARIES.find((item) => item.patientId === patientId.value) || null
  )
}

function load(item) {
  current.value = item
  AI_SECTIONS.forEach((section) => {
    draft[section.key] = item.sections[section.key] || ''
  })
}

watch(
  patientId,
  () => {
    const found = pickSummary()
    if (found) load(found)
    else current.value = null
  },
  { immediate: true }
)

function buildMockSummary(target) {
  return {
    basic: `${target.name}，${target.sex}，${target.age} 岁，${target.diagnoseDate} 确诊稳定性心绞痛，合并症：${target.comorbidity}，过敏史：${target.allergy}，基线危险分层为${target.risk}，当前由${target.doctor}管理。`,
    angina: `统计周期内累计记录心绞痛发作 ${target.attacks30} 次，发作诱因、疼痛评分与缓解方式详见患者打卡记录。`,
    bp: `统计周期内血压异常记录 ${target.bpAbnormal} 次，收缩压/舒张压晨晚波动情况详见血压监测模块。`,
    adherence: `当前医嘱用药依从率为 ${target.adherence}%，${target.adherence < 70 ? '存在明显漏服，需要重点提醒' : target.adherence < 85 ? '依从性一般，建议加强提醒' : '服药较为规律'}。`,
    problems: `1. 近 30 天发作 ${target.attacks30} 次；2. 血压异常 ${target.bpAbnormal} 次；3. 用药依从率 ${target.adherence}%${target.tags.length ? `；4. 关注标签：${target.tags.join('、')}` : ''}。`,
    assessment: `综合症状、血压与依从性数据，建议按${target.risk}管理周期安排随访，${target.risk === '高危' ? '1 周内完成电话或门诊复评' : '按计划完成常规随访'}，必要时由医生调整用药方案。`
  }
}

function generate() {
  generating.value = true
  setTimeout(() => {
    const existing = pickSummary()
    if (existing) {
      load(existing)
      toast('已生成最新摘要（Mock）')
    } else {
      const target = patient.value
      const record = {
        id: `AI${Date.now()}`,
        patientId: target.id,
        patientName: target.name,
        range: rangeType.value === '30' ? '2026-10-21 ~ 2026-11-20' : rangeType.value === '90' ? '2026-08-22 ~ 2026-11-20' : '2026-05-24 ~ 2026-11-20',
        createdAt: nowText(),
        model: 'deepseek-chat',
        temperature: 0.2,
        tokens: 1680,
        duration: '5.6s',
        status: '待审核',
        editor: '',
        editedAt: '',
        sections: buildMockSummary(target)
      }
      AI_SUMMARIES.unshift(record)
      load(record)
      toast('AI 摘要生成完成（Mock 模式）')
    }
    generating.value = false
  }, 900)
}

function regenerate() {
  generate()
}

function saveDraft() {
  if (!current.value) return
  current.value.status = '草稿'
  AI_SECTIONS.forEach((section) => {
    current.value.sections[section.key] = draft[section.key]
  })
  toast('草稿已保存')
}

function approve() {
  if (!current.value) return
  AI_SECTIONS.forEach((section) => {
    current.value.sections[section.key] = draft[section.key]
  })
  current.value.status = '已归档'
  current.value.editor = '李医生'
  current.value.editedAt = nowText()
  toast('审核通过，摘要已归档')
}

function reject() {
  if (!current.value) return
  current.value.status = '待审核'
  current.value.editor = '李医生'
  current.value.editedAt = nowText()
  toast('已驳回，请修改后重新提交')
}
</script>

<style scoped>
.ai-summary {
  display: block;
}
.ai-grid {
  display: grid;
  grid-template-columns: 288px minmax(0, 1fr);
  gap: 12px;
  align-items: start;
}
.ai-side :deep(.el-card) {
  margin-bottom: 12px;
}
.ai-main :deep(.el-card) {
  margin-bottom: 12px;
}
.param-box {
  border: 1px solid var(--line);
  border-radius: var(--radius-sub);
  background: var(--surface-2);
  padding: 8px 10px;
  font-size: 12px;
  margin-bottom: 12px;
}
.param-box div {
  display: flex;
  justify-content: space-between;
  padding: 3px 0;
}
.param-box span {
  color: var(--ink-3);
}
.param-box b {
  color: var(--ink-2);
  font-weight: 500;
}
.param-box b.green {
  color: var(--green);
}
.generate-btn {
  width: 100%;
}
.history-item {
  padding: 9px 10px;
  border: 1px solid var(--line);
  border-radius: var(--radius-sub);
  margin-bottom: 8px;
  cursor: pointer;
  transition: border-color 0.15s, background 0.15s;
}
.history-item:last-child {
  margin-bottom: 0;
}
.history-item:hover {
  border-color: #bedaff;
  background: #f7faff;
}
.history-item.active {
  border-color: var(--brand);
  background: var(--brand-soft);
}
.history-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 12.5px;
}
.history-sub {
  margin-top: 2px;
  color: var(--ink-3);
  font-size: 11.5px;
}
.empty-panel :deep(.el-card__body) {
  padding: 60px 20px;
  text-align: center;
  color: var(--ink-3);
}
.empty-panel .empty-icon {
  font-size: 30px;
  color: var(--ink-3);
}
.empty-panel p {
  margin: 10px 0 16px;
}
.summary-title {
  display: flex;
  align-items: center;
  gap: 8px;
}
.summary-title h2 {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
}
.summary-meta {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  margin-top: 6px;
  color: var(--ink-3);
  font-size: 12px;
}
.summary-ops {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 14px;
}
.summary-ops .el-button + .el-button {
  margin-left: 0;
}
.summary-ops .op-approve {
  margin-left: auto;
}
.section-body {
  padding-top: 4px;
}
.ai-section {
  padding: 12px 0;
  border-bottom: 1px dashed var(--line);
}
.ai-section:last-child {
  border-bottom: 0;
  padding-bottom: 0;
}
.ai-section-h {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}
.ai-section-h .no {
  display: grid;
  place-items: center;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: var(--brand-soft);
  color: var(--brand);
  font-size: 11px;
  font-weight: 600;
}
.ai-section-h b {
  font-size: 13px;
  font-weight: 600;
}
.ai-section-h .len {
  margin-left: auto;
  color: var(--ink-3);
  font-size: 11.5px;
}
.evidence-grid {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.evidence-item {
  display: flex;
  gap: 10px;
  font-size: 12.5px;
}
.evidence-item .k {
  flex: none;
  width: 72px;
  color: var(--ink-3);
}
</style>
