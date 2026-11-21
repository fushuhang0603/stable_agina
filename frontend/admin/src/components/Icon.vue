<template>
  <svg
    class="ico"
    :style="{ width: size + 'px', height: size + 'px' }"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    :stroke-width="strokeWidth"
    stroke-linecap="round"
    stroke-linejoin="round"
    v-html="ICON_PATHS[name] || ''"
  />
</template>

<script setup>
import { ICON_PATHS } from '../utils/icons'

defineProps({
  name: { type: String, required: true },
  size: { type: Number, default: 16 },
  strokeWidth: { type: [Number, String], default: 1.6 }
})
</script>
