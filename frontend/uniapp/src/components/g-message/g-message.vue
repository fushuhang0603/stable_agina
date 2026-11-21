<template>
  <view v-if="messageStore.isShow" class="g-message-root" @tap="maskClick">
    <view class="g-message-box">
      <text class="text">{{ messageStore.msg }}</text>
    </view>
  </view>
</template>

<script setup>
import { useMessageStore } from '@/store/message'

/**
 * 全局轻提示（对齐和仁 g-message：居中黑底圆角、点击遮罩关闭、支持自动关闭）
 * 使用方式：页面根节点放 <g-message />，调用 toast() 或 messageStore.showMessage()
 */
const messageStore = useMessageStore()

function maskClick() {
  messageStore.closeMessage()
  messageStore.maskClickCallBack()
}
</script>

<style lang="scss" scoped>
.g-message-root {
  position: fixed;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  z-index: 10077;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0);
}
.g-message-box {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20rpx 40rpx;
  color: #fff;
  border-radius: 10rpx;
  background-color: rgba(0, 0, 0, 0.6);
  overflow: hidden;
  max-width: 450rpx;
  position: relative;
  top: -160rpx;
  word-break: break-all;
}
.text {
  font-size: var(--hr-font-size-base);
}
</style>
