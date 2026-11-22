<template>
  <div class="operation-log">
    <div class="grid g3 log-stats">
      <StatCard
        title="今日操作数"
        :value="stats.todayCount"
        icon="clock"
        tone="blue"
        desc="统计范围：今日 00:00 起"
      />
      <StatCard
        title="失败次数"
        :value="stats.failedCount"
        icon="alert"
        tone="red"
        desc="失败操作需重点核查"
      />
      <StatCard
        title="涉及用户数"
        :value="stats.userCount"
        icon="users"
        tone="amber"
        desc="去重后的操作账号数"
      />
    </div>

    <el-card shadow="never" class="filter-card">
      <div class="filter-bar">
        <el-input
          v-model.trim="filters.operName"
          placeholder="操作人账号"
          clearable
          style="width: 160px"
          @keyup.enter="search"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-input
          v-model.trim="filters.operUrl"
          placeholder="请求地址"
          clearable
          style="width: 200px"
          @keyup.enter="search"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="filters.title" placeholder="全部模块" clearable style="width: 140px">
          <el-option v-for="o in moduleOptions" :key="o.value" :value="o.value" :label="o.label" />
        </el-select>
        <el-input
          v-model.trim="filters.businessType"
          placeholder="动作（精确匹配）"
          clearable
          style="width: 160px"
          @keyup.enter="search"
        />
        <el-select v-model="filters.status" placeholder="全部结果" clearable style="width: 110px">
          <el-option v-for="o in resultOptions" :key="o.value" :value="o.value" :label="o.label" />
        </el-select>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </div>
    </el-card>

    <Card title="操作日志">
      <template #extra>
        <span class="card-hint">共 {{ total }} 条，按时间倒序展示</span>
      </template>
      <el-table v-loading="loading" :data="rows" size="small">
        <el-table-column label="时间" width="160">
          <template #default="{ row }"><span class="mono">{{ row.operTime }}</span></template>
        </el-table-column>
        <el-table-column prop="operName" label="操作人" width="110" />
        <el-table-column prop="title" label="模块" width="110" />
        <el-table-column prop="businessType" label="动作" width="100" />
        <el-table-column prop="operUrl" label="请求地址" min-width="200" show-overflow-tooltip>
          <template #default="{ row }"><span class="mono">{{ row.operUrl }}</span></template>
        </el-table-column>
        <el-table-column label="结果" width="90">
          <template #default="{ row }"><Chip :text="row.statusText" /></template>
        </el-table-column>
        <el-table-column label="IP" width="130">
          <template #default="{ row }"><span class="mono">{{ row.operIp }}</span></template>
        </el-table-column>
        <el-table-column label="耗时" width="90">
          <template #default="{ row }">{{ row.costTime }} ms</template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="detail = row">详情</el-button>
          </template>
        </el-table-column>
        <template #empty>未找到符合条件的操作日志</template>
      </el-table>

      <ListPager v-model:page="page" :total="total" :size="pageSize" @change="loadPage" />
    </Card>

    <el-dialog v-model="detailVisible" title="操作日志详情" width="680px">
      <dl v-if="detail" class="kv">
        <dt>日志编号</dt>
        <dd class="mono">{{ detail.id }}</dd>
        <dt>操作时间</dt>
        <dd class="mono">{{ detail.operTime }}</dd>
        <dt>操作人</dt>
        <dd>{{ detail.operName }}</dd>
        <dt>所属模块</dt>
        <dd>{{ detail.title }}</dd>
        <dt>动作</dt>
        <dd>{{ detail.businessType }}</dd>
        <dt>请求方式</dt>
        <dd class="mono">{{ detail.requestMethod }}</dd>
        <dt>请求地址</dt>
        <dd class="mono">{{ detail.operUrl }}</dd>
        <dt>来源 IP</dt>
        <dd class="mono">{{ detail.operIp }}</dd>
        <dt>执行结果</dt>
        <dd><Chip :text="detail.statusText" /></dd>
        <dt>耗时时长</dt>
        <dd>{{ detail.costTime }} ms</dd>
        <dt>目标方法</dt>
        <dd class="mono">{{ detail.method }}</dd>
        <dt>请求入参</dt>
        <dd><pre class="json">{{ pretty(detail.operParam) }}</pre></dd>
        <dt>返回结果</dt>
        <dd><pre class="json">{{ pretty(detail.jsonResult) }}</pre></dd>
        <template v-if="detail.errorMsg">
          <dt>异常信息</dt>
          <dd><pre class="json json-error">{{ detail.errorMsg }}</pre></dd>
        </template>
      </dl>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import Card from '../components/Card.vue'
import Chip from '../components/Chip.vue'
import ListPager from '../components/ListPager.vue'
import StatCard from '../components/StatCard.vue'
import request from '../utils/request'
import { toast } from '../utils/toast'

const stats = reactive({ todayCount: 0, failedCount: 0, userCount: 0 })
const rows = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = 10
const loading = ref(false)

const filters = reactive({ operName: '', operUrl: '', title: undefined, businessType: '', status: undefined })

const moduleOptions = [
  { value: '账号管理', label: '账号管理' },
  { value: '角色与权限', label: '角色与权限' },
  { value: '基础字典', label: '基础字典' },
  { value: '医生管理', label: '医生管理' }
]
const resultOptions = [
  { value: 1, label: '成功' },
  { value: 0, label: '失败' }
]

const detail = ref(null)
const detailVisible = computed({
  get: () => !!detail.value,
  set: (val) => {
    if (!val) detail.value = null
  }
})

function pretty(text) {
  if (!text) return '—'
  try {
    return JSON.stringify(JSON.parse(text), null, 2)
  } catch {
    return text
  }
}

async function loadStats() {
  Object.assign(stats, await request.get('/api/system/oper-logs/stats'))
}

async function loadPage() {
  loading.value = true
  try {
    const data = await request.get('/api/system/oper-logs', {
      params: {
        operName: filters.operName || undefined,
        operUrl: filters.operUrl || undefined,
        title: filters.title || undefined,
        businessType: filters.businessType || undefined,
        status: filters.status ?? undefined,
        pageNum: page.value,
        pageSize
      }
    })
    rows.value = data.records
    total.value = data.total
  } catch (e) {
    toast.error(e.message || '操作日志加载失败')
  } finally {
    loading.value = false
  }
}

function search() {
  page.value = 1
  loadPage()
}

function reset() {
  filters.operName = ''
  filters.operUrl = ''
  filters.title = undefined
  filters.businessType = ''
  filters.status = undefined
  page.value = 1
  loadPage()
}

onMounted(async () => {
  try {
    await Promise.all([loadPage(), loadStats()])
  } catch (e) {
    toast.error(e.message || '操作日志加载失败')
  }
})
</script>

<style scoped>
.log-stats {
  margin-bottom: 12px;
}
.filter-card {
  margin-bottom: 12px;
}
.mono {
  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
  font-size: 12px;
}
.json {
  margin: 0;
  max-height: 220px;
  overflow: auto;
  padding: 8px 10px;
  border-radius: 6px;
  background: #f7f8fa;
  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
  font-size: 12px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-all;
}
.json-error {
  background: #fef0f0;
  color: #c45656;
}
</style>
