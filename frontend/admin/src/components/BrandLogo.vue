<template>
  <span class="brand-mark" :style="markStyle">
    <Icon name="heart" :size="iconSize" :stroke-width="2.2" />
  </span>
</template>

<script setup>
import { computed } from 'vue'
import Icon from './Icon.vue'

const props = defineProps({
  size: { type: [Number, String], default: 32 }
})

// 心形品牌标：浅蓝底 + 蓝色描边心形，按底座尺寸等比缩放
const markStyle = computed(() => {
  const s = Number(props.size)
  return {
    width: s + 'px',
    height: s + 'px',
    borderRadius: Math.round(s * 0.3) + 'px'
  }
})

const iconSize = computed(() => Math.round(Number(props.size) * 0.48))
</script>

<style scoped>
.brand-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex: none;
  background: #e9f1ff;
  color: #165dff;
}
</style>
