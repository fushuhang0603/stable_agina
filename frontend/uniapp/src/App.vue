<script setup>
import { onLaunch, onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'

onLaunch(() => {
  const user = useUserStore()
  user.restore()
})

onShow(() => {
  // 预留：小程序/H5 前后台切换时刷新待办
})
</script>

<style lang="scss">
/* 基础层：变量 + 旧工具类（适老模式仍依赖 --hr-* 变量） */
@import '@/styles/buttom.scss';
@import '@/styles/layout.scss';
@import '@/styles/other.scss';
@import '@/styles/variables.css';
@import '@/styles/modeOldVariables.scss';
@import '@/styles/modeStyleVariables.scss';
/* 医疗风样式层：必须最后引入，用于覆盖旧类名（.seg / .radio-box / page 背景） */
@import '@/styles/global.css';
</style>
