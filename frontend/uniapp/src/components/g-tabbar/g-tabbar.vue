<template>
  <view class="g-tabbar">
    <view
      v-for="item in items"
      :key="item.key"
      class="tb-item"
      :class="{ on: item.key === current }"
      @click="go(item)"
    >
      <g-icon :name="item.icon" :size="44" />
      <text>{{ item.text }}</text>
    </view>
  </view>
</template>

<script setup>
/**
 * 自绘底部导航（pages.json 未使用原生 tabBar，避免 UniApp tabBar 图标资源）
 * 命名说明：和仁工程中的 tab-bar 是「页面顶部横向滚动分类 tab」，
 * 与本项目的底部导航不是同一事物，故此处使用 g-tabbar 避免语义冲突。
 */
const props = defineProps({
  current: { type: String, default: 'home' }
})

const items = [
  { key: 'home', text: '首页', icon: 'home', url: '/pages/index/index' },
  { key: 'followup', text: '随访', icon: 'clipboard', url: '/pages/followup/followup' },
  { key: 'message', text: '消息', icon: 'message', url: '/pages/message/message' },
  { key: 'profile', text: '我的', icon: 'user', url: '/pages/profile/profile' }
]

function go(item) {
  if (item.key === props.current) return
  uni.reLaunch({ url: item.url })
}
</script>
