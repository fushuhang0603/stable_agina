import { defineStore } from 'pinia'

/**
 * 全局轻提示（对齐和仁 src/store/globalMessage.ts）
 * 页面放置 <g-message /> 后，任意位置调用 showMessage 即可居中弹提示。
 */
let timer = null

export const useMessageStore = defineStore('message', {
  state: () => ({
    isShow: false,
    msg: '',
    duration: 0,
    popupDuration: 500,
    maskClickCallBack: () => {},
    closeCallBack: () => {}
  }),

  actions: {
    showMessage(message, duration = 0, options = {}) {
      if (options.uniToast) {
        uni.showToast({ title: message, icon: 'none', duration })
        return
      }
      const { maskClickCallBack, closeCallBack } = options
      this.isShow = true
      this.duration = duration
      this.msg = message
      uni.$emit('showMessage')

      if (timer) clearTimeout(timer)
      if (duration) {
        timer = setTimeout(() => {
          this.closeMessage()
        }, duration)
      }

      this.maskClickCallBack = maskClickCallBack || (() => {})
      this.closeCallBack = closeCallBack || (() => {})
    },

    closeMessage() {
      if (timer) clearTimeout(timer)
      this.isShow = false
      uni.$emit('closeMessage')
      this.closeCallBack()
    }
  }
})
