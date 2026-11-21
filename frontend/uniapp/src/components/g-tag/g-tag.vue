<template>
  <text class="g-tag" :class="type">{{ text }}</text>
</template>

<script setup>
/** 标签：type = '' | blue | red | amber | green */
defineProps({
  text: { type: String, default: '' },
  type: { type: String, default: '' }
})
</script>
