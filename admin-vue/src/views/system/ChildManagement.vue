<template>
  <div class="child-management">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="儿童姓名">
          <el-input v-model="searchForm.name" placeholder="请输入儿童姓名" clearable />
        </el-form-item>
        <el-form-item label="家长ID">
          <el-input v-model="searchForm.parentId" placeholder="请输入家长ID" clearable />
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
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            <span v-if="row.gender === 1">男</span>
            <span v-else-if="row.gender === 2">女</span>
            <span v-else>未知</span>
          </template>
        </el-table-column>
        <el-table-column prop="birthDate" label="出生日期" width="120" />
        <el-table-column prop="parentId" label="家长ID" width="100" />
        <el-table-column prop="isKeyChild" label="是否关键儿童" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.isKeyChild === 1" type="success">是</el-tag>
            <el-tag v-else type="info">否</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">正常</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
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
          @size-change="loadChildren"
          @current-change="loadChildren"
        />
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="儿童详情" width="800px">
      <el-descriptions v-if="currentChild" :column="2" border>
        <el-descriptions-item label="ID">{{ currentChild.id }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ currentChild.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">
          {{ currentChild.gender === 1 ? '男' : currentChild.gender === 2 ? '女' : '未知' }}
        </el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ currentChild.birthDate }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ currentChild.idCard || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="家长ID">{{ currentChild.parentId }}</el-descriptions-item>
        <el-descriptions-item label="是否关键儿童">
          {{ currentChild.isKeyChild === 1 ? '是' : '否' }}
        </el-descriptions-item>
        <el-descriptions-item label="关键原因" v-if="currentChild.isKeyChild === 1">
          {{ currentChild.keyReason || '未填写' }}
        </el-descriptions-item>
        <el-descriptions-item label="家庭地址" :span="2">
          {{ currentChild.address || '未填写' }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          {{ currentChild.status === 1 ? '正常' : '禁用' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentChild.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间" :span="2">{{ currentChild.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getChildPage } from '@/api/system'

const searchForm = ref({
  name: '',
  parentId: ''
})

const tableData = ref([])
const loading = ref(false)
const detailVisible = ref(false)
const currentChild = ref(null)

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const loadChildren = async () => {
  loading.value = true
  try {
    const res = await getChildPage({
      page: pagination.value.current,
      pageSize: pagination.value.size,
      name: searchForm.value.name,
      parentId: searchForm.value.parentId
    })
    tableData.value = res.data.records
    pagination.value.total = res.data.total
  } catch (error) {
    console.error('加载儿童列表失败:', error)
    ElMessage.error('加载儿童列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.value.current = 1
  loadChildren()
}

const handleReset = () => {
  searchForm.value = {
    name: '',
    parentId: ''
  }
  pagination.value.current = 1
  loadChildren()
}

const handleView = (row) => {
  currentChild.value = row
  detailVisible.value = true
}

onMounted(() => {
  loadChildren()
})
</script>

<style scoped>
.child-management {
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