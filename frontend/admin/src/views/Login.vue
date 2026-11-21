<template>
  <div class="login-page">
    <div class="bg" aria-hidden="true">
      <span class="blob blob-1"></span>
      <span class="blob blob-2"></span>
      <span class="blob blob-3"></span>
      <svg class="heart-echo" viewBox="0 0 24 24">
        <path d="M12 20s-7.5-4.7-7.5-9.6A4.4 4.4 0 0 1 12 7.4a4.4 4.4 0 0 1 7.5 3c0 4.9-7.5 9.6-7.5 9.6z" />
      </svg>
      <svg class="ecg" viewBox="0 0 900 120" preserveAspectRatio="none">
        <path
          d="M0 78 H236 l14 -6 8 -38 12 76 14 -46 10 -10 10 16 10 -6 H516 l14 -6 8 -38 12 76 14 -46 10 -10 10 16 10 -6 H900"
        />
      </svg>
    </div>

    <section class="overview">
      <div class="ov-inner">
        <div class="brand">
          <Icon class="brand-heart" name="heart" :size="78" :stroke-width="2.2" />
          <div class="brand-text">
            <strong>稳定性心绞痛患者随访</strong>
            <span>Stable Angina Follow-up</span>
          </div>
        </div>

        <p class="ov-desc">
          围绕稳定性心绞痛患者，打通患者档案、随访计划、用药依从与指标监测，
          结合风险分层预警与 AI 随访摘要，让长期随访落到每一次复诊。
        </p>

        <ul class="ov-list">
          <li v-for="m in modules" :key="m.title">
            <span class="m-ico"><Icon :name="m.icon" :size="17" /></span>
            <div class="m-text">
              <b>{{ m.title }}</b>
              <span>{{ m.desc }}</span>
            </div>
          </li>
        </ul>
      </div>
    </section>

    <section class="panel">
      <el-card shadow="never" class="login-card">
        <span class="card-badge"><Icon name="heart" :size="22" :stroke-width="2.2" /></span>
        <header class="login-head">
          <h2>账号登录</h2>
        </header>

        <el-form class="login-form" @submit.prevent="onSubmit">
          <el-form-item>
            <el-input
              v-model.trim="form.account"
              placeholder="请输入账号"
              autocomplete="username"
              clearable
            >
              <template #prefix><el-icon><User /></el-icon></template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              autocomplete="current-password"
              show-password
              @keyup.enter="onSubmit"
            >
              <template #prefix><el-icon><Lock /></el-icon></template>
            </el-input>
          </el-form-item>

          <div class="login-options">
            <el-checkbox v-model="form.remember">记住账号</el-checkbox>
          </div>

          <el-button
            type="primary"
            native-type="submit"
            class="login-btn"
            :loading="loading"
          >
            {{ loading ? '登录中…' : '登 录' }}
          </el-button>
        </el-form>

        <p class="card-foot">稳定性心绞痛患者随访管理系统</p>
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Icon from '../components/Icon.vue'
import { useAuthStore } from '../stores/auth'
import { toast } from '../utils/toast'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const loading = ref(false)

const modules = [
  { icon: 'users', title: '患者档案', desc: '基本资料与诊断一档贯通' },
  { icon: 'clipboard', title: '随访计划', desc: '按周期生成任务并记录结论' },
  { icon: 'bag', title: '用药依从', desc: '处方记录与服药情况跟踪' },
  { icon: 'chart', title: '指标监测', desc: '血压心率等趋势可视化' },
  { icon: 'shield', title: '风险预警', desc: '高危患者自动标记提示' },
  { icon: 'activity', title: 'AI 摘要', desc: '自动归纳随访要点辅助决策' }
]

const form = reactive({
  account: localStorage.getItem('sa_account') || '',
  password: '',
  remember: !!localStorage.getItem('sa_account')
})

async function onSubmit() {
  if (!form.account) return toast('请输入账号')
  if (!form.password) return toast('请输入密码')

  loading.value = true
  try {
    await auth.login({ username: form.account.trim(), password: form.password })
    if (form.remember) localStorage.setItem('sa_account', form.account.trim())
    else localStorage.removeItem('sa_account')

    toast('登录成功')
    const redirect =
      typeof route.query.redirect === 'string' && route.query.redirect.startsWith('/')
        ? route.query.redirect
        : '/'
    router.replace(redirect)
  } catch (error) {
    toast(error?.message || '登录失败，请检查账号或密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  --accent: #165dff;
  --accent-deep: #0f4ae0;
  --ink: #1f2329;
  --ink-2: #5a6472;
  --ink-3: #98a3b3;

  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 48px;
  min-height: 100vh;
  padding: 56px clamp(72px, 12vw, 190px) 56px clamp(40px, 6vw, 96px);
  overflow: hidden;
  background: #f7fafe;
  color: var(--ink);
}

/* ---------- 背景装饰 ---------- */
.bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}
.blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(6px);
}
.blob-1 {
  width: 420px;
  height: 420px;
  top: -230px;
  left: -160px;
  background: #e8f0ff;
}
.blob-2 {
  width: 320px;
  height: 320px;
  top: 120px;
  right: -140px;
  background: #eef4ff;
  opacity: 0.9;
}
.blob-3 {
  width: 260px;
  height: 260px;
  bottom: -150px;
  left: 34%;
  background: #eff5ff;
  opacity: 0.8;
}
.heart-echo {
  position: absolute;
  width: 460px;
  height: 460px;
  left: -170px;
  bottom: -190px;
  transform: rotate(-12deg);
  fill: none;
  stroke: var(--accent);
  stroke-width: 0.6;
  opacity: 0.07;
}
.ecg {
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 110px;
  fill: none;
  stroke: var(--accent);
  stroke-width: 1.4;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-dasharray: 12 10;
  opacity: 0.12;
  animation: ecg-flow 3s linear infinite;
}
@keyframes ecg-flow {
  to { stroke-dashoffset: -44; }
}

