<template>
  <view :class="user.pageClass">
    <g-message />
    <g-nav-bar title="我的医生" />

    <view class="fa-body">
      <!-- 当前绑定医生 -->
      <template v-if="boundDoctor">
        <view class="h5-sec">
          <text class="b">当前随访医生</text>
          <text class="chip chip-green ml-auto">已绑定</text>
        </view>
        <view class="h5-card tap" @click="openDetail(boundDoctor)">
          <view class="fa-row" style="gap: 12px">
            <view class="doc-avatar">{{ (boundDoctor.name || '医').slice(-1) }}</view>
            <view class="fa-grow">
              <view class="fa-row" style="gap: 6px">
                <text class="doc-name">{{ boundDoctor.name || '—' }}</text>
                <text class="chip chip-blue">{{ boundDoctor.title || '医师' }}</text>
                <text v-if="boundDoctor.position" class="chip chip-violet">{{ boundDoctor.position }}</text>
              </view>
              <text class="doc-meta">{{ doctorDept(boundDoctor) }}</text>
            </view>
            <g-icon name="arrowRight" :size="24" class="doc-arrow" />
          </view>
          <view v-if="boundDoctor.specialty" class="doc-block">
            <text class="doc-block-t">擅长领域</text>
            <text class="doc-block-c">{{ boundDoctor.specialty }}</text>
          </view>
          <view v-if="boundDoctor.introduction" class="doc-block">
            <text class="doc-block-t">医生简介</text>
            <text class="doc-block-c">{{ boundDoctor.introduction }}</text>
          </view>
          <view class="doc-block">
            <text class="doc-block-t">在管患者</text>
            <text class="doc-block-c">{{ countText(boundDoctor) }}</text>
          </view>
        </view>
      </template>

      <!-- 待审核申请 -->
      <template v-if="pendingApply">
        <view class="h5-sec">
          <text class="b">待审核申请</text>
          <text class="chip chip-amber ml-auto">{{ pendingApply.statusText || '待审核' }}</text>
        </view>
        <view class="h5-card">
          <view class="kv">
            <text class="kv-k">目标医生</text>
            <text class="kv-v">{{ pendingApply.doctorName || '—' }}</text>
            <text class="kv-k">所属科室</text>
            <text class="kv-v">{{ applyDept }}</text>
            <text class="kv-k">申请类型</text>
            <text class="kv-v">{{ pendingApply.applyTypeText || '—' }}</text>
            <text class="kv-k">提交时间</text>
            <text class="kv-v">{{ pendingApply.createTime || '—' }}</text>
            <text class="kv-k">申请说明</text>
            <text class="kv-v">{{ pendingApply.applyReason || '—' }}</text>
          </view>
          <view class="section-note">
            <g-icon name="clock" :size="28" class="note-ico" />
            <text>医生审核通过后绑定生效，如需更换医生请先取消本次申请。</text>
          </view>
          <view class="h5-btn ghost block" @click="cancelApply">取消申请</view>
        </view>
      </template>

      <!-- 医生列表 -->
      <template v-else>
        <view class="h5-sec">
          <text class="b">{{ boundDoctor ? '申请换绑医生' : '选择随访医生' }}</text>
          <text class="ml-auto t-12 ink-3">{{ doctors.length }} 位可申请</text>
        </view>
        <view v-for="d in doctors" :key="d.id" class="h5-card tap" @click="openDetail(d)">
          <view class="fa-row" style="gap: 12px">
            <view class="doc-avatar">{{ (d.name || '医').slice(-1) }}</view>
            <view class="fa-grow">
              <view class="fa-row" style="gap: 6px">
                <text class="doc-name">{{ d.name || '—' }}</text>
                <text class="chip chip-blue">{{ d.title || '医师' }}</text>
                <text v-if="d.position" class="chip chip-violet">{{ d.position }}</text>
              </view>
              <text class="doc-meta">{{ doctorDept(d) }}</text>
            </view>
            <g-icon name="arrowRight" :size="24" class="doc-arrow" />
          </view>
          <text v-if="d.introduction || d.specialty" class="doc-intro">{{ d.introduction || d.specialty }}</text>
          <view class="doc-foot">
            <text class="t-12 ink-3">在管患者 {{ countText(d) }}</text>
            <view
              class="h5-btn sm doc-btn"
              :class="btnClass(d)"
              @click.stop="openApply(d)"
            >
              {{ btnText(d) }}
            </view>
          </view>
        </view>
        <view v-if="!doctors.length" class="empty">暂无可申请的医生</view>
      </template>
    </view>

    <g-popup ref="applyPopup" title="提交绑定申请" type="center">
      <view class="form-row">
        <text class="lbl">申请医生</text>
        <text class="doc-pick">{{ selectedDoctor ? selectedDoctor.name : '' }}</text>
      </view>
      <view class="form-row">
        <text class="lbl">申请说明<text class="req">*</text></text>
        <textarea
          v-model="applyReason"
          class="fa-textarea"
          maxlength="200"
          placeholder="请简要说明病情与诉求，便于医生确认"
          placeholder-class="ph"
        />
      </view>
      <template #footer>
        <view class="h5-btn ghost" @click="applyPopup.hide()">取消</view>
        <view class="h5-btn primary" @click="submitApply">提交</view>
      </template>
    </g-popup>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { get, post } from '@/utils/request'
