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
        <text class="lbl">账号<text class="req">*</text></text>
        <input
          v-model="username"
          class="fa-input"
          type="text"
          placeholder="请输入用户名"
          placeholder-class="ph"
        />
      </view>

      <view class="form-row">
        <text class="lbl">密码<text class="req">*</text></text>
        <input
          v-model="password"
          class="fa-input"
          type="text"
          password
          placeholder="请输入登录密码"
          placeholder-class="ph"
        />
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

      <view class="lg-register" @click="goRegister">
        <text class="t-12 ink-3">还没有账号？</text>
        <text class="t-12 lg-link">立即注册</text>
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
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { toast } from '@/utils/format'

const user = useUserStore()

const username = ref('')
const password = ref('')
const agreed = ref(true)
const submitting = ref(false)

const canSubmit = computed(() => username.value.trim().length > 0 && password.value.length > 0 && agreed.value)

onLoad(() => {
  if (user.isLogin) {
    uni.reLaunch({ url: '/pages/index/index' })
  }
})

function goRegister() {
  uni.navigateTo({ url: '/pages/register/register' })
}

async function submit() {
  if (submitting.value) return
  if (!agreed.value) {
    toast('请先阅读并同意隐私政策')
    return
  }
  if (!username.value.trim()) {
    toast('请输入用户名')
    return
  }
  if (!password.value) {
    toast('请输入登录密码')
    return
  }
  submitting.value = true
  try {
    await user.login({ username: username.value.trim(), password: password.value })
    toast('登录成功')
    setTimeout(() => uni.reLaunch({ url: '/pages/index/index' }), 400)
  } catch (e) {
    // 错误提示已在请求层统一处理
  } finally {
    submitting.value = false
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
.lg-register {
  margin-top: 12px;
  text-align: center;
}
.lg-link {
  color: var(--brand);
  font-weight: 600;
  margin-left: 2px;
}
.note-ico {
  color: var(--brand);
}
.lg-foot {
  padding: 16px 0 calc(8px + env(safe-area-inset-bottom));
  text-align: center;
}
</style>
