<template>
  <div class="forum-management">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="标题">
          <el-input v-model="searchForm.title" placeholder="请输入标题" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.category" placeholder="请选择分类" clearable>
            <el-option label="经验交流" value="经验交流" />
            <el-option label="问题咨询" value="问题咨询" />
            <el-option label="分享讨论" value="分享讨论" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="已删除" :value="0" />
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
        <el-table-column prop="title" label="标题" width="300" show-overflow-tooltip />
        <el-table-column prop="authorName" label="作者" width="120" />
        <el-table-column prop="authorPhone" label="联系电话" width="120" />
        <el-table-column prop="category" label="分类" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.category === '经验交流'" type="success">经验交流</el-tag>
            <el-tag v-else-if="row.category === '问题咨询'" type="warning">问题咨询</el-tag>
            <el-tag v-else-if="row.category === '分享讨论'" type="info">分享讨论</el-tag>
            <el-tag v-else type="info">其他</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览量" width="100" />
        <el-table-column prop="likeCount" label="点赞数" width="100" />
        <el-table-column prop="commentCount" label="评论数" width="100" />
        <el-table-column prop="isTop" label="置顶" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isTop === 1" type="danger">是</el-tag>
            <el-tag v-else type="info">否</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">正常</el-tag>
            <el-tag v-else type="danger">已删除</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
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
          @size-change="loadPosts"
          @current-change="loadPosts"
        />
      </div>
    </el-card>

    <!-- 帖子详情对话框 -->
    <el-dialog v-model="detailVisible" title="帖子详情" width="800px">
      <div v-if="currentPost" class="post-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="帖子ID">{{ currentPost.id }}</el-descriptions-item>
          <el-descriptions-item label="帖子标题">{{ currentPost.title }}</el-descriptions-item>
          <el-descriptions-item label="作者">{{ currentPost.authorName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentPost.authorPhone || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="分类">
            <el-tag v-if="currentPost.category === '经验交流'" type="success">经验交流</el-tag>
            <el-tag v-else-if="currentPost.category === '问题咨询'" type="warning">问题咨询</el-tag>
            <el-tag v-else-if="currentPost.category === '分享讨论'" type="info">分享讨论</el-tag>
            <el-tag v-else type="info">其他</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="置顶状态">
            <el-tag v-if="currentPost.isTop === 1" type="danger">已置顶</el-tag>
            <el-tag v-else type="info">未置顶</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="浏览量">{{ currentPost.viewCount }}</el-descriptions-item>
          <el-descriptions-item label="点赞数">{{ currentPost.likeCount }}</el-descriptions-item>
          <el-descriptions-item label="评论数">{{ currentPost.commentCount }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag v-if="currentPost.status === 1" type="success">正常</el-tag>
            <el-tag v-else type="danger">已删除</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">{{ currentPost.createTime }}</el-descriptions-item>
          <el-descriptions-item label="更新时间" :span="2">{{ currentPost.updateTime }}</el-descriptions-item>
        </el-descriptions>
        
        <div class="post-content">
          <h4>帖子内容：</h4>
          <div class="content-text">{{ currentPost.content }}</div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPostPage, deletePost } from '@/api/forum'

const searchForm = ref({
  title: '',
  category: '',
  status: null
})

const tableData = ref([])
const loading = ref(false)
const detailVisible = ref(false)
const currentPost = ref(null)

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const loadPosts = async () => {
  loading.value = true
  try {
    const res = await getPostPage({
      page: pagination.value.current,
      pageSize: pagination.value.size,
      title: searchForm.value.title,
      category: searchForm.value.category,
      status: searchForm.value.status
    })
    tableData.value = res.data.records
    pagination.value.total = res.data.total
  } catch (error) {
    console.error('加载帖子列表失败:', error)
    ElMessage.error('加载帖子列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.value.current = 1
  loadPosts()
}

const handleReset = () => {
  searchForm.value = {
    title: '',
    category: '',
    status: null
  }
  pagination.value.current = 1
  loadPosts()
}

const handleView = (row) => {
  currentPost.value = row
  detailVisible.value = true
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该帖子吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deletePost(id)
      ElMessage.success('删除成功')
      loadPosts()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  loadPosts()
})
</script>

<style scoped>
.forum-management {
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

.post-detail {
  padding: 10px 0;
}

.post-content {
  margin-top: 20px;
}

.post-content h4 {
  margin-bottom: 10px;
  color: #333;
}

.content-text {
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
  line-height: 1.8;
  white-space: pre-wrap;
  word-wrap: break-word;
  max-height: 400px;
  overflow-y: auto;
}
</style>