import { confirm, toast } from '@/utils/format'

const user = useUserStore()

const boundDoctor = ref(null)
const pendingApply = ref(null)
const doctors = ref([])
const applyPopup = ref(null)
const selectedDoctor = ref(null)
const applyReason = ref('')
const submitting = ref(false)

const applyDept = computed(() => {
  const apply = pendingApply.value
  if (!apply) return '—'
  return [apply.doctorHospital, apply.doctorDepartment].filter(Boolean).join(' · ') || '—'
})

function doctorDept(doctor) {
  return [doctor.hospital, doctor.department].filter(Boolean).join(' · ') || '暂未完善科室信息'
}

function countText(doctor) {
  const count = doctor.patientCount || 0
  return doctor.maxPatients > 0 ? `${count} / ${doctor.maxPatients} 人` : `${count} 人`
}

function btnText(doctor) {
  if (doctor.bound) return '已绑定'
  if (doctor.pending) return '审核中'
  return boundDoctor.value ? '申请换绑' : '申请绑定'
}

function btnClass(doctor) {
  if (doctor.bound || doctor.pending) return 'ghost disabled'
  return 'primary'
}

async function loadAll() {
  try {
    const [doctor, apply, list] = await Promise.all([
      get('/api/patient/doctor/current'),
      get('/api/patient/doctor-apply/current'),
      get('/api/patient/doctors')
    ])
    boundDoctor.value = doctor || null
    pendingApply.value = apply || null
    doctors.value = list || []
  } catch (e) {
    // 错误提示已由请求封装统一处理
  }
}

onShow(() => {
  if (!user.requireLogin()) return
  loadAll()
})

function openDetail(doctor) {
  if (!doctor || !doctor.id) return
  uni.navigateTo({ url: `/pages/doctor-detail/doctor-detail?id=${doctor.id}` })
}

function openApply(doctor) {
  if (doctor.bound || doctor.pending) return
  if (pendingApply.value) {
    toast('您有待审核的绑定申请，请先取消')
    return
  }
  selectedDoctor.value = doctor
  applyReason.value = ''
  applyPopup.value.show()
}

async function submitApply() {
  if (!selectedDoctor.value || submitting.value) return
  if (!applyReason.value.trim()) {
    toast('请填写申请说明')
    return
  }
  submitting.value = true
  try {
    await post('/api/patient/doctor-apply', {
      doctorId: selectedDoctor.value.id,
      applyReason: applyReason.value.trim()
    })
    applyPopup.value.hide()
    toast('申请已提交，等待医生审核')
    await loadAll()
  } catch (e) {
    // 错误提示已由请求封装统一处理
  } finally {
    submitting.value = false
  }
}

async function cancelApply() {
  if (!pendingApply.value) return
  const ok = await confirm('取消后需重新提交申请，确定取消吗？', '取消申请')
  if (!ok) return
  try {
    await post(`/api/patient/doctor-apply/${pendingApply.value.id}/cancel`)
    toast('已取消申请')
    await loadAll()
  } catch (e) {
    // 错误提示已由请求封装统一处理
  }
}
</script>

<style lang="scss" scoped>
.doc-avatar {
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
.doc-name {
  font-size: 16px;
  font-weight: 700;
  color: var(--ink);
}
.doc-meta {
  display: block;
  margin-top: 3px;
  font-size: 12px;
  color: var(--ink-3);
}
.doc-arrow {
  color: var(--ink-3);
  flex: none;
}
.doc-intro {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-top: 10px;
  font-size: 12.5px;
  line-height: 1.7;
  color: var(--ink-2);
}
.doc-block {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding-top: 10px;
}
.doc-block-t {
  font-size: 12.5px;
  color: var(--ink-3);
}
.doc-block-c {
  font-size: 13px;
  line-height: 1.7;
  color: var(--ink-2);
}
.doc-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 12px;
  padding-top: 10px;
  border-top: 1px solid var(--line-2);
}
.doc-btn {
  flex: none;
  padding: 0 14px;
}
.doc-pick {
  display: block;
  padding: 9px 12px;
  border-radius: 10px;
  background: var(--surface-2);
  font-size: 13.5px;
  font-weight: 600;
  color: var(--ink);
}
.h5-card .section-note {
  margin: 12px 0;
}
</style>
