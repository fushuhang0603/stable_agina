<template>
  <el-card shadow="never">
    <div class="forbidden">
      <el-icon class="forbidden-ico"><Lock /></el-icon>
      <div class="forbidden-title">无权访问该页面</div>
      <div class="forbidden-desc">当前账号未分配该菜单权限，请联系系统管理员</div>
      <el-button type="primary" @click="$router.replace('/')">返回首页</el-button>
    </div>
  </el-card>
</template>

<style scoped>
.forbidden {
  padding: 72px 16px;
  text-align: center;
}
.forbidden-ico {
  font-size: 34px;
  color: var(--ink-3);
}
.forbidden-title {
  margin: 10px 0 4px;
  font-size: 14px;
  color: var(--ink);
}
.forbidden-desc {
  margin-bottom: 16px;
  font-size: 12.5px;
  color: var(--ink-3);
}
</style>
