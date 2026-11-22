<template>
  <view :class="user.pageClass">
    <g-message />
    <g-nav-bar title="医生档案" />

    <view class="fa-body">
      <template v-if="detail">
        <view class="h5-card">
          <view class="fa-row" style="gap: 12px">
            <view class="doc-avatar">{{ (detail.name || '医').slice(-1) }}</view>
            <view class="fa-grow">
              <view class="fa-row" style="gap: 6px">
                <text class="doc-name">{{ detail.name || '—' }}</text>
                <text class="chip chip-blue">{{ detail.title || '医师' }}</text>
                <text v-if="detail.position" class="chip chip-violet">{{ detail.position }}</text>
              </view>
              <text class="doc-meta">{{ hospitalText }}</text>
            </view>
          </view>
          <view v-if="detail.bound || detail.pending" class="doc-state">
            <text v-if="detail.bound" class="chip chip-green">当前随访医生</text>
            <text v-else class="chip chip-amber">绑定申请审核中</text>
          </view>
        </view>

        <view class="h5-sec">
          <text class="b">执业信息</text>
        </view>
        <view class="h5-card">
          <view class="kv">
            <text class="kv-k">职称</text>
            <text class="kv-v">{{ detail.title || '—' }}</text>
            <text class="kv-k">行政职务</text>
            <text class="kv-v">{{ detail.position || '—' }}</text>
            <text class="kv-k">所属医院</text>
            <text class="kv-v">{{ detail.hospital || '—' }}</text>
            <text class="kv-k">所属科室</text>
            <text class="kv-v">{{ detail.department || '—' }}</text>
            <text class="kv-k">执业类别</text>
            <text class="kv-v">{{ detail.licenseType || '—' }}</text>
            <text class="kv-k">执业范围</text>
            <text class="kv-v">{{ detail.practiceScope || '—' }}</text>
            <text class="kv-k">出诊时间</text>
            <text class="kv-v">{{ detail.schedule || '—' }}</text>
            <text class="kv-k">挂号费</text>
            <text class="kv-v">{{ detail.registrationFee != null ? detail.registrationFee + ' 元' : '—' }}</text>
            <text class="kv-k">在管患者</text>
            <text class="kv-v">{{ countText }}</text>
          </view>
        </view>

        <view class="h5-sec">
          <text class="b">擅长领域</text>
        </view>
        <view class="h5-card">
          <text class="doc-text">{{ detail.specialty || '暂未填写' }}</text>
        </view>

        <view class="h5-sec">
          <text class="b">医生简介</text>
        </view>
        <view class="h5-card">
          <text class="doc-text">{{ detail.introduction || '暂未填写' }}</text>
        </view>

        <template v-if="detail.experience">
          <view class="h5-sec">
            <text class="b">工作与教育经历</text>
          </view>
          <view class="h5-card">
            <text class="doc-text">{{ detail.experience }}</text>
          </view>
        </template>
      </template>
      <view v-else-if="loaded" class="empty">未找到该医生信息</view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { get } from '@/utils/request'

const user = useUserStore()

const detail = ref(null)
const loaded = ref(false)

const hospitalText = computed(() => {
  const d = detail.value
  if (!d) return ''
  return [d.hospital, d.department].filter(Boolean).join(' · ') || '暂未完善科室信息'
})

const countText = computed(() => {
  const d = detail.value
  if (!d) return '—'
  const count = d.patientCount || 0
  return d.maxPatients > 0 ? `${count} / ${d.maxPatients} 人` : `${count} 人`
})

onLoad(async (options) => {
  if (!user.requireLogin()) return
  try {
    detail.value = await get(`/api/patient/doctors/${options.id}`)
  } catch (e) {
    // 错误提示已由请求封装统一处理
  } finally {
    loaded.value = true
  }
})
</script>

<style lang="scss" scoped>
.doc-avatar {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: var(--brand-soft);
  color: var(--brand);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 21px;
  font-weight: 700;
  flex: none;
}
.doc-name {
  font-size: 17px;
  font-weight: 700;
  color: var(--ink);
}
.doc-meta {
  display: block;
  margin-top: 4px;
  font-size: 12.5px;
  color: var(--ink-3);
}
.doc-state {
  display: flex;
  gap: 6px;
  margin-top: 12px;
  padding-top: 10px;
  border-top: 1px solid var(--line-2);
}
.doc-text {
  display: block;
  font-size: 13px;
  line-height: 1.8;
  color: var(--ink-2);
}
</style>
