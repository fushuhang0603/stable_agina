<template>
  <view class="fa-page reg-page" :class="user.pageClass">
    <g-message />
    <g-nav-bar title="注册账号" />

    <view class="fa-body">
      <view class="reg-tip">
        <g-icon name="shield" :size="30" class="tip-ico" />
        <text>注册即建立您的随访档案，身份证仅用于实名核验。</text>
      </view>

      <view class="h5-card reg-card">
        <view class="form-row">
          <text class="lbl">用户名<text class="req">*</text></text>
          <input
            v-model="form.username"
            class="fa-input"
            type="text"
            placeholder="用于登录，请设置易记的用户名"
            placeholder-class="ph"
          />
        </view>

        <view class="form-row">
          <text class="lbl">密码<text class="req">*</text></text>
          <input
            v-model="form.password"
            class="fa-input"
            type="text"
            password
            maxlength="32"
            placeholder="6~32 位登录密码"
            placeholder-class="ph"
          />
        </view>

        <view class="form-row">
          <text class="lbl">姓名<text class="req">*</text></text>
          <input
            v-model="form.realName"
            class="fa-input"
            type="text"
            placeholder="请输入真实姓名"
            placeholder-class="ph"
          />
        </view>

        <view class="form-row">
          <text class="lbl">身份证号<text class="req">*</text></text>
          <input
            v-model="form.idCard"
            class="fa-input"
            type="idcard"
            maxlength="18"
            placeholder="请输入 18 位身份证号"
            placeholder-class="ph"
          />
        </view>

        <view class="form-row">
          <text class="lbl">手机号<text class="req">*</text></text>
          <input
            v-model="form.phone"
            class="fa-input"
            type="number"
            maxlength="11"
            placeholder="请输入 11 位手机号"
            placeholder-class="ph"
          />
        </view>

        <view class="check-row mt-9" @click="agreed = !agreed">
          <view class="fa-check" :class="{ on: agreed }">
            <g-icon v-if="agreed" name="check" :size="22" class="check-ico" />
          </view>
          <text>我已阅读并同意《患者隐私政策》与《随访数据使用说明》</text>
        </view>

        <view class="h5-btn primary block reg-submit" :class="{ disabled: !canSubmit }" @click="submit">
          注 册
        </view>

        <view class="reg-login" @click="goLogin">
          <text class="t-12 ink-3">已有账号？</text>
          <text class="t-12 reg-link">返回登录</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { reactive, ref, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { toast } from '@/utils/format'

const user = useUserStore()

const form = reactive({
  username: '',
  password: '',
  realName: '',
  idCard: '',
  phone: ''
})
const agreed = ref(true)
const submitting = ref(false)

const canSubmit = computed(() => {
  return (
    form.username.trim().length > 0 &&
    form.password.length >= 6 &&
    form.realName.trim().length > 0 &&
    /^\d{17}[\dXx]$/.test(form.idCard.trim()) &&
    /^1\d{10}$/.test(form.phone.trim()) &&
    agreed.value
  )
})

function goLogin() {
  uni.navigateBack()
}

function validate() {
  if (!form.username.trim()) return '请输入用户名'
  if (form.password.length < 6 || form.password.length > 32) return '密码长度需为 6~32 位'
  if (!form.realName.trim()) return '请输入真实姓名'
  if (!/^\d{17}[\dXx]$/.test(form.idCard.trim())) return '请输入正确的 18 位身份证号'
  if (!/^1\d{10}$/.test(form.phone.trim())) return '请输入正确的手机号'
  if (!agreed.value) return '请先阅读并同意隐私政策'
  return ''
}

async function submit() {
  if (submitting.value) return
  const message = validate()
  if (message) {
    toast(message)
    return
  }
  submitting.value = true
  try {
    await user.register({
      username: form.username.trim(),
      password: form.password,
      realName: form.realName.trim(),
      idCard: form.idCard.trim().toUpperCase(),
      phone: form.phone.trim()
    })
    toast('注册成功')
    setTimeout(() => uni.reLaunch({ url: '/pages/index/index' }), 400)
  } catch (e) {
    // 错误提示已在请求层统一处理
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.reg-page {
  background: var(--bg);
}
.reg-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 4px 12px;
  font-size: 12px;
  color: var(--ink-3);
}
.tip-ico {
  color: var(--brand);
  flex: none;
}
.reg-card {
  padding: 16px 14px;
}
.check-ico {
  color: #fff;
}
.reg-submit {
  height: 42px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  margin-top: 14px;
  letter-spacing: 2px;
}
.reg-login {
  margin-top: 12px;
  text-align: center;
}
.reg-link {
  color: var(--brand);
  font-weight: 600;
  margin-left: 2px;
}
</style>
