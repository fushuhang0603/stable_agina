<template>
  <el-card class="sa-card" shadow="never">
    <template v-if="title || $slots.extra" #header>
      <div class="sa-card-h">
        <span v-if="title" class="sa-card-title">{{ title }}</span>
        <div class="sa-card-extra"><slot name="extra" /></div>
      </div>
    </template>
    <slot />
  </el-card>
</template>

<script setup>
defineProps({
  title: { type: String, default: '' }
})
</script>
