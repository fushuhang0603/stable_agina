<template>
  <el-tag :type="chipType(text)" effect="light" disable-transitions>{{ text }}</el-tag>
</template>

<script setup>
import { chipType } from '../utils/chip'

defineProps({
  text: { type: String, default: '' }
})
</script>
