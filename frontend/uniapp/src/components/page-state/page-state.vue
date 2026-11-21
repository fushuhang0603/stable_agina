<template>
  <view v-if="state === 'loading'" class="g-skeleton-wrap">
    <view v-for="i in 3" :key="i" class="g-skeleton-card">
      <view class="g-skeleton line-lg" />
      <view class="g-skeleton line-sm" />
      <view class="g-skeleton line-md" />
    </view>
  </view>
  <g-empty v-else-if="state === 'empty'" :text="text" :current="current" />
  <view v-else-if="state === 'error'" class="g-empty">
    <view class="empty-ico">
      <g-icon name="warning" :size="60" />
    </view>
    <text class="empty-text">{{ text || '加载失败' }}</text>
    <view class="btn btn-normal btn-border btn-size-small mt16" @click="$emit('retry')">重新加载</view>
  </view>
</template>

<script setup>
/** 页面状态：loading 骨架 / empty 空态 / error 失败重试 */
defineProps({
  state: { type: String, default: 'empty' },
  text: { type: String, default: '' },
  /** 和仁空态图片索引 1~12，透传给 g-empty */
  current: { type: Number, default: 1 }
})
defineEmits(['retry'])
</script>

<style lang="scss" scoped>
.g-skeleton-wrap {
  padding: 24rpx;
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}
.g-skeleton-card {
  background: var(--h-color-white);
  border-radius: var(--hr-radius-24);
  padding: 32rpx;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}
.line-lg { height: 36rpx; width: 46%; }
.line-md { height: 28rpx; width: 82%; }
.line-sm { height: 24rpx; width: 64%; }
</style>
