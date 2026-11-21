<template>
  <view :class="user.pageClass">
    <g-message />
    <view class="fa-body has-tabbar">
      <!-- 患者概要 -->
      <view class="h5-card pf-head">
        <view class="pf-avatar">{{ avatarText }}</view>
        <view class="pf-main">
          <view class="fa-row">
            <text class="pf-name">{{ patient.name || '患者' }}</text>
            <text class="chip" :class="patient.risk === '高危' ? 'chip-red' : 'chip-blue'">
              {{ patient.risk || '—' }}
            </text>
          </view>
          <text class="pf-sub">{{ patient.id || '—' }} · {{ patient.dept || '—' }}</text>
          <text class="pf-sub">随访医生 {{ patient.doctor || '—' }} · 下次随访 {{ patient.nextVisit || '—' }}</text>
        </view>
      </view>

      <!-- 我的服务 -->
      <view class="h5-sec">
        <text class="b">我的服务</text>
        <text class="sp ml-auto">常用功能</text>
      </view>
      <view class="h5-card pf-list">
        <view v-for="s in services" :key="s.url" class="pin-list-row" @click="go(s)">
          <view class="ic" :class="s.ic">
            <g-icon :name="s.icon" :size="36" />
          </view>
          <view class="fa-grow">
            <text class="nm">{{ s.title }}</text>
            <text class="sub">{{ s.desc }}</text>
          </view>
          <g-icon name="arrowRight" :size="26" class="pf-arrow" />
        </view>
      </view>

      <!-- 显示设置 -->
      <view class="h5-sec">
        <text class="b">显示设置</text>
        <text class="sp ml-auto">仅本机生效</text>
      </view>
      <view class="h5-card pf-list">
        <view class="pin-list-row">
          <view class="ic ic-blue">
            <g-icon name="user" :size="36" />
          </view>
          <view class="fa-grow">
            <text class="nm">适老模式</text>
            <text class="sub">放大字号、加大按钮，便于阅读操作</text>
          </view>
          <view class="right">
            <switch :checked="user.modeOld" color="#165dff" @change="onModeOld" />
          </view>
        </view>
        <view class="pin-list-row">
          <view class="ic ic-amber">
            <g-icon name="heart" :size="36" />
          </view>
          <view class="fa-grow">
            <text class="nm">中医风格皮肤</text>
            <text class="sub">暖棕配色，视觉更柔和</text>
          </view>
          <view class="right">
            <switch :checked="user.tcmStyle" color="#165dff" @change="onTcm" />
          </view>
        </view>
      </view>

      <!-- 安全与帮助 -->
      <view class="h5-sec">
        <text class="b">安全与帮助</text>
      </view>
      <view class="h5-card pf-list">
        <view class="pin-list-row" @click="showPrivacy">
          <view class="ic ic-green">
            <g-icon name="shield" :size="36" />
          </view>
          <view class="fa-grow">
            <text class="nm">隐私与数据说明</text>
            <text class="sub">健康数据仅用于随访管理</text>
          </view>
          <g-icon name="arrowRight" :size="26" class="pf-arrow" />
        </view>
        <view class="pin-list-row" @click="callDoctor">
          <view class="ic ic-red">
            <g-icon name="phone" :size="36" />
          </view>
          <view class="fa-grow">
            <text class="nm">联系随访医生</text>
            <text class="sub">工作时间 08:00 - 17:30</text>
          </view>
          <g-icon name="arrowRight" :size="26" class="pf-arrow" />
        </view>
      </view>

      <!-- 退出登录 -->
      <view class="h5-card pf-out">
        <view class="h5-btn danger block" @click="logout">退出登录</view>
        <text class="version">稳定性心绞痛随访 v1.0.0 · 当前为演示数据</text>
      </view>
    </view>

    <g-tabbar current="profile" />
  </view>
</template>

<script setup>
import { computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { confirm, toast } from '@/utils/format'

const user = useUserStore()

const patient = computed(() => user.patient || {})
const avatarText = computed(() => (patient.value.name || '患').slice(-1))

const services = [
  { title: '健康档案', desc: '基本信息与基线资料', icon: 'doc', ic: 'ic-blue', url: '/pages/archive/archive' },
  { title: '我的随访', desc: '问卷、结论与申请', icon: 'clipboard', ic: 'ic-amber', url: '/pages/followup/followup', tab: true },
  { title: '发作记录', desc: '心绞痛打卡与趋势', icon: 'activity', ic: 'ic-red', url: '/pages/angina/angina' },
  { title: '血压记录', desc: '晨起 / 晚间血压', icon: 'heart', ic: 'ic-green', url: '/pages/bp/bp' },
  { title: '用药管理', desc: '医嘱、打卡与不良反应', icon: 'pill', ic: 'ic-blue', url: '/pages/medication/medication' }
]

onShow(() => {
  user.requireLogin()
})

function go(s) {
  if (s.tab) uni.reLaunch({ url: s.url })
  else uni.navigateTo({ url: s.url })
}

function onModeOld(e) {
  const val = e.detail.value
  user.setModeOld(val)
  toast(val ? '已开启适老模式' : '已关闭适老模式')
}

function onTcm(e) {
  const val = e.detail.value
  user.setTcmStyle(val)
  toast(val ? '已切换为中医风格' : '已恢复默认风格')
}

function showPrivacy() {
  confirm('您的健康数据仅用于随访管理与诊疗评估，医生端按权限查看，不会用于其他用途。', '隐私与数据说明')
}

function callDoctor() {
  uni.makePhoneCall({
    phoneNumber: '057100000000',
    fail: () => toast('演示环境暂不支持拨号')
  })
}

async function logout() {
  const ok = await confirm('退出后需重新登录，确定退出吗？', '退出登录')
  if (!ok) return
  user.logout()
  uni.reLaunch({ url: '/pages/login/login' })
}
</script>

<style lang="scss" scoped>
.pf-head {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 12px;
}
.pf-avatar {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  flex: none;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 600;
  color: #fff;
  background: linear-gradient(135deg, var(--brand), var(--teal));
  box-shadow: 0 6px 14px rgba(22, 93, 255, 0.26);
}
.pf-main {
  flex: 1;
  min-width: 0;
}
.pf-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--ink);
}
.pf-sub {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: var(--ink-3);
}
.pf-list {
  padding: 2px 12px;
}
.pf-list .nm,
.pf-list .sub {
  display: block;
}
.pf-list .ic-blue {
  color: var(--brand);
}
.pf-list .ic-amber {
  color: var(--amber);
}
.pf-list .ic-red {
  color: var(--red);
}
.pf-list .ic-green {
  color: var(--green);
}
.pf-arrow {
  color: var(--ink-3);
  flex: none;
}
.pf-out {
  margin-top: 12px;
  padding: 14px 12px 12px;
}
.version {
  display: block;
  margin-top: 10px;
  text-align: center;
  font-size: 11.5px;
  color: var(--ink-3);
}
</style>
