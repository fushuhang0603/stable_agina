<template>
  <view :class="{ ty: !noTransformY }" class="page-state g-flex-rc-cc">
    <image :src="imgSrc" :style="{ height: imgHeight }" mode="heightFix" />
    <view class="alt">{{ text || state.label }}</view>

    <view class="g-flex-rc-cc empty-content">
      <slot />
    </view>
  </view>
</template>

<script setup>
/**
 * 空态（对齐和仁 D:\Code\heren-projecth5\src\components\g-empty\g-empty.vue）
 * 图片取自 OSS 基址 config/global.js BASE_IMG，共 12 种索引（1~12）。
 * 中医风皮肤下自动改用 `-tcm` 后缀图片。
 */
import { computed } from 'vue'
import { BASE_IMG } from '@/config/global'
import { useUserStore } from '@/store/user'

const props = defineProps({
  /** 空态索引 1~12，见下方 states */
  current: { type: Number, default: 1 },
  text: { type: String, default: '' },
  /** 直接指定图片地址（优先于 current） */
  src: { type: String, default: '' },
  imgHeight: { type: String, default: '240rpx' },
  noTransformY: { type: Boolean, default: false }
})

const user = useUserStore()

const states = computed(() => {
  const tcm = user.tcmStyle ? '-tcm' : ''
  return [
    { icon: `img_404_no content@3x${tcm}.png`, label: '暂未查到相关信息' },
    { icon: `img_404_no record@3x${tcm}.png`, label: '您没有相关的订单' },
    { icon: `img_404_network@3x${tcm}.png`, label: '当前网络状态不佳' },
    { icon: `img_404_no  search@3x${tcm}.png`, label: '未搜到相关内容' },
    { icon: `img_404_no evaluation@3x${tcm}.png`, label: '还没有评论哦，赶快抢占沙发吧 ' },
    { icon: `img_404_shopping cart empty@3x${tcm}.png`, label: '购物车竟然是空的' },
    { icon: `img_404_message@3x${tcm}.png`, label: '暂时没有消息' },
    { icon: `img_404_add@3x${tcm}.png`, label: '您还未添加地址' },
    { icon: `img_404_message2@3x${tcm}.png`, label: '暂时没有问诊消息' },
    { icon: `img_404_no follow@3x${tcm}.png`, label: '还没有关注任何内容哦' },
    { icon: `img_404_message2@3x${tcm}.png`, label: '页面维护中...' },
    { icon: `img_404_no  search@3x${tcm}.png`, label: '正在查询数据中' }
  ]
})

// 文件名含空格，统一 encodeURI 后再交给 <image>
const state = computed(() => states.value[props.current - 1] || states.value[0])
const imgSrc = computed(() => props.src || encodeURI(BASE_IMG + state.value.icon))
</script>

<style lang="scss" scoped>
.page-state {
  flex-direction: column;
  position: relative;

  &.ty {
    transform: translateY(-25%);
  }
  .alt {
    margin-top: 16rpx;
    color: #888;
    font-size: var(--hr-font-size-xs);
    max-width: 480rpx;
    text-align: center;
  }
}

.empty-content {
  margin-top: 32rpx;
}
</style>
