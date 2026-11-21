<template>
  <div class="system-config">
    <Card v-for="group in groups" :key="group.name" :title="group.name">
      <template #extra>
        <span class="card-hint">{{ group.rows.length }} 项参数</span>
      </template>
      <div class="cfg-list">
        <div v-for="item in group.rows" :key="item.key" class="cfg-row">
          <div class="cfg-meta">
            <div class="cfg-name">
              <b>{{ item.name }}</b>
              <span class="cfg-key">{{ item.key }}</span>
              <Chip v-if="item.key === 'ai.temperature'" text="谨慎调整" />
            </div>
            <div class="cfg-desc">
              {{ item.desc }}
              <span v-if="item.key === 'ai.temperature'" class="warn-text">
                该参数控制摘要生成的随机性，取值过高会导致措辞发散、结论不稳定，摘要场景建议保持 0.1~0.3。
              </span>
            </div>
          </div>
          <div class="cfg-ctrl">
            <el-switch
              v-if="isBool(item)"
              :model-value="item.value === 'true'"
              @change="toggle(item)"
            />
            <template v-else>
              <el-input v-model="item.value" class="cfg-input" />
              <span v-if="item.unit" class="cfg-unit">{{ item.unit }}</span>
            </template>
          </div>
        </div>
      </div>
    </Card>

    <Card title="AI 连通性测试">
      <template #extra>
        <span class="card-hint">用于验证当前 AI 配置是否可用</span>
      </template>
      <div class="ai-test">
        <dl class="kv">
          <dt>Base URL</dt>
          <dd class="cfg-key">{{ aiBaseUrl }}</dd>
          <dt>模型</dt>
          <dd class="cfg-key">{{ aiModel }}</dd>
          <dt>Mock 模式</dt>
          <dd>
            <Chip :text="mockOn ? '启用' : '停用'" />
            <span class="mock-tip">{{ mockOn ? '不会发起真实大模型请求' : '将向真实模型服务发起请求' }}</span>
          </dd>
        </dl>
        <div class="ai-test-row">
          <el-button type="primary" :loading="testing" @click="testConnection">
            {{ testing ? '正在测试…' : '测试连接' }}
          </el-button>
          <span class="t-muted">测试只发送一次最小化健康检查请求，不包含患者数据。</span>
        </div>
      </div>
    </Card>

    <Card class="cfg-footer-card">
      <div class="cfg-actions">
        <span class="t-muted">保存后立即生效，变更记录可在「操作日志」中追溯。</span>
        <div class="cfg-action-btns">
          <el-button @click="restoreDefaults">恢复默认</el-button>
          <el-button type="primary" @click="saveConfig">保存修改</el-button>
        </div>
      </div>
    </Card>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import Card from '../components/Card.vue'
import Chip from '../components/Chip.vue'
import { CONFIGS } from '../mock'
import { toast } from '../utils/toast'

const GROUP_ORDER = ['血压告警', '高危判定', '随访周期', 'AI 配置']

function orderWeight(name) {
  const index = GROUP_ORDER.indexOf(name)
  return index === -1 ? GROUP_ORDER.length : index
}

const groups = computed(() => {
  const names = [...new Set(CONFIGS.map((item) => item.group))]
  names.sort((a, b) => orderWeight(a) - orderWeight(b))
  return names.map((name) => ({
    name,
    rows: CONFIGS.filter((item) => item.group === name)
  }))
})

const defaults = new Map(CONFIGS.map((item) => [item.key, item.value]))
const aiBaseUrl = computed(() => CONFIGS.find((item) => item.key === 'ai.base-url')?.value || '—')
const aiModel = computed(() => CONFIGS.find((item) => item.key === 'ai.model')?.value || '—')
const mockOn = computed(() => CONFIGS.find((item) => item.key === 'ai.mock')?.value === 'true')

const testing = ref(false)

function isBool(item) {
  return item.value === 'true' || item.value === 'false'
}

function toggle(item) {
  item.value = item.value === 'true' ? 'false' : 'true'
}

function testConnection() {
  if (testing.value) return
  testing.value = true
  setTimeout(() => {
    testing.value = false
    toast('Mock 模式：未发起真实请求，连接正常')
  }, 1000)
}

function saveConfig() {
  toast('参数已保存（演示）')
}

function restoreDefaults() {
  CONFIGS.forEach((item) => {
    item.value = defaults.get(item.key)
  })
  toast('已恢复默认参数（演示）')
}
</script>

<style scoped>
.system-config :deep(.el-card) {
  margin-bottom: 12px;
}
.cfg-row {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 2px;
  border-bottom: 1px solid var(--line-2);
}
.cfg-row:last-child {
  border-bottom: 0;
  padding-bottom: 0;
}
.cfg-meta {
  flex: 1;
  min-width: 0;
}
.cfg-name {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.cfg-name b {
  font-weight: 500;
}
.cfg-key {
  padding: 1px 5px;
  border-radius: 4px;
  background: var(--surface-2);
  color: var(--ink-3);
  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
  font-size: 11.5px;
}
.cfg-desc {
  margin-top: 2px;
  color: var(--ink-3);
  font-size: 12px;
  line-height: 1.6;
}
.warn-text {
  margin-left: 4px;
  color: var(--amber);
}
.cfg-ctrl {
  flex: none;
  display: flex;
  align-items: center;
  gap: 6px;
}
.cfg-input {
  width: 130px;
}
.cfg-unit {
  min-width: 44px;
  color: var(--ink-3);
  font-size: 12px;
}
.ai-test .kv {
  margin: 0 0 14px;
}
.ai-test .kv dd.cfg-key {
  justify-self: start;
}
.mock-tip {
  margin-left: 8px;
  color: var(--ink-3);
  font-size: 12px;
}
.ai-test-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
.cfg-footer-card {
  position: sticky;
  bottom: 0;
  z-index: 6;
  box-shadow: 0 -2px 10px rgba(16, 26, 38, 0.06);
}
.cfg-footer-card :deep(.el-card__body) {
  padding: 0;
}
.cfg-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  padding: 12px 16px;
}
.cfg-action-btns {
  margin-left: auto;
  display: flex;
  gap: 8px;
}
.cfg-action-btns .el-button + .el-button {
  margin-left: 0;
}
</style>
