<template>
  <view v-if="visible" class="g-popup-root">
    <view
      class="wyb-popup-mask"
      :style="{ zIndex: zIndex - 1, backgroundColor: `rgba(13, 40, 92, ${maskAlpha})` }"
      @tap.stop="close"
      @touchmove.stop.prevent
    />
    <view
      class="g-popup-body"
      :class="[type, 'popup-in-' + type]"
      :style="{ zIndex, transitionDuration: duration + 'ms' }"
      @tap.stop
    >
      <view class="popup-container" :class="{ 'border-radius-none': type === 'top' }">
        <view v-if="type !== 'top' && !isHideNav" class="header flex-between">
          <text class="popup-title text-ellipsis">{{ title }}</text>
          <view v-if="showCloseIcon" class="ico-close" @click="hide">
            <g-icon name="close" :size="40" />
          </view>
        </view>

        <slot name="header" />

        <scroll-view scroll-y class="popup-box" :class="{ 'auto-height': type === 'top' }">
          <slot />
        </scroll-view>

        <!-- 项目扩展：和仁 g-popup 无 footer 插槽，本项目页面按「取消 / 提交」双按钮使用 -->
        <view v-if="$slots.footer" class="popup-footer">
          <slot name="footer" />
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

/**
 * 通用弹层（对齐和仁 g-popup：命令式调用，非 v-model）
 * const popup = ref(null)
 * popup.value.show() / popup.value.hide() / popup.value.close()
 */
const props = defineProps({
  disabled: { type: Boolean, default: false },
  showCloseIcon: { type: Boolean, default: true },
  isHideNav: { type: Boolean, default: false },
  type: { type: String, default: 'bottom' },
  maskClickClose: { type: Boolean, default: true },
  title: { type: String, default: '' },
  duration: { type: Number, default: 100 },
  maskAlpha: { type: Number, default: 0.5 },
  zIndex: { type: [String, Number], default: 10076 }
})

const emit = defineEmits(['show', 'hide'])

const visible = ref(false)

function show() {
  if (props.disabled || visible.value) return
  visible.value = true
  emit('show')
}

function hide() {
  if (!visible.value) return
  visible.value = false
  emit('hide')
}

/** 遮罩点击：maskClickClose 为 true 时才关闭 */
function close() {
  if (props.maskClickClose) hide()
}

defineExpose({ show, hide, close })
</script>

<style lang="scss" scoped>
/* 医疗风弹层：与急救一件事客户端 FaSheet / FaDialog 同一套视觉
   底部弹层 20px 圆角 + 蓝调蒙层；居中弹窗 18px 圆角 */
.g-popup-root {
  position: relative;
}
.wyb-popup-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(13, 40, 92, 0.45);
  animation: popup-fade 0.2s ease;
}
.g-popup-body {
  position: fixed;
  left: 0;
  right: 0;
  transition-property: opacity, transform;
  transition-timing-function: ease-out;
  width: 100%;

  &.bottom {
    bottom: 0;
  }
  &.center {
    top: 50%;
    left: 50%;
    right: auto;
    width: 86%;
    max-width: 320px;
    transform: translate(-50%, -50%);
  }
}
.popup-container {
  display: flex;
  flex-direction: column;
  max-width: 560px;
  margin: 0 auto;
  background-color: #fff;
  border-radius: 20px 20px 0 0;
  overflow: hidden;
  box-shadow: 0 -10px 34px rgba(22, 93, 255, 0.16);

  &.border-radius-none {
    border-radius: 0;
  }

  .header {
    display: flex;
    align-items: flex-start;
    gap: 10px;
    padding: 15px 16px 12px;
    border-bottom: 1px solid #edf3ff;
    background: #fff;

    .popup-title {
      flex: 1;
      min-width: 0;
      font-size: 15.5px;
      font-weight: 600;
      line-height: 1.4;
      color: #1d2129;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }

    .ico-close {
      width: 28px;
      height: 28px;
      border-radius: 50%;
      background: #e8f3ff;
      display: flex;
      align-items: center;
      justify-content: center;
      flex: none;
      color: #165dff;
    }
    .ico-close:active {
      background: #d6e8ff;
    }
  }

  .popup-box {
    max-height: 58vh;
    min-height: 0;
    padding: 14px 16px 16px;
    background: #fff;
    box-sizing: border-box;

    &.auto-height {
      min-height: auto;
    }
  }

  .popup-footer {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 12px 16px calc(14px + env(safe-area-inset-bottom));
    background: #f8fbff;
    border-top: 1px solid #edf3ff;
  }
}
/* 居中弹窗：四角圆角，底栏不再叠安全区 */
.popup-in-center .popup-container {
  border-radius: 18px;
  box-shadow: 0 16px 44px rgba(22, 93, 255, 0.2);

  .popup-box {
    max-height: 56vh;
  }
  .popup-footer {
    padding-bottom: 14px;
  }
}
.popup-in-bottom {
  animation: popup-up 0.26s cubic-bezier(0.22, 0.61, 0.36, 1);
}
.popup-in-center {
  animation: popup-zoom 0.2s ease-out;
}
@keyframes popup-up {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}
@keyframes popup-zoom {
  from { transform: translate(-50%, -50%) scale(0.94); opacity: 0; }
  to { transform: translate(-50%, -50%) scale(1); opacity: 1; }
}
@keyframes popup-fade {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>
