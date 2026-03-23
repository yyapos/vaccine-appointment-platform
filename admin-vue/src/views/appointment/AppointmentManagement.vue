<template>
  <div class="appointment-management">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="儿童姓名">
          <el-input v-model="searchForm.childName" placeholder="请输入儿童姓名" clearable />
        </el-form-item>
        <el-form-item label="疫苗名称">
          <el-input v-model="searchForm.vaccineName" placeholder="请输入疫苗名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="审核通过" :value="1" />
            <el-option label="审核拒绝" :value="2" />
            <el-option label="已接种" :value="3" />
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
        <el-table-column prop="childName" label="儿童姓名" width="120" />
        <el-table-column prop="childGender" label="性别" width="80">
          <template #default="{ row }">
            <span v-if="row.childGender === 1">男</span>
            <span v-else-if="row.childGender === 2">女</span>
            <span v-else>未知</span>
          </template>
        </el-table-column>
        <el-table-column prop="vaccineName" label="疫苗名称" width="200" />
        <el-table-column prop="manufacturer" label="生产厂家" width="150" />
        <el-table-column prop="doseNumber" label="剂次" width="80" />
        <el-table-column prop="appointmentDate" label="预约日期" width="120" />
        <el-table-column prop="parentPhone" label="联系电话" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">待审核</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">审核通过</el-tag>
            <el-tag v-else-if="row.status === 2" type="danger">审核拒绝</el-tag>
            <el-tag v-else-if="row.status === 3" type="info">已接种</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="预约时间" width="180" />
        <el-table-column label="操作" width="300">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              type="success"
              size="small"
              @click="handleApprove(row)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.status === 0"
              type="danger"
              size="small"
              @click="handleReject(row)"
            >
              拒绝
            </el-button>
            <el-button
              v-if="row.status === 1"
              type="primary"
              size="small"
              @click="handleComplete(row)"
            >
              完成
            </el-button>
            <el-button type="info" size="small" @click="handleView(row)">查看</el-button>
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
          @size-change="loadAppointments"
          @current-change="loadAppointments"
        />
      </div>
    </el-card>

    <el-dialog v-model="detailDialogVisible" title="预约详情" width="800px">
      <el-descriptions v-if="currentDetail" :column="2" border>
        <el-descriptions-item label="预约ID">{{ currentDetail.id }}</el-descriptions-item>
        <el-descriptions-item label="预约状态">
          <el-tag v-if="currentDetail.status === 0" type="warning">待审核</el-tag>
          <el-tag v-else-if="currentDetail.status === 1" type="success">审核通过</el-tag>
                      <el-tag v-else-if="currentDetail.status === 2" type="danger">审核拒绝</el-tag>
                      <el-tag v-else-if="currentDetail.status === 3" type="info">已接种</el-tag>        </el-descriptions-item>
        
        <el-descriptions-item label="儿童姓名">{{ currentDetail.childName }}</el-descriptions-item>
        <el-descriptions-item label="儿童性别">
          <span v-if="currentDetail.childGender === 1">男</span>
          <span v-else-if="currentDetail.childGender === 2">女</span>
          <span v-else>未知</span>
        </el-descriptions-item>
        
        <el-descriptions-item label="出生日期">{{ currentDetail.childBirthDate || '未知' }}</el-descriptions-item>
        <el-descriptions-item label="是否关键儿童">
          <el-tag v-if="currentDetail.isKeyChild === 1" type="danger">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </el-descriptions-item>
        
        <el-descriptions-item label="疫苗名称">{{ currentDetail.vaccineName }}</el-descriptions-item>
        <el-descriptions-item label="生产厂家">{{ currentDetail.manufacturer }}</el-descriptions-item>
        
        <el-descriptions-item label="接种剂次">{{ currentDetail.doseNumber || 1 }}</el-descriptions-item>
        <el-descriptions-item label="预约日期">{{ currentDetail.appointmentDate }}</el-descriptions-item>
        
        <el-descriptions-item label="预约时间">{{ currentDetail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="审核时间">{{ currentDetail.auditTime || '未审核' }}</el-descriptions-item>
        
        <el-descriptions-item label="联系电话">{{ currentDetail.parentPhone || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="家长ID">{{ currentDetail.parentId }}</el-descriptions-item>
        
        <el-descriptions-item label="备注信息" :span="2">{{ currentDetail.remark || '无' }}</el-descriptions-item>
        <el-descriptions-item label="拒绝原因" :span="2">
          <el-text v-if="currentDetail.rejectReason" type="danger">{{ currentDetail.rejectReason }}</el-text>
          <span v-else>无</span>
        </el-descriptions-item>
        <el-descriptions-item label="审核意见" :span="2">{{ currentDetail.auditOpinion || '无' }}</el-descriptions-item>
        
        <el-descriptions-item label="创建时间" :span="2">{{ currentDetail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间" :span="2">{{ currentDetail.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 拒绝对话框 -->
    <el-dialog v-model="rejectDialogVisible" title="拒绝预约" width="500px">
      <el-form :model="rejectForm" label-width="100px">
        <el-form-item label="拒绝原因">
          <el-input v-model="rejectForm.reason" type="textarea" :rows="4" placeholder="请输入拒绝原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReject">确定拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAppointmentPage, approveAppointment, rejectAppointment, completeAppointment } from '@/api/appointment'

const searchForm = ref({
  childName: '',
  vaccineName: '',
  status: null
})

const tableData = ref([])
const loading = ref(false)
const detailDialogVisible = ref(false)
const rejectDialogVisible = ref(false)
const currentDetail = ref(null)
const currentRejectId = ref(null)

const rejectForm = ref({
  reason: ''
})

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const loadAppointments = async () => {
  loading.value = true
  try {
    const res = await getAppointmentPage({
      page: pagination.value.current,
      pageSize: pagination.value.size,
      status: searchForm.value.status,
      childName: searchForm.value.childName,
      vaccineName: searchForm.value.vaccineName
    })
    tableData.value = res.data.records
    pagination.value.total = res.data.total
  } catch (error) {
    console.error('加载预约列表失败:', error)
    ElMessage.error('加载预约列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.value.current = 1
  loadAppointments()
}

const handleReset = () => {
  searchForm.value = {
    childName: '',
    vaccineName: '',
    status: null
  }
  pagination.value.current = 1
  loadAppointments()
}

const handleView = (row) => {
  currentDetail.value = row
  detailDialogVisible.value = true
}

const handleApprove = async (row) => {
  try {
    await approveAppointment(row.id)
    ElMessage.success('审核通过')
    loadAppointments()
  } catch (error) {
    console.error('审核通过失败:', error)
    ElMessage.error('审核通过失败')
  }
}

const handleReject = (row) => {
  currentRejectId.value = row.id
  rejectForm.value.reason = ''
  rejectDialogVisible.value = true
}

const confirmReject = async () => {
  if (!rejectForm.value.reason.trim()) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  
  try {
    await rejectAppointment(currentRejectId.value, rejectForm.value.reason)
    ElMessage.success('已拒绝预约')
    rejectDialogVisible.value = false
    loadAppointments()
  } catch (error) {
    console.error('拒绝预约失败:', error)
    ElMessage.error('拒绝预约失败')
  }
}

const handleComplete = async (row) => {
  try {
    await completeAppointment(row.id)
    ElMessage.success('已完成接种')
    loadAppointments()
  } catch (error) {
    console.error('完成接种失败:', error)
    ElMessage.error('完成接种失败')
  }
}

onMounted(() => {
  loadAppointments()
})
</script>

<style scoped>
.appointment-management {
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