<template>
  <div v-if="showPager" class="list-pager">
    <el-pagination
      :current-page="page"
      :page-size="size"
      :total="total"
      layout="total, prev, pager, next"
      background
      @current-change="onPageChange"
    />
  </div>
</template>

<script setup>
/** 列表分页：统一「共 N 条 + 上一页 / 页码 / 下一页」居中样式 */
const props = defineProps({
  total: { type: Number, default: 0 },
  page: { type: Number, default: 1 },
  size: { type: Number, default: 10 },
  showPager: { type: Boolean, default: true }
})

const emit = defineEmits(['update:page', 'change'])

function onPageChange(nextPage) {
  emit('update:page', nextPage)
  emit('change', nextPage, props.size)
}
</script>

<style scoped>
.list-pager {
  display: flex;
  justify-content: center;
}
</style>