/* ---------- 左侧：蓝色爱心 + 系统概述 ---------- */
.overview {
  position: relative;
  z-index: 1;
  flex: 1 1 auto;
  min-width: 0;
  max-width: 660px;
}
.ov-inner {
  width: 100%;
  max-width: 620px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 18px;
  margin-bottom: 32px;
}
.brand-heart {
  flex: none;
  color: var(--accent);
}
.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}
.brand-text strong {
  font-size: 21px;
  font-weight: 600;
  letter-spacing: 0.6px;
  color: var(--ink);
}
.brand-text span {
  margin-top: 4px;
  font-size: 11.5px;
  letter-spacing: 1.8px;
  text-transform: uppercase;
  color: var(--ink-3);
}

.ov-desc {
  margin: 0;
  max-width: 580px;
  font-size: 15px;
  line-height: 1.95;
  color: var(--ink-2);
}

.ov-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px 20px;
  max-width: 570px;
  margin: 30px 0 0;
  padding: 0;
  list-style: none;
}
.ov-list li {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 12px 14px;
  background: #fff;
  border: 1px solid #eaf0fb;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(22, 93, 255, 0.04);
  transition: box-shadow 0.2s ease, transform 0.2s ease;
}
.ov-list li:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 18px rgba(22, 93, 255, 0.09);
}
.m-ico {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex: none;
  width: 33px;
  height: 33px;
  border-radius: 9px;
  background: #eef4ff;
  color: var(--accent);
}
.m-text {
  display: flex;
  flex-direction: column;
  padding-top: 1px;
}
.m-text b {
  font-size: 13.5px;
  font-weight: 600;
  color: var(--ink);
}
.m-text span {
  margin-top: 2px;
  font-size: 11.5px;
  color: var(--ink-3);
}

/* ---------- 右侧：正方形登录弹窗 ---------- */
.panel {
  position: relative;
  z-index: 1;
  flex: 0 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-card {
  position: relative;
  overflow: visible;
  width: 400px;
  height: 400px;
  background: #fff;
  border: 1px solid #e6eefb;
  border-radius: 18px;
  box-shadow: 0 20px 48px rgba(22, 93, 255, 0.09), 0 2px 6px rgba(22, 93, 255, 0.04);
}
.card-badge {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translate(-50%, -50%);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: #eaf2ff;
  border: 4px solid #f7fafe;
  color: var(--accent);
}
.login-card :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  justify-content: center;
  height: 100%;
  padding: 34px 34px 26px;
}

.login-head {
  margin-bottom: 26px;
  text-align: center;
}
.login-head h2 {
  margin: 0;
  font-size: 21px;
  font-weight: 600;
  letter-spacing: 1px;
  color: var(--ink);
}

.login-form :deep(.el-form-item) {
  margin-bottom: 16px;
}
.login-form :deep(.el-input__inner) {
  height: 40px;
}
.login-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #e3eaf5 inset;
}
.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #b9cdf3 inset;
}
.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--accent) inset, 0 0 0 3px rgba(22, 93, 255, 0.12);
}

.login-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin: 0 0 16px;
}
.login-options :deep(.el-checkbox__label) {
  font-size: 12.5px;
  color: var(--ink-2);
}
.login-options :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background: var(--accent);
  border-color: var(--accent);
}
.login-options :deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  color: var(--accent);
}

.login-btn {
  width: 100%;
  height: 44px;
  border: none;
  border-radius: 9px;
  font-size: 14.5px;
  letter-spacing: 3px;
  background: var(--accent);
  box-shadow: 0 8px 18px rgba(22, 93, 255, 0.22);
}
.login-btn:hover,
.login-btn:focus {
  background: #3b76ff;
  box-shadow: 0 10px 22px rgba(22, 93, 255, 0.28);
}

.card-foot {
  margin: 14px 0 0;
  text-align: center;
  font-size: 10.5px;
  letter-spacing: 0.3px;
  color: #b6bfcd;
}

@media (max-width: 1120px) {
  .login-page { padding: 48px 64px 48px 40px; gap: 32px; }
  .ov-desc { font-size: 14px; }
  .ov-list { gap: 12px 16px; max-width: 540px; }
  .ov-list li { padding: 11px 13px; }
  .login-card { width: 360px; height: 360px; }
  .login-card :deep(.el-card__body) { padding: 30px 28px 22px; }
  .card-badge { width: 48px; height: 48px; }
}

@media (max-width: 900px) {
  .login-page {
    flex-direction: column;
    justify-content: center;
    gap: 26px;
    padding: 34px 22px 40px;
  }
  .overview { flex: none; max-width: 100%; }
  .ov-inner { max-width: 100%; }
  .brand { margin-bottom: 18px; }
  .ov-desc { font-size: 13.5px; }
  .ov-list { display: none; }
  .heart-echo { display: none; }
  .ecg { height: 84px; }
}

@media (max-width: 520px) {
  .login-page { padding: 26px 16px 34px; }
  .ov-desc { display: none; }
  .brand-text strong { font-size: 13px; letter-spacing: 0.5px; }
  .brand-text span { display: none; }
  .login-card { width: 320px; height: 320px; }
  .login-card :deep(.el-card__body) { padding: 24px 22px 18px; }
  .login-head { margin-bottom: 16px; }
  .card-foot { display: none; }
}
</style>
