<!-- <template>
  <div class="site-management">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="站点名称">
          <el-input v-model="searchForm.name" placeholder="请输入站点名称" clearable />
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="searchForm.city" placeholder="请输入城市" clearable />
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
          <el-button type="success" @click="handleAdd">新增站点</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="siteCode" label="站点编码" width="120" />
        <el-table-column prop="siteName" label="站点名称" width="180" />
        <el-table-column prop="address" label="地址" width="200" show-overflow-tooltip />
        <el-table-column prop="city" label="城市" width="100" />
        <el-table-column prop="district" label="区县" width="100" />
        <el-table-column prop="phone" label="联系电话" width="120" />
        <el-table-column prop="contactPerson" label="联系人" width="100" />
        <el-table-column prop="capacity" label="日容量" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">启用</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button :type="row.status === 1 ? 'warning' : 'success'" size="small" @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
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
          @size-change="loadSites"
          @current-change="loadSites"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
        <el-form-item label="站点编码" prop="siteCode">
          <el-input v-model="formData.siteCode" placeholder="请输入站点编码" />
        </el-form-item>
        <el-form-item label="站点名称" prop="siteName">
          <el-input v-model="formData.siteName" placeholder="请输入站点名称" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="formData.province" placeholder="请输入省份" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="formData.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="区县" prop="district">
          <el-input v-model="formData.district" placeholder="请输入区县" />
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="formData.address" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="formData.contactPerson" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="工作时间" prop="workHours">
          <el-input v-model="formData.workHours" placeholder="例如: 周一至周五 08:30-17:30" />
        </el-form-item>
        <el-form-item label="服务范围" prop="serviceScope">
          <el-input v-model="formData.serviceScope" placeholder="请输入服务范围" />
        </el-form-item>
        <el-form-item label="日接种容量" prop="capacity">
          <el-input-number v-model="formData.capacity" :min="1" :max="1000" />
        </el-form-item>
        <el-form-item label="经度" prop="longitude">
          <el-input-number v-model="formData.longitude" :precision="6" :step="0.000001" />
        </el-form-item>
        <el-form-item label="纬度" prop="latitude">
          <el-input-number v-model="formData.latitude" :precision="6" :step="0.000001" />
        </el-form-item>
        <el-form-item label="描述信息" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入描述信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:9000'

const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const searchForm = reactive({
  name: '',
  city: '',
  status: null
})
const formData = reactive({
  id: null,
  siteCode: '',
  siteName: '',
  province: '',
  city: '',
  district: '',
  address: '',
  phone: '',
  contactPerson: '',
  workHours: '',
  serviceScope: '',
  capacity: 100,
  longitude: null,
  latitude: null,
  description: ''
})
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const rules = {
  siteCode: [{ required: true, message: '请输入站点编码', trigger: 'blur' }],
  siteName: [{ required: true, message: '请输入站点名称', trigger: 'blur' }],
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }]
}

const loadSites = async () => {
  try {
    const token = localStorage.getItem('token')
    const params = {
      page: pagination.current,
      pageSize: pagination.size,
      name: searchForm.name || undefined,
      city: searchForm.city || undefined,
      status: searchForm.status !== null ? searchForm.status : undefined
    }
    const response = await axios.get(`${API_BASE_URL}/api/site/page`, {
      headers: { Authorization: `Bearer ${token}` },
      params
    })
    if (response.data.code === 200) {
      tableData.value = response.data.data.records
      pagination.total = response.data.data.total
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadSites()
}

const handleReset = () => {
  searchForm.name = ''
  searchForm.city = ''
  searchForm.status = null
  pagination.current = 1
  loadSites()
}

const handleAdd = () => {
  dialogTitle.value = '新增站点'
  Object.assign(formData, {
    id: null,
    siteCode: '',
    siteName: '',
    province: '',
    city: '',
    district: '',
    address: '',
    phone: '',
    contactPerson: '',
    workHours: '',
    serviceScope: '',
    capacity: 100,
    longitude: null,
    latitude: null,
    description: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑站点'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const token = localStorage.getItem('token')
        const url = formData.id 
          ? `${API_BASE_URL}/api/site/update`
          : `${API_BASE_URL}/api/site/add`
        const method = formData.id ? 'put' : 'post'
        const response = await axios({ method, url, data: formData, headers: { Authorization: `Bearer ${token}` } })
        if (response.data.code === 200) {
          ElMessage.success(formData.id ? '更新成功' : '添加成功')
          dialogVisible.value = false
          loadSites()
        }
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

const handleToggleStatus = async (row) => {
  try {
    const token = localStorage.getItem('token')
    const newStatus = row.status === 1 ? 0 : 1
    const response = await axios.put(`${API_BASE_URL}/api/site/status/${row.id}`, 
      { status: newStatus },
      { headers: { Authorization: `Bearer ${token}` } }
    )
    if (response.data.code === 200) {
      ElMessage.success('状态更新成功')
      loadSites()
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该站点吗？', '提示', { type: 'warning' })
    const token = localStorage.getItem('token')
    const response = await axios.delete(`${API_BASE_URL}/api/site/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      loadSites()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadSites()
})
</script>

<style scoped>
.site-management {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
.table-card {
  min-height: 500px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> -->