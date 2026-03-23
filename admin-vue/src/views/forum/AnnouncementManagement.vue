<template>
  <div class="announcement-management">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="标题">
          <el-input v-model="searchForm.title" placeholder="请输入标题" clearable />
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
          <el-button type="success" @click="handleAdd">新增公告</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" width="200" />
        <el-table-column prop="content" label="内容" min-width="300">
          <template #default="{ row }">
            <div class="content-preview">{{ row.content }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="isPopup" label="弹窗显示" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.isPopup === 1" type="success">是</el-tag>
            <el-tag v-else type="info">否</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">启用</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button type="warning" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
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
          @size-change="loadAnnouncements"
          @current-change="loadAnnouncements"
        />
      </div>
    </el-card>

    <!-- 新增/编辑公告对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="announcementForm" :rules="announcementRules" ref="announcementFormRef" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="announcementForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="announcementForm.content"
            type="textarea"
            :rows="5"
            placeholder="请输入内容"
          />
        </el-form-item>
        <el-form-item label="弹窗显示" prop="isPopup">
          <el-switch v-model="announcementForm.isPopup" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="announcementForm.status">
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

    <!-- 查看公告对话框 -->
    <el-dialog v-model="viewDialogVisible" title="公告详情" width="600px">
      <el-descriptions :column="1" border v-if="currentAnnouncement">
        <el-descriptions-item label="ID">{{ currentAnnouncement.id }}</el-descriptions-item>
        <el-descriptions-item label="标题">{{ currentAnnouncement.title }}</el-descriptions-item>
        <el-descriptions-item label="内容">{{ currentAnnouncement.content }}</el-descriptions-item>
        <el-descriptions-item label="弹窗显示">
          <el-tag v-if="currentAnnouncement.isPopup === 1" type="success">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="currentAnnouncement.status === 1" type="success">启用</el-tag>
          <el-tag v-else type="danger">禁用</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentAnnouncement.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ currentAnnouncement.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAnnouncements, createAnnouncement, updateAnnouncement, deleteAnnouncement } from '@/api/forum'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增公告')
const viewDialogVisible = ref(false)
const announcementFormRef = ref(null)
const currentAnnouncement = ref(null)

const searchForm = reactive({
  title: '',
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const announcementForm = reactive({
  id: null,
  title: '',
  content: '',
  isPopup: 0,
  status: 1
})

const announcementRules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入内容', trigger: 'blur' }
  ]
}

const loadAnnouncements = async () => {
  loading.value = true
  try {
    const res = await getAnnouncements()
    // 过滤并分页
    let filtered = res.data || []
    if (searchForm.title) {
      filtered = filtered.filter(item => item.title.includes(searchForm.title))
    }
    if (searchForm.status !== null) {
      filtered = filtered.filter(item => item.status === searchForm.status)
    }

    pagination.total = filtered.length
    const start = (pagination.current - 1) * pagination.size
    const end = start + pagination.size
    tableData.value = filtered.slice(start, end)
  } catch (error) {
    ElMessage.error('加载公告列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadAnnouncements()
}

const handleReset = () => {
  searchForm.title = ''
  searchForm.status = null
  pagination.current = 1
  loadAnnouncements()
}

const handleAdd = () => {
  dialogTitle.value = '新增公告'
  dialogVisible.value = true
  resetForm()
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑公告'
  dialogVisible.value = true
  Object.assign(announcementForm, {
    id: row.id,
    title: row.title,
    content: row.content,
    isPopup: row.isPopup,
    status: row.status
  })
}

const handleView = (row) => {
  currentAnnouncement.value = row
  viewDialogVisible.value = true
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该公告吗？', '提示', {
      type: 'warning'
    })
    await deleteAnnouncement(id)
    ElMessage.success('删除成功')
    loadAnnouncements()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  await announcementFormRef.value.validate()
  try {
    if (dialogTitle.value === '新增公告') {
      await createAnnouncement(announcementForm)
      ElMessage.success('新增成功')
    } else {
      await updateAnnouncement(announcementForm)
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    loadAnnouncements()
  } catch (error) {
    ElMessage.error(dialogTitle.value === '新增公告' ? '新增失败' : '更新失败')
  }
}

const resetForm = () => {
  announcementForm.id = null
  announcementForm.title = ''
  announcementForm.content = ''
  announcementForm.isPopup = 0
  announcementForm.status = 1
  if (announcementFormRef.value) {
    announcementFormRef.value.resetFields()
  }
}

onMounted(() => {
  loadAnnouncements()
})
</script>

<style scoped>
.announcement-management {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.content-preview {
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>