<template>
  <div ref="chartRef" class="echart" :style="{ height: height + 'px' }"></div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { init, use } from 'echarts/core'
import { BarChart, LineChart, PieChart } from 'echarts/charts'
import {
  GridComponent,
  LegendComponent,
  MarkLineComponent,
  MarkPointComponent,
  TitleComponent,
  TooltipComponent
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

use([
  BarChart,
  LineChart,
  PieChart,
  GridComponent,
  LegendComponent,
  MarkLineComponent,
  MarkPointComponent,
  TitleComponent,
  TooltipComponent,
  CanvasRenderer
])

const props = defineProps({
  option: { type: Object, required: true },
  height: { type: Number, default: 200 }
})

const chartRef = ref(null)
let chart = null
let observer = null

function render() {
  if (chart) chart.setOption(props.option, true)
}

onMounted(() => {
  chart = init(chartRef.value)
  render()
  // 侧边栏折叠、窗口缩放、Tab 切换时自动适配尺寸
  observer = new ResizeObserver(() => {
    if (chart) chart.resize()
  })
  observer.observe(chartRef.value)
})

watch(() => props.option, render, { deep: true })

onBeforeUnmount(() => {
  if (observer) {
    observer.disconnect()
    observer = null
  }
  if (chart) {
    chart.dispose()
    chart = null
  }
})
</script>

<style scoped>
.echart {
  width: 100%;
}
</style>
