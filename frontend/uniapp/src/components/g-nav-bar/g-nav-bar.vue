<template>
  <view class="g-nav">
    <view class="g-nav-inner">
      <view v-if="back" class="g-nav-back" @click="goBack">
        <g-icon name="arrowLeft" :size="40" />
      </view>
      <text class="g-nav-title" :class="{ 'g-nav-title-left': alignLeft }">{{ title }}</text>
      <view v-if="rightText" class="g-nav-right" @click="$emit('right')">{{ rightText }}</view>
      <slot name="right" />
    </view>
  </view>
</template>

<script setup>
/** 自定义导航栏（pages.json 已设置 navigationStyle: custom） */
const props = defineProps({
  title: { type: String, default: '' },
  back: { type: Boolean, default: true },
  rightText: { type: String, default: '' },
  alignLeft: { type: Boolean, default: false }
})

defineEmits(['right'])

function goBack() {
  const pages = getCurrentPages()
  if (pages.length > 1) {
    uni.navigateBack()
  } else {
    uni.reLaunch({ url: '/pages/index/index' })
  }
}
</script>
