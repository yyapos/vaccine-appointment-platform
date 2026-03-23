<template>
  <div class="admin-management">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="searchForm.realName" placeholder="请输入真实姓名" clearable />
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
          <el-button type="success" @click="handleAdd">新增管理员</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">启用</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button type="warning" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button
              v-if="row.status === 1"
              type="danger"
              size="small"
              @click="handleDisable(row.id)"
            >禁用</el-button>
            <el-button
              v-else
              type="success"
              size="small"
              @click="handleEnable(row.id)"
            >启用</el-button>
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
          @size-change="loadAdmins"
          @current-change="loadAdmins"
        />
      </div>
    </el-card>

    <!-- 新增/编辑管理员对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="adminForm" :rules="adminRules" ref="adminFormRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="adminForm.username" placeholder="请输入用户名" :disabled="dialogTitle === '编辑管理员'" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialogTitle === '新增管理员'">
          <el-input v-model="adminForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="adminForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="adminForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="adminForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="adminForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看管理员对话框 -->
    <el-dialog v-model="viewDialogVisible" title="管理员详情" width="600px">
      <el-descriptions :column="2" border v-if="currentAdmin">
        <el-descriptions-item label="ID">{{ currentAdmin.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentAdmin.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ currentAdmin.realName }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentAdmin.phone || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentAdmin.email || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="currentAdmin.status === 1" type="success">启用</el-tag>
          <el-tag v-else type="danger">禁用</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ currentAdmin.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间" :span="2">{{ currentAdmin.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserPage, createUser, updateUser, enableUser, disableUser } from '@/api/system'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增管理员')
const viewDialogVisible = ref(false)
const adminFormRef = ref(null)
const currentAdmin = ref(null)

const searchForm = reactive({
  username: '',
  realName: '',
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const adminForm = reactive({
  id: null,
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  status: 1
})

const adminRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const loadAdmins = async () => {
  loading.value = true
  try {
    const res = await getUserPage({
      page: pagination.current,
      pageSize: pagination.size,
      username: searchForm.username,
      status: searchForm.status
    })
    console.log(res.data)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('加载管理员列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadAdmins()
}

const handleReset = () => {
  searchForm.username = ''
  searchForm.realName = ''
  searchForm.status = null
  pagination.current = 1
  loadAdmins()
}

const handleAdd = () => {
  dialogTitle.value = '新增管理员'
  dialogVisible.value = true
  resetForm()
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑管理员'
  dialogVisible.value = true
  Object.assign(adminForm, {
    id: row.id,
    username: row.username,
    password: '',
    realName: row.realName,
    phone: row.phone,
    email: row.email,
    status: row.status
  })
}

const handleView = (row) => {
  currentAdmin.value = row
  viewDialogVisible.value = true
}

const handleDisable = async (id) => {
  try {
    await ElMessageBox.confirm('确定要禁用该管理员吗？', '提示', {
      type: 'warning'
    })
    await disableUser(id)
    ElMessage.success('禁用成功')
    loadAdmins()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('禁用失败')
    }
  }
}

const handleEnable = async (id) => {
  try {
    await ElMessageBox.confirm('确定要启用该管理员吗？', '提示', {
      type: 'warning'
    })
    await enableUser(id)
    ElMessage.success('启用成功')
    loadAdmins()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('启用失败')
    }
  }
}

const handleSubmit = async () => {
  await adminFormRef.value.validate()
  try {
    if (dialogTitle.value === '新增管理员') {
      await createUser(adminForm)
      ElMessage.success('新增成功')
    } else {
      await updateUser(adminForm)
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    loadAdmins()
  } catch (error) {
    ElMessage.error(dialogTitle.value === '新增管理员' ? '新增失败' : '更新失败')
  }
}

const resetForm = () => {
  adminForm.id = null
  adminForm.username = ''
  adminForm.password = ''
  adminForm.realName = ''
  adminForm.phone = ''
  adminForm.email = ''
  adminForm.status = 1
  if (adminFormRef.value) {
    adminFormRef.value.resetFields()
  }
}

onMounted(() => {
  loadAdmins()
})
</script>

<style scoped>
.admin-management {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>