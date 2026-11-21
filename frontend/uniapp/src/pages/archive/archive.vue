<template>
  <view :class="user.pageClass">
    <g-message />
    <g-nav-bar title="健康档案" />

    <view class="fa-body has-footer">
      <!-- 患者概览 -->
      <view class="h5-card arc-head">
        <view class="fa-row" style="gap: 12px">
          <view class="arc-avatar">{{ avatarText }}</view>
          <view class="fa-grow">
            <view class="fa-row" style="gap: 6px">
              <text class="arc-name">{{ patient.name }}</text>
              <text class="chip" :class="patient.risk === '高危' ? 'chip-red' : 'chip-blue'">{{ patient.risk }}</text>
            </view>
            <text class="arc-meta">{{ patient.sex }} · {{ patient.age }} 岁 · 档案号 {{ patient.id }}</text>
            <text class="arc-meta">{{ patient.dept }} · {{ patient.doctor }}</text>
          </view>
        </view>
        <view class="med-wave arc-wave" />
      </view>

      <!-- 基本信息 -->
      <view class="h5-sec">
        <text class="b">基本信息</text>
        <text class="ml-auto t-12 ink-3">建档于 {{ BASELINE.registerDate }}</text>
      </view>
      <view class="h5-card">
        <view class="kv">
          <text class="kv-k">姓名</text><text class="kv-v">{{ patient.name }}</text>
          <text class="kv-k">性别 / 年龄</text><text class="kv-v">{{ patient.sex }} / {{ patient.age }} 岁</text>
          <text class="kv-k">联系电话</text><text class="kv-v">{{ patient.phone }}</text>
          <text class="kv-k">身份证号</text><text class="kv-v mono">{{ patient.idCard }}</text>
          <text class="kv-k">确诊日期</text><text class="kv-v">{{ patient.diagnoseDate }}</text>
          <text class="kv-k">随访医生</text><text class="kv-v">{{ patient.doctor }}</text>
          <text class="kv-k">下次随访</text><text class="kv-v">{{ patient.nextVisit }}</text>
        </view>
      </view>

      <!-- 基线信息 -->
      <view class="h5-sec">
        <text class="b">基线信息</text>
        <text class="chip chip-blue">医生填写 · 只读</text>
      </view>
      <view class="h5-card">
        <view class="kv">
          <text class="kv-k">危险分层</text>
          <view class="kv-v">
            <text class="chip" :class="BASELINE.riskLevel === '高危' ? 'chip-red' : 'chip-amber'">{{ BASELINE.riskLevel }}</text>
          </view>
          <text class="kv-k">分层依据</text><text class="kv-v">{{ BASELINE.riskBasis }}</text>
          <text class="kv-k">过敏史</text><text class="kv-v">{{ BASELINE.allergy }}</text>
          <text class="kv-k">合并症</text><text class="kv-v">{{ BASELINE.comorbidity }}</text>
        </view>
        <view class="hr" />
        <view class="arc-block">
          <text class="arc-block-t">首次发作情况</text>
          <text class="arc-block-c">{{ BASELINE.firstAttack }}</text>
        </view>
        <view class="arc-block">
          <text class="arc-block-t">关键检查结果</text>
          <text class="arc-block-c">{{ BASELINE.exam }}</text>
        </view>
        <view class="section-note">
          <g-icon name="shield" :size="28" class="note-ico" />
          <text>基线档案由医生建档，关键病史不可自行修改，如有出入请提交变更申请。</text>
        </view>
      </view>

      <!-- 变更留痕 -->
      <view class="h5-sec">
        <text class="b">变更留痕</text>
        <text class="ml-auto t-12 ink-3">共 {{ logs.length }} 条</text>
      </view>
      <view class="h5-card">
        <view v-if="logs.length" class="timeline">
          <view v-for="(log, i) in logs" :key="i" class="tl" :class="i === 0 ? 'now' : 'done'">
            <view class="tl-rail">
              <view class="tl-dot" />
              <view v-if="i < logs.length - 1" class="tl-line" />
            </view>
            <view class="tl-body">
              <text class="t">{{ log.field }}：{{ log.before }} → {{ log.after }}</text>
              <view class="d">{{ log.time }} · {{ log.operator }}</view>
            </view>
          </view>
        </view>
        <view v-else class="empty">暂无变更记录</view>
      </view>
    </view>

    <view class="fa-footer">
      <view class="h5-btn ghost" @click="openApply">申请修改档案</view>
      <view class="h5-btn primary" @click="go('/pages/angina/angina')">查看发作记录</view>
    </view>

    <g-popup ref="applyPopup" title="申请修改档案信息" type="center">
      <view class="form-row">
        <text class="lbl">需要修改的内容<text class="req">*</text></text>
        <input
          v-model="applyForm.field"
          class="fa-input"
          placeholder="如：联系电话 / 合并症"
          placeholder-class="ph"
        />
      </view>
      <view class="form-row">
        <text class="lbl">修改说明<text class="req">*</text></text>
        <textarea
          v-model="applyForm.reason"
          class="fa-textarea"
          maxlength="200"
          placeholder="请说明修改原因，医生审核后生效"
          placeholder-class="ph"
        />
      </view>
      <template #footer>
        <view class="h5-btn ghost" @click="applyPopup.hide()">取消</view>
        <view class="h5-btn primary" @click="submitApply">提交申请</view>
      </template>
    </g-popup>
  </view>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { ARCHIVE_LOGS, BASELINE } from '@/mock'
import { toast } from '@/utils/format'

const user = useUserStore()
const patient = computed(() => user.patient || {})
const logs = ARCHIVE_LOGS

const avatarText = computed(() => (patient.value.name || '患').slice(-1))
const applyPopup = ref(null)
const applyForm = reactive({ field: '', reason: '' })

onShow(() => {
  user.requireLogin()
})

function openApply() {
  applyForm.field = ''
  applyForm.reason = ''
  applyPopup.value.show()
}

function submitApply() {
  if (!applyForm.field.trim() || !applyForm.reason.trim()) {
    toast('请填写修改内容与说明')
    return
  }
  applyPopup.value.hide()
  toast('申请已提交，医生审核后生效')
}

function go(url) {
  uni.navigateTo({ url })
}
</script>

<style lang="scss" scoped>
.arc-head {
  position: relative;
  overflow: hidden;
}
.arc-wave {
  position: absolute;
  top: 14px;
  right: 12px;
  width: 52px;
  height: 18px;
  color: var(--brand);
}
.arc-avatar {
  width: 46px;
  height: 46px;
  border-radius: 50%;
  background: var(--brand-soft);
  color: var(--brand);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 19px;
  font-weight: 700;
  flex: none;
}
.arc-name {
  font-size: 16px;
  font-weight: 700;
  color: var(--ink);
}
.arc-meta {
  display: block;
  margin-top: 3px;
  font-size: 12px;
  color: var(--ink-3);
}
.arc-block {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding-top: 10px;
}
.arc-block-t {
  font-size: 12.5px;
  color: var(--ink-3);
}
.arc-block-c {
  font-size: 13px;
  line-height: 1.7;
  color: var(--ink-2);
}
.h5-card .section-note {
  margin: 12px 0 0;
}
.tl .t {
  display: block;
  font-size: 12.5px;
  font-weight: 600;
  color: var(--ink);
}
.tl .d {
  margin-top: 2px;
  font-size: 11px;
  color: var(--ink-3);
}
</style>
