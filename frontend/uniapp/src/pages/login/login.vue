<template>
  <view class="fa-page login-page" :class="user.pageClass">
    <g-message />

    <view class="lg-hero">
      <view class="lg-logo">
        <g-icon name="heart" :size="64" />
      </view>
      <text class="lg-name">稳定性心绞痛随访</text>
      <text class="lg-sub">患者端 · 发作与用药自我管理</text>
    </view>

    <view class="h5-card lg-card">
      <view class="form-row">
        <text class="lbl">手机号<text class="req">*</text></text>
        <input
          v-model="phone"
          class="fa-input"
          type="number"
          maxlength="11"
          placeholder="请输入建档手机号"
          placeholder-class="ph"
        />
      </view>

      <view class="form-row">
        <text class="lbl">验证码<text class="req">*</text></text>
        <view class="fa-row">
          <input
            v-model="code"
            class="fa-input fa-grow"
            type="number"
            maxlength="6"
            placeholder="请输入 6 位验证码"
            placeholder-class="ph"
          />
          <view class="h5-btn blue sm code-btn" :class="{ disabled: counting > 0 }" @click="sendCode">
            {{ counting > 0 ? counting + 's' : '获取验证码' }}
          </view>
        </view>
        <text v-if="mockCode" class="form-hint">演示验证码：{{ mockCode }}</text>
      </view>

      <view class="check-row mt-9" @click="agreed = !agreed">
        <view class="fa-check" :class="{ on: agreed }">
          <g-icon v-if="agreed" name="check" :size="22" class="check-ico" />
        </view>
        <text>我已阅读并同意《患者隐私政策》与《随访数据使用说明》</text>
      </view>

      <view class="h5-btn primary block lg-submit" :class="{ disabled: !canSubmit }" @click="submit">
        登 录
      </view>

      <view class="lg-demo" @click="fillDemo">
        <text class="t-12 ink-3">演示账号 13800006621 / 123456，点击自动填充</text>
      </view>
    </view>

    <view class="section-note">
      <g-icon name="shield" :size="28" class="note-ico" />
      <text>本系统数据仅用于随访管理，严格遵循医疗数据安全与隐私保护规范。</text>
    </view>

    <view class="lg-foot">
      <text class="t-12 ink-3">中医药人工智能学院 · 医学信息工程</text>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onUnmounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { toast } from '@/utils/format'

const user = useUserStore()

const phone = ref('')
const code = ref('')
const agreed = ref(true)
const counting = ref(0)
const mockCode = ref('')
let timer = null

const canSubmit = computed(() => /^1\d{10}$/.test(phone.value) && code.value.length === 6 && agreed.value)

onLoad(() => {
  if (user.isLogin) {
    uni.reLaunch({ url: '/pages/index/index' })
  }
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

function sendCode() {
  if (counting.value > 0) return
  if (!/^1\d{10}$/.test(phone.value)) {
    toast('请先输入正确的手机号')
    return
  }
  mockCode.value = '123456'
  counting.value = 60
  toast('验证码已发送（演示码 123456）')
  timer = setInterval(() => {
    counting.value -= 1
    if (counting.value <= 0) {
      clearInterval(timer)
      timer = null
    }
  }, 1000)
}

function fillDemo() {
  phone.value = '13800006621'
  code.value = '123456'
  mockCode.value = '123456'
}

function submit() {
  if (!agreed.value) {
    toast('请先阅读并同意隐私政策')
    return
  }
  try {
    user.login({ phone: phone.value, code: code.value })
    toast('登录成功')
    setTimeout(() => uni.reLaunch({ url: '/pages/index/index' }), 400)
  } catch (e) {
    toast(e.message || '登录失败')
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  background: var(--bg);
}
.lg-hero {
  padding: calc(env(safe-area-inset-top) + 34px) 8px 18px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}
.lg-logo {
  width: 64px;
  height: 64px;
  border-radius: 20px;
  background: var(--brand-soft);
  border: 1px solid #d5e6ff;
  color: var(--brand);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 4px;
  box-shadow: 0 6px 16px rgba(22, 93, 255, 0.1);
}
.lg-name {
  font-size: 19px;
  font-weight: 700;
  color: var(--ink);
  letter-spacing: 0.5px;
}
.lg-sub {
  font-size: 12px;
  color: var(--ink-3);
}
.lg-card {
  padding: 16px 14px;
}
.code-btn {
  width: 92px;
  flex: none;
  height: 34px;
}
.check-ico {
  color: #fff;
}
.lg-submit {
  height: 42px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  margin-top: 14px;
  letter-spacing: 2px;
}
.lg-demo {
  margin-top: 12px;
  text-align: center;
}
.note-ico {
  color: var(--brand);
}
.lg-foot {
  padding: 16px 0 calc(8px + env(safe-area-inset-bottom));
  text-align: center;
}
</style>
