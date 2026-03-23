<template>
  <div class="vaccine-management">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="疫苗名称">
          <el-input v-model="searchForm.name" placeholder="请输入疫苗名称" clearable />
        </el-form-item>
        <el-form-item label="生产厂家">
          <el-input v-model="searchForm.manufacturer" placeholder="请输入生产厂家" clearable />
        </el-form-item>
        <el-form-item label="疫苗类别">
          <el-select v-model="searchForm.category" placeholder="请选择疫苗类别" clearable>
            <el-option label="一类疫苗" value="一类疫苗" />
            <el-option label="二类疫苗" value="二类疫苗" />
          </el-select>
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
          <el-button type="success" @click="handleAdd">新增疫苗</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="code" label="疫苗编码" width="120" />
        <el-table-column prop="name" label="疫苗名称" width="200" />
        <el-table-column prop="category" label="类别" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.category === '一类疫苗'" type="primary">一类</el-tag>
            <el-tag v-else type="warning">二类</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="manufacturer" label="生产厂家" width="150" />
        <el-table-column prop="targetAge" label="可接种年龄" width="120">
          <template #default="{ row }">
            {{ row.targetAge !== null && row.targetAge !== undefined ? row.targetAge + '岁' : '未设置' }}
          </template>
        </el-table-column>
        <el-table-column prop="doseNumber" label="剂次" width="80" />
        <el-table-column prop="intervalDays" label="间隔天数" width="100" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            <span v-if="row.price > 0">¥{{ row.price }}</span>
            <span v-else>免费</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.stock === 0" type="danger">缺货</el-tag>
            <el-tag v-else-if="row.stock < 100" type="warning">紧张</el-tag>
            <el-tag v-else type="success">充足</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">启用</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250">
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
          @size-change="loadVaccines"
          @current-change="loadVaccines"
        />
      </div>
    </el-card>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewVisible" title="疫苗详情" width="800px">
      <el-descriptions v-if="currentVaccine" :column="2" border>
        <el-descriptions-item label="疫苗编码">{{ currentVaccine.code }}</el-descriptions-item>
        <el-descriptions-item label="疫苗名称">{{ currentVaccine.name }}</el-descriptions-item>
        <el-descriptions-item label="疫苗类别">{{ currentVaccine.category }}</el-descriptions-item>
        <el-descriptions-item label="生产厂家">{{ currentVaccine.manufacturer }}</el-descriptions-item>
        <el-descriptions-item label="可接种年龄">
          {{ currentVaccine.targetAge !== null && currentVaccine.targetAge !== undefined ? currentVaccine.targetAge + '岁' : '未设置' }}
        </el-descriptions-item>
        <el-descriptions-item label="接种剂次">{{ currentVaccine.doseNumber }}剂</el-descriptions-item>
        <el-descriptions-item label="间隔天数">{{ currentVaccine.intervalDays }}天</el-descriptions-item>
        <el-descriptions-item label="单位">{{ currentVaccine.unit }}</el-descriptions-item>
        <el-descriptions-item label="价格">
          <span v-if="currentVaccine.price > 0">¥{{ currentVaccine.price }}</span>
          <span v-else>免费</span>
        </el-descriptions-item>
        <el-descriptions-item label="库存数量">{{ currentVaccine.stock }}</el-descriptions-item>
        <el-descriptions-item label="禁忌症" :span="2">{{ currentVaccine.contraindications || '无' }}</el-descriptions-item>
        <el-descriptions-item label="不良反应" :span="2">{{ currentVaccine.adverseReactions || '无' }}</el-descriptions-item>
        <el-descriptions-item label="注意事项" :span="2">{{ currentVaccine.precautions || '无' }}</el-descriptions-item>
        <el-descriptions-item label="疫苗描述" :span="2">{{ currentVaccine.description || '无' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="currentVaccine.status === 1" type="success">启用</el-tag>
          <el-tag v-else type="danger">禁用</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentVaccine.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间" :span="2">{{ currentVaccine.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="110px">
        <el-form-item label="疫苗名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入疫苗名称" />
        </el-form-item>
        <el-form-item label="疫苗编码" prop="code">
          <el-input v-model="formData.code" placeholder="请输入疫苗编码" />
        </el-form-item>
        <el-form-item label="生产厂家" prop="manufacturer">
          <el-input v-model="formData.manufacturer" placeholder="请输入生产厂家" />
        </el-form-item>
        <el-form-item label="疫苗类别" prop="category">
          <el-select v-model="formData.category" placeholder="请选择疫苗类别" style="width: 100%">
            <el-option label="一类疫苗" value="一类疫苗" />
            <el-option label="二类疫苗" value="二类疫苗" />
          </el-select>
        </el-form-item>
        <el-form-item label="可接种年龄" prop="targetAge">
          <el-input-number v-model="formData.targetAge" :min="0" :max="18" placeholder="请输入年龄（岁）" />
          <span class="form-tip">输入岁数，例如0表示出生时，1表示1岁，2表示2岁</span>
        </el-form-item>
        <el-form-item label="接种剂次" prop="doseNumber">
          <el-input-number v-model="formData.doseNumber" :min="1" />
        </el-form-item>
        <el-form-item label="间隔天数" prop="intervalDays">
          <el-input-number v-model="formData.intervalDays" :min="0" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="formData.unit" placeholder="例如: 支、瓶" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="formData.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="formData.stock" :min="0" />
        </el-form-item>
        <el-form-item label="禁忌症" prop="contraindications">
          <el-input v-model="formData.contraindications" type="textarea" :rows="2" placeholder="请输入禁忌症" />
        </el-form-item>
        <el-form-item label="不良反应" prop="adverseReactions">
          <el-input v-model="formData.adverseReactions" type="textarea" :rows="2" placeholder="请输入不良反应" />
        </el-form-item>
        <el-form-item label="注意事项" prop="precautions">
          <el-input v-model="formData.precautions" type="textarea" :rows="2" placeholder="请输入注意事项" />
        </el-form-item>
        <el-form-item label="疫苗描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入疫苗描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getVaccinePage, createVaccine, updateVaccine, deleteVaccine } from '@/api/vaccine'

const searchForm = ref({
  name: '',
  manufacturer: '',
  category: '',
  status: null
})

const tableData = ref([])
const loading = ref(false)

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const dialogVisible = ref(false)
const viewVisible = ref(false)
const dialogTitle = ref('新增疫苗')
const formRef = ref()
const currentVaccine = ref(null)

const formData = ref({
  id: null,
  name: '',
  code: '',
  manufacturer: '',
  category: '',
  targetAge: null,
  doseNumber: 1,
  intervalDays: 0,
  unit: '',
  price: 0,
  stock: 0,
  contraindications: '',
  adverseReactions: '',
  precautions: '',
  description: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入疫苗名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入疫苗编码', trigger: 'blur' }],
  manufacturer: [{ required: true, message: '请输入生产厂家', trigger: 'blur' }],
  category: [{ required: true, message: '请选择疫苗类别', trigger: 'change' }],
  doseNumber: [{ required: true, message: '请输入接种剂次', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }]
}

const loadVaccines = async () => {
  loading.value = true
  try {
    const res = await getVaccinePage({
      page: pagination.value.current,
      pageSize: pagination.value.size,
      name: searchForm.value.name,
      manufacturer: searchForm.value.manufacturer,
      category: searchForm.value.category,
      status: searchForm.value.status
    })
    tableData.value = res.data.records
    pagination.value.total = res.data.total
  } catch (error) {
    console.error('加载疫苗列表失败:', error)
    ElMessage.error('加载疫苗列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.value.current = 1
  loadVaccines()
}

const handleReset = () => {
  searchForm.value = {
    name: '',
    manufacturer: '',
    category: '',
    status: null
  }
  pagination.value.current = 1
  loadVaccines()
}

const handleView = (row) => {
  currentVaccine.value = row
  viewVisible.value = true
}

const handleAdd = () => {
  dialogTitle.value = '新增疫苗'
  formData.value = {
    id: null,
    name: '',
    code: '',
    manufacturer: '',
    category: '',
    targetAge: null,
    doseNumber: 1,
    intervalDays: 0,
    unit: '',
    price: 0,
    stock: 0,
    contraindications: '',
    adverseReactions: '',
    precautions: '',
    description: '',
    status: 1
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑疫苗'
  formData.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这个疫苗吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteVaccine(id)
      ElMessage.success('删除成功')
      loadVaccines()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  await formRef.value.validate()
  
  try {
    if (formData.value.id) {
      await updateVaccine(formData.value)
      ElMessage.success('更新成功')
    } else {
      await createVaccine(formData.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadVaccines()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadVaccines()
})
</script>

<style scoped>
.vaccine-management {
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

.form-tip {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}
</style>