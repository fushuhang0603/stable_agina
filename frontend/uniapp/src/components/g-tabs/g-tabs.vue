<template>
  <view class="v-tabs">
    <scroll-view
      id="scrollContainer"
      class="scroll-h"
      :scroll-x="scroll"
      :scroll-left="scroll ? scrollLeft : 0"
      :scroll-with-animation="scroll"
      :style="{ position: fixed ? 'fixed' : 'relative', zIndex }"
    >
      <view
        class="v-tabs__container"
        :style="{
          display: scroll ? 'inline-flex' : 'flex',
          whiteSpace: scroll ? 'nowrap' : 'normal',
          background: bgColor,
          height,
          padding
        }"
      >
        <view
          v-for="(item, i) in normalized"
          :key="item.value"
          class="v-tabs__container-item"
          :style="{
            color: i === current ? activeColor : color,
            fontWeight: allBlod || (bold && i === current) ? 'bold' : '',
            justifyContent: !scroll ? 'center' : '',
            flex: scroll ? '' : 1,
            padding: paddingItem,
            minWidth: itemMinWidth
          }"
          @click="change(i)"
        >
          {{ field && item.raw[field] ? item.raw[field] : item.label }}
        </view>
        <view
          v-if="!pills"
          class="v-tabs__container-line"
          :style="{
            background: lineColor,
            width: lineWidth + 'px',
            height: lineHeight,
            borderRadius: lineRadius,
            left: lineLeft + 'px',
            transform: `translateX(-${lineWidth / 2}px)`
          }"
        />
        <view
          v-else
          class="v-tabs__container-pills"
          :style="{
            background: pillsColor,
            borderRadius: pillsBorderRadius,
            left: pillsLeft + 'px',
            width: currentWidth + 'px',
            height
          }"
        />
      </view>
    </scroll-view>
    <view class="v-tabs__placeholder" :style="{ height: fixed ? height : '0', padding }" />
  </view>
</template>

<script setup>
import { computed, getCurrentInstance, nextTick, onMounted, ref, watch } from 'vue'

/**
 * 滑动下划线页签（对齐和仁 g-tabs）
 * 双 API 兼容：v-model 支持 value（本项目页面用法）与下标两种语义
 * tabs 支持 ['全部','待随访'] 或 [{ label, value }]
 */
const props = defineProps({
  modelValue: { type: [String, Number], default: '' },
  tabs: { type: Array, default: () => [] },
  bgColor: { type: String, default: '#fff' },
  color: { type: String, default: '#999' },
  activeColor: { type: String, default: '#111111' },
  bold: { type: Boolean, default: true },
  allBlod: { type: Boolean, default: true },
  scroll: { type: Boolean, default: true },
  height: { type: String, default: '88rpx' },
  padding: { type: String, default: '0' },
  paddingItem: { type: String, default: '0 22rpx' },
  itemMinWidth: { type: String, default: 'auto' },
  lineColor: {
    type: String,
    default: 'linear-gradient(180deg,var(--hr-brand-color-3),var(--hr-brand-color-6) 80%)'
  },
  lineHeight: { type: String, default: '10rpx' },
  lineWidth: { type: Number, default: 24 },
  lineRadius: { type: String, default: '0' },
  pills: { type: Boolean, default: false },
  pillsColor: { type: String, default: 'var(--hr-brand-color-6)' },
  pillsBorderRadius: { type: String, default: '0' },
  field: { type: String, default: '' },
  fixed: { type: Boolean, default: false },
  zIndex: { type: String, default: '2' }
})

const emit = defineEmits(['update:modelValue', 'change'])
const instance = getCurrentInstance()

const normalized = computed(() =>
  props.tabs.map((t) =>
    typeof t === 'object' && t !== null
      ? { label: String(t.label ?? t.value ?? ''), value: t.value, raw: t }
      : { label: String(t), value: t, raw: t }
  )
)

const current = ref(0)
const currentWidth = ref(0)
const lineLeft = ref(0)
const pillsLeft = ref(0)
const scrollLeft = ref(0)
const containerWidth = ref(0)

function wait(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms))
}

/** 由外部 value 反推下标 */
function syncCurrent() {
  const idx = normalized.value.findIndex((it) => it.value === props.modelValue)
  current.value = idx > -1 ? idx : 0
}

/** 量取 tab 宽度，计算下划线 / 胶囊位置 */
async function getTabItemWidth() {
  await wait(200)
  const query = uni.createSelectorQuery().in(instance.proxy)
  query
    .select('#scrollContainer')
    .boundingClientRect((data) => {
      if (!containerWidth.value && data) containerWidth.value = data.width
    })
    .exec()
  query
    .selectAll('.v-tabs__container-item')
    .boundingClientRect(async (data) => {
      if (!data || !data.length) return
      let left = 0
      let width = 0
      for (let i = 0; i < data.length; i++) {
        if (i < current.value) left += data[i].width
        else if (i === current.value) width = data[i].width
        else break
      }
      currentWidth.value = width
      lineLeft.value = left + width / 2
      pillsLeft.value = left
      if (props.scroll) scrollLeft.value = lineLeft.value - containerWidth.value / 2
      if (currentWidth.value < 15) {
        await wait(1000)
        getTabItemWidth()
      }
    })
    .exec()
}

function change(index) {
  if (current.value === index) return
  current.value = index
  emit('update:modelValue', normalized.value[index].value)
  emit('change', normalized.value[index].value, index)
  nextTick(() => getTabItemWidth())
}

watch(
  () => props.modelValue,
  () => {
    syncCurrent()
    nextTick(() => getTabItemWidth())
  }
)
watch(
  () => props.tabs,
  () => {
    syncCurrent()
    nextTick(() => getTabItemWidth())
  },
  { deep: true }
)

onMounted(() => {
  syncCurrent()
  nextTick(() => getTabItemWidth())
})
</script>

<style lang="scss" scoped>
.v-tabs {
  width: 100%;
  box-sizing: border-box;
  overflow: hidden;

  ::-webkit-scrollbar {
    display: none;
  }

  &__container {
    min-width: 100%;
    position: relative;
    display: inline-flex;
    align-items: center;
    white-space: nowrap;
    overflow: hidden;

    &-item {
      display: flex;
      align-items: center;
      height: 100%;
      position: relative;
      z-index: 10;
      font-size: var(--hr-font-size-base);
      transition: all 0.4s;
      white-space: nowrap;
      justify-content: center;
    }

    &-line {
      position: absolute;
      bottom: 0;
      transition: all 0.4s linear;
    }

    &-pills {
      position: absolute;
      transition: all 0.4s linear;
      z-index: 9;
    }
  }
}
</style>
