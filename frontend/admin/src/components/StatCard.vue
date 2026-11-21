<template>
  <el-card class="stat" :class="tone" shadow="never">
    <div class="k">
      <span class="stat-title">{{ title }}</span>
      <Icon v-if="icon" :name="icon" :size="14" style="margin-left:auto;color:var(--ink-3)" />
    </div>
    <div class="v">{{ displayValue }}</div>
    <div v-if="desc" class="d">{{ desc }}</div>
  </el-card>
</template>

<script setup>
import { computed } from 'vue'
import Icon from './Icon.vue'

const props = defineProps({
  title: { type: String, default: '' },
  value: { type: [Number, String], default: undefined },
  desc: { type: String, default: '' },
  // 数值强调色：blue / green / amber / red，留空为默认深色
  tone: { type: String, default: '' },
  icon: { type: String, default: '' },
  formatter: { type: Function, default: undefined }
})

const displayValue = computed(() => {
  const value = props.value
  if (value == null || value === '') return '—'
  if (props.formatter) return props.formatter(value)
  if (typeof value === 'number') {
    return Number.isFinite(value) ? value.toLocaleString() : '—'
  }
  return String(value)
})
</script>

<style scoped>
.stat-title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.stat :deep(.el-card__body) {
  padding: 14px 16px;
}
</style>
