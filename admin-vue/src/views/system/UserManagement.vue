<template>
  <div class="user-management">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="昵称/电话号">
          <el-input v-model="searchForm.nickname" placeholder="请输入昵称或手机号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="nickname" label="微信昵称" width="150" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="avatar" label="头像" width="100">
          <template #default="{ row }">
            <el-avatar v-if="row.avatar" :src="row.avatar" :size="40" />
            <el-avatar v-else :size="40">未设置</el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            <span v-if="row.gender === 1">男</span>
            <span v-else-if="row.gender === 2">女</span>
            <span v-else>未知</span>
          </template>
        </el-table-column>
        <el-table-column prop="province" label="地区" width="150">
          <template #default="{ row }">
            {{ row.province }} {{ row.city }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">启用</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column prop="lastLoginTime" label="最后登录" width="180" />
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button type="success" size="small" @click="handleEnable(row)" :disabled="row.status === 1">启用</el-button>
            <el-button type="danger" size="small" @click="handleDisable(row)" :disabled="row.status === 0">禁用</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadUsers"
          @current-change="loadUsers"
        />
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="家长详情" width="700px">
      <div v-if="currentUser">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="微信昵称">{{ currentUser.nickname || '未设置' }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ getGenderText(currentUser.gender) }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ currentUser.phone || '未绑定' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ currentUser.email || '未设置' }}</el-descriptions-item>
          
          <el-descriptions-item label="城市">{{ currentUser.city || '未设置' }}</el-descriptions-item>
          <el-descriptions-item label="openid" :span="2">
            <el-input v-model="currentUser.openid" readonly size="small" />
          </el-descriptions-item>
          <el-descriptions-item label="unionid" :span="2" v-if="currentUser.unionid">
            <el-input v-model="currentUser.unionid" readonly size="small" />
          </el-descriptions-item>
          <el-descriptions-item label="注册时间" :span="2">{{ currentUser.createTime || '未知' }}</el-descriptions-item>
          <el-descriptions-item label="最后登录时间" :span="2">{{ currentUser.lastLoginTime || '从未登录' }}</el-descriptions-item>
          <el-descriptions-item label="更新时间" :span="2">{{ currentUser.updateTime || '未知' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getParentPage, enableParent, disableParent } from '@/api/system'

const searchForm = ref({
  nickname: '',
  status: null
})

const tableData = ref([])
const loading = ref(false)
const detailVisible = ref(false)
const currentUser = ref(null)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const getGenderText = (gender) => {
  if (gender === 1) return '男'
  if (gender === 2) return '女'
  return '未知'
}

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await getParentPage({
      page: pagination.current,
      pageSize: pagination.size,
      nickname: searchForm.value.nickname,
      status: searchForm.value.status
    })
    console.log(res.data)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载家长列表失败:', error)
    ElMessage.error('加载家长列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadUsers()
}

const handleReset = () => {
  searchForm.value = {
    nickname: '',
    status: null
  }
  pagination.current = 1
  loadUsers()
}

const handleView = (row) => {
  currentUser.value = row
  detailVisible.value = true
}

const handleEnable = async (row) => {
  try {
    await enableParent(row.id)
    ElMessage.success('已启用')
    loadUsers()
  } catch (error) {
    console.error('启用失败:', error)
    ElMessage.error('启用失败')
  }
}

const handleDisable = async (row) => {
  try {
    await disableParent(row.id)
    ElMessage.success('已禁用')
    loadUsers()
  } catch (error) {
    console.error('禁用失败:', error)
    ElMessage.error('禁用失败')
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user-management {
  padding: 0;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}
</style